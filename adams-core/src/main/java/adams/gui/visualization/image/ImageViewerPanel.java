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
 * ImageViewerPanel.java
 * Copyright (C) 2010-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import adams.core.Properties;
import adams.core.ShorteningType;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.core.option.OptionUtils;
import adams.data.image.AbstractImageContainer;
import adams.data.image.AbstractImageTransformer;
import adams.data.image.BufferedImageContainer;
import adams.data.io.input.ImageReader;
import adams.env.Environment;
import adams.env.ImageViewerPanelDefinition;
import adams.env.Modules;
import adams.gui.chooser.ImageFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.MouseUtils;
import adams.gui.core.RecentFilesHandlerWithCommandline;
import adams.gui.core.RecentFilesHandlerWithCommandline.Setup;
import adams.gui.core.TitleGenerator;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.plugin.ToolPluginSupporter;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.visualization.core.PopupMenuCustomizer;
import adams.gui.visualization.image.plugins.ImageViewerPluginManager;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * A simple image viewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageViewerPanel
  extends BasePanel
  implements MenuBarProvider, SendToActionSupporter, ToolPluginSupporter<ImagePanel> {

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

  /** the menu item "pick color". */
  protected JMenuItem m_MenuItemImagePickColor;

  /** the menu "zoom". */
  protected JMenu m_MenuViewZoom;

  /** the menu item "zoom in". */
  protected JMenuItem m_MenuItemViewZoomIn;

  /** the menu item "zoom out". */
  protected JMenuItem m_MenuItemViewZoomOut;

  /** the menu item "remove overlays". */
  protected JMenuItem m_MenuItemViewRemoveOverlays;

  /** the menu item "background color". */
  protected JMenuItem m_MenuItemViewBackgroundColor;

  /** the menu item "show properties". */
  protected JMenuItem m_MenuItemViewShowProperties;

  /** the menu item "show log". */
  protected JMenuItem m_MenuItemViewShowLog;

  /** for managing the plugins. */
  protected ImageViewerPluginManager m_PluginManager;

  /** the tabbed pane with the images. */
  protected ImageMultiPagePane m_MultiPagePane;

  /** the recent files handler. */
  protected RecentFilesHandlerWithCommandline<JMenu> m_RecentFilesHandler;

  /** for generating the title. */
  protected TitleGenerator m_TitleGenerator;
  
  /** whether adams-imaging is present. */
  protected boolean m_ImagingModulePresent;
  
  /** the color picker dialog. */
  protected ColorPickerDialog m_DialogColorPicker;

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
    m_TitleGenerator       = new TitleGenerator("Image viewer", true, ShorteningType.START);
    m_PluginManager        = new ImageViewerPluginManager(this);
    m_PluginManager.setMenuUpdateListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
	update();
      }
    });
    m_ImagingModulePresent = Modules.getSingleton().isAvailable("adams-imaging");
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());

    m_MultiPagePane = new ImageMultiPagePane(this);
    m_MultiPagePane.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	if (getCurrentPanel() != null)
	  getCurrentPanel().showStatus("");
	update();
      }
    });
    add(m_MultiPagePane, BorderLayout.CENTER);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (!MouseUtils.isPrintScreenClick(e)) {
	  if (MouseUtils.isRightClick(e)) {
	    BasePopupMenu menu = getPopupMenu(e);
	    menu.showAbsolute(m_Self, e);
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
    m_MultiPagePane.updateTabTitles();
    updateMenu();
  }
  
  /**
   * Returns the title generator in use.
   * 
   * @return		the generator
   */
  public TitleGenerator getTitleGenerator() {
    return m_TitleGenerator;
  }

  /**
   * Updats the title of the dialog/frame if applicable.
   */
  protected void updateTitle() {
    boolean	modified;

    if (!m_TitleGenerator.isEnabled())
      return;
    
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
      m_MenuItemEditUndo.setText("Undo - " + panel.getUndo().peekUndoComment());
      m_MenuItemEditUndo.setToolTipText(panel.getUndo().peekUndoComment());
    }
    else {
      m_MenuItemEditUndo.setText("Undo");
      m_MenuItemEditUndo.setToolTipText(null);
    }
    m_MenuItemEditRedo.setEnabled((panel != null) && panel.getUndo().canRedo());
    if ((panel != null) && panel.getUndo().canRedo()) {
      m_MenuItemEditRedo.setText("Redo - " + panel.getUndo().peekRedoComment());
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
    m_MenuItemImagePickColor.setEnabled(imageAvailable);

    // View
    m_MenuViewZoom.setEnabled(imageAvailable);
    m_MenuItemViewZoomIn.setEnabled(imageAvailable);
    m_MenuItemViewZoomOut.setEnabled(imageAvailable);
    m_MenuItemViewRemoveOverlays.setEnabled(imageAvailable && panel.imageOverlays().hasNext());
    m_MenuItemViewBackgroundColor.setEnabled(imageAvailable);
    m_MenuItemViewShowProperties.setSelected(imageAvailable && getCurrentPanel().getShowProperties());
    m_MenuItemViewShowProperties.setEnabled(imageAvailable);
    m_MenuItemViewShowLog.setSelected(imageAvailable && getCurrentPanel().getShowLog());
    m_MenuItemViewShowLog.setEnabled(imageAvailable);
  }

  /**
   * Returns the image panel in the currently selected tab.
   *
   * @return		the image panel, null if none available
   */
  public ImagePanel getCurrentPanel() {
    return m_MultiPagePane.getCurrentPanel();
  }

  /**
   * Returns the image panel of the specified tab.
   *
   * @param index	the tab index
   * @return		the image panel, null if none available
   */
  public ImagePanel getPanelAt(int index) {
    return m_MultiPagePane.getPanelAt(index);
  }

  /**
   * Returns all the image panels.
   *
   * @return		the image panels
   */
  public ImagePanel[] getAllPanels() {
    return m_MultiPagePane.getAllPanels();
  }

  /**
   * Returns the underlying image.
   *
   * @return		the current image, can be null
   */
  public BufferedImage getCurrentImage() {
    return m_MultiPagePane.getCurrentImage();
  }

  /**
   * Returns the underlying image.
   *
   * @param index	the tab index
   * @return		the current image, can be null
   */
  public BufferedImage getImageAt(int index) {
    return m_MultiPagePane.getImageAt(index);
  }

  /**
   * Returns the current filename.
   *
   * @return		the current filename, can be null
   */
  public File getCurrentFile() {
    return m_MultiPagePane.getCurrentFile();
  }

  /**
   * Returns the current filename.
   *
   * @param index	the tab index
   * @return		the current filename, can be null
   */
  public File getFileAt(int index) {
    return m_MultiPagePane.getFileAt(index);
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
      menuitem.setIcon(ImageManager.getIcon("open.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  open();
	}
      });

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandlerWithCommandline<JMenu>(
	  SESSION_FILE, getProperties().getInteger("MaxRecentImages", 5), submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,Setup>() {
	public void recentItemAdded(RecentItemEvent<JMenu,Setup> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,Setup> e) {
	  load(e.getItem().getFile(), (ImageReader) e.getItem().getHandler());
	}
      });
      m_MenuItemFileLoadRecent = submenu;

      // File/Open...
      menuitem = new JMenuItem("Save as...");
      menu.add(menuitem);
      menuitem.setMnemonic('v');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed S"));
      menuitem.setIcon(ImageManager.getIcon("save.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  saveAs();
	}
      });
      m_MenuItemFileSaveAs = menuitem;

      // File/Close tab
      menuitem = new JMenuItem("Close page");
      menu.add(menuitem);
      menuitem.setMnemonic('t');
      menuitem.setIcon(ImageManager.getIcon("delete.gif"));
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
      menuitem.setIcon(ImageManager.getIcon("exit.png"));
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
      menuitem.setIcon(ImageManager.getIcon("undo.gif"));
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
      menuitem.setIcon(ImageManager.getIcon("redo.gif"));
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
      menuitem.setIcon(ImageManager.getIcon("copy.gif"));
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
      menuitem.setIcon(ImageManager.getIcon("rotate_left.png"));
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
      menuitem.setIcon(ImageManager.getIcon("rotate_right.png"));
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
      menuitem.setIcon(ImageManager.getIcon("flip_horizontal.png"));
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
      menuitem.setIcon(ImageManager.getIcon("flip_vertical.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  flip(false);
	}
      });
      m_MenuItemImageFlipVertically = menuitem;

      // Image/Pick color...
      menuitem = new JMenuItem("Pick color...");
      menu.add(menuitem);
      menuitem.setMnemonic('P');
      menuitem.setIcon(ImageManager.getIcon("colorpicker.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  pickColor();
	}
      });
      m_MenuItemImagePickColor = menuitem;

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
      submenu.setIcon(ImageManager.getIcon("glasses.gif"));
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
	  getCurrentPanel().setScale(getCurrentPanel().getActualScale() * 1.5);
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
	  getCurrentPanel().setScale(getCurrentPanel().getActualScale() / 1.5);
	}
      });
      m_MenuItemViewZoomOut = menuitem;

      // zoom levels
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

      // View/Remove overlays
      menuitem = new JMenuItem("Remove overlays");
      menu.add(menuitem);
      menuitem.setIcon(ImageManager.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  removeOverlays();
	}
      });
      m_MenuItemViewRemoveOverlays = menuitem;

      // View/Background color
      menuitem = new JMenuItem("Background color...");
      menu.add(menuitem);
      menuitem.setIcon(ImageManager.getIcon("colorpicker.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  chooseBackgroundColor();
	}
      });
      m_MenuItemViewBackgroundColor = menuitem;

      // View/Properties
      menuitem = new JCheckBoxMenuItem("Show properties");
      menu.add(menuitem);
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  getCurrentPanel().setShowProperties(!getCurrentPanel().getShowProperties());
	}
      });
      m_MenuItemViewShowProperties = menuitem;

      // View/Log
      menuitem = new JCheckBoxMenuItem("Show log");
      menu.add(menuitem);
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  getCurrentPanel().setShowLog(!getCurrentPanel().getShowLog());
	}
      });
      m_MenuItemViewShowLog = menuitem;

      m_PluginManager.addToMenuBar(result);

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
      load(file, m_FileChooser.getImageReader());
  }

  /**
   * Loads the specified file in a new panel.
   *
   * @param file	the file to load
   */
  public void load(File file) {
    load(file, null);
  }

  /**
   * Loads the specified file in a new panel.
   *
   * @param file	the file to load
   * @param reader	the reader to use, null for auto-detection
   */
  public void load(File file, ImageReader reader) {
    if (m_MultiPagePane.load(file, reader)) {
      if (m_RecentFilesHandler != null)
	m_RecentFilesHandler.addRecentItem(new Setup(file, reader));
    }
    update();
  }

  /**
   * Saves the current image under a new name.
   */
  protected void saveAs() {
    int			retVal;
    File		file;
    ImagePanel		panel;
    int			index;

    index = m_MultiPagePane.getSelectedIndex();
    if (index == -1)
      return;
    panel = getPanelAt(index);
    if (panel == null)
      return;

    m_FileChooser.setSelectedFile(getCurrentFile());
    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != ImageFileChooser.APPROVE_OPTION)
      return;

    file = m_FileChooser.getSelectedFile();
    if (!panel.save(file, m_FileChooser.getImageWriter())) {
      GUIHelper.showErrorMessage(
	  this, "Failed to write image to '" + file + "'!");
    }
    else {
      m_MultiPagePane.setTitleAt(index, file.getName());
      if ((m_RecentFilesHandler != null) && (m_FileChooser.getImageWriter().getCorrespondingReader() != null))
	m_RecentFilesHandler.addRecentItem(
	    new Setup(file, m_FileChooser.getImageWriter().getCorrespondingReader()));
    }

    update();
  }

  /**
   * Closes the current image.
   */
  protected void close() {
    int		index;
    ImagePanel	panel;
    boolean	canClose;

    canClose = false;
    index    = m_MultiPagePane.getSelectedIndex();
    panel    = getPanelAt(index);
    if (panel != null)
      canClose = m_MultiPagePane.checkForModified(panel);

    if (canClose) {
      panel.cleanUp();
      m_MultiPagePane.remove(index);
      update();
    }
  }

  /**
   * Exits the viewer.
   */
  protected void exit() {
    int		i;

    i = 0;
    while (i < m_MultiPagePane.getPageCount()) {
      if (!m_MultiPagePane.checkForModified(m_MultiPagePane.getPanelAt(i)))
	return;
      else
	m_MultiPagePane.removePageAt(i);
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
    ClipboardHelper.copyToClipboard(getCurrentImage());
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
    AbstractImageContainer[]		trans;
    
    getCurrentPanel().addUndoPoint("Saving undo data...", operation);
    try {
      cont = new BufferedImageContainer();
      cont.setImage(getCurrentImage());
      transformer = (AbstractImageTransformer) OptionUtils.forCommandLine(AbstractImageTransformer.class, cmd);
      trans = transformer.transform(cont);
      if (trans.length > 0) {
	getCurrentPanel().log("JAITransformer: " + cmd);
	getCurrentPanel().setCurrentImage(trans[0]);
      }
      else {
	GUIHelper.showErrorMessage(this, "Failed to " + operation + "?");
      }
    }
    catch (Exception e) {
      msg = "Failed to " + operation + ":\n" + LoggingHelper.throwableToString(e);
      GUIHelper.showErrorMessage(this, msg);
      ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, msg + "\n");
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
   * Allows the user to pick a color.
   */
  protected void pickColor() {
    if (m_DialogColorPicker == null) {
      if (getParentDialog() != null)
	m_DialogColorPicker = new ColorPickerDialog(getParentDialog(), getCurrentPanel());
      else
	m_DialogColorPicker = new ColorPickerDialog(getParentFrame(), getCurrentPanel());
      m_DialogColorPicker.setLocationRelativeTo(getCurrentPanel());
    }
    m_DialogColorPicker.setVisible(true);
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
   * Removes all image overlays.
   */
  protected void removeOverlays() {
    getCurrentPanel().clearImageOverlays();
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
  public BasePopupMenu getPopupMenu(MouseEvent e) {
    BasePopupMenu	result;

    result = new BasePopupMenu();

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

    panel = getCurrentPanel();
    if (panel == null)
      return;
    panel.undo();
    update();
  }

  /**
   * peforms a redo if possible.
   */
  public void redo() {
    ImagePanel	panel;

    panel = getCurrentPanel();
    if (panel == null)
      return;
    panel.redo();
    update();
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
