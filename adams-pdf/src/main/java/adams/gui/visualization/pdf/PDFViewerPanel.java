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
 * PDFViewerPanel.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.pdf;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.Properties;
import adams.core.io.JPod;
import adams.core.io.PlaceholderFile;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandler;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import de.intarsys.pdf.pd.PDDocument;

/**
 * A basic PDF viewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PDFViewerPanel
  extends BasePanel
  implements MenuBarProvider, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 1270944412770632645L;

  /**
   * A specialized tabbed pane with a few methods for easier access.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class TabbedPane
    extends BaseTabbedPane {

    /** for serialization. */
    private static final long serialVersionUID = -2048229771213837710L;

    /**
     * Returns the PDF panel at the specified position.
     *
     * @param index	the tab index of the table
     * @return		the PDF panel
     */
    public PDFPanel getPanelAt(int index) {
      PDFPanel		result;
      JScrollPane	pane;

      if (getComponentAt(index) instanceof JScrollPane) {
	pane   = (JScrollPane) getComponentAt(index);
	result = (PDFPanel) pane.getViewport().getView();
      }
      else {
	result = (PDFPanel) getComponentAt(index);
      }

      return result;
    }
  }

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "PDFViewerSession.props";

  /** the properties. */
  protected static Properties m_Properties;

  /** the tabbed pane for displaying the CSV files. */
  protected TabbedPane m_TabbedPane;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "open" menu item. */
  protected JMenuItem m_MenuItemFileOpen;

  /** the "load recent" submenu. */
  protected JMenu m_MenuItemFileOpenRecent;

  /** the "close" menu item. */
  protected JMenuItem m_MenuItemFileClose;

  /** the "exit" menu item. */
  protected JMenuItem m_MenuItemFileExit;

  /** the menu "zoom". */
  protected JMenu m_MenuViewZoom;

  /** the menu item "zoom in". */
  protected JMenuItem m_MenuItemViewZoomIn;

  /** the menu item "zoom out". */
  protected JMenuItem m_MenuItemViewZoomOut;

  /** the filedialog for loading CSV files. */
  protected transient BaseFileChooser m_FileChooser;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_RecentFilesHandler = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new TabbedPane();
    m_TabbedPane.setCloseTabsWithMiddelMouseButton(true);
    add(m_TabbedPane, BorderLayout.CENTER);
  }

  /**
   * Returns the file chooser and creates it if necessary.
   */
  protected BaseFileChooser getFileChooser() {
    BaseFileChooser	fileChooser;
    
    if (m_FileChooser == null) {
      fileChooser = new BaseFileChooser();
      fileChooser.addChoosableFileFilter(ExtensionFileFilter.getPdfFileFilter());
      fileChooser.setDefaultExtension(ExtensionFileFilter.getPdfFileFilter().getExtensions()[0]);
      fileChooser.setMultiSelectionEnabled(true);
      m_FileChooser = fileChooser;
    }
    
    return m_FileChooser;
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenu	submenu;
    JMenuItem	menuitem;
    int		i;

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

      // File/Open
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
      m_MenuItemFileOpenRecent = submenu;

      // File/Close tab
      menuitem = new JMenuItem("Close tab");
      menu.add(menuitem);
      menuitem.setMnemonic('t');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed W"));
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  closeFile();
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
	  close();
	}
      });
      m_MenuItemFileExit = menuitem;

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
      submenu.addSeparator();
      for (i = 0; i < PDFPanel.ZOOMS.length; i++) {
	final int fZoom = PDFPanel.ZOOMS[i];
	menuitem = new JMenuItem(PDFPanel.ZOOMS[i] + "%");
	submenu.add(menuitem);
	if (PDFPanel.ZOOMS[i] == 100)
	  menuitem.setAccelerator(GUIHelper.getKeyStroke("1"));
	else if (PDFPanel.ZOOMS[i] == 200)
	  menuitem.setAccelerator(GUIHelper.getKeyStroke("2"));
	else if (PDFPanel.ZOOMS[i] == 400)
	  menuitem.setAccelerator(GUIHelper.getKeyStroke("4"));
	menuitem.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    zoom(fZoom);
	  }
	});
      }

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
  public PDFPanel getCurrentPanel() {
    PDFPanel	result;
    int		index;

    result = null;

    index = m_TabbedPane.getSelectedIndex();
    if (index >= 0)
      result = (PDFPanel) m_TabbedPane.getComponentAt(index);

    return result;
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    boolean	pdfAvailable;

    if (m_MenuBar == null)
      return;

    pdfAvailable = (m_TabbedPane.getTabCount() > 0) && (m_TabbedPane.getSelectedIndex() != -1);

    // File
    m_MenuItemFileClose.setEnabled(pdfAvailable);

    // View
    m_MenuViewZoom.setEnabled(pdfAvailable);
    m_MenuItemViewZoomIn.setEnabled(pdfAvailable);
    m_MenuItemViewZoomOut.setEnabled(pdfAvailable);
  }

  /**
   * Opens one or more CSV files.
   */
  protected void open() {
    int			retVal;
    PlaceholderFile[]	files;

    retVal = getFileChooser().showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    files = getFileChooser().getSelectedPlaceholderFiles();
    for (File file: files)
      load(file);
  }

  /**
   * Loads the specified PDF file.
   *
   * @param file	the file to load
   */
  public void load(File file) {
    PDFPanel	panel;
    PDDocument	document;

    document = JPod.load(file);
    if (document == null) {
      GUIHelper.showErrorMessage(
	  this, "Error loading PDF file:\n" + file);
    }
    else {
      panel = new PDFPanel();
      panel.setDocument(document);
      m_TabbedPane.addTab(file.getName(), panel);
      m_TabbedPane.setSelectedIndex(m_TabbedPane.getTabCount() - 1);

      getFileChooser().setCurrentDirectory(file.getParentFile());
      if (m_RecentFilesHandler != null)
	m_RecentFilesHandler.addRecentItem(file);
    }
  }

  /**
   * Closes the current active tab.
   */
  protected void closeFile() {
    int		index;

    index = m_TabbedPane.getSelectedIndex();
    if (index == -1)
      return;

    m_TabbedPane.getPanelAt(index).closeDocument();
    m_TabbedPane.remove(index);
  }

  /**
   * Closes the dialog or frame.
   */
  protected void close() {
    closeParent();
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
   * Returns the class that the supporter generates.
   *
   * @return		the class
   */
  public Class[] getSendToClasses() {
    return new Class[]{PlaceholderFile.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the requested classes
   * @return		true if an object is available for sending
   */
  public boolean hasSendToItem(Class[] cls) {
    return    (SendToActionUtils.isAvailable(PlaceholderFile.class, cls))
           && (getCurrentPanel() != null)
           && (getCurrentPanel().getDocument() != null);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the requested classes
   * @return		the item to send
   */
  public Object getSendToItem(Class[] cls) {
    PlaceholderFile	result;

    result = null;

    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      if (getCurrentPanel().getDocument() != null) {
	result = SendToActionUtils.nextTmpFile("pdfviewer", "pdf");
	if (!JPod.save(getCurrentPanel().getDocument(), result)) {
	  System.err.println("Failed to save PDF to '" + result + "'!");
	  result = null;
	}
      }
    }

    return result;
  }
}
