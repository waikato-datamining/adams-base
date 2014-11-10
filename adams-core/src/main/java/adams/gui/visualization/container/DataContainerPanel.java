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
 * DataContainerPanel.java
 * Copyright (C) 2008-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import adams.core.CleanUpHandler;
import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOption;
import adams.data.container.DataContainer;
import adams.data.image.BufferedImageHelper;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnectionHandler;
import adams.event.DatabaseConnectionChangeEvent;
import adams.event.DatabaseConnectionChangeListener;
import adams.gui.core.BasePanel;
import adams.gui.event.DataChangeEvent;
import adams.gui.event.DataChangeListener;
import adams.gui.scripting.AbstractScriptingEngine;
import adams.gui.scripting.ScriptingEngineHandler;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.visualization.core.PaintablePanel;
import adams.gui.visualization.core.Paintlet;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.axis.AbstractTickGenerator;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.core.axis.Visibility;
import adams.gui.visualization.core.plot.Axis;

/**
 * Special panel for displaying the DataContainer data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of container to visualize
 * @param <M> the type of container manager to use
 */
public abstract class DataContainerPanel<T extends DataContainer, M extends AbstractContainerManager>
  extends PaintablePanel
  implements StatusMessageHandler, ScriptingEngineHandler,
             DatabaseConnectionHandler, DatabaseConnectionChangeListener,
             DataChangeListener, ContainerListManager<M>, CleanUpHandler,
             SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 5719709547705333418L;

  /** the titel of the panel. */
  protected String m_Title;

  /** holds the data. */
  protected M m_Manager;

  /** the content panel for drawing. */
  protected PlotPanel m_PlotPanel;

  /** the wrapper panel for the plot panel. */
  protected BasePanel m_PlotWrapperPanel;
  
  /** the listeners for data changes. */
  protected HashSet<DataChangeListener> m_DataChangeListeners;

  /** a status message handler for displaying status messages. */
  protected StatusMessageHandler m_StatusMessageHandler;

  /** the database connection. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /** the setup for the panel. */
  protected static Hashtable<String,Properties> m_PanelProperties;
  static {
    m_PanelProperties = new Hashtable<String,Properties>();
  }

  /**
   * Initializes the panel without title.
   */
  public DataContainerPanel() {
    this(null);
  }

  /**
   * Initializes the panel with the given title.
   *
   * @param title	the title for the panel, use "null" for none
   */
  public DataContainerPanel(String title) {
    super();

    setTitle(title);
  }

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Manager = newContainerManager();
    if (m_Manager != null)
      m_Manager.addDataChangeListener(this);
    
    m_DataChangeListeners  = new HashSet<DataChangeListener>();
    m_StatusMessageHandler = null;
    m_DatabaseConnection   = getDefaultDatabaseConnection();
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    if (m_DatabaseConnection != null)
      m_DatabaseConnection.addChangeListener(this);
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  protected abstract AbstractDatabaseConnection getDefaultDatabaseConnection();

  /**
   * Returns the properties for this panel.
   *
   * @return		the properties file for this panel
   */
  public synchronized Properties getProperties() {
    Properties	result;
    String 	props;

    try {
      props = getClass().getName().replaceAll("\\.", "/") + ".props";
      if (m_PanelProperties.containsKey(props)) {
	result = m_PanelProperties.get(props);
      }
      else {
	result = Properties.read(props);
	m_PanelProperties.put(props, result);
      }
    }
    catch (Exception e) {
      result = new Properties();
    }

    return result;
  }

  /**
   * Returns the container manager to use.
   *
   * @return		the container manager
   */
  protected abstract M newContainerManager();

  /**
   * Initializes the GUI.
   */
  @Override
  protected void initGUI() {
    Properties	props;

    super.initGUI();

    props = getProperties();

    setLayout(new BorderLayout());

    setTitle(props.getPath("Title", getTitle()));

    m_PlotWrapperPanel = new BasePanel(new BorderLayout());
    add(m_PlotWrapperPanel, BorderLayout.CENTER);

    m_PlotPanel = new PlotPanel();
    m_PlotPanel.addPaintListener(this);
    for (Axis axis: Axis.values()) {
      m_PlotPanel.setAxisVisibility(axis, Visibility.valueOf(props.getProperty("Axis." + axis + ".Visibility", Visibility.INVISIBLE.toString())));
      if (m_PlotPanel.getAxisVisibility(axis) != Visibility.INVISIBLE) {
	m_PlotPanel.getAxis(axis).setAxisName(props.getPath("Axis." + axis + ".Name", ""));
	m_PlotPanel.getAxis(axis).setAxisNameCentered(props.getBoolean("Axis." + axis + ".NameCentered", false));
	m_PlotPanel.setAxisWidth(axis, props.getInteger("Axis." + axis + ".Width", 20));
	if (props.getBoolean("Axis." + axis + ".ToolTip", false))
	  m_PlotPanel.addToolTipAxis(axis);
	m_PlotPanel.getAxis(axis).setTickGenerator(AbstractTickGenerator.forCommandLine(props.getProperty("Axis." + axis + ".TickGenerator", new FancyTickGenerator().toString())));
	m_PlotPanel.getAxis(axis).setNthValueToShow(props.getInteger("Axis." + axis + ".NthValueToShow", 5));
	m_PlotPanel.getAxis(axis).setLengthTicks(props.getInteger("Axis." + axis + ".LengthTicks", 4));
	m_PlotPanel.getAxis(axis).setBottomMargin(props.getDouble("Axis." + axis + ".BottomMargin", 0.0));
	m_PlotPanel.getAxis(axis).setTopMargin(props.getDouble("Axis." + axis + ".TopMargin", 0.0));
	m_PlotPanel.getAxis(axis).setShowGridLines(props.getBoolean("Axis." + axis + ".ShowGridLines", true));
	m_PlotPanel.getAxis(axis).setType(Type.valueOf((AbstractOption) null, props.getPath("Axis." + axis + ".Type", "Absolute")));
	m_PlotPanel.getAxis(axis).setNumberFormat(props.getPath("Axis." + axis + ".NumberFormat", "0.00E0;-0.00E0"));
      }
    }
    m_PlotPanel.setForeground(props.getColor("Plot.ForegroundColor", Color.BLACK));
    m_PlotPanel.setForeground(props.getColor("Plot.BackgroundColor", Color.WHITE));
    m_PlotPanel.setGridColor(props.getColor("Plot.GridColor", new Color(235, 235, 235)));
    
    m_PlotWrapperPanel.add(m_PlotPanel, BorderLayout.CENTER);
  }

  /**
   * Sets the manager for handling the containers.
   *
   * @param value	the manager
   */
  public void setContainerManager(M value) {
    // deregister old listener
    m_Manager.removeDataChangeListener(this);

    // set new manager
    m_Manager = value;

    // register new listener
    m_Manager.addDataChangeListener(this);
  }

  /**
   * Returns the current container manager.
   *
   * @return		the manager
   */
  public M getContainerManager() {
    return m_Manager;
  }

  /**
   * Returns the paintlet used for painting the containers.
   * 
   * @return		the paintlet
   */
  public abstract Paintlet getContainerPaintlet();
  
  /**
   * Sets the title for the border.
   *
   * @param value	the title, use null for no title
   */
  public void setTitle(String value) {
    m_Title = value;
    if (m_Title == null)
      setBorder(BorderFactory.createEmptyBorder());
    else
      setBorder(BorderFactory.createTitledBorder(m_Title));
  }

  /**
   * Returns the title of border.
   *
   * @return		the title, can be null
   */
  public String getTitle() {
    return m_Title;
  }

  /**
   * Returns the content panel, which is used for drawing.
   *
   * @return		the content panel
   */
  @Override
  public PlotPanel getPlot() {
    return m_PlotPanel;
  }

  /**
   * Sets the foreground color to use.
   *
   * @param value	the color to use
   */
  public void setForegroundColor(Color value) {
    getPlot().setForegroundColor(value);
  }

  /**
   * Returns the current foreground color in use.
   *
   * @return		the color in use
   */
  public Color getForegroundColor() {
    return getPlot().getForegroundColor();
  }

  /**
   * Sets the background color to use.
   *
   * @param value	the color to use
   */
  public void setBackgroundColor(Color value) {
    getPlot().setBackgroundColor(value);
  }

  /**
   * Returns the current background color in use.
   *
   * @return		the color in use
   */
  public Color getBackgroundColor() {
    return getPlot().getBackgroundColor();
  }

  /**
   * Resets components, etc. Default implementation does nothing.
   */
  protected void reset() {
  }

  /**
   * Gets called if the data of the container panel has changed.
   *
   * @param e		the event that the container panel sent
   */
  public void dataChanged(DataChangeEvent e) {
    update();
  }

  /**
   * Sets the handler for status messages.
   *
   * @param value	the handler to use, can be null
   */
  public void setStatusMessageHandler(StatusMessageHandler value) {
    m_StatusMessageHandler = value;
  }

  /**
   * Returns the currently set handler for status messages.
   *
   * @return		the current handler, can be null
   */
  public StatusMessageHandler getStatusMessageHandler() {
    return m_StatusMessageHandler;
  }

  /**
   * Displays a message. If a status message handler is set, then this
   * handler's showStatus method is called, otherwise the message is printed
   * to stdout.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    if (m_StatusMessageHandler != null)
      m_StatusMessageHandler.showStatus(msg);
    else
      System.out.println(msg);
  }

  /**
   * Returns the current scripting engine, can be null.
   *
   * @return		the current engine
   */
  public abstract AbstractScriptingEngine getScriptingEngine();

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DatabaseConnection;
  }

  /**
   * Sets the database connection object to use.
   *
   * @param value	the object to use
   */
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    m_DatabaseConnection.removeChangeListener(this);
    m_DatabaseConnection = value;
    m_DatabaseConnection.addChangeListener(this);
  }

  /**
   * A change in the database connection occurred.
   *
   * @param e		the event
   */
  public void databaseConnectionStateChanged(DatabaseConnectionChangeEvent e) {
    getContainerManager().clear();
    if (e.getType() == DatabaseConnectionChangeEvent.EventType.CONNECT)
      setDatabaseConnection(e.getDatabaseConnection());
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  public Class[] getSendToClasses() {
    return new Class[]{PlaceholderFile.class, JComponent.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve an item for
   * @return		true if an object is available for sending
   */
  public boolean hasSendToItem(Class[] cls) {
    if (SendToActionUtils.isAvailable(new Class[]{PlaceholderFile.class, JComponent.class}, cls)) {
      if (getContainerManager() instanceof VisibilityContainerManager)
	return (((VisibilityContainerManager) getContainerManager()).countVisible() > 0);
      else
	return (getContainerManager().count() > 0);
    }

    return false;
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve an item for
   * @return		the item to send, null if nothing available at the
   * 			moment
   */
  public Object getSendToItem(Class[] cls) {
    Object		result;
    BufferedImage	bi;
    Graphics		g;

    result = null;

    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      result = SendToActionUtils.nextTmpFile(getClass().getName().toLowerCase(), "png");
      bi = new BufferedImage(getPlot().getWidth(), getPlot().getHeight(), BufferedImage.TYPE_INT_RGB);
      g  = bi.getGraphics();
      g.setPaintMode();
      g.setColor(getBackground());
      g.fillRect(0, 0, getPlot().getWidth(), getPlot().getHeight());
      getPlot().printAll(g);
      BufferedImageHelper.write(bi, ((File) result).getAbsoluteFile());
    }
    else if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
      result = this;
    }

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_StatusMessageHandler = null;
    m_PlotPanel.removePaintListener(this);
    m_DataChangeListeners.clear();
    getContainerManager().clear();
    getContainerManager().removeDataChangeListener(this);
    m_DatabaseConnection.removeChangeListener(this);
  }
}
