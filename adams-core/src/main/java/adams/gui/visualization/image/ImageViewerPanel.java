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
 * ImageViewerPanel.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.image.AbstractImage;
import adams.data.image.AbstractImageTransformer;
import adams.data.image.BufferedImageContainer;
import adams.env.Environment;
import adams.env.ImageViewerPanelDefinition;
import adams.env.Modules;
import adams.gui.chooser.ImageFileChooser;
import adams.gui.core.BaseMenu;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ConsolePanel.OutputType;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.MouseUtils;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.TitleGenerator;
import adams.gui.core.Undo.UndoPoint;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.visualization.core.PopupMenuCustomizer;
import adams.gui.visualization.image.plugins.AbstractImageViewerPlugin;

/**
 * A simple image viewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageViewerPanel
  extends BasePanel
  implements MenuBarProvider, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -7291572371004753723L;

  /** the name of the props file. */
  public final static String FILENAME = "ImageViewer.props";

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "ImageViewerSession.props";

  /** the properties. */
  protected static Properties m_Properties;

  /** the panel itself. */
  protected ImageViewerPanel m_Self;

  /** the file chooser for the pictures. */
  protected ImageFileChooser m_FileChooser;

  /** an optional customizer for the right-click popup. */
  protected PopupMenuCustomizer m_PopupMenuCustomizer;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "load recent" submenu. */
  protected JMenu m_MenuItemFileLoadRecent;

  /** the menu item "save as". */
  protected JMenuItem m_MenuItemFileSaveAs;

  /** the menu item "close". */
  protected JMenuItem m_MenuItemFileClose;

  /** the menu item "enable undo". */
  protected JCheckBoxMenuItem m_MenuItemEditEnableUndo;

  /** the menu item "undo". */
  protected JMenuItem m_MenuItemEditUndo;

  /** the menu item "redo". */
  protected JMenuItem m_MenuItemEditRedo;

  /** the menu item "copy". */
  protected JMenuItem m_MenuItemEditCopy;

  /** the menu item "rotate left". */
  protected JMenuItem m_MenuItemImageRotateLeft;

  /** the menu item "rotate right". */
  protected JMenuItem m_MenuItemImageRotateRight;

  /** the menu item "flip horizontally". */
  protected JMenuItem m_MenuItemImageFlipHorizontally;

  /** the menu item "flip vertically". */
  protected JMenuItem m_MenuItemImageFlipVertically;

  /** the menu "zoom". */
  protected JMenu m_MenuViewZoom;

  /** the menu item "zoom in". */
  protected JMenuItem m_MenuItemViewZoomIn;

  /** the menu item "zoom out". */
  protected JMenuItem m_MenuItemViewZoomOut;

  /** the menu item "background color". */
  protected JMenuItem m_MenuItemViewBackgroundColor;

  /** the menu item "show properties". */
  protected JMenuItem m_MenuItemViewShowProperties;

  /** the menu "plugins". */
  protected BaseMenu m_MenuPlugins;

  /** the plugins. */
  protected Vector<AbstractImageViewerPlugin> m_Plugins;

  /** the plugin menu items. */
  protected Vector<JMenuItem> m_MenuItemPlugins;

  /** the tabbed pane with the images. */
  protected BaseTabbedPane m_TabbedPane;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /** for generating the title. */
  protected TitleGenerator m_TitleGenerator;
  
  /** whether adams-imaging is present. */
  protected boolean m_ImagingModulePresent;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Self                 = this;
    m_FileChooser          = new ImageFileChooser();
    m_FileChooser.setCurrentDirectory(new File(getProperties().getPath("InitialDir", "%h")));
    m_FileChooser.setAutoAppendExtension(true);
    m_FileChooser.setMultiSelectionEnabled(true);
    m_PopupMenuCustomizer  = null;
    m_RecentFilesHandler   = null;
    m_TitleGenerator       = new TitleGenerator("Image viewer", true);
    m_MenuItemPlugins      = new Vector<JMenuItem>();
    m_Plugins              = new Vector<AbstractImageViewerPlugin>();
    m_ImagingModulePresent = Modules.getSingleton().isAvailable("adams-imaging");
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new BaseTabbedPane() {
      private static final long serialVersionUID = -2247884686333300541L;
      @Override
      protected boolean canCloseTabWithMiddleMouseButton(int index) {
	return checkForModified((ImagePanel) m_TabbedPane.getComponentAt(index));
      };
    };
    m_TabbedPane.setCloseTabsWithMiddelMouseButton(true);
    m_TabbedPane.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	if (getCurrentPanel() != null)
	  getCurrentPanel().showStatus("");
	update();
      }
    });
    add(m_TabbedPane, BorderLayout.CENTER);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (!MouseUtils.isPrintScreenClick(e)) {
	  if (MouseUtils.isRightClick(e)) {
	    JPopupMenu menu = getPopupMenu(e);
	    menu.show(m_Self, e.getX(), e.getY());
	  }
	}
      }
    });
  }

  /**
   * Updates the title and menu.
   */
  protected void update() {
    updateTitle();
    updateTabTitles();
    updateMenu();
  }

  /**
   * Updates the title of all tabs, takes modified state into account.
   */
  protected void updateTabTitles() {
    int		i;
    
    for (i = 0; i < m_TabbedPane.getTabCount(); i++)
      updateTabTitle(i);
  }

  /**
   * Updates the title of the current tab, takes modified state into account.
   */
  protected void updateCurrentTabTitle() {
    updateTabTitle(m_TabbedPane.getSelectedIndex());
  }

  /**
   * Updates the title of the specified tab, takes modified state into account.
   * 
   * @param index	the index of the tab
   */
  protected void updateTabTitle(int index) {
    String	title;
    boolean	modified;

    if (index >= 0) {
      title   = m_TabbedPane.getTitleAt(index);
      modified = title.startsWith("*");
      if (modified)
	title = title.substring(1);
      if (getPanelAt(index).isModified() != modified) {
	if (getPanelAt(index).isModified())
	  title = "*" + title;
	m_TabbedPane.setTitleAt(index, title);
      }
    }
  }

  /**
   * Updats the title of the dialog/frame if applicable.
   */
  protected void updateTitle() {
    boolean	modified;

    modified = false;
    if (getCurrentPanel() != null)
      modified = getCurrentPanel().isModified();

    setParentTitle(m_TitleGenerator.generate(getCurrentFile(), modified));
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    boolean	imageAvailable;
    ImagePanel	panel;
    int		i;
    boolean	enabled;

    if (m_MenuBar == null)
      return;

    imageAvailable = (getCurrentImage() != null);
    panel          = getCurrentPanel();

    // File
    m_MenuItemFileLoadRecent.setEnabled(m_RecentFilesHandler.size() > 0);
    m_MenuItemFileSaveAs.setEnabled(imageAvailable);
    m_MenuItemFileClose.setEnabled(imageAvailable);

    // Edit
    m_MenuItemEditEnableUndo.setEnabled(panel != null);
    m_MenuItemEditEnableUndo.setSelected((panel != null) && panel.getUndo().isEnabled());
    m_MenuItemEditUndo.setEnabled((panel != null) && panel.getUndo().canUndo());
    if ((panel != null) && panel.getUndo().canUndo()) {
      m_MenuItemEditUndo.setText("Undo - " + panel.getUndo().peekUndoComment(true));
      m_MenuItemEditUndo.setToolTipText(panel.getUndo().peekUndoComment());
    }
    else {
      m_MenuItemEditUndo.setText("Undo");
      m_MenuItemEditUndo.setToolTipText(null);
    }
    m_MenuItemEditRedo.setEnabled((panel != null) && panel.getUndo().canRedo());
    if ((panel != null) && panel.getUndo().canRedo()) {
      m_MenuItemEditRedo.setText("Redo - " + panel.getUndo().peekRedoComment(true));
      m_MenuItemEditRedo.setToolTipText(panel.getUndo().peekRedoComment());
    }
    else {
      m_MenuItemEditRedo.setText("Redo");
      m_MenuItemEditRedo.setToolTipText(null);
    }
    m_MenuItemEditCopy.setEnabled(imageAvailable);
    
    // Image
    m_MenuItemImageRotateLeft.setEnabled(imageAvailable);
    m_MenuItemImageRotateRight.setEnabled(imageAvailable);
    m_MenuItemImageFlipHorizontally.setEnabled(imageAvailable);
    m_MenuItemImageFlipVertically.setEnabled(imageAvailable);

    // View
    m_MenuViewZoom.setEnabled(imageAvailable);
    m_MenuItemViewZoomIn.setEnabled(imageAvailable);
    m_MenuItemViewZoomOut.setEnabled(imageAvailable);
    m_MenuItemViewBackgroundColor.setEnabled(imageAvailable);
    m_MenuItemViewShowProperties.setSelected(imageAvailable && getCurrentPanel().getShowProperties());
    m_MenuItemViewShowProperties.setEnabled(imageAvailable);

    // Plugins
    for (i = 0; i < m_Plugins.size(); i++) {
      enabled = m_Plugins.get(i).canExecute(getCurrentPanel());
      m_MenuItemPlugins.get(i).setEnabled(enabled);
    }
  }

  /**
   * Returns the image panel in the currently selected tab.
   *
   * @return		the image panel, null if none available
   */
  public ImagePanel getCurrentPanel() {
    return getPanelAt(m_TabbedPane.getSelectedIndex());
  }

  /**
   * Returns the image panel of the specified tab.
   *
   * @param index	the tab index
   * @return		the image panel, null if none available
   */
  public ImagePanel getPanelAt(int index) {
    if ((index < 0) || (index >= m_TabbedPane.getTabCount()))
      return null;
    else
      return (ImagePanel) m_TabbedPane.getComponentAt(index);
  }

  /**
   * Returns all the image panels.
   *
   * @return		the image panels
   */
  public ImagePanel[] getAllPanels() {
    ImagePanel[]	result;
    int			i;
    
    result = new ImagePanel[m_TabbedPane.getTabCount()];
    for (i = 0; i < m_TabbedPane.getTabCount(); i++)
      result[i] = (ImagePanel) m_TabbedPane.getComponentAt(i);
    
    return result;
  }

  /**
   * Returns the underlying image.
   *
   * @return		the current image, can be null
   */
  public BufferedImage getCurrentImage() {
    return getImageAt(m_TabbedPane.getSelectedIndex());
  }

  /**
   * Returns the underlying image.
   *
   * @param index	the tab index
   * @return		the current image, can be null
   */
  public BufferedImage getImageAt(int index) {
    BufferedImage	result;
    ImagePanel		panel;

    result = null;
    panel  = getPanelAt(index);
    if (panel != null)
      result = panel.getCurrentImage();

    return result;
  }

  /**
   * Returns the current filename.
   *
   * @return		the current filename, can be null
   */
  public File getCurrentFile() {
    return getFileAt(m_TabbedPane.getSelectedIndex());
  }

  /**
   * Returns the current filename.
   *
   * @param index	the tab index
   * @return		the current filename, can be null
   */
  public File getFileAt(int index) {
    File	result;
    ImagePanel	panel;

    result = null;
    panel  = getPanelAt(index);
    if (panel != null)
      result = panel.getCurrentFile();

    return result;
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
    JMenu		submenu;
    JMenuItem		menuitem;
    int			i;
    int[]		zooms;
    String[]		shortcuts;
    String[]		plugins;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // File/Open...
      menuitem = new JMenuItem("Open...");
      menu.add(menuitem);
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.setIcon(GUIHelper.getIcon("open.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  open();
	}
      });

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<JMenu>(
	  SESSION_FILE, getProperties().getInteger("MaxRecentImages", 5), submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  load(e.getItem());
	}
      });
      m_MenuItemFileLoadRecent = submenu;

      // File/Open...
      menuitem = new JMenuItem("Save as...");
      menu.add(menuitem);
      menuitem.setMnemonic('v');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed S"));
      menuitem.setIcon(GUIHelper.getIcon("save.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  saveAs();
	}
      });
      m_MenuItemFileSaveAs = menuitem;

      // File/Close tab
      menuitem = new JMenuItem("Close tab");
      menu.add(menuitem);
      menuitem.setMnemonic('t');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed W"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  close();
	}
      });
      m_MenuItemFileClose = menuitem;

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
	public void actionPerformed(ActionEvent e) {
	  exit();
	}
      });

      // Edit
      menu = new JMenu("Edit");
      result.add(menu);
      menu.setMnemonic('E');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // Edit/Enable undo
      menuitem = new JCheckBoxMenuItem("Undo enabled");
      menu.add(menuitem);
      menuitem.setMnemonic('n');
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  getCurrentPanel().getUndo().setEnabled(!getCurrentPanel().getUndo().isEnabled());
	}
      });
      m_MenuItemEditEnableUndo = (JCheckBoxMenuItem) menuitem;

      // Edit/Undo
      menuitem = new JMenuItem("Undo");
      menu.add(menuitem);
      menuitem.setMnemonic('U');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Z"));
      menuitem.setIcon(GUIHelper.getIcon("undo.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  undo();
	}
      });
      m_MenuItemEditUndo = menuitem;

      // Edit/Redo
      menuitem = new JMenuItem("Redo");
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Y"));
      menuitem.setIcon(GUIHelper.getIcon("redo.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  redo();
	}
      });
      m_MenuItemEditRedo = menuitem;

      // Edit/Copy
      menuitem = new JMenuItem("Copy");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed C"));
      menuitem.setIcon(GUIHelper.getIcon("copy.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  copy();
	}
      });
      m_MenuItemEditCopy = menuitem;

      // Image
      menu = new JMenu("Image");
      menu.setVisible(m_ImagingModulePresent);
      result.add(menu);
      menu.setMnemonic('I');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // Image/Rotate left
      menuitem = new JMenuItem("Rotate left");
      menu.add(menuitem);
      menuitem.setMnemonic('l');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed L"));
      menuitem.setIcon(GUIHelper.getIcon("rotate_left.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  rotate(true);
	}
      });
      m_MenuItemImageRotateLeft = menuitem;

      // Image/Rotate right
      menuitem = new JMenuItem("Rotate right");
      menu.add(menuitem);
      menuitem.setMnemonic('r');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed R"));
      menuitem.setIcon(GUIHelper.getIcon("rotate_right.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  rotate(false);
	}
      });
      m_MenuItemImageRotateRight = menuitem;

      // Image/Flip horizontally
      menuitem = new JMenuItem("Flip horizontally");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('h');
      menuitem.setIcon(GUIHelper.getIcon("flip_horizontal.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  flip(true);
	}
      });
      m_MenuItemImageFlipHorizontally = menuitem;

      // Image/Flip vertically
      menuitem = new JMenuItem("Flip vertically");
      menu.add(menuitem);
      menuitem.setMnemonic('v');
      menuitem.setIcon(GUIHelper.getIcon("flip_vertical.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  flip(false);
	}
      });
      m_MenuItemImageFlipVertically = menuitem;

      // View
      menu = new JMenu("View");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // View/Zoom
      submenu = new JMenu("Zoom");
      menu.add(submenu);
      submenu.setMnemonic('Z');
      submenu.setIcon(GUIHelper.getIcon("glasses.gif"));
      submenu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });
      m_MenuViewZoom = submenu;

      //View/Zoom/Zoom in
      menuitem = new JMenuItem("Zoom in");
      submenu.add(menuitem);
      menuitem.setMnemonic('i');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed I"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  getCurrentPanel().setScale(getCurrentPanel().getScale() * 1.5);
	}
      });
      m_MenuItemViewZoomIn = menuitem;

      //View/Zoom/Zoom out
      menuitem = new JMenuItem("Zoom out");
      submenu.add(menuitem);
      menuitem.setMnemonic('o');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed O"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  getCurrentPanel().setScale(getCurrentPanel().getScale() / 1.5);
	}
      });
      m_MenuItemViewZoomOut = menuitem;

      // zoom levels
      // TODO: add "fit" zoom
      zooms = new int[]{
	  -100,
	  25,
	  50,
	  66,
	  75,
	  100,
	  150,
	  200,
	  400,
	  800};
      shortcuts = new String[]{
	  "F",
	  "",
	  "",
	  "",
	  "",
	  "1",
	  "",
	  "2",
	  "4",
	  ""
      };
      submenu.addSeparator();
      for (i = 0; i < zooms.length; i++) {
	final int fZoom = zooms[i];
	if (zooms[i] == -100)
	  menuitem = new JMenuItem("Best fit");
	else
	  menuitem = new JMenuItem(zooms[i] + "%");
	submenu.add(menuitem);
	if (shortcuts[i].length() > 0)
	  menuitem.setAccelerator(GUIHelper.getKeyStroke(shortcuts[i]));
	menuitem.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    zoom(fZoom);
	  }
	});
      }

      // View/Background color
      menuitem = new JMenuItem("Background color...");
      menu.add(menuitem);
      menuitem.setIcon(GUIHelper.getIcon("colorpicker.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  chooseBackgroundColor();
	}
      });
      m_MenuItemViewBackgroundColor = menuitem;

      // View/Properties
      menuitem = new JCheckBoxMenuItem("Show properties");
      menu.add(menuitem);
      menuitem.setIcon(GUIHelper.getIcon("properties.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  getCurrentPanel().setShowProperties(!getCurrentPanel().getShowProperties());
	}
      });
      m_MenuItemViewShowProperties = menuitem;

      // Plugins
      plugins = AbstractImageViewerPlugin.getPlugins();
      menu = new BaseMenu("Plugins");
      result.add(menu);
      menu.setMnemonic('P');
      menu.setVisible(plugins.length > 0);
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });
      m_MenuPlugins = (BaseMenu) menu;

      // add plugins
      m_MenuItemPlugins.clear();
      m_Plugins.clear();
      for (i = 0; i < plugins.length; i++) {
	try {
	  final AbstractImageViewerPlugin plugin = (AbstractImageViewerPlugin) Class.forName(plugins[i]).newInstance();
	  menuitem = new JMenuItem(plugin.getCaption());
	  menu.add(menuitem);
	  menuitem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      String error = plugin.execute(getCurrentPanel());
	      if (error != null)
		GUIHelper.showErrorMessage(
		    getCurrentPanel(),
		    "Error occurred executing plugin '" + plugin.getCaption() + "':\n" + error);
	      update();
	    }
	  });
	  m_Plugins.add(plugin);
	  m_MenuItemPlugins.add(menuitem);
	}
	catch (Exception e) {
	  System.err.println("Failed to install plugin '" + plugins[i] + "':");
	  e.printStackTrace();
	}
      }
      m_MenuPlugins.sort();

      // update menu
      m_MenuBar = result;
      updateMenu();
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Opens an image.
   */
  protected void open() {
    int		retVal;
    File[]	files;

    retVal = m_FileChooser.showOpenDialog(this);
    if (retVal != ImageFileChooser.APPROVE_OPTION)
      return;

    files = m_FileChooser.getSelectedFiles();
    for (File file: files)
      load(file);
  }

  /**
   * Loads the specified file in a new panel.
   *
   * @param file	the file to load
   */
  public void load(File file) {
    ImagePanel	panel;

    panel = new ImagePanel();
    panel.setSelectionEnabled(true);
    if (!panel.load(file)) {
      GUIHelper.showErrorMessage(
	  this, "Failed to open image '" + file + "'!");
    }
    else {
      panel.setScale(getProperties().getDouble("ZoomLevel") / 100);
      m_TabbedPane.addTab(file.getName(), panel);
      m_TabbedPane.setSelectedComponent(panel);
      if (m_RecentFilesHandler != null)
	m_RecentFilesHandler.addRecentItem(file);
    }

    update();
  }

  /**
   * Saves the current image under a new name.
   */
  protected void saveAs() {
    int			index;
    int			retVal;
    File		file;
    ImagePanel		panel;

    index  = m_TabbedPane.getSelectedIndex();
    if (index < 0)
      return;
    panel = (ImagePanel) m_TabbedPane.getComponentAt(index);

    m_FileChooser.setSelectedFile(getCurrentFile());
    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != ImageFileChooser.APPROVE_OPTION)
      return;

    file = m_FileChooser.getSelectedFile();
    if (!panel.save(file)) {
      GUIHelper.showErrorMessage(
	  this, "Failed to write image to '" + file + "'!");
    }
    else {
      if (m_RecentFilesHandler != null)
	m_RecentFilesHandler.addRecentItem(file);
    }

    update();
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the flow or discarded the changes.
   *
   * @param panel	the panel to check
   * @return		true if safe to proceed
   */
  protected boolean checkForModified(ImagePanel panel) {
    boolean 	result;
    int		retVal;
    String	msg;

    if (panel == null)
      return true;

    result = !panel.isModified();

    if (!result) {
      if (getCurrentFile() == null)
	msg = "Image not saved - save?";
      else
	msg = "Image not saved - save?\n" + getCurrentFile();
      retVal = GUIHelper.showConfirmMessage(this, msg, "Image not saved");
      switch (retVal) {
	case GUIHelper.APPROVE_OPTION:
	  saveAs();
	  result = !panel.isModified();
	  break;
	case GUIHelper.DISCARD_OPTION:
	  result = true;
	  break;
	case GUIHelper.CANCEL_OPTION:
	  result = false;
	  break;
      }
    }

    return result;
  }

  /**
   * Closes the current image.
   */
  protected void close() {
    int		index;
    ImagePanel	panel;
    boolean	canClose;

    canClose = false;
    index    = m_TabbedPane.getSelectedIndex();
    panel    = getPanelAt(index);
    if (panel != null)
      canClose = checkForModified(panel);

    if (canClose) {
      panel.cleanUp();
      m_TabbedPane.remove(index);
      update();
    }
  }

  /**
   * Exits the viewer.
   */
  protected void exit() {
    int		i;

    i = 0;
    while (i < m_TabbedPane.getTabCount()) {
      if (!checkForModified((ImagePanel) m_TabbedPane.getComponentAt(i)))
	return;
      else
	m_TabbedPane.remove(i);
    }

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
   * Copies the current image to the clipboard.
   */
  protected void copy() {
    GUIHelper.copyToClipboard(getCurrentImage());
  }
  
  /**
   * Applies a JAI transformer. Transformer gets applied to the current image,
   * replacing it in the process.
   * 
   * @param cmd		the transformer commandline
   * @param operation	the name of the operation, to be used in error message
   */
  protected void applyJAITransformer(String cmd, String operation) {
    AbstractImageTransformer	transformer;
    BufferedImageContainer	cont;
    String			msg;
    AbstractImage[]		trans;
    
    getCurrentPanel().addUndoPoint("Saving undo data...", operation);
    try {
      cont = new BufferedImageContainer();
      cont.setImage(getCurrentImage());
      transformer = (AbstractImageTransformer) OptionUtils.forCommandLine(AbstractImageTransformer.class, cmd);
      trans = transformer.transform(cont);
      if (trans.length > 0)
	getCurrentPanel().setCurrentImage(trans[0]);
      else
	GUIHelper.showErrorMessage(this, "Failed to " + operation + "?");
    }
    catch (Exception e) {
      msg = "Failed to " + operation + ":\n" + Utils.throwableToString(e);
      GUIHelper.showErrorMessage(this, msg);
      ConsolePanel.getSingleton().append(OutputType.ERROR, msg);
    }
  }
  
  /**
   * Rotates the image.
   * 
   * @param left	whether to rotate 90 degrees left
   */
  protected void rotate(boolean left) {
    String			cmd;
    
    cmd = "adams.data.jai.transformer.Rotate -angle ";
    if (left)
      cmd += "270";
    else
      cmd += "90";
    
    applyJAITransformer(cmd, "rotate image");
  }

  /**
   * Flips the image.
   * 
   * @param horizontal	whether to flip horizontally
   */
  protected void flip(boolean horizontal) {
    String			cmd;
    
    cmd = "adams.data.jai.transformer.Flip -direction ";
    if (horizontal)
      cmd += "HORIZONTAL";
    else
      cmd += "VERTICAL";
    
    applyJAITransformer(cmd, "flip image");
  }

  /**
   * Zooms in/out.
   *
   * @param zoom	the zoom (in percent)
   */
  protected void zoom(int zoom) {
    getCurrentPanel().setScale((double) zoom / 100);
  }

  /**
   * Lets the user select the background color for the image.
   */
  protected void chooseBackgroundColor() {
    Color	bgcolor;

    bgcolor = JColorChooser.showDialog(this, "Background color", getCurrentPanel().getBackgroundColor());
    if (bgcolor == null)
      return;
    getCurrentPanel().setBackgroundColor(bgcolor);
  }

  /**
   * Sets the class to customize the right-click popup menu.
   *
   * @param value	the customizer
   */
  public void setPopupMenuCustomizer(PopupMenuCustomizer value) {
    m_PopupMenuCustomizer = value;
  }

  /**
   * Returns the current customizer, can be null.
   *
   * @return		the customizer
   */
  public PopupMenuCustomizer getPopupMenuCustomizer() {
    return m_PopupMenuCustomizer;
  }

  /**
   * Returns the popup menu, potentially customized.
   *
   * @param e		the mouse event
   * @return		the popup menu
   * @see		#m_PopupMenuCustomizer
   */
  public JPopupMenu getPopupMenu(MouseEvent e) {
    JPopupMenu	result;

    result = new JPopupMenu();

    // customize it?
    if (m_PopupMenuCustomizer != null)
      m_PopupMenuCustomizer.customizePopupMenu(e, result);

    return result;
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
    return (getCurrentImage() != null);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  public Object getSendToItem(Class[] cls) {
    Object	result;
    File	file;

    result = null;
    file   = getCurrentFile();

    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      if ((file == null) && (getCurrentImage() != null)) {
	result = SendToActionUtils.nextTmpFile("imageviewer", "png");
	getCurrentPanel().save((File) result);
	getCurrentPanel().setCurrentImage(getCurrentPanel().getCurrentImage());
      }
      else if (file != null) {
	result = new PlaceholderFile(file);
      }
    }
    else if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
      if (getCurrentPanel() != null)
	result = getCurrentPanel().getPaintPanel();
    }

    return result;
  }

  /**
   * peforms an undo if possible.
   */
  public void undo() {
    ImagePanel	panel;
    UndoPoint 	point;

    panel = getCurrentPanel();
    if (panel == null)
      return;
    if (!panel.getUndo().canUndo())
      return;

    panel.showStatus("Performing Undo...");

    // add redo point
    panel.getUndo().addRedo(panel.getState(), panel.getUndo().peekUndoComment());

    point = panel.getUndo().undo();
    panel.setState((Vector) point.getData());
    update();
    panel.showStatus("");
  }

  /**
   * peforms a redo if possible.
   */
  public void redo() {
    ImagePanel	panel;
    UndoPoint 	point;

    panel = getCurrentPanel();
    if (panel == null)
      return;
    if (!panel.getUndo().canRedo())
      return;

    panel.showStatus("Performing Redo...");

    // add undo point
    panel.getUndo().addUndo(panel.getState(), panel.getUndo().peekRedoComment(), true);

    point = panel.getUndo().redo();
    panel.setState((Vector) point.getData());

    update();
    panel.showStatus("");
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(ImageViewerPanelDefinition.KEY);

    return m_Properties;
  }
}
