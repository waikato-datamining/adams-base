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
 * InstancePanel.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.instance;

import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.data.instance.Instance;
import adams.data.instance.InstancePoint;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.GUIHelper;
import adams.gui.core.Undo;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.event.PaintListener;
import adams.gui.scripting.AbstractScriptingEngine;
import adams.gui.scripting.ScriptingEngine;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.ContainerListPopupMenuSupplier;
import adams.gui.visualization.container.ContainerTable;
import adams.gui.visualization.container.DataContainerPanelWithSidePanel;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.CoordinatesPaintlet;
import adams.gui.visualization.core.CoordinatesPaintlet.Coordinates;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.PopupMenuCustomizer;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.core.plot.TipTextCustomizer;
import weka.core.Instances;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * A panel for displaying instances.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstancePanel
  extends DataContainerPanelWithSidePanel<Instance, InstanceContainerManager>
  implements PaintListener, ContainerListPopupMenuSupplier<InstanceContainerManager,InstanceContainer>, PopupMenuCustomizer,
             TipTextCustomizer, AntiAliasingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 7985845939008731534L;

  /** the instance ID list. */
  protected InstanceContainerList m_InstanceContainerList;

  /** paintlet for drawing the graph. */
  protected InstanceLinePaintlet m_InstancePaintlet;

  /** paintlet for drawing the X-axis. */
  protected CoordinatesPaintlet m_CoordinatesPaintlet;

  /** the undo manager. */
  protected Undo m_Undo;

  /** whether to adjust to visible data or not. */
  protected boolean m_AdjustToVisibleData;

  /** the hit detector for the tooltip. */
  protected InstancePointHitDetector m_InstancePointHitDetector;

  /** the maximum number of columns for the tooltip. */
  protected int m_ToolTipMaxColumns;

  /** the zoom overview panel. */
  protected InstanceZoomOverviewPanel m_PanelZoomOverview;

  /** the file chooser for saving a specific sequence. */
  protected SpreadSheetFileChooser m_FileChooser;

  /** the dialog for displaying a sequence. */
  protected List<SpreadSheetDialog> m_ViewDialogs;

  /**
   * Initializes the panel.
   */
  public InstancePanel() {
    super();
  }

  /**
   * Initializes the panel.
   *
   * @param title	the title for the panel
   */
  public InstancePanel(String title) {
    super(title);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    m_Undo                  = null;
    m_AdjustToVisibleData   = true;

    super.initialize();
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
  protected InstanceContainerManager newContainerManager() {
    return new InstanceContainerManager(this);
  }

  /**
   * Returns the paintlet used for painting the containers.
   *
   * @return		the paintlet
   */
  @Override
  public InstanceLinePaintlet getContainerPaintlet() {
    return m_InstancePaintlet;
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

    m_InstanceContainerList = new InstanceContainerList();
    m_InstanceContainerList.setTitle("Instances");
    m_InstanceContainerList.setManager(getContainerManager());
    m_InstanceContainerList.setAllowSearch(props.getBoolean("ContainerList.AllowSearch", false));
    m_InstanceContainerList.setPopupMenuSupplier(this);
    m_InstanceContainerList.addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent e) {
	final ContainerTable table = m_InstanceContainerList.getTable();
	if ((table.getRowCount() > 0) && (table.getSelectedRowCount() == 0)) {
	  Runnable runnable = new Runnable() {
	    @Override
	    public void run() {
	      table.getSelectionModel().addSelectionInterval(0, 0);
	    }
	  };
	  SwingUtilities.invokeLater(runnable);
	}
      }
    });

    m_SidePanel.setLayout(new BorderLayout(0, 0));
    m_SidePanel.add(m_InstanceContainerList, BorderLayout.CENTER);

    panel = new JPanel();
    panel.setMinimumSize(new Dimension(1, props.getInteger("Axis.Bottom.Width", 0)));
    panel.setPreferredSize(new Dimension(1, props.getInteger("Axis.Bottom.Width", 0)));
    m_SidePanel.add(panel, BorderLayout.SOUTH);

    getPlot().setPopupMenuCustomizer(this);

    // paintlets
    m_InstancePaintlet = new InstanceLinePaintlet();
    m_InstancePaintlet.setStrokeThickness(props.getDouble("Plot.StrokeThickness", 1.0).floatValue());
    m_InstancePaintlet.setAntiAliasingEnabled(props.getBoolean("Plot.AntiAliasing", true));
    m_InstancePaintlet.setPanel(this);
    m_CoordinatesPaintlet = new CoordinatesPaintlet();
    m_CoordinatesPaintlet.setYInvisible(true);
    m_CoordinatesPaintlet.setPanel(this);
    m_CoordinatesPaintlet.setXColor(props.getColor("Plot.CoordinatesColor." + Coordinates.X, Color.DARK_GRAY));
    m_CoordinatesPaintlet.setYColor(props.getColor("Plot.CoordinatesColor." + Coordinates.Y, Color.DARK_GRAY));

    m_InstancePointHitDetector = new InstancePointHitDetector(this);
    getPlot().setTipTextCustomizer(this);

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

    m_PanelZoomOverview = new InstanceZoomOverviewPanel();
    m_PlotWrapperPanel.add(m_PanelZoomOverview, BorderLayout.SOUTH);
    m_PanelZoomOverview.setDataContainerPanel(this);
  }

  /**
   * Returns the current container manager.
   *
   * @return		the manager
   */
  public AbstractContainerManager getSequenceManager() {
    return m_Manager;
  }

  /**
   * Sets the undo manager to use, can be null if no undo-support wanted.
   *
   * @param value	the undo manager to use
   */
  public void setUndo(Undo value) {
    m_Undo = value;
  }

  /**
   * Returns the current undo manager, can be null.
   *
   * @return		the undo manager, if any
   */
  public Undo getUndo() {
    return m_Undo;
  }

  /**
   * Returns whether an Undo manager is currently available.
   *
   * @return		true if an undo manager is set
   */
  public boolean isUndoSupported() {
    return (m_Undo != null);
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
    List<InstancePoint>	points;
    Iterator<InstancePoint>	iter;
    InstancePoint		point;
    double 			minX;
    double 			maxX;
    double 			minY;
    double 			maxY;
    int				i;

    minX = Double.MAX_VALUE;
    maxX = -Double.MAX_VALUE;
    minY = 0;
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
      if (InstancePoint.toDouble(points.get(0).getX()) < minX)
	minX = InstancePoint.toDouble(points.get(0).getX());
      if (InstancePoint.toDouble(points.get(points.size() - 1).getX()) > maxX)
	maxX = InstancePoint.toDouble(points.get(points.size() - 1).getX());

      iter = points.iterator();
      while (iter.hasNext()) {
	point = iter.next();
	if (InstancePoint.toDouble(point.getY()) > maxY)
	  maxY = InstancePoint.toDouble(point.getY());
	if (InstancePoint.toDouble(point.getY()) < minY)
	  minY = InstancePoint.toDouble(point.getY());
      }
    }

    // update axes
    getPlot().getAxis(Axis.LEFT).setMinimum(minY);
    getPlot().getAxis(Axis.LEFT).setMaximum(maxY);
    getPlot().getAxis(Axis.BOTTOM).setMinimum(minX);
    getPlot().getAxis(Axis.BOTTOM).setMaximum(maxX);
  }

  /**
   * Optional customizing of the menu that is about to be popped up.
   *
   * @param e		the mous event
   * @param menu	the menu to customize
   */
  @Override
  public void customizePopupMenu(MouseEvent e, JPopupMenu menu) {
    JMenuItem	item;

    item = new JMenuItem();
    item.setIcon(GUIHelper.getEmptyIcon());
    if (!m_InstancePaintlet.isMarkersDisabled())
      item.setText("Disable markers");
    else
      item.setText("Enable markers");
    item.addActionListener((ActionEvent ae) -> {
      m_InstancePaintlet.setMarkersDisabled(
        !m_InstancePaintlet.isMarkersDisabled());
      repaint();
    });
    menu.add(item);

    menu.addSeparator();

    item = new JMenuItem();
    item.setIcon(GUIHelper.getEmptyIcon());
    if (isSidePanelVisible())
      item.setText("Hide side panel");
    else
      item.setText("Show side panel");
    item.addActionListener((ActionEvent ae) -> setSidePanelVisible(!isSidePanelVisible()));
    menu.add(item);

    item = new JMenuItem();
    item.setIcon(GUIHelper.getEmptyIcon());
    if (m_AdjustToVisibleData)
      item.setText("Adjust to loaded data");
    else
      item.setText("Adjust to visible data");
    item.addActionListener((ActionEvent ae) -> {
      m_AdjustToVisibleData = !m_AdjustToVisibleData;
      update();
    });
    menu.add(item);
  }

  /**
   * Returns a popup menu for the table of the spectrum list.
   *
   * @param table	the affected table
   * @param row	the row the mouse is currently over
   * @return		the popup menu
   */
  @Override
  public BasePopupMenu getContainerListPopupMenu(final ContainerTable<InstanceContainerManager,InstanceContainer> table, final int row) {
    BasePopupMenu	result;
    JMenuItem		item;
    final int[] 	indices;

    result    = new BasePopupMenu();
    if (table.getSelectedRows().length == 0)
      indices = new int[]{row};
    else
      indices = table.getSelectedRows();

    item = new JMenuItem("Toggle visibility");
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	for (int i = 0; i < getContainerManager().count(); i++) {
	  InstanceContainer c = getContainerManager().get(indices[i]);
	  c.setVisible(!c.isVisible());
	}
      }
    });
    result.add(item);

    item = new JMenuItem("Show all");
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	for (int i = 0; i < getContainerManager().count(); i++) {
	  if (!getContainerManager().get(i).isVisible())
	    getContainerManager().get(i).setVisible(true);
	}
      }
    });
    result.add(item);

    item = new JMenuItem("Hide all");
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	for (int i = 0; i < getContainerManager().count(); i++) {
	  if (getContainerManager().get(i).isVisible())
	    getContainerManager().get(i).setVisible(false);
	}
      }
    });
    result.add(item);

    item = new JMenuItem("Choose color...");
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String msg = "Choose color";
	InstanceContainer cont = null;
	Color color = Color.BLUE;
	if (indices.length == 1) {
	  cont = getContainerManager().get(indices[0]);
	  msg += " for " + cont.getData().getID();
	  color = cont.getColor();
	}
	Color c = JColorChooser.showDialog(
	    table,
	    msg,
	    color);
	if (c == null)
	  return;
	for (int i: indices)
	  getContainerManager().get(i).setColor(c);
      }
    });
    result.add(item);

    if (getContainerManager().getAllowRemoval()) {
      result.addSeparator();

      item = new JMenuItem("Remove");
      item.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  m_InstanceContainerList.getTable().removeContainers(indices);
	}
      });
      result.add(item);

      item = new JMenuItem("Remove all");
      item.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  m_InstanceContainerList.getTable().removeAllContainers();
	}
      });
      result.add(item);
    }

    result.addSeparator();

    item = new JMenuItem("Save as...");
    item.setEnabled(indices.length == 1);
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	saveInstance(getContainerManager().get(indices[0]));
      }
    });
    result.add(item);

    item = new JMenuItem("View as table");
    item.setEnabled(indices.length == 1);
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	viewInstance(getContainerManager().get(indices[0]));
      }
    });
    result.add(item);

    return result;
  }

  /**
   * Saves the specified instance as spreadsheet file.
   *
   * @param cont	the instance to save
   */
  protected void saveInstance(InstanceContainer cont) {
    int			retVal;
    Instance 		inst;
    SpreadSheetWriter	writer;

    if (m_FileChooser == null)
      m_FileChooser = new SpreadSheetFileChooser();

    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
      return;

    inst   = cont.getData();
    writer = m_FileChooser.getWriter();
    if (!writer.write(inst.toSpreadSheet(), m_FileChooser.getSelectedFile()))
      GUIHelper.showErrorMessage(
	  this, "Failed to save instance to file:\n" + m_FileChooser.getSelectedFile());
  }

  /**
   * Views the specified instance in a table.
   *
   * @param cont	the instance to view
   */
  protected void viewInstance(InstanceContainer cont) {
    Instance 		isnt;
    SpreadSheetDialog	dialog;
    SpreadSheet		sheet;

    if (m_ViewDialogs == null)
      m_ViewDialogs = new ArrayList<SpreadSheetDialog>();

    isnt  = cont.getData();
    sheet = isnt.toSpreadSheet();
    if (getParentDialog() != null)
      dialog = new SpreadSheetDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new SpreadSheetDialog(getParentFrame(), false);
    m_ViewDialogs.add(dialog);
    dialog.setTitle("Instance: " + cont.getDisplayID());
    dialog.setSize(
      GUIHelper.getInteger("DefaultSmallDialog.Height", 400),
      GUIHelper.getInteger("DefaultSmallDialog.Width", 600));
    dialog.setLocationRelativeTo(this);
    dialog.setSpreadSheet(sheet);
    dialog.setVisible(true);
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
  public InstanceZoomOverviewPanel getZoomOverviewPanel() {
    return m_PanelZoomOverview;
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
   * Returns whether the display is adjusted to only the visible spectrums
   * or all of them.
   *
   * @return		true if the plot is adjusted to only the visible data
   */
  public boolean getAdjustToVisibleData() {
    return m_AdjustToVisibleData;
  }

  /**
   * Returns the paintlet for painting the instance.
   *
   * @return		the paintlet
   */
  public InstanceLinePaintlet getInstancePaintlet() {
    return m_InstancePaintlet;
  }

  /**
   * Returns the panel with the instance list.
   *
   * @return		the panel
   */
  public InstanceContainerList getInstanceContainerList() {
    return m_InstanceContainerList;
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  public void setAntiAliasingEnabled(boolean value) {
    m_InstancePaintlet.setAntiAliasingEnabled(value);
    if (m_PanelZoomOverview.getContainerPaintlet() instanceof AntiAliasingSupporter)
      ((AntiAliasingSupporter) m_PanelZoomOverview.getContainerPaintlet()).setAntiAliasingEnabled(value);
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		true if anti-aliasing is used
   */
  public boolean isAntiAliasingEnabled() {
    return m_InstancePaintlet.isAntiAliasingEnabled();
  }

  /**
   * Returns the currently visible instances.
   *
   * @return		the instances, null if none visible
   */
  public Instances getInstances() {
    Instances			result;
    Instance			inst;
    int				i;
    List<InstanceContainer>	list;

    result = null;

    list = getContainerManager().getAllVisible();
    for (i = 0; i < list.size(); i++) {
      inst = list.get(i).getData();
      if (result == null)
	result = inst.getDatasetHeader();
      result.add(inst.toInstance());
    }

    return result;
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
    String	result;
    MouseEvent	event;
    String	hit;

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

    hit = (String) m_InstancePointHitDetector.detect(event);
    if (hit != null)
      result = GUIHelper.processTipText(hit, m_ToolTipMaxColumns);

    return result;
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
   * Hook method, called after the update was performed.
   */
  @Override
  protected void postUpdate() {
    super.postUpdate();

    if (m_PanelZoomOverview != null)
      m_PanelZoomOverview.update();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_InstancePointHitDetector != null) {
      m_InstancePointHitDetector.cleanUp();
      m_InstancePointHitDetector = null;
    }

    if (m_ViewDialogs != null) {
      for (SpreadSheetDialog dialog: m_ViewDialogs)
	dialog.dispose();
      m_ViewDialogs.clear();
      m_ViewDialogs = null;
    }

    super.cleanUp();
  }
}
