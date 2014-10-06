/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * TimeseriesPanel.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.timeseries;

import gnu.trove.list.array.TIntArrayList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import adams.core.Properties;
import adams.core.Range;
import adams.core.base.BaseDateTime;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.io.output.AbstractTimeseriesWriter;
import adams.data.io.output.MetaFileWriter;
import adams.data.report.Report;
import adams.data.statistics.InformativeStatistic;
import adams.data.timeseries.PeriodicityHelper;
import adams.data.timeseries.PeriodicityType;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.event.PaintListener;
import adams.gui.scripting.AbstractScriptingEngine;
import adams.gui.scripting.Invisible;
import adams.gui.scripting.ScriptingEngine;
import adams.gui.scripting.Visible;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.visualization.container.ContainerListPopupMenuSupplier;
import adams.gui.visualization.container.ContainerTable;
import adams.gui.visualization.container.DataContainerPanelWithSidePanel;
import adams.gui.visualization.container.NotesFactory;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.CoordinatesPaintlet;
import adams.gui.visualization.core.CoordinatesPaintlet.Coordinates;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.PopupMenuCustomizer;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.core.plot.TipTextCustomizer;
import adams.gui.visualization.report.ReportContainer;
import adams.gui.visualization.report.ReportFactory;
import adams.gui.visualization.statistics.HistogramFactory;
import adams.gui.visualization.statistics.InformativeStatisticFactory;

