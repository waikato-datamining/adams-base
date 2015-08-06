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
 * SystemInfoPanel.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools;

import adams.core.SystemInfo;
import adams.core.io.PlaceholderFile;
import adams.core.management.JConsole;
import adams.core.management.JMap;
import adams.core.management.JVisualVM;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTable;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.HashtableTableModel;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.MouseUtils;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.dialog.TextDialog;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A panel displaying information about the system.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SystemInfoPanel
  extends BasePanel
  implements MenuBarProvider, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 5962605234258828474L;

  /** the table model displaying the data. */
  protected HashtableTableModel m_TableModel;

  /** the table displaying the information. */
  protected SortableAndSearchableTable m_Table;

  /** the search panel. */
  protected SearchPanel m_PanelSearch;

  /** the hashtable containing the information. */
  protected SystemInfo m_SystemInfo;

  /** the menu bar. */
  protected JMenuBar m_MenuBar;

  /** the save as menu item. */
  protected JMenuItem m_MenuItemFileSaveAs;

  /** the copy menu item. */
  protected JMenuItem m_MenuItemEditCopy;

  /** the select all menu item. */
  protected JMenuItem m_MenuItemEditSelectAll;

  /** the file chooser for saving the system info data. */
  protected BaseFileChooser m_FileChooser;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    ExtensionFileFilter	filter;

    super.initialize();

    m_SystemInfo  = new SystemInfo();

    m_FileChooser = new BaseFileChooser();
    filter        = ExtensionFileFilter.getCsvFileFilter();
    m_FileChooser.addChoosableFileFilter(filter);
    m_FileChooser.setFileFilter(filter);
    m_FileChooser.setAutoAppendExtension(true);
    m_FileChooser.setDefaultExtension(filter.getExtensions()[0]);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());

    // table
    m_TableModel = new HashtableTableModel(m_SystemInfo.getInfo());
    m_Table      = new SortableAndSearchableTable(m_TableModel);
    m_Table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    add(new BaseScrollPane(m_Table), BorderLayout.CENTER);
    m_Table.setOptimalColumnWidth();

    m_Table.addMouseListener(new MouseAdapter() {
	@Override
	public void mouseClicked(MouseEvent e) {
	  if (MouseUtils.isRightClick(e)) {
	    BasePopupMenu menu = getPopupMenu(m_Table.rowAtPoint(e.getPoint()));
	    if (menu == null)
	      return;
	    e.consume();
	    menu.showAbsolute(m_Table, e);
	  }

	  if (!e.isConsumed())
	    super.mouseClicked(e);
	}
    });

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
   * Returns a popup menu if appropriate.
   *
   * @param row	the row that got the click
   * @return		the menu if appropriate, otherwise null
   */
  protected BasePopupMenu getPopupMenu(int row) {
    BasePopupMenu	result;
    JMenuItem		menuitem;
    String		field;
    String		valueStr;

    result = null;

    if (row > -1) {
      field    = m_Table.getValueAt(row, 0).toString();
      valueStr = m_Table.getValueAt(row, 1).toString();

      // jmap
      if (field.equals(SystemInfo.JVM_PID)) {
	final int pid = Integer.parseInt(valueStr);
	if (result == null)
	  result = new BasePopupMenu();
	menuitem = new JMenuItem("Run " + JMap.EXECUTABLE);
	menuitem.setEnabled(JMap.isAvailable());
	menuitem.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    runJMap(pid);
	  }
	});
	result.add(menuitem);

	menuitem = new JMenuItem("Start up " + JVisualVM.EXECUTABLE);
	menuitem.setEnabled(JVisualVM.isAvailable());
	menuitem.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    runJVisualVM(pid);
	  }
	});
	result.add(menuitem);

	menuitem = new JMenuItem("Start up " + JConsole.EXECUTABLE);
	menuitem.setEnabled(JConsole.isAvailable());
	menuitem.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    runJConsole(pid);
	  }
	});
	result.add(menuitem);
      }
    }

    return result;
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenuItem	menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      menu.setMnemonic('F');
      result.add(menu);

      // File/Save as
      menuitem = new JMenuItem("Save as...");
      menuitem.setIcon(GUIHelper.getIcon("save.gif"));
      menuitem.setMnemonic('a');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed S"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  saveAs();
	}
      });
      menu.add(menuitem);
      m_MenuItemFileSaveAs = menuitem;

      // File/Send to
      menu.addSeparator();
      if (SendToActionUtils.addSendToSubmenu(this, menu))
	menu.addSeparator();

      // File/Close
      menuitem = new JMenuItem("Close", GUIHelper.getIcon("exit.png"));
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  closeParent();
	}
      });
      menu.add(menuitem);

      // Edit
      menu = new JMenu("Edit");
      menu.setMnemonic('E');
      result.add(menu);

      // Edit/Copy
      menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed C"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_Table.copyToClipboard();
	}
      });
      menu.add(menuitem);
      m_MenuItemEditCopy = menuitem;

      // Edit/Select all
      menuitem = new JMenuItem("Select all", GUIHelper.getEmptyIcon());
      menuitem.setMnemonic('S');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed A"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_Table.selectAll();
	}
      });
      menu.addSeparator();
      menu.add(menuitem);
      m_MenuItemEditSelectAll = menuitem;
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Runs jmap and displays the result.
   *
   * @param pid		the pid to run jmap on
   */
  protected void runJMap(int pid) {
    SwingWorker 	worker;
    String 		options;

    // query for options
    options = GUIHelper.showInputDialog(
	GUIHelper.getParentComponent(this), "Enter the options for " + JMap.EXECUTABLE + ":",
	JMap.getDefaultOptions());
    if (options == null)
      return;

    // run jmap and display output
    final int fPid = pid;
    final String fOptions = options;
    worker = new SwingWorker() {
      protected String m_Output = null;
      @Override
      protected Object doInBackground() throws Exception {
	m_Output = JMap.execute(fOptions, fPid);
        return null;
      }
      @Override
      protected void done() {
	TextDialog dialog = new TextDialog();
	dialog.setTitle(JMap.EXECUTABLE + " (" + fPid + ")");
	dialog.setContent(m_Output);
	dialog.setLocationRelativeTo(SystemInfoPanel.this);
	dialog.setVisible(true);
        super.done();
      }
    };
    worker.execute();
  }

  /**
   * Starts up jvisualvm.
   *
   * @param pid		the pid to run jvisualvm on
   */
  protected void runJVisualVM(int pid) {
    Thread	thread;
    String 	options;

    // query for options
    options = GUIHelper.showInputDialog(
	GUIHelper.getParentComponent(this), "Enter the options for " + JVisualVM.EXECUTABLE + ":",
	JVisualVM.getDefaultOptions());
    if (options == null)
      return;

    final int fPid = pid;
    final String fOptions = options;
    thread = new Thread(new Runnable() {
      public void run() {
	JVisualVM.execute(fOptions, fPid);
      }
    });
    thread.start();
  }

  /**
   * Starts up jconsole.
   *
   * @param pid		the pid to run jconsole on
   */
  protected void runJConsole(int pid) {
    Thread	thread;
    String 	options;

    // query for options
    options = GUIHelper.showInputDialog(
	GUIHelper.getParentComponent(this), "Enter the options for " + JConsole.EXECUTABLE + ":",
	JConsole.getDefaultOptions());
    if (options == null)
      return;

    final int fPid = pid;
    final String fOptions = options;
    thread = new Thread(new Runnable() {
      public void run() {
	JConsole.execute(fOptions, fPid);
      }
    });
    thread.start();
  }

  /**
   * Generates a spreadsheet object from the system info data table.
   *
   * @return		the generated spreadsheet
   */
  public SpreadSheet getContent() {
    SpreadSheet	result;
    Row		row;
    int		i;
    int		n;

    result = new SpreadSheet();

    // header
    for (i = 0; i < 2; i++)
      result.getHeaderRow().addCell(m_TableModel.getColumnName(i)).setContent(m_TableModel.getColumnName(i));

    // content
    for (n = 0; n < m_TableModel.getRowCount(); n++) {
      row = result.addRow("" + result.getRowCount());
      for (i = 0; i < 2; i++)
	row.addCell(m_TableModel.getColumnName(i)).setContent("" + m_TableModel.getValueAt(n, i));
    }

    return result;
  }

  /**
   * Saves the table content as CSV file.
   */
  protected void saveAs() {
    SpreadSheet			sheet;
    int				retVal;
    CsvSpreadSheetWriter	writer;

    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    sheet  = getContent();
    writer = new CsvSpreadSheetWriter();
    if (!writer.write(sheet, m_FileChooser.getSelectedFile()))
      GUIHelper.showErrorMessage(this, "Failed to write data to file:\n" + m_FileChooser.getSelectedFile());
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
    return (SendToActionUtils.isAvailable(new Class[]{PlaceholderFile.class, JTable.class}, cls));
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the requested classes
   * @return		the item to send
   */
  public Object getSendToItem(Class[] cls) {
    Object			result;
    CsvSpreadSheetWriter	writer;

    result = null;

    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      result = SendToActionUtils.nextTmpFile("systeminfo", "csv");
      writer = new CsvSpreadSheetWriter();
      if (!writer.write(getContent(), (PlaceholderFile) result))
	result = null;
    }
    else if (SendToActionUtils.isAvailable(JTable.class, cls)) {
      result = m_Table;
    }

    return result;
  }
}
