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
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.timeseries;

import adams.core.Properties;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.io.output.AbstractTimeseriesWriter;
import adams.data.io.output.MetaFileWriter;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.statistics.InformativeStatistic;
import adams.data.timeseries.PeriodicityHelper;
import adams.data.timeseries.PeriodicityType;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.ColorHelper;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.event.PaintListener;
import adams.gui.scripting.AbstractScriptingEngine;
import adams.gui.scripting.ScriptingEngine;
import adams.gui.visualization.container.ContainerTable;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.CoordinatesPaintlet;
import adams.gui.visualization.core.CoordinatesPaintlet.Coordinates;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.Paintlet;
import adams.gui.visualization.core.PaintletWithFixedXRange;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.core.plot.TipTextCustomizer;
import adams.gui.visualization.report.ReportContainer;
import adams.gui.visualization.report.ReportFactory;
import adams.gui.visualization.statistics.InformativeStatisticFactory;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Special panel for displaying the spectral data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesPanel<T extends Timeseries, M extends TimeseriesContainerManager<C>, C extends TimeseriesContainer>
  extends DataContainerPanelWithContainerList<T, M, C>
  implements PaintListener, TipTextCustomizer, AntiAliasingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -9059718549932104312L;

  /** whether to adjust to visible data or not. */
  protected boolean m_AdjustToVisibleData;

  /** paintlet for drawing the X-axis. */
  protected CoordinatesPaintlet m_CoordinatesPaintlet;

  /** paintlet for drawing the timeseries. */
  protected AbstractTimeseriesPaintlet m_TimeseriesPaintlet;

  /** paintlet for drawing the periodicity background. */
  protected PeriodicityPaintlet m_PeriodicityPaintlet;

  /** paintlet for drawing the timeseries. */
  protected SelectedTimestampPaintlet m_SelectedTimestampPaintlet;

  /** for detecting hits. */
  protected TimeseriesPointHitDetector m_TimeseriesPointHitDetector;

  /** the maximum number of columns for the tooltip. */
  protected int m_ToolTipMaxColumns;

  /** the zoom overview panel. */
  protected TimeseriesZoomOverviewPanel m_PanelZoomOverview;

  /** the export dialog. */
  protected TimeseriesExportDialog m_ExportDialog;

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
  public AbstractTimeseriesPaintlet getContainerPaintlet() {
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

    panel = new JPanel();
    panel.setMinimumSize(new Dimension(1, props.getInteger("Axis.Bottom.Width", 0)));
    panel.setPreferredSize(new Dimension(1, props.getInteger("Axis.Bottom.Width", 0)));
    m_SidePanel.add(panel, BorderLayout.SOUTH);

    // paintlets
    m_TimeseriesPaintlet = new TimeseriesPaintlet();
    m_TimeseriesPaintlet.setStrokeThickness(props.getDouble("Plot.StrokeThickness", 1.0).floatValue());
    m_TimeseriesPaintlet.setPanel(this);
    if (m_TimeseriesPaintlet instanceof AntiAliasingSupporter)
      ((AntiAliasingSupporter) m_TimeseriesPaintlet).setAntiAliasingEnabled(props.getBoolean("Plot.AntiAliasing", true));
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
   * Returns the container list.
   *
   * @return		the list
   */
  @Override
  protected TimeseriesContainerList createContainerList() {
    TimeseriesContainerList 	result;

    result = new TimeseriesContainerList();
    result.setManager(getContainerManager());
    result.setAllowSearch(getProperties().getBoolean("ContainerList.AllowSearch", false));
    result.setPopupMenuSupplier(this);
    result.setDisplayDatabaseID(true);
    result.addTableModelListener((TableModelEvent e) -> {
        final ContainerTable table = result.getTable();
        if ((table.getRowCount() > 0) && (table.getSelectedRowCount() == 0)) {
          SwingUtilities.invokeLater(() -> table.getSelectionModel().addSelectionInterval(0, 0));
        }
    });

    return result;
  }

  /**
   * Sets the paintlet for painting the timeseries.
   *
   * @param paintlet		the paintlet
   */
  public void setTimeseriesPaintlet(AbstractTimeseriesPaintlet paintlet) {
    removePaintlet(m_TimeseriesPaintlet);
    m_TimeseriesPaintlet = paintlet;
    m_TimeseriesPaintlet.setPanel(this);
    addPaintlet(m_TimeseriesPaintlet);
  }

  /**
   * Returns the paintlet for painting the timeseries.
   *
   * @return		the paintlet
   */
  public AbstractTimeseriesPaintlet getTimeseriesPaintlet() {
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
   * Returns the zoom overview panel.
   *
   * @return		the panel
   */
  public TimeseriesZoomOverviewPanel getZoomOverviewPanel() {
    return m_PanelZoomOverview;
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
    double 			minX;
    double 			maxX;
    double 			minY;
    double 			maxY;
    int				i;
    boolean			determineYRange;
    boolean			determineXRange;

    determineYRange = !(m_TimeseriesPaintlet instanceof adams.gui.visualization.core.PaintletWithFixedYRange);
    determineXRange = !(m_TimeseriesPaintlet instanceof PaintletWithFixedXRange);
    minY            = Double.MAX_VALUE;
    maxY            = -Double.MAX_VALUE;
    minX            = Double.MAX_VALUE;
    maxX            = -Double.MAX_VALUE;

    if (!determineYRange) {
      minY = ((adams.gui.visualization.core.PaintletWithFixedYRange) m_TimeseriesPaintlet).getMinimumY();
      maxY = ((adams.gui.visualization.core.PaintletWithFixedYRange) m_TimeseriesPaintlet).getMaximumY();
    }
    if (!determineXRange) {
      minX = ((PaintletWithFixedXRange) m_TimeseriesPaintlet).getMinimumX();
      maxX = ((PaintletWithFixedXRange) m_TimeseriesPaintlet).getMaximumX();
    }

    for (i = 0; i < getContainerManager().count(); i++) {
      if (m_AdjustToVisibleData) {
        if (!getContainerManager().isVisible(i))
          continue;
      }

      points = getContainerManager().get(i).getData().toList();
      if (points.size() == 0)
        continue;

      // determine min/max
      if (determineXRange) {
        minX = Math.min(minX, points.get(0).getTimestamp().getTime());
        maxX = Math.max(maxX, points.get(points.size() - 1).getTimestamp().getTime());
      }
      if (determineYRange) {
        maxY = Math.max(maxY, getContainerManager().get(i).getData().getMaxValue().getValue());
        minY = Math.min(minY, getContainerManager().get(i).getData().getMinValue().getValue());
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
   * Displays a dialog with the given statistics.
   *
   * @param stats	the statistics to display
   */
  public void showStatistics(List<InformativeStatistic> stats) {
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
  public void showRawData(C cont) {
    SpreadSheetDialog	dialog;

    if (getParentDialog() != null)
      dialog = new SpreadSheetDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new SpreadSheetDialog(getParentFrame(), false);
    dialog.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Timeseries");
    dialog.setSize(
      GUIHelper.getInteger("DefaultSmallDialog.Width", 600),
      GUIHelper.getInteger("DefaultSmallDialog.Width", 600));
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
  public void showReports(List<C> data) {
    ReportFactory.Dialog	dialog;
    List<ReportContainer>	conts;
    ReportContainer		rc;

    if (getParentDialog() != null)
      dialog = ReportFactory.getDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = ReportFactory.getDialog(getParentFrame(), false);

    conts = new ArrayList<>();
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
    return m_ContainerList.getTable().getSelectedRows();
  }

  /**
   * Returns the selected spectra.
   *
   * @return		the spectra
   */
  public Timeseries[] getSelectedSeries() {
    List<Timeseries>		result;

    result = new ArrayList<>();
    for (int index: getSelectedIndices())
      result.add(getContainerManager().get(index).getData());

    return result.toArray(new Timeseries[result.size()]);
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
    if (m_TimeseriesPaintlet instanceof AntiAliasingSupporter)
      ((AntiAliasingSupporter) m_TimeseriesPaintlet).setAntiAliasingEnabled(value);
    if (m_PanelZoomOverview.getContainerPaintlet() instanceof AntiAliasingSupporter)
      ((AntiAliasingSupporter) m_PanelZoomOverview.getContainerPaintlet()).setAntiAliasingEnabled(value);
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		true if anti-aliasing is used
   */
  public boolean isAntiAliasingEnabled() {
    return (m_TimeseriesPaintlet instanceof AntiAliasingSupporter)
      && ((AntiAliasingSupporter) m_TimeseriesPaintlet).isAntiAliasingEnabled();
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
  @Override
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
   * Returns the paintlet used for painting the data.
   *
   * @return		the paintlet
   */
  @Override
  public Paintlet getDataPaintlet() {
    return m_TimeseriesPaintlet;
  }

  /**
   * Sets the paintlet to use for painting the data.
   *
   * @param value	the paintlet
   */
  public void setDataPaintlet(Paintlet value) {
    removePaintlet(m_TimeseriesPaintlet);
    m_TimeseriesPaintlet = (AbstractTimeseriesPaintlet) value;
    m_TimeseriesPaintlet.setPanel(this);
    addPaintlet(m_TimeseriesPaintlet);
  }

  /**
   * Saves the visible timeseries to a directory.
   */
  public void saveVisibleSeries() {
    AbstractTimeseriesWriter 	writer;
    String 			filename;
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
      data = new ArrayList<>();
      for (C c: getTableModelContainers(true))
	data.add(c.getData());
      if (!writer.write(data))
        GUIHelper.showErrorMessage(TimeseriesPanel.this, "Failed to write timeseries data to '" + filename + "'!");
    }
    else {
      prefix = m_ExportDialog.getDirectory().getAbsolutePath();
      for (C c: getTableModelContainers(true)) {
        filename = prefix + File.separator + FileUtils.createFilename(c.getDisplayID(), "") + "." + ext[0];
        writer.setOutput(new PlaceholderFile(filename));
        if (!writer.write(c.getData())) {
          GUIHelper.showErrorMessage(TimeseriesPanel.this, "Failed to write timeseries #" + c + " to '" + filename + "'!");
          break;
        }
      }
    }
  }

  /**
   * Returns true if storing the color in the report of container's data object
   * is supported.
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsStoreColorInReport() {
    return true;
  }

  /**
   * Stores the color of the container in the report of container's
   * data object.
   *
   * @param indices	the indices of the containers of the container manager
   * @param name	the field name to use
   */
  @Override
  public void storeColorInReport(int[] indices, String name) {
    Field 	field;
    C		cont;

    field = new Field(name, DataType.STRING);
    for (int index: indices) {
      cont = getContainerManager().get(index);
      cont.getData().getReport().addField(field);
      cont.getData().getReport().setValue(field, ColorHelper.toHex(cont.getColor()));
    }
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_ContainerList.cleanUp();

    if (m_ExportDialog != null) {
      m_ExportDialog.dispose();
      m_ExportDialog = null;
    }

    super.cleanUp();
  }
}
