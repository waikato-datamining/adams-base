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
 * PropertiesViewerPanel.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.env.Environment;
import adams.env.Project;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTable;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.PropertiesTableModel;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;

/**
 * A panel displaying properties files. If the properties file is managed
 * through the Environment class, then it will be loaded through this
 * mechanism, otherwise a simple loading of the properties, just for the
 * current project will be performed.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PropertiesViewerPanel
  extends BasePanel
  implements MenuBarProvider, TableModelListener, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3421194728424946082L;

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "PropertiesViewerSession.props";

  /** the combobox with the properties keys. */
  protected JComboBox m_ComboBoxKeys;

  /** the table model displaying the data. */
  protected PropertiesTableModel m_TableModel;

  /** the table displaying the information. */
  protected SortableAndSearchableTable m_Table;

  /** the search panel. */
  protected SearchPanel m_PanelSearch;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "open" menu item. */
  protected JMenuItem m_MenuItemOpen;

  /** the "load recent" submenu. */
  protected JMenu m_MenuLoadRecent;

  /** the "save" menu item. */
  protected JMenuItem m_MenuItemSave;

  /** the "close" menu item. */
  protected JMenuItem m_MenuItemClose;

  /** the current props file. */
  protected File m_CurrentFile;

  /** the file chooser for loading external props files. */
  protected BaseFileChooser m_FileChooser;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    ExtensionFileFilter	filter;
    
    super.initialize();

    m_CurrentFile = null;
    m_FileChooser = new BaseFileChooser();
    filter = ExtensionFileFilter.getPropertiesFileFilter();
    m_FileChooser.addChoosableFileFilter(filter);
    m_FileChooser.setFileFilter(filter);
    m_FileChooser.setDefaultExtension(filter.getExtensions()[0]);
    m_FileChooser.setAutoAppendExtension(true);
    m_RecentFilesHandler = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel		panel;
    Iterator<String>	keys;
    List<String>	list;

    super.initGUI();

    setLayout(new BorderLayout());

    // keys
    keys = Environment.getInstance().keys();
    list = new ArrayList<String>();
    while (keys.hasNext())
      list.add(keys.next());
    Collections.sort(list);
    list.add(0, "");
    m_ComboBoxKeys = new JComboBox(list.toArray(new String[list.size()]));
    m_ComboBoxKeys.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_ComboBoxKeys.getSelectedIndex() <= 0) {
	  open((Properties) null);
	  return;
	}
	m_CurrentFile = null;
	Properties props;
	try {
	  props = Environment.getInstance().read((String) m_ComboBoxKeys.getSelectedItem());
	}
	catch (Exception ex) {
	  System.err.println("Failed to load properties for key '" + m_ComboBoxKeys.getSelectedItem() + "':");
	  ex.printStackTrace();
	  props = null;
	}
	open(props);
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(new JLabel("Key"));
    panel.add(m_ComboBoxKeys);
    add(panel, BorderLayout.NORTH);

    // table
    m_TableModel = new PropertiesTableModel(new Properties());
    m_Table      = new SortableAndSearchableTable(m_TableModel);
    m_Table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    add(new BaseScrollPane(m_Table), BorderLayout.CENTER);
    m_Table.setOptimalColumnWidth(0);

    // search
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(panel, BorderLayout.SOUTH);
    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, true, "_Search", "_Go");
    m_PanelSearch.addSearchListener(new SearchListener() {
      public void searchInitiated(SearchEvent e) {
	m_Table.search(m_PanelSearch.getSearchText(), m_PanelSearch.isRegularExpression());
	m_PanelSearch.grabFocus();
      }
    });
    panel.add(m_PanelSearch);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    m_ComboBoxKeys.setSelectedIndex(0);
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

    if (m_MenuBar == null) {
      // register window listener since we're part of a dialog or frame
      if (getParentFrame() != null) {
	final JFrame frame = (JFrame) getParentFrame();
	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	frame.addWindowListener(new WindowAdapter() {
	  @Override
	  public void windowClosing(WindowEvent e) {
	    close();
	  }
	});
      }
      else if (getParentDialog() != null) {
	final JDialog dialog = (JDialog) getParentDialog();
	dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	dialog.addWindowListener(new WindowAdapter() {
	  @Override
	  public void windowClosing(WindowEvent e) {
	    close();
	  }
	});
      }

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
      m_MenuItemOpen = menuitem;

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<JMenu>(SESSION_FILE, 5, submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  if (!checkForModified())
	    return;
	  open(e.getItem());
	}
      });
      m_MenuLoadRecent = submenu;

      // File/Save
      menuitem = new JMenuItem("Save");
      menu.add(menuitem);
      menuitem.setMnemonic('s');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
      menuitem.setIcon(GUIHelper.getIcon("save.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  save();
	}
      });
      m_MenuItemSave = menuitem;

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
      m_MenuItemClose = menuitem;

      // update menu
      m_MenuBar = result;
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the flow or discarded the changes.
   *
   * @return		true if safe to proceed
   */
  protected boolean checkForModified() {
    boolean 	result;
    int		retVal;
    String	msg;

    result = !m_TableModel.isModified();

    if (!result) {
      msg    = "Properties not saved - save?\n" + m_CurrentFile;
      retVal = GUIHelper.showConfirmMessage(this, msg, "Properties not saved");
      switch (retVal) {
	case GUIHelper.APPROVE_OPTION:
	  save();
	  result = !m_TableModel.isModified();
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
   * Updates the menu items.
   */
  protected void updateMenu() {
    m_MenuItemOpen.setEnabled(true);
    m_MenuLoadRecent.setEnabled(m_RecentFilesHandler.size() > 0);
    m_MenuItemSave.setEnabled((m_CurrentFile != null) && m_TableModel.isModified());
    m_MenuItemClose.setEnabled(true);
  }

  /**
   * Updates the title of the dialog.
   */
  protected void updateTitle() {
    String	title;

    title = "Properties viewer";
    if (m_CurrentFile != null)
      title += " [" + m_CurrentFile + "]";

    if (m_TableModel.isModified())
      title = "*" + title;

    setParentTitle(title);
  }

  /**
   * Closes the dialog or frame.
   */
  protected void close() {
    if (getParentFrame() != null)
      ((JFrame) getParentFrame()).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    closeParent();
  }

  /**
   * Presents the user with an input dialog to enter the props file.
   */
  protected void open() {
    int			retVal;
    File		file;

    retVal = m_FileChooser.showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;
    file = m_FileChooser.getSelectedFile();
    if (file == null)
      return;

    open(file);
  }

  /**
   * Opens the specified properties file and displays it.
   *
   * @param file	the file to load and display
   */
  protected void open(File file) {
    Properties	props;

    m_ComboBoxKeys.setSelectedIndex(0);

    try {
      props = new Properties();
      props.load(file.getAbsolutePath());
      m_CurrentFile = file;
      if (m_RecentFilesHandler != null)
	m_RecentFilesHandler.addRecentItem(m_CurrentFile);
    }
    catch (Exception e) {
      e.printStackTrace();
      GUIHelper.showErrorMessage(
	  this, "Error loading properties file '" + file + "':\n" + e);
      props = null;
    }

    open(props);
  }

  /**
   * Displays the properties in the table.
   *
   * @param props	the properties to display
   */
  protected void open(Properties props) {
    m_TableModel.removeTableModelListener(this);
    if (props != null) {
      m_TableModel = new PropertiesTableModel(props);
      m_TableModel.addTableModelListener(this);
    }
    else {
      m_TableModel  = new PropertiesTableModel();
      m_CurrentFile = null;
    }
    m_TableModel.setEditable(m_CurrentFile != null);  // only if external file!
    m_Table.setModel(m_TableModel);
    m_Table.setOptimalColumnWidth();

    updateTitle();
  }

  /**
   * Saves the current properties.
   */
  protected void save() {
    Properties		props;
    BufferedWriter	writer;
    String		msg;

    props = m_TableModel.getProperties();
    try {
      writer = new BufferedWriter(new FileWriter(m_CurrentFile.getAbsoluteFile()));
      props.store(writer, "Modified using " + Project.NAME);
      writer.flush();
      writer.close();
      m_TableModel.setModified(false);
    }
    catch (Exception e) {
      msg = "Failed to save properties to '" + m_CurrentFile + "':";
      System.err.println(msg);
      e.printStackTrace();
      GUIHelper.showErrorMessage(this, msg + "\n" + e);
    }

    updateTitle();
    updateMenu();
  }

  /**
   * This fine grain notification tells listeners the exact range
   * of cells, rows, or columns that changed.
   *
   * @param e		the event
   */
  public void tableChanged(TableModelEvent e) {
    updateTitle();
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  public Class[] getSendToClasses() {
    return new Class[]{PlaceholderFile.class, JTable.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the requested classes
   * @return		true if an object is available for sending
   */
  public boolean hasSendToItem(Class[] cls) {
    return    (SendToActionUtils.isAvailable(new Class[]{PlaceholderFile.class, JTable.class}, cls))
           && (m_TableModel.getRowCount() > 0);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the requested classes
   * @return		the item to send
   */
  public Object getSendToItem(Class[] cls) {
    Object		result;
    Properties		props;

    if (m_TableModel.getRowCount() == 0)
      return null;

    result = null;

    if ((SendToActionUtils.isAvailable(PlaceholderFile.class, cls))) {
      props  = m_TableModel.getProperties();
      result = SendToActionUtils.nextTmpFile("propsviewer", "props");
      if (!props.save(((PlaceholderFile) result).getAbsolutePath()))
	result = null;
    }
    else if ((SendToActionUtils.isAvailable(JTable.class, cls))) {
      result = m_Table;
    }

    return result;
  }
}
