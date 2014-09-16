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
 * ImageProcessorPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.gui.chooser.ImageFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.TitleGenerator;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.tools.ImageProcessingPanel.LayoutType;
import adams.gui.visualization.image.ImageViewerPanel;

/**
 * Interface for processing images using a flow snippet.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageProcessorPanel
  extends BasePanel
  implements MenuBarProvider {

  /** for serialization. */
  private static final long serialVersionUID = 5882173310359920644L;

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "ImageProcessorSession.props";

  /** the tabbed pane for the images. */
  protected ImageProcessorTabbedPane m_TabbedPane;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the menu item "open". */
  protected JMenuItem m_MenuItemFileOpen;

  /** the "load recent" submenu. */
  protected JMenu m_MenuItemFileLoadRecent;

  /** the menu item "close". */
  protected JMenuItem m_MenuItemFileClose;

  /** the menu item "horizontal". */
  protected JMenuItem m_MenuItemViewHorizontal;

  /** the menu item "vertical". */
  protected JMenuItem m_MenuItemViewVertical;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /** for generating the title. */
  protected TitleGenerator m_TitleGenerator;

  /** the file chooser for the pictures. */
  protected ImageFileChooser m_FileChooser;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_RecentFilesHandler = null;
    m_TitleGenerator     = new TitleGenerator("Image processor", true);
    m_FileChooser        = new ImageFileChooser();
    m_FileChooser.setCurrentDirectory(new File(ImageViewerPanel.getProperties().getPath("InitialDir", "%h")));
    m_FileChooser.setAutoAppendExtension(true);
    m_FileChooser.setMultiSelectionEnabled(true);
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_TabbedPane = new ImageProcessorTabbedPane(this);
    m_TabbedPane.setCloseTabsWithMiddelMouseButton(true);
    m_TabbedPane.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	update();
      }
    });
    add(m_TabbedPane, BorderLayout.CENTER);
  }

  /**
   * Updates the title and menu.
   */
  protected void update() {
    updateTitle();
    updateMenu();
  }

  /**
   * Updats the title of the dialog/frame if applicable.
   */
  protected void updateTitle() {
    setParentTitle(m_TitleGenerator.generate(getCurrentFile()));
  }

  /**
   * Returns the current filename.
   *
   * @return		the current filename, can be null
   */
  public File getCurrentFile() {
    return m_TabbedPane.getCurrentFile();
  }

  /**
   * Returns the current filename.
   *
   * @param index	the tab index
   * @return		the current filename, can be null
   */
  public File getFileAt(int index) {
    return m_TabbedPane.getFileAt(index);
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;

    // File
    m_MenuItemFileLoadRecent.setEnabled(m_RecentFilesHandler.size() > 0);
    m_MenuItemFileClose.setEnabled(m_TabbedPane.getTabCount() > 0);
    
    // View
    m_MenuItemViewHorizontal.setEnabled(m_TabbedPane.getTabCount() > 0);
    m_MenuItemViewVertical.setEnabled(m_TabbedPane.getTabCount() > 0);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   * 
   * @return		the menu bar
   */
  @Override
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
    JMenu		submenu;
    JMenuItem		menuitem;
    ButtonGroup		group;

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
      m_MenuItemFileOpen = menuitem;

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<JMenu>(SESSION_FILE, 5, submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  load(e.getItem());
	}
      });
      m_MenuItemFileLoadRecent = submenu;

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

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  exit();
	}
      });

      // View
      menu = new JMenu("View");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });
      group = new ButtonGroup();

      // View/Horizontal
      menuitem = new JRadioButtonMenuItem("Horizontal");
      menu.add(menuitem);
      menuitem.setMnemonic('H');
      menuitem.setSelected(false);
      menuitem.setIcon(GUIHelper.getIcon("ip_layout_horizontal.png"));
      menuitem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
	  if (getCurrentPanel() == null)
	    return;
	  getCurrentPanel().setLayoutType(LayoutType.HORIZONTAL);
	  updateMenu();
        }
      });
      group.add(menuitem);
      m_MenuItemViewHorizontal = menuitem;

      // View/Vertical
      menuitem = new JRadioButtonMenuItem("Vertical");
      menu.add(menuitem);
      menuitem.setMnemonic('V');
      menuitem.setSelected(true);
      menuitem.setIcon(GUIHelper.getIcon("ip_layout_vertical.png"));
      menuitem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
	  if (getCurrentPanel() == null)
	    return;
	  getCurrentPanel().setLayoutType(LayoutType.VERTICAL);
	  updateMenu();
        }
      });
      group.add(menuitem);
      m_MenuItemViewVertical = menuitem;

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
   * Returns the image panel in the currently selected tab.
   *
   * @return		the image panel, null if none available
   */
  public ImageProcessingPanel getCurrentPanel() {
    return m_TabbedPane.getCurrentPanel();
  }

  /**
   * Returns the image panel of the specified tab.
   *
   * @param index	the tab index
   * @return		the image panel, null if none available
   */
  public ImageProcessingPanel getPanelAt(int index) {
    return m_TabbedPane.getPanelAt(index);
  }

  /**
   * Returns all the image panels.
   *
   * @return		the image panels
   */
  public ImageProcessingPanel[] getAllPanels() {
    return m_TabbedPane.getAllPanels();
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
    if (m_TabbedPane.load(file)) {
      if (m_RecentFilesHandler != null)
	m_RecentFilesHandler.addRecentItem(file);
    }
    update();
  }

  /**
   * Closes the current image.
   */
  protected void close() {
    int				index;
    ImageProcessingPanel	panel;

    index = m_TabbedPane.getSelectedIndex();
    panel = getPanelAt(index);
    panel.cleanUp();
    m_TabbedPane.remove(index);
    update();
  }

  /**
   * Exits the viewer.
   */
  protected void exit() {
    int		i;

    i = 0;
    while (i < m_TabbedPane.getTabCount())
      m_TabbedPane.remove(i);

    if (getParentFrame() != null) {
      getParentFrame().setVisible(false);
      getParentFrame().dispose();
    }
    else if (getParentDialog() != null) {
      getParentDialog().setVisible(false);
      getParentDialog().dispose();
    }
  }
}
