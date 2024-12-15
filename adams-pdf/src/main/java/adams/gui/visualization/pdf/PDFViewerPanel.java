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
 * PDFViewerPanel.java
 * Copyright (C) 2011-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.pdf;

import adams.core.Properties;
import adams.core.UniqueIDs;
import adams.core.io.PlaceholderFile;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandler;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.visualization.pdf.menu.AbstractPDFViewerAction;
import adams.gui.visualization.pdf.menu.ToolExtractImages;
import adams.gui.visualization.pdf.menu.ToolExtractPages;
import adams.gui.visualization.pdf.menu.ToolExtractText;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.icepdf.core.pobjects.Document;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A basic PDF viewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
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
   */
  public static class MultiPagePane
      extends adams.gui.core.MultiPagePane {

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

      if (getPageAt(index) instanceof JScrollPane) {
	pane   = (JScrollPane) getPageAt(index);
	result = (PDFPanel) pane.getViewport().getView();
      }
      else {
	result = (PDFPanel) getPageAt(index);
      }

      return result;
    }
  }

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "PDFViewerSession.props";

  /** the properties. */
  protected static Properties m_Properties;

  /** the multipage pane for displaying the PDF files. */
  protected MultiPagePane m_MultiPagePane;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "open" menu item. */
  protected JMenuItem m_MenuItemFileOpen;

  /** the "load recent" submenu. */
  protected JMenu m_MenuItemFileOpenRecent;

  /** the "close" menu item. */
  protected JMenuItem m_MenuItemFileClose;

  /** the "print" menu item. */
  protected JMenuItem m_MenuItemFilePrint;

  /** the "exit" menu item. */
  protected JMenuItem m_MenuItemFileExit;

  /** the "extract pages" menu item. */
  protected AbstractPDFViewerAction m_MenuItemToolsExtractPages;

  /** the "extract text" menu item. */
  protected AbstractPDFViewerAction m_MenuItemToolsExtractText;

  /** the "extract images" menu item. */
  protected AbstractPDFViewerAction m_MenuItemToolsExtractImages;

  /** the menu item actions. */
  protected List<AbstractPDFViewerAction> m_ViewerActions;

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
    m_ViewerActions      = new ArrayList<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_MultiPagePane = new MultiPagePane();
    add(m_MultiPagePane, BorderLayout.CENTER);
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
      menuitem.setIcon(ImageManager.getIcon("open.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  open();
	}
      });
      m_MenuItemFileOpen = menuitem;

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<>(SESSION_FILE, 5, submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<>() {
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
      menuitem.setIcon(ImageManager.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  closeFile();
	}
      });
      m_MenuItemFileClose = menuitem;

      // File/Print...
      menu.addSeparator();
      menuitem = new JMenuItem("Print...");
      menu.add(menuitem);
      menuitem.setMnemonic('P');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed P"));
      menuitem.setIcon(ImageManager.getIcon("print.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  printFile();
	}
      });
      m_MenuItemFilePrint = menuitem;

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
	  close();
	}
      });
      m_MenuItemFileExit = menuitem;

      // Tools
      menu = new JMenu("Tools");
      result.add(menu);
      menu.setMnemonic('T');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // Tools/Extract pages
      m_MenuItemToolsExtractPages = new ToolExtractPages();
      menu.add(m_MenuItemToolsExtractPages);
      m_ViewerActions.add(m_MenuItemToolsExtractPages);

      // Tools/Extract text
      m_MenuItemToolsExtractText = new ToolExtractText();
      menu.add(m_MenuItemToolsExtractText);
      m_ViewerActions.add(m_MenuItemToolsExtractText);

      // Tools/Extract images
      m_MenuItemToolsExtractImages = new ToolExtractImages();
      menu.add(m_MenuItemToolsExtractImages);
      m_ViewerActions.add(m_MenuItemToolsExtractImages);

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

    index = m_MultiPagePane.getSelectedIndex();
    if (index >= 0)
      result = (PDFPanel) m_MultiPagePane.getPageAt(index);

    return result;
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    boolean	pdfAvailable;

    if (m_MenuBar == null)
      return;

    pdfAvailable = (m_MultiPagePane.getPageCount() > 0) && (m_MultiPagePane.getSelectedIndex() != -1);

    // File
    m_MenuItemFileClose.setEnabled(pdfAvailable);
    m_MenuItemFilePrint.setEnabled(pdfAvailable);

    // actions
    for (AbstractPDFViewerAction action: m_ViewerActions) {
      action.setOwner(this);
      action.update();
    }
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

    panel = new PDFPanel();
    panel.setDocument(file);
    m_MultiPagePane.addPage(file.getName(), panel);
    m_MultiPagePane.setSelectedIndex(m_MultiPagePane.getPageCount() - 1);

    getFileChooser().setCurrentDirectory(file.getParentFile().getAbsoluteFile());
    if (m_RecentFilesHandler != null)
      m_RecentFilesHandler.addRecentItem(file);
  }

  /**
   * Loads the specified PDF document.
   *
   * @param data	the data of the document to load
   */
  public void load(byte[] data) {
    load(data, null);
  }

  /**
   * Loads the specified PDF document.
   *
   * @param data	the data of the document to load
   * @param desc 	the description for the document, can be null
   */
  public void load(byte[] data, String desc) {
    PDFPanel	panel;

    desc  = newDescriptionIfNecessary(desc);
    panel = new PDFPanel();
    panel.setDocument(data, desc);
    m_MultiPagePane.addPage(desc, panel);
    m_MultiPagePane.setSelectedIndex(m_MultiPagePane.getPageCount() - 1);
  }

  /**
   * Closes the current active tab.
   */
  protected void closeFile() {
    int		index;

    index = m_MultiPagePane.getSelectedIndex();
    if (index == -1)
      return;

    m_MultiPagePane.getPanelAt(index).closeDocument();
    m_MultiPagePane.remove(index);
  }

  /**
   * Prints the current active tab.
   */
  protected void printFile() {
    int		index;

    index = m_MultiPagePane.getSelectedIndex();
    if (index == -1)
      return;

    m_MultiPagePane.getPanelAt(index).print(true);
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
    return new Class[]{PlaceholderFile.class, Document.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the requested classes
   * @return		true if an object is available for sending
   */
  public boolean hasSendToItem(Class[] cls) {
    return    (SendToActionUtils.isAvailable(PlaceholderFile.class, cls) || SendToActionUtils.isAvailable(Document.class, cls))
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
    Object	result;
    String	msg;

    result = null;

    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      if (getCurrentPanel().getDocument() != null) {
	result = SendToActionUtils.nextTmpFile("pdfviewer", "pdf");
	msg    = getCurrentPanel().saveTo((File) result);
	if (msg != null) {
	  System.err.println("Failed to save PDF to '" + result + "':\n" + msg);
	  result = null;
	}
      }
    }
    else if (SendToActionUtils.isAvailable(PDDocument.class, cls)) {
      result = getCurrentPanel().getDocument();
    }

    return result;
  }

  /**
   * Creates a new description if necessary (new+INT).
   *
   * @param desc	the description, auto-generates one if null
   * @return		the potentially updated description
   */
  public static String newDescriptionIfNecessary(String desc) {
    if (desc == null)
      desc = "new" + UniqueIDs.nextInt("pdf");
    return desc;
  }
}