/**
 * Special panel for displaying the spectral data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesPanel<T extends Timeseries, M extends TimeseriesContainerManager<C>, C extends TimeseriesContainer>
  extends DataContainerPanelWithSidePanel<T, M>
  implements PaintListener, ContainerListPopupMenuSupplier<M,C>,
             PopupMenuCustomizer, TipTextCustomizer {

  /** for serialization. */
  private static final long serialVersionUID = -9059718549932104312L;

  /** whether to adjust to visible data or not. */
  protected boolean m_AdjustToVisibleData;

  /** paintlet for drawing the X-axis. */
  protected CoordinatesPaintlet m_CoordinatesPaintlet;

  /** paintlet for drawing the timeseries. */
  protected TimeseriesPaintlet m_TimeseriesPaintlet;

  /** paintlet for drawing the periodicity background. */
  protected PeriodicityPaintlet m_PeriodicityPaintlet;

  /** paintlet for drawing the timeseries. */
  protected SelectedTimestampPaintlet m_SelectedTimestampPaintlet;

  /** the panel listing the spectra. */
  protected TimeseriesContainerList m_TimeseriesContainerList;

  /** the dialog for the histogram setup. */
  protected HistogramFactory.SetupDialog m_HistogramSetup;

  /** for detecting hits. */
  protected TimeseriesPointHitDetector m_TimeseriesPointHitDetector;

  /** the maximum number of columns for the tooltip. */
  protected int m_ToolTipMaxColumns;

  /** the zoom overview panel. */
  protected TimeseriesZoomOverviewPanel m_PanelZoomOverview;
  
  /** the export dialog. */
  protected TimeseriesExportDialog m_ExportDialog;
  
  /** the minimum Y to use. */
  protected Double m_MinY;
  
  /** the maximum Y to use. */
  protected Double m_MaxY;
  
  /** the minimum X to use. */
  protected BaseDateTime m_MinX;
  
  /** the maximum X to use. */
  protected BaseDateTime m_MaxX;

  /**
   * Initializes the panel without title.
   */
  public TimeseriesPanel() {
    super();
  }

  /**
   * Initializes the panel with the given title.
   *
   * @param title	the title for the panel
   */
  public TimeseriesPanel(String title) {
    super(title);
  }

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_AdjustToVisibleData = true;
    m_HistogramSetup      = null;
    m_MinY                = null;
    m_MaxY                = null;
    m_MinX                = null;
    m_MaxX                = null;
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Returns the container manager to use.
   *
   * @return		the container manager
   */
  @Override
  protected M newContainerManager() {
    return (M) new TimeseriesContainerManager(this, getDatabaseConnection());
  }

  /**
   * Returns the paintlet used for painting the containers.
   * 
   * @return		the paintlet
   */
  @Override
  public TimeseriesPaintlet getContainerPaintlet() {
    return m_TimeseriesPaintlet;
  }

  /**
   * Returns the paintlet used for painting the periodicity background.
   * 
   * @return		the paintlet
   */
  public PeriodicityPaintlet getPeriodicityPaintlet() {
    return m_PeriodicityPaintlet;
  }

  /**
   * Returns whether the panel has a side panel. If that is the case, a
   * JSplitPanel is used.
   *
   * @return		true if a side panel is to be added
   */
  protected boolean hasSidePanel() {
    return true;
  }

  /**
   * Initializes the GUI.
   */
  @Override
  protected void initGUI() {
    Properties	props;
    JPanel	panel;

    super.initGUI();

    props = getProperties();

    m_ToolTipMaxColumns = props.getInteger("Plot.ToolTip.MaxColumns", 80);

    setAdjustToVisibleData(props.getBoolean("Plot.AdjustToVisibleData", false));

    m_TimeseriesContainerList = new TimeseriesContainerList();
    m_TimeseriesContainerList.setManager(getContainerManager());
    m_TimeseriesContainerList.setAllowSearch(props.getBoolean("ContainerList.AllowSearch", false));
    m_TimeseriesContainerList.setPopupMenuSupplier(this);
    m_TimeseriesContainerList.setDisplayDatabaseID(true);
    m_TimeseriesContainerList.addTableModelListener(new TableModelListener() {
      public void tableChanged(TableModelEvent e) {
	final ContainerTable table = m_TimeseriesContainerList.getTable();
	if ((table.getRowCount() > 0) && (table.getSelectedRowCount() == 0)) {
	  Runnable runnable = new Runnable() {
	    public void run() {
	      table.getSelectionModel().addSelectionInterval(0, 0);
	    }
	  };
	  SwingUtilities.invokeLater(runnable);
	}
      }
    });

    m_SidePanel.setLayout(new BorderLayout(0, 0));
    m_SidePanel.add(m_TimeseriesContainerList);

    panel = new JPanel();
    panel.setMinimumSize(new Dimension(1, props.getInteger("Axis.Bottom.Width", 0)));
    panel.setPreferredSize(new Dimension(1, props.getInteger("Axis.Bottom.Width", 0)));
    m_SidePanel.add(panel, BorderLayout.SOUTH);

    // paintlets
    m_TimeseriesPaintlet = new TimeseriesPaintlet();
    m_TimeseriesPaintlet.setStrokeThickness(props.getDouble("Plot.StrokeThickness", 1.0).floatValue());
    m_TimeseriesPaintlet.setPanel(this);
    m_TimeseriesPaintlet.setAntiAliasingEnabled(props.getBoolean("Plot.AntiAliasing", true));
    m_PeriodicityPaintlet = new PeriodicityPaintlet();
    m_PeriodicityPaintlet.setPanel(this);
    m_PeriodicityPaintlet.setPeriodicity(PeriodicityType.valueOf(props.getProperty("Plot.Periodicity", PeriodicityType.NONE.toString())));
    m_PeriodicityPaintlet.setColorProvider(AbstractColorProvider.forCommandLine(props.getProperty("Plot.PeriodicityColorProvider", OptionUtils.getCommandLine(new PeriodicityColorProvider()))));
    getPlot().getAxis(Axis.BOTTOM).setNumberFormat(PeriodicityHelper.getFormat(m_PeriodicityPaintlet.getPeriodicity()));

    m_SelectedTimestampPaintlet = new SelectedTimestampPaintlet();
    m_SelectedTimestampPaintlet.setPanel(this);
    
    m_CoordinatesPaintlet = new CoordinatesPaintlet();
    m_CoordinatesPaintlet.setYInvisible(true);
    m_CoordinatesPaintlet.setPanel(this);
    m_CoordinatesPaintlet.setXColor(props.getColor("Plot.CoordinatesColor." + Coordinates.X, Color.DARK_GRAY));
    m_CoordinatesPaintlet.setYColor(props.getColor("Plot.CoordinatesColor." + Coordinates.Y, Color.DARK_GRAY));
    
    getPlot().setPopupMenuCustomizer(this);

    try {
      getContainerManager().setColorProvider(
	  (AbstractColorProvider) OptionUtils.forAnyCommandLine(
	      AbstractColorProvider.class,
	      props.getProperty("Plot.ColorProvider", DefaultColorProvider.class.getName())));
    }
    catch (Exception e) {
      System.err.println(getClass().getName() + " - Failed to set the color provider:");
      getContainerManager().setColorProvider(new DefaultColorProvider());
    }

    m_TimeseriesPointHitDetector = new TimeseriesPointHitDetector(this);
    getPlot().setTipTextCustomizer(this);

    m_PanelZoomOverview = new TimeseriesZoomOverviewPanel();
    m_PlotWrapperPanel.add(m_PanelZoomOverview, BorderLayout.SOUTH);
    m_PanelZoomOverview.setDataContainerPanel(this);
  }

  /**
   * Returns the paintlet for painting the timeseries.
   *
   * @return		the paintlet
   */
  public TimeseriesPaintlet getTimeseriesPaintlet() {
    return m_TimeseriesPaintlet;
  }

  /**
   * Returns the paintlet for painting the selected timestamps.
   *
   * @return		the paintlet
   */
  public SelectedTimestampPaintlet getSelectedTimestampPaintlet() {
    return m_SelectedTimestampPaintlet;
  }

  /**
   * Sets whether the display is adjusted to only the visible data or
   * everything currently loaded.
   *
   * @param value	if true then plot is adjusted to visible data
   */
  public void setAdjustToVisibleData(boolean value) {
    m_AdjustToVisibleData = value;
    update();
  }

  /**
   * Returns whether the display is adjusted to only the visible timeseries
   * or all of them.
   *
   * @return		true if the plot is adjusted to only the visible data
   */
  public boolean getAdjustToVisibleData() {
    return m_AdjustToVisibleData;
  }

  /**
   * Sets the zoom overview panel visible or hides it.
   * 
   * @param value	if true then the panel is displayed
   */
  public void setZoomOverviewPanelVisible(boolean value) {
    m_PanelZoomOverview.setVisible(value);
  }
  
  /**
   * Returns whether the zoom overview panel is visible or not.
   * 
   * @return		true if visible
   */
  public boolean isZoomOverviewPanelVisible() {
    return m_PanelZoomOverview.isVisible();
  }

  /**
   * Sets the fixed minimum for the Y axis.
   * 
   * @param value	the minimum, null to automatically calculate
   */
  public void setMinY(Double value) {
    m_MinY = value;
    update();
  }
  
  /**
   * Returns the fixed minimum for the Y axis.
   * 
   * @return		the minimum, null if automatically calculated
   */
  public Double getMinY() {
    return m_MinY;
  }

  /**
   * Sets the fixed maximum for the Y axis.
   * 
   * @param value	the maximum, null to automatically calculate
   */
  public void setMaxY(Double value) {
    m_MaxY = value;
    update();
  }
  
  /**
   * Returns the fixed maximum for the Y axis.
   * 
   * @return		the maximum, null if automatically calculated
   */
  public Double getMaxY() {
    return m_MaxY;
  }

  /**
   * Sets the fixed minimum for the X axis.
   * 
   * @param value	the minimum, null to automatically calculate
   */
  public void setMinX(BaseDateTime value) {
    m_MinX = value;
    update();
  }
  
  /**
   * Returns the fixed minimum for the X axis.
   * 
   * @return		the minimum, null if automatically calculated
   */
  public BaseDateTime getMinX() {
    return m_MinX;
  }

  /**
   * Sets the fixed maximum for the X axis.
   * 
   * @param value	the maximum, null to automatically calculate
   */
  public void setMaxX(BaseDateTime value) {
    m_MaxX = value;
    update();
  }
  
  /**
   * Returns the fixed maximum for the X axis.
   * 
   * @return		the maximum, null if automatically calculated
   */
  public BaseDateTime getMaxX() {
    return m_MaxX;
  }
  
  /**
   * Returns true if the paintlets can be executed.
   *
   * @param g		the graphics context
   * @return		true if painting can go ahead
   */
  @Override
  protected boolean canPaint(Graphics g) {
    return ((getPlot() != null) && (m_Manager != null));
  }

  /**
   * Updates the axes with the min/max of the new data.
   */
  @Override
  public void prepareUpdate() {
    List<TimeseriesPoint>	points;
    double 				minX;
    double 				maxX;
    double 				minY;
    double 				maxY;
    int					i;

    // X
    if (m_MinX != null)
      minX = m_MinX.dateValue().getTime();
    else
      minX = Double.MAX_VALUE;
    if (m_MaxX != null)
      maxX = m_MaxX.dateValue().getTime();
    else
      maxX = -Double.MAX_VALUE;
    // Y
    if (m_MinY != null)
      minY = m_MinY;
    else
      minY = Double.MAX_VALUE;
    if (m_MaxY != null)
      maxY = m_MaxY;
    else
      maxY = -Double.MAX_VALUE;

    for (i = 0; i < getContainerManager().count(); i++) {
      if (m_AdjustToVisibleData) {
	if (!getContainerManager().isVisible(i))
	  continue;
      }

      points = getContainerManager().get(i).getData().toList();
      if (points.size() == 0)
	continue;

      // determine min/max
      if (m_MinX == null)
	minX = Math.min(minX, points.get(0).getTimestamp().getTime());
      if (m_MaxX == null)
	maxX = Math.max(maxX, points.get(points.size() - 1).getTimestamp().getTime());
      for (TimeseriesPoint point: points) {
	if (m_MaxY == null)
	  maxY = Math.max(maxY, point.getValue());
	if (m_MinY == null)
	  minY = Math.min(minY, point.getValue());
      }
    }

    // center, if only 1 data point
    if (minX == maxX) {
      minX -= 1;
      maxX += 1;
    }

    // update axes
    getPlot().getAxis(Axis.LEFT).setMinimum(minY);
    getPlot().getAxis(Axis.LEFT).setMaximum(maxY);
    getPlot().getAxis(Axis.BOTTOM).setMinimum(minX);
    getPlot().getAxis(Axis.BOTTOM).setMaximum(maxX);
  }

  /**
   * Returns a popup menu for the table of the timeseries list.
   *
   * @param table	the affected table
   * @param row	the row the mouse is currently over
   * @return		the popup menu
   */
  public JPopupMenu getContainerListPopupMenu(final ContainerTable<M,C> table, final int row) {
    JPopupMenu		result;
    JMenuItem		item;
    final int[]		indices;

    result = new JPopupMenu();

    if (table.getSelectedRows().length == 0)
      indices = new int[]{row};
    else
      indices = table.getSelectedRows();
    
    item = new JMenuItem("Toggle visibility");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	TIntArrayList visible = new TIntArrayList();
	TIntArrayList invisible = new TIntArrayList();
	for (int i = 0; i < indices.length; i++) {
	  int index = indices[i];
	  if (getContainerManager().get(index).isVisible())
	    invisible.add(index);
	  else
	    visible.add(index);
	}
	Range range = new Range();
	range.setMax(getContainerManager().count());
	if (invisible.size() > 0) {
	  range.setIndices(invisible.toArray());
	  getScriptingEngine().add(
	      TimeseriesPanel.this,
	      Invisible.ACTION + " " + range.getRange());
	}
	if (visible.size() > 0) {
	  range.setIndices(visible.toArray());
	  getScriptingEngine().add(
	      TimeseriesPanel.this,
	      Visible.ACTION + " " + range.getRange());
	}
      }
    });
    result.add(item);

    item = new JMenuItem("Choose color...");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	Color c = null;
	if (indices.length == 1) {
	  c = JColorChooser.showDialog(
	      table,
	      "Choose color for " + getContainerManager().get(indices[0]).getData().getID(),
	      getContainerManager().get(indices[0]).getColor());
	}
	else {
	  c = JColorChooser.showDialog(
	      table,
	      "Choose color",
	      getContainerManager().get(row).getColor());
	}
	if (c != null) {
	  for (int index: indices)
	    getContainerManager().get(index).setColor(c);
	}
      }
    });
    result.add(item);

    if (getContainerManager().getAllowRemoval()) {
      result.addSeparator();

      item = new JMenuItem("Remove");
      item.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_TimeseriesContainerList.getTable().removeContainers(indices);
	}
      });
      result.add(item);

      item = new JMenuItem("Remove all");
      item.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_TimeseriesContainerList.getTable().removeAllContainers();
	}
      });
      result.add(item);
    }

    result.addSeparator();

    item = new JMenuItem("Information");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	List<InformativeStatistic> stats = new ArrayList<InformativeStatistic>();
	for (int i = 0; i < indices.length; i++)
	  stats.add(getContainerManager().get(indices[i]).getData().toStatistic());
	showStatistics(stats);
      }
    });
    result.add(item);

    item = new JMenuItem("Raw data");
    item.setEnabled(indices.length == 1);
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	showRawData(getContainerManager().get(row));
      }
    });
    result.add(item);

    item = new JMenuItem("Reports");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	List<TimeseriesContainer> data = new ArrayList<TimeseriesContainer>();
	for (int i = 0; i < indices.length; i++)
	  data.add(getContainerManager().get(indices[i]));
	showReports(data);
      }
    });
    result.add(item);

    item = new JMenuItem("Notes");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	List<TimeseriesContainer> data = new ArrayList<TimeseriesContainer>();
	for (int i = 0; i < indices.length; i++)
	  data.add(getContainerManager().get(indices[i]));
	showNotes(data);
      }
    });
    result.add(item);

    return result;
  }

  /**
   * Optional customizing of the menu that is about to be popped up.
   *
   * @param e		the mous event
   * @param menu	the menu to customize
   */
  public void customizePopupMenu(MouseEvent e, JPopupMenu menu) {
    JMenuItem	item;

    menu.addSeparator();

    item = new JMenuItem();
    if (!getTimeseriesPaintlet().isMarkersDisabled())
      item.setText("Disable markers");
    else
      item.setText("Enable markers");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	getTimeseriesPaintlet().setMarkersDisabled(!getTimeseriesPaintlet().isMarkersDisabled());
	repaint();
      }
    });
    menu.add(item);

    item = new JMenuItem();
    if (isSidePanelVisible())
      item.setText("Hide side panel");
    else
      item.setText("Show side panel");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	setSidePanelVisible(!isSidePanelVisible());
      }
    });
    menu.add(item);

    item = new JMenuItem();
    if (getAdjustToVisibleData())
      item.setText("Adjust to loaded data");
    else
      item.setText("Adjust to visible data");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	setAdjustToVisibleData(!getAdjustToVisibleData());
      }
    });
    menu.add(item);

    menu.addSeparator();

    item = new JMenuItem("Series statistics");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	List<InformativeStatistic> stats = new ArrayList<InformativeStatistic>();
	for (int i = 0; i < getContainerManager().count(); i++) {
	  if (getContainerManager().isVisible(i))
	    stats.add(getContainerManager().get(i).getData().toStatistic());
	}
	showStatistics(stats);
      }
    });
    menu.add(item);

    menu.addSeparator();

    item = new JMenuItem("Save visible series...");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	saveVisibleSeries();
      }
    });
    menu.add(item);

    SendToActionUtils.addSendToSubmenu(this, menu);
  }

  /**
   * Displays the notes for the given chromatograms.
   *
   * @param data	the chromatograms to display
   */
  protected void showNotes(List<TimeseriesContainer> data) {
    NotesFactory.Dialog		dialog;

    if (getParentDialog() != null)
      dialog = NotesFactory.getDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = NotesFactory.getDialog(getParentFrame(), false);
    dialog.setData(data);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Displays a dialog with the given statistics.
   *
   * @param stats	the statistics to display
   */
  protected void showStatistics(List<InformativeStatistic> stats) {
    InformativeStatisticFactory.Dialog	dialog;

    if (getParentDialog() != null)
      dialog = InformativeStatisticFactory.getDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = InformativeStatisticFactory.getDialog(getParentFrame(), false);

    dialog.setStatistics(stats);
    dialog.setVisible(true);
  }

  /**
   * Displays a dialog with the given timeseries as raw data.
   *
   * @param cont	the container to display the raw data for
   */
  protected void showRawData(TimeseriesContainer cont) {
    SpreadSheetDialog	dialog;
    
    if (getParentDialog() != null)
      dialog = new SpreadSheetDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new SpreadSheetDialog(getParentFrame(), false);
    dialog.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Timeseries");
    dialog.setSize(600, 600);
    dialog.setLocationRelativeTo(this);
    dialog.setSpreadSheet(cont.getData().toSpreadSheet());
    dialog.setNumDecimals(getProperties().getInteger("SpreadSheetPanel.NumDecimals", 3));
    dialog.setVisible(true);
  }

  /**
   * Displays a dialog with the given reports.
   *
   * @param data	the timeseries to display the reports for
   */
  protected void showReports(List<TimeseriesContainer> data) {
    ReportFactory.Dialog	dialog;
    List<ReportContainer>	conts;
    ReportContainer		rc;

    if (getParentDialog() != null)
      dialog = ReportFactory.getDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = ReportFactory.getDialog(getParentFrame(), false);

    conts = new ArrayList<ReportContainer>();
    for (TimeseriesContainer c: data) {
      if (c.getData().hasReport())
	rc = new ReportContainer(null, c.getData());
      else
	rc = new ReportContainer(null, new Report());
      conts.add(rc);
    }

    dialog.setData(conts);
    dialog.setReportContainerListWidth((int) getSidePanel().getPreferredSize().getWidth());
    dialog.setVisible(true);
  }

  /**
   * Returns the indices of the selected spectra.
   *
   * @return		the indices
   */
  public int[] getSelectedIndices() {
    return m_TimeseriesContainerList.getTable().getSelectedRows();
  }

  /**
   * Returns the selected spectra.
   *
   * @return		the spectra
   */
  public Timeseries[] getSelectedSpectra() {
    Timeseries[]	result;
    int[]		indices;
    int			i;
    M			manager;

    indices = getSelectedIndices();
    result  = new Timeseries[indices.length];
    manager = getContainerManager();

    for (i = 0; i < indices.length; i++)
      result[i] = manager.get(i).getData();

    return result;
  }

  /**
   * Returns the panel listing the timeseries.
   *
   * @return		the panel
   */
  public TimeseriesContainerList getTimeseriesContainerList() {
    return m_TimeseriesContainerList;
  }

  /**
   * Returns the current scripting engine, can be null.
   *
   * @return		the current engine
   */
  @Override
  public AbstractScriptingEngine getScriptingEngine() {
    return ScriptingEngine.getSingleton(getDatabaseConnection());
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  public void setAntiAliasingEnabled(boolean value) {
    m_TimeseriesPaintlet.setAntiAliasingEnabled(value);
    if (m_PanelZoomOverview.getContainerPaintlet() instanceof AntiAliasingSupporter)
      ((AntiAliasingSupporter) m_PanelZoomOverview.getContainerPaintlet()).setAntiAliasingEnabled(value);
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		true if anti-aliasing is used
   */
  public boolean isAntiAliasingEnabled() {
    return m_TimeseriesPaintlet.isAntiAliasingEnabled();
  }

  /**
   * Hook method, called after the update was performed.
   */
  @Override
  protected void postUpdate() {
    super.postUpdate();
    
    if (m_PanelZoomOverview != null)
      m_PanelZoomOverview.update();
  }

  /**
   * Processes the given tip text. Among the current mouse position, the
   * panel that initiated the call are also provided.
   *
   * @param panel	the content panel that initiated this call
   * @param mouse	the mouse position
   * @param tiptext	the tiptext so far
   * @return		the processed tiptext
   */
  public String processTipText(PlotPanel panel, Point mouse, String tiptext) {
    String			result;
    MouseEvent			event;
    String			hit;

    result = tiptext;
    event  = new MouseEvent(
			getPlot().getContent(),
			MouseEvent.MOUSE_MOVED,
			new Date().getTime(),
			0,
			(int) mouse.getX(),
			(int) mouse.getY(),
			0,
			false);

    hit = (String) m_TimeseriesPointHitDetector.detect(event);
    if (hit != null)
      result += hit;

    result = GUIHelper.processTipText(result, m_ToolTipMaxColumns);

    return result;
  }

  /**
   * Saves the visible timeseries to a directory.
   */
  protected void saveVisibleSeries() {
    AbstractTimeseriesWriter 	writer;
    String 			filename;
    int				i;
    TimeseriesContainer 	cont;
    String[] 			ext;
    List<Timeseries> 		data;
    String 			prefix;
    
    if (m_ExportDialog == null) {
      if (getParentDialog() != null)
	m_ExportDialog = new TimeseriesExportDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_ExportDialog = new TimeseriesExportDialog(getParentFrame(), true);
    }
    m_ExportDialog.setLocationRelativeTo(TimeseriesPanel.this);
    m_ExportDialog.setVisible(true);
    if (m_ExportDialog.getOption() != TimeseriesExportDialog.APPROVE_OPTION)
      return;

    // write data
    writer = m_ExportDialog.getExport();
    if (writer instanceof MetaFileWriter)
      ext = ((MetaFileWriter) writer).getActualFormatExtensions();
    else
      ext = writer.getFormatExtensions();
    if (writer.canWriteMultiple() && m_ExportDialog.getCombine()) {
      filename = getContainerManager().getVisible(0).getDisplayID() + "_and_" + (getContainerManager().countVisible() - 1) + "_more";
      filename = FileUtils.createFilename(filename, "");
      filename = m_ExportDialog.getDirectory().getAbsolutePath() + File.separator + filename + "." + ext[0];
      writer.setOutput(new PlaceholderFile(filename));
      data = new ArrayList<Timeseries>();
      for (i = 0; i < getContainerManager().countVisible(); i++) {
	cont = getContainerManager().getVisible(i);
	data.add(cont.getData());
      }
      if (!writer.write(data))
	GUIHelper.showErrorMessage(TimeseriesPanel.this, "Failed to write timeseries data to '" + filename + "'!");
    }
    else {
      prefix = m_ExportDialog.getDirectory().getAbsolutePath();
      for (i = 0; i < getContainerManager().countVisible(); i++) {
	cont = getContainerManager().getVisible(i);
	filename = prefix + File.separator + FileUtils.createFilename(cont.getDisplayID(), "") + "." + ext[0];
	writer.setOutput(new PlaceholderFile(filename));
	if (!writer.write(cont.getData())) {
	  GUIHelper.showErrorMessage(TimeseriesPanel.this, "Failed to write timeseries #" + (i+1) + " to '" + filename + "'!");
	  break;
	}
      }
    }
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_TimeseriesContainerList.cleanUp();
    
    if (m_ExportDialog != null) {
      m_ExportDialog.dispose();
      m_ExportDialog = null;
    }

    super.cleanUp();
  }
}
