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

/**
 * OpenStreetMapViewerPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.osm;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource.Mapnik;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.gps.AbstractGPS;
import adams.data.gps.GPSDecimalDegrees;
import adams.flow.sink.openstreetmapviewer.AbstractTileLoaderProvider;
import adams.flow.sink.openstreetmapviewer.AbstractTileSourceProvider;
import adams.flow.sink.openstreetmapviewer.OpenStreetMapLoader;
import adams.flow.sink.openstreetmapviewer.OpenStreetMapSource;
import adams.gui.core.BasePanel;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ConsolePanel.OutputType;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;

/**
 * Viewer for OpenStreetMap.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenStreetMapViewerPanel
  extends BasePanel
  implements MenuBarProvider, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -433042545597419167L;

  /** the menu bar. */
  protected JMenuBar m_MenuBar;

  /** the map viewer. */
  protected OpenStreetMapViewerTree m_Viewer;
  
  /** the last coordinates in use. */
  protected String m_LastCoordinates;

  /** the properties. */
  protected Properties m_Properties;

  /** for selecting a different tile source provider. */
  protected GenericObjectEditorDialog m_GOEDialogSource;
  
  /** the current tile source provider. */
  protected AbstractTileSourceProvider m_CurrentSource;

  /** for selecting a different tile loader. */
  protected GenericObjectEditorDialog m_GOEDialogLoader;
  
  /** the current tile loader provider. */
  protected AbstractTileLoaderProvider m_CurrentLoader;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    Properties			props;

    super.initialize();

    props = getProperties();
    
    m_LastCoordinates = props.getProperty("Coordinates", "0, 0");
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties			props;
    
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    props = getProperties();
    
    m_Viewer = new OpenStreetMapViewerTree(props.getProperty("LayerRoot", "Layers"));
    m_Viewer.setTreeVisible(props.getBoolean("LayerTreeVisible", false));
    m_Viewer.setDividerLocation(props.getInteger("DividerLocation", 150));
    m_Viewer.getViewer().setZoom(props.getInteger("Zoom", 1));
    m_Viewer.getViewer().setZoomContolsVisible(props.getBoolean("ZoomControlsVisible", true));
    m_Viewer.getViewer().setTileGridVisible(props.getBoolean("TileGridVisible", false));

    m_CurrentSource = new OpenStreetMapSource();
    try {
      m_CurrentSource = (AbstractTileSourceProvider) OptionUtils.forAnyCommandLine(
	  AbstractTileSourceProvider.class, 
	  props.getProperty("TileSourceProvider", OptionUtils.getCommandLine(m_CurrentSource)));
      m_Viewer.getViewer().setTileSource(m_CurrentSource.generate());
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append(
	  OutputType.ERROR, 
	  "Failed to use tile source from provider: " + props.getProperty("TileSourceProvider") + "\n" 
	  + Utils.throwableToString(e) + "\n");
      m_Viewer.getViewer().setTileSource(new Mapnik());
    }

    m_CurrentLoader = new OpenStreetMapLoader();
    try {
      m_CurrentLoader = (AbstractTileLoaderProvider) OptionUtils.forAnyCommandLine(
	  AbstractTileLoaderProvider.class, 
	  props.getProperty("TileLoaderProvider", OptionUtils.getCommandLine(m_CurrentLoader)));
      m_Viewer.getViewer().setTileLoader(m_CurrentLoader.generate(m_Viewer.getViewer()));
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append(
	  OutputType.ERROR, 
	  "Failed to use tile loader from provider: " + props.getProperty("TileLoaderProvider") + "\n" 
	  + Utils.throwableToString(e));
      m_Viewer.getViewer().setTileLoader(new OsmTileLoader(m_Viewer.getViewer()));
    }

    add(m_Viewer, BorderLayout.CENTER);
  }
  
  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    setCenter(m_LastCoordinates);
  }
  
  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   * 
   * @return		the menu bar
   */
  @Override
  public JMenuBar getMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenuItem	menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // File/Tile source
      menuitem = new JMenuItem("Tile source...");
      menu.add(menuitem);
      menuitem.setMnemonic('s');
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  selectTileSourceProvider();
	}
      });

      // File/Tile loader
      menuitem = new JMenuItem("Tile loader...");
      menu.add(menuitem);
      menuitem.setMnemonic('l');
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  selectTileLoaderProvider();
	}
      });

      // File/Send to
      menu.addSeparator();
      if (SendToActionUtils.addSendToSubmenu(this, menu))
	menu.addSeparator();

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  close();
	}
      });

      // View
      menu = new JMenu("View");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // View/Show 
      menuitem = new JCheckBoxMenuItem("Show tile grid");
      menu.add(menuitem);
      menuitem.setSelected(m_Viewer.getViewer().isTileGridVisible());
      menuitem.setMnemonic('g');
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  m_Viewer.getViewer().setTileGridVisible(!m_Viewer.getViewer().isTileGridVisible());
	}
      });

      // View/Show coordinates
      menuitem = new JMenuItem("Show coordinates...");
      menu.add(menuitem);
      menuitem.setSelected(false);
      menuitem.setMnemonic('c');
      menuitem.setIcon(GUIHelper.getIcon("crosshair.png"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  String coords = JOptionPane.showInputDialog(
	      OpenStreetMapViewerPanel.this, 
	      "Please enter coordinates for centering the map in decimal notation ('lat [,] lon'):", 
	      m_LastCoordinates);
	  if (coords == null)
	    return;
	  setCenter(coords);
	}
      });

      m_MenuBar = result;
      updateMenu();
    }

    return m_MenuBar;
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;
    
    // nothing at the moment
  }
  
  /**
   * closes the dialog/frame.
   */
  public void close() {
    if (getParentFrame() != null) {
      getParentFrame().setVisible(false);
      getParentFrame().dispose();
    }
    else if (getParentDialog() != null) {
      getParentDialog().setVisible(false);
      getParentDialog().dispose();
    }
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  @Override
  public Class[] getSendToClasses() {
    return new Class[]{JComponent.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve an item for
   * @return		true if an object is available for sending
   */
  @Override
  public boolean hasSendToItem(Class[] cls) {
    return true;
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  @Override
  public Object getSendToItem(Class[] cls) {
    Object	result;

    result = null;

    if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
      result = m_Viewer;
    }

    return result;
  }
  
  /**
   * Sets the center of the map.
   * 
   * @param coords	the coordinates in decimal notation ("lat [,] lon")
   */
  public void setCenter(String coords) {
    GPSDecimalDegrees 	gps;
    
    gps = new GPSDecimalDegrees(coords);
    setCenter(gps);
  }
  
  /**
   * Sets the center of the map.
   * 
   * @param coords	the coordinates
   */
  public void setCenter(AbstractGPS coords) {
    m_Viewer.getViewer().setDisplayPositionByLatLon(
	coords.getLatitude().toDecimal(), 
	coords.getLongitude().toDecimal(), 
	m_Viewer.getViewer().getZoom());
    m_LastCoordinates = coords.getLatitude().toDecimal() + ", " + coords.getLongitude().toDecimal();
  }
  
  /**
   * Returns the current center of the map.
   * 
   * @return		the center
   */
  public AbstractGPS getCenter() {
    AbstractGPS	result;
    Coordinate	coords;
    
    coords = m_Viewer.getViewer().getPosition();
    result = new GPSDecimalDegrees(coords.getLat(), coords.getLon());
    
    return result;
  }

  /**
   * Allows the user to select a different TileSource provider.
   */
  public void selectTileSourceProvider() {
    if (m_GOEDialogSource == null) {
    if (getParentDialog() != null)
      m_GOEDialogSource = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      m_GOEDialogSource = new GenericObjectEditorDialog(getParentFrame(), true);
    m_GOEDialogSource.setTitle("Tile source provider");
    m_GOEDialogSource.getGOEEditor().setClassType(AbstractTileSourceProvider.class);
    m_GOEDialogSource.getGOEEditor().setCanChangeClassInDialog(true);
    m_GOEDialogSource.setLocationRelativeTo(this);
    }
    
    m_GOEDialogSource.setCurrent(m_CurrentSource);
    m_GOEDialogSource.setVisible(true);
    if (m_GOEDialogSource.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    
    m_CurrentSource = (AbstractTileSourceProvider) m_GOEDialogSource.getCurrent();
    try {
      m_Viewer.getViewer().setTileSource(m_CurrentSource.generate());
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	  this, 
	  "Failed to use tile source from provider: " + OptionUtils.getCommandLine(m_CurrentSource) + "\n" 
	  + Utils.throwableToString(e), 
	  "Tile source provider");
    }
  }

  /**
   * Allows the user to select a different TileLoader provider.
   */
  public void selectTileLoaderProvider() {
    if (m_GOEDialogLoader == null) {
    if (getParentDialog() != null)
      m_GOEDialogLoader = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      m_GOEDialogLoader = new GenericObjectEditorDialog(getParentFrame(), true);
    m_GOEDialogLoader.setTitle("Tile loader provider");
    m_GOEDialogLoader.getGOEEditor().setClassType(AbstractTileLoaderProvider.class);
    m_GOEDialogLoader.getGOEEditor().setCanChangeClassInDialog(true);
    m_GOEDialogLoader.setLocationRelativeTo(this);
    }
    
    m_GOEDialogLoader.setCurrent(m_CurrentLoader);
    m_GOEDialogLoader.setVisible(true);
    if (m_GOEDialogLoader.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    
    m_CurrentLoader = (AbstractTileLoaderProvider) m_GOEDialogLoader.getCurrent();
    try {
      m_Viewer.getViewer().setTileLoader(m_CurrentLoader.generate(m_Viewer.getViewer()));
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	  this, 
	  "Failed to use tile loader from provider: " + OptionUtils.getCommandLine(m_CurrentLoader) + "\n" 
	  + Utils.throwableToString(e), 
	  "Tile loader provider");
    }
  }
  
  /**
   * Returns the properties for this panel.
   *
   * @return		the properties file for this panel
   */
  public synchronized Properties getProperties() {
    Properties	result;
    String 	props;

    try {
      if (m_Properties == null) {
	props        = getClass().getName().replaceAll("\\.", "/") + ".props";
	result       = Properties.read(props);
	m_Properties = result;
      }
      else {
	result = m_Properties;
      }
    }
    catch (Exception e) {
      result = new Properties();
    }

    return result;
  }
}
