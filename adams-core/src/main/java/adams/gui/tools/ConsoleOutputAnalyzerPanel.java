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
 * ConsoleOutputAnalyzerPanel.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import adams.core.Shortening;
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.flow.sink.TextSupplier;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.TextFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ConsolePanel.PanelType;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.ParameterPanel;
import adams.gui.core.RecentFilesWithEncodingHandler;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.core.TitleGenerator;
import adams.gui.event.PopupMenuListener;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.menu.ConsoleWindow;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Frontend for analyzing the {@link ConsoleWindow}'s output.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConsoleOutputAnalyzerPanel
  extends BasePanel
  implements MenuBarProvider, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -9220648401733691034L;

  /**
   * Represents a single line from the console output.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ConsoleOutputEntry
    implements Serializable, Comparable<ConsoleOutputEntry> {

    /** for serialization. */
    private static final long serialVersionUID = -7650152159132046191L;

    /** unknown origin. */
    public final static String ORIGIN_UNKNOWN = "unknown";
    
    /** the line number of this entry in the output. */
    protected int m_LineNo;
    
    /** the raw content. */
    protected String m_Raw;
    
    /** the origin, e.g., path to actor. */
    protected String m_Origin;
    
    /** the type of output (ERR/OUT/DEBUG). */
    protected String m_Type;
    
    /** the timestamp. */
    protected String m_Timestamp;
    
    /** the actual output. */
    protected String m_Output;
    
    /**
     * Initializes the entry with the given line from the console output.
     * 
     * @param lineNo 	the line number
     * @param raw	the content to parse
     */
    public ConsoleOutputEntry(int lineNo, String raw) {
      super();
      
      m_LineNo = lineNo;
      m_Raw    = raw;
      
      parse();
    }
    
    /**
     * Parses the raw content.
     */
    protected void parse() {
      boolean	parsed;
      String	content;
      String	prefix;

      parsed  = false;
      content = m_Raw.trim();
      try {
	if (content.startsWith("[")) {
	  prefix      = content.substring(1, content.indexOf(']')).trim();
	  m_Output    = content.substring(content.indexOf(']') + 1).trim();
	  if (prefix.matches("(.*)-(ERROR|INFO|DEBUG)/([0-9\\-\\.]*)")) {
	    m_Origin    = prefix.replaceAll("(.*)-(ERROR|INFO|DEBUG)/([0-9\\-\\.]*)", "$1").trim();
	    m_Type      = prefix.replaceAll("(.*)-(ERROR|INFO|DEBUG)/([0-9\\-\\.]*)", "$2").trim();
	    m_Timestamp = prefix.replaceAll("(.*)-(ERROR|INFO|DEBUG)/([0-9\\-\\.]*)", "$3").trim();
	  }
	  else {
	    m_Origin    = prefix.replaceAll("(.*)-(ERROR|INFO|DEBUG)", "$1").trim();
	    m_Type      = prefix.replaceAll("(.*)-(ERROR|INFO|DEBUG)", "$2").trim();
	    m_Timestamp = "";
	  }
	  if (m_Origin.matches(".*\\/[0-9]+"))
	    m_Origin = m_Origin.replaceAll("(.*)\\/[0-9]+", "$1");
	  parsed = true;
	}
      }
      catch (Exception e) {
	System.err.println("Failed to parse #" + (m_LineNo + 1) + ": " + m_Raw);
	e.printStackTrace();
	parsed = false;
      }
      
      if (!parsed) {
	m_Origin    = ORIGIN_UNKNOWN;
	m_Type      = "";
	m_Timestamp = "";
	m_Output    = m_Raw;
      }
    }
    
    /**
     * Returns the line number of this entry.
     * 
     * @return		the line number
     */
    public int getLineNo() {
      return m_LineNo;
    }
    
    /**
     * Returns the raw content.
     * 
     * @return		the content
     */
    public String getRaw() {
      return m_Raw;
    }
    
    /**
     * Returns the origin of the output.
     * 
     * @return		the origin
     */
    public String getOrigin() {
      return m_Origin;
    }
    
    /**
     * Returns the output type (ERR/OUT/DEBUG).
     * 
     * @return		the type
     */
    public String getType() {
      return m_Type;
    }
    
    /**
     * Returns the timestamp.
     * 
     * @return		the timestamp
     */
    public String getTimestamp() {
      return m_Timestamp;
    }
    
    /**
     * Returns the actual output.
     * 
     * @return		the output
     */
    public String getOutput() {
      return m_Output;
    }
    
    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param   o the object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(ConsoleOutputEntry o) {
      if (o == null)
	return 1;
      else
	return new Integer(m_LineNo).compareTo(o.getLineNo());
    }
    
    /**
     * Returns the raw content.
     * 
     * @return		the content
     */
    @Override
    public String toString() {
      return m_Raw;
    }
  }
  
  /**
   * Class for encapsulating a complete console output.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ConsoleOutput
    implements Serializable, SpreadSheetSupporter {

    /** for serialization. */
    private static final long serialVersionUID = 4261087821410856963L;
    
    /** the content. */
    protected List<ConsoleOutputEntry> m_Entries;
    
    /** the origins. */
    protected List<String> m_Origins;
    
    /** the origin IDs. */
    protected List<Integer> m_OriginIDs;
    
    /** the types. */
    protected List<String> m_Types;
    
    /**
     * Loads the console output from the given file.
     * 
     * @param file	the file to load the content from
     * @param encoding	the encoding to use
     */
    public ConsoleOutput(File file, String encoding) {
      this(FileUtils.loadFromFile(file, encoding));
    }
    
    /**
     * Uses the content, provided as single string.
     * 
     * @param content	the content as single string
     */
    public ConsoleOutput(String content) {
      this(Arrays.asList(content.split("\n")));
    }
    
    /**
     * Initializes the object with the given content.
     * 
     * @param content	the content to use
     */
    public ConsoleOutput(List<String> content) {
      m_Entries   = new ArrayList<ConsoleOutputEntry>();
      m_Origins   = null;
      m_OriginIDs = null;
      m_Types     = null;
      if (content != null)
	parse(content);
    }
    
    /**
     * Initializes the object with the given content.
     * 
     * @param content	the content to use
     */
    public ConsoleOutput(ArrayList<ConsoleOutputEntry> content) {
      m_Entries = new ArrayList<ConsoleOutputEntry>(content);
    }
    
    /**
     * Parses the content.
     * 
     * @param content	the content to parse
     */
    protected void parse(List<String> content) {
      int			i;
      ConsoleOutputEntry	entry;
      
      for (i = 0; i < content.size(); i++) {
	if (!content.get(i).trim().isEmpty()) {
	  entry = new ConsoleOutputEntry(i, content.get(i));
	  m_Entries.add(entry);
	}
      }
    }
    
    /**
     * Returns all available origins in the data (sorted).
     * 
     * @return		the origins
     */
    public synchronized List<String> getOrigins() {
      List<String>	result;
      HashSet<String>	unique;
      
      if (m_Origins == null) {
	unique = new HashSet<String>();
	for (ConsoleOutputEntry entry: m_Entries) {
	  if (entry.getOrigin() == null)
	    continue;
	  if (entry.getOrigin().length() > 0)
	    unique.add(entry.getOrigin());
	}
	result = new ArrayList<String>(unique);
	Collections.sort(result);
	m_Origins = result;
      }
      else {
	result = m_Origins;
      }
      
      return result;
    }
    
    /**
     * Returns all available types in the data (sorted).
     * 
     * @return		the types
     */
    public synchronized List<String> getTypes() {
      List<String>	result;
      HashSet<String>	unique;
      
      if (m_Types == null) {
	unique = new HashSet<String>();
	for (ConsoleOutputEntry entry: m_Entries) {
	  if (entry.getType() == null)
	    continue;
	  if (entry.getType().length() > 0)
	    unique.add(entry.getType());
	}
	result = new ArrayList<String>(unique);
	Collections.sort(result);
	m_Types = result;
      }
      else {
	result = m_Types;
      }
      
      return result;
    }
    
    /**
     * Filters the data.
     * 
     * @param filter	the filter to apply
     */
    public ConsoleOutput applyFilter(ConsoleEntryFilter filter) {
      ArrayList<ConsoleOutputEntry>	filtered;
      
      filtered = new ArrayList<ConsoleOutputEntry>();
      for (ConsoleOutputEntry entry: m_Entries) {
	if (filter.isMatch(entry))
	  filtered.add(entry);
      }
	
      return new ConsoleOutput(filtered);
    }
    
    /**
     * Returns the content as spreadsheet.
     * 
     * @return		the content
     */
    @Override
    public SpreadSheet toSpreadSheet() {
      SpreadSheet	result;
      Row		row;
      
      result = new DefaultSpreadSheet();
      
      // header
      row = result.getHeaderRow();
      row.addCell("1").setContent("Origin");
      row.addCell("2").setContent("Type");
      row.addCell("3").setContent("Timestamp");
      row.addCell("4").setContent("Output");
      
      // data
      for (ConsoleOutputEntry entry: m_Entries) {
	row = result.addRow();
	row.addCell("1").setContent((entry.getOrigin() == null) ? SpreadSheet.MISSING_VALUE : entry.getOrigin());
	row.addCell("2").setContent((entry.getType() == null) ? SpreadSheet.MISSING_VALUE : entry.getType());
	row.addCell("3").setContent((entry.getTimestamp() == null) ? SpreadSheet.MISSING_VALUE : entry.getTimestamp());
	row.addCell("4").setContent((entry.getOutput() == null) ? SpreadSheet.MISSING_VALUE : entry.getOutput());
      }
      
      return result;
    }
    
    /**
     * Returns the managed content as string.
     * 
     * @return		the content
     */
    @Override
    public String toString() {
      StringBuilder	result;
      
      result = new StringBuilder();
      for (ConsoleOutputEntry entry: m_Entries) {
	if (result.length() > 0)
	  result.append("\n");
	result.append(entry.toString());
      }
      
      return result.toString();
    }
  }

  /**
   * For filtering the console output.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ConsoleEntryFilter {
    
    /** the origin regexp. */
    protected BaseRegExp m_OriginRegExp;
    
    /** the exact origin. */
    protected String m_Origin;
    
    /** the type. */
    protected String m_Type;
    
    /** the output regexp. */
    protected BaseRegExp m_OutputRegExp;
    
    /**
     * Initializes a MATCH-ALL filter.
     */
    public ConsoleEntryFilter() {
      this(null, null, null, null);
    }
    
    /**
     * Initializes the filter, use null to turn off filtering.
     * 
     * @param originRegExp	the regular expression on the origin
     * @param origin		the exact origin string
     * @param type		the type to look for
     * @param outputRegExp	the regular expression on the output
     */    
    public ConsoleEntryFilter(BaseRegExp originRegExp, String origin, String type, BaseRegExp outputRegExp) {
      super();
      
      m_OriginRegExp = originRegExp;
      m_Origin       = origin;
      m_Type         = type;
      m_OutputRegExp = outputRegExp;
    }
    
    /**
     * Returns the regular expression on the origin.
     * 
     * @return		the regexp, can be null
     */
    public BaseRegExp getOriginRegExp() {
      return m_OriginRegExp;
    }
    
    /**
     * Returns the exact origin string.
     * 
     * @return		the origin, can be null
     */
    public String getOrigin() {
      return m_Origin;
    }
    
    /**
     * Returns the exact type string.
     * 
     * @return		the type, can be null
     */
    public String getType() {
      return m_Type;
    }
    
    /**
     * Returns the regular expression on the output.
     * 
     * @return		the regexp, can be null
     */
    public BaseRegExp getOutputRegExp() {
      return m_OutputRegExp;
    }
    
    /**
     * Checks whether the entry matches the filter.
     * 
     * @param entry	the entry to check
     * @return		true if a match
     */
    public boolean isMatch(ConsoleOutputEntry entry) {
      boolean	result;
      
      result = true;
      
      if (result && (m_OriginRegExp != null))
	result = m_OriginRegExp.isMatch(entry.getOrigin());
      if (result && (m_Origin != null))
	result = m_Origin.equals(entry.getOrigin());
      if (result && (m_Type != null))
	result = m_Type.equals(entry.getType());
      if (result && (m_OutputRegExp != null))
	result = m_OutputRegExp.isMatch(entry.getOutput());
	
      return result;
    }
    
    /**
     * Checks whether this is a MATCH ALL filter.
     * 
     * @return		true if a match all filter
     */
    public boolean isMatchAll() {
      return (m_OriginRegExp == null) && (m_Origin == null) && (m_Type == null) && (m_OutputRegExp == null);
    }
    
    /**
     * Returns a string representation of the filter.
     * 
     * @return		the string representation
     */
    @Override
    public String toString() {
      return "originRegExp=" + m_OriginRegExp + ", origin=" + m_Origin + ", type=" + m_Type + ", outputRegExp=" + m_OutputRegExp;
    }
  }

  /** the file to store the recent directories. */
  public final static String SESSION_FILE = "ConsoleOutputAnalyzerSession.props";

  /** the split pane to use. */
  protected BaseSplitPane m_SplitPane;
  
  /** the filechooser for loading the files. */
  protected transient TextFileChooser m_FileChooser;
  
  /** the regexp for the origin. */
  protected JTextField m_TextOrigin;
  
  /** the combobox with all origins. */
  protected JComboBox m_ComboBoxOrigin;
  
  /** the combobox with the types. */
  protected JComboBox m_ComboBoxType;
  
  /** the regexp for the output. */
  protected JTextField m_TextOutput;
  
  /** the panel with the search parameters. */
  protected ParameterPanel m_PanelSearch;

  /** the button for triggering the search. */
  protected JButton m_ButtonSearch;

  /** the button for clearing the search. */
  protected JButton m_ButtonClearSearch;
  
  /** the table for displaying the output. */
  protected SpreadSheetTable m_Table;
  
  /** the table model. */
  protected SpreadSheetTableModel m_TableModel;

  /** the original console output. */
  protected ConsoleOutput m_Original;
  
  /** the file the console output was loaded from. */
  protected File m_CurrentFile;
  
  /** the current file encoding. */
  protected String m_CurrentEncoding;

  /** the filtered console output. */
  protected ConsoleOutput m_Filtered;

  /** the recent files handler. */
  protected RecentFilesWithEncodingHandler<JMenu> m_RecentFilesHandler;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "load recent" submenu. */
  protected JMenu m_MenuFileLoadRecent;
  
  /** the "reload" menu item. */
  protected JMenuItem m_MenuFileReload;

  /** for generating the title of the dialog/frame. */
  protected TitleGenerator m_TitleGenerator;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Original           = null;
    m_Filtered           = null;
    m_CurrentFile        = null;
    m_CurrentEncoding    = null;
    m_RecentFilesHandler = null;
    m_TitleGenerator     = new TitleGenerator("Console output analyzer", false);
  }
  
  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setOneTouchExpandable(true);
    m_SplitPane.setResizeWeight(1.0);
    add(m_SplitPane, BorderLayout.CENTER);
    
    m_TableModel = new SpreadSheetTableModel();
    m_Table      = new SpreadSheetTable(m_TableModel);
    m_SplitPane.setLeftComponent(new BaseScrollPane(m_Table));
    m_Table.addCellPopupMenuListener(new PopupMenuListener() {
      @Override
      public void showPopupMenu(MouseEvent e) {
	BasePopupMenu menu = createCellPopup(e);
	if (menu != null)
	  menu.showAbsolute(m_Table, e);
      }
    });
    
    m_PanelSearch = new ParameterPanel();
    panel         = new JPanel(new BorderLayout());
    panel.add(m_PanelSearch, BorderLayout.NORTH);
    m_SplitPane.setRightComponent(panel);

    m_TextOrigin = new JTextField(20);
    m_PanelSearch.addParameter("_Origin (regexp)", m_TextOrigin);
    
    m_ComboBoxOrigin = new JComboBox();
    m_PanelSearch.addParameter("Origin (list)", m_ComboBoxOrigin);
    
    m_ComboBoxType = new JComboBox();
    m_PanelSearch.addParameter("_Type", m_ComboBoxType);
    
    m_TextOutput = new JTextField(20);
    m_PanelSearch.addParameter("Out_put", m_TextOutput);
    
    m_ButtonSearch = new JButton("Search");
    m_ButtonSearch.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	search();
      }
    });
    
    m_ButtonClearSearch = new JButton("Clear");
    m_ButtonClearSearch.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	clearSearch(true);
      }
    });
    
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_ButtonSearch);
    panel.add(m_ButtonClearSearch);
    m_PanelSearch.addParameter("", panel);
  }
  
  /**
   * Returns a popup menu for the table.
   * 
   * @param e		the event that triggered the call
   * @return		the popup menu, null if not applicable
   */
  protected BasePopupMenu createCellPopup(MouseEvent e) {
    BasePopupMenu	result;
    JMenuItem		menuitem;
    final int		row;
    final int		col;
    final Object	value;
    
    result = null;
    row    = m_Table.rowAtPoint(e.getPoint());
    col    = m_Table.columnAtPoint(e.getPoint());
    if ((row == -1) || (col == -1))
      return result;
    value = m_Table.getValueAt(row, col);
    if (value == null)
      return result;
    
    result = new BasePopupMenu();

    // origin
    if (col == 1) {
      // search
      menuitem = new JMenuItem("Search for origin '" + Shortening.shortenEnd(value.toString(), 15) + "'");
      menuitem.setIcon(GUIHelper.getIcon("find.gif"));
      menuitem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          clearSearch(false);
          m_ComboBoxOrigin.setSelectedItem(value.toString());
          search();
        }
      });
      result.add(menuitem);
    }
    // type
    else if (col == 2) {
      // search
      menuitem = new JMenuItem("Search for type '" + value.toString() + "'");
      menuitem.setIcon(GUIHelper.getIcon("find.gif"));
      menuitem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          clearSearch(false);
          m_ComboBoxType.setSelectedItem(value.toString());
          search();
        }
      });
      result.add(menuitem);
    }

    // copy
    menuitem = new JMenuItem("Copy");
    menuitem.setIcon(GUIHelper.getIcon("copy.gif"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ClipboardHelper.copyToClipboard(value.toString());
      }
    });
    if (result.getSubElements().length > 0)
      result.addSeparator();
    result.add(menuitem);
    
    return result;
  }
  
  /**
   * Returns the filechooser for opening the files.
   * 
   * @return		the filechooser
   */
  protected TextFileChooser getFileChooser() {
    TextFileChooser	fileChooser;
    ExtensionFileFilter	filter;
    
    if (m_FileChooser == null) {
      fileChooser = new TextFileChooser();
      filter      = null;
      if (this instanceof TextSupplier)
	filter = ((TextSupplier) this).getCustomTextFileFilter();
      if (filter != null) {
	fileChooser.resetChoosableFileFilters();
	fileChooser.addChoosableFileFilter(filter);
	fileChooser.setFileFilter(filter);
	fileChooser.setDefaultExtension(filter.getExtensions()[0]);
      }
      m_FileChooser = fileChooser;
    }
    
    return m_FileChooser;
  }

  /**
   * Returns the filter based on the current search parameters.
   * 
   * @return		the filter
   */
  protected ConsoleEntryFilter getFilter() {
    BaseRegExp		originRegExp;
    String		origin;
    String		type;
    BaseRegExp		outputRegExp;
    
    originRegExp = null;
    if (m_TextOrigin.getText().length() > 0)
      originRegExp = new BaseRegExp(m_TextOrigin.getText());
    
    origin = null;
    if ((m_ComboBoxOrigin.getSelectedIndex() > -1) && (m_ComboBoxOrigin.getSelectedItem().toString().length() > 0))
      origin = m_ComboBoxOrigin.getSelectedItem().toString();
    
    type = null;
    if ((m_ComboBoxType.getSelectedIndex() > -1) && (m_ComboBoxType.getSelectedItem().toString().length() > 0))
      type = m_ComboBoxType.getSelectedItem().toString();
    
    outputRegExp = null;
    if (m_TextOutput.getText().length() > 0)
      outputRegExp = new BaseRegExp(m_TextOutput.getText());
    
    return new ConsoleEntryFilter(originRegExp, origin, type, outputRegExp);
  }

  /**
   * Resets the search fields.
   * 
   * @param update	whether to update the display
   */
  protected void clearSearch(boolean update) {
    m_TextOrigin.setText("");
    m_ComboBoxOrigin.setSelectedIndex(0);
    m_ComboBoxType.setSelectedIndex(0);
    m_TextOutput.setText("");
    
    if (update) {
      m_Filtered = null;
      update();
    }
  }
  
  /**
   * Performs the search.
   */
  protected void search() {
    ConsoleEntryFilter	filter;
    
    if (m_Original == null)
      return;
    
    filter       = getFilter();
    m_Filtered   = m_Original.applyFilter(filter);
    m_TableModel = new SpreadSheetTableModel(m_Filtered.toSpreadSheet());
    m_Table.setModel(m_TableModel);
  }
  
  /**
   * Updates the menu.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;
    
    m_MenuFileReload.setEnabled(m_CurrentFile != null);
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

      // File/Open...
      menuitem = new JMenuItem("Open...");
      menu.add(menuitem);
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.setIcon(GUIHelper.getIcon("open.gif"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  open();
	}
      });

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesWithEncodingHandler<JMenu>(
	  SESSION_FILE, 5, submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,String>() {
	@Override
	public void recentItemAdded(RecentItemEvent<JMenu,String> e) {
	  // ignored
	}
	@Override
	public void recentItemSelected(RecentItemEvent<JMenu,String> e) {
	  open(RecentFilesWithEncodingHandler.getFile(e.getItem()), RecentFilesWithEncodingHandler.getEncoding(e.getItem()));
	}
      });
      m_MenuFileLoadRecent = submenu;

      // File/Reload
      menuitem = new JMenuItem("Reload");
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("F5"));
      menuitem.setIcon(GUIHelper.getIcon("refresh.gif"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  reload();
	}
      });
      m_MenuFileReload = menuitem;

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
	  closeParent();
	}
      });

      // Edit
      menu = new JMenu("Edit");
      result.add(menu);
      menu.setMnemonic('E');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // Edit/Use current output
      menuitem = new JMenuItem("Use current output");
      menu.add(menuitem);
      menuitem.setMnemonic('c');
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  useCurrentOutput();
	}
      });

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
   * Opens a dialog for the user to choose a file containing console output.
   */
  protected void open() {
    int		retVal;
    
    retVal = getFileChooser().showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;
    
    open(getFileChooser().getSelectedFile(), getFileChooser().getEncoding());
  }
  
  /**
   * Opens the specified file with the console output, using UTF-8.
   * 
   * @param file	the file to load
   */
  public void open(File file) {
    open(file, null);
  }
  
  /**
   * Opens the specified file with the console output.
   * 
   * @param file	the file to load
   * @param encoding	the encoding to use, null for default UTF-8
   */
  public void open(final File file, final String encoding) {
    SwingWorker	worker;
    
    if (file == null)
      return;
    if (!file.exists()) {
      System.err.println("File with console output does not exist: " + file);
      return;
    }
    if (file.isDirectory()) {
      System.err.println("File with console output is pointing to a directory: " + file);
      return;
    }
    
    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	m_Original        = new ConsoleOutput(file, encoding);
	m_Filtered        = null;
	m_CurrentFile     = file;
	m_CurrentEncoding = encoding;
        return null;
      }
      @Override
      protected void done() {
	if (m_RecentFilesHandler != null)
	  m_RecentFilesHandler.addRecentItem(m_CurrentFile.getAbsolutePath() + "\t" + m_CurrentEncoding);
	update();
        super.done();
      }
    };
    worker.execute();
  }
  
  /**
   * Reloads the console output from the current file.
   */
  protected void reload() {
    if (m_CurrentFile == null)
      return;
    open(m_CurrentFile, m_CurrentEncoding);
  }
  
  /**
   * Uses the currently displayed output.
   */
  protected void useCurrentOutput() {
    SwingWorker	worker;
    
    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	m_CurrentFile     = null;
	m_CurrentEncoding = null;
	m_Original        = new ConsoleOutput(ConsolePanel.getSingleton().getPanel(PanelType.ALL).getContent());
	m_Filtered        = null;
        return null;
      }
      @Override
      protected void done() {
	update();
        super.done();
      }
    };
    worker.execute();
  }

  /**
   * Adds the specified object at the head of the list (if not already present) 
   * and returns the updated list.
   * 
   * @param list	the list to update
   * @param first	the element to insert
   * @return		the (potentially) updated list
   */
  protected List addFirstElement(List list, Object first) {
    if (!list.contains(first))
      list.add(0, first);
    return list;
  }
  
  /**
   * Updating the GUI.
   */
  protected void update() {
    ConsoleOutput	current;
    
    if (m_Filtered != null)
      current = m_Filtered;
    else
      current = m_Original;

    if (current != null) {
      m_ComboBoxOrigin.setModel(new DefaultComboBoxModel(addFirstElement(current.getOrigins(), "").toArray()));
      m_ComboBoxType.setModel(new DefaultComboBoxModel(addFirstElement(current.getTypes(), "").toArray()));
    }
    else {
      m_ComboBoxOrigin.setModel(new DefaultComboBoxModel(addFirstElement(new ArrayList(), "").toArray()));
      m_ComboBoxType.setModel(new DefaultComboBoxModel(addFirstElement(new ArrayList(), "").toArray()));
    }
    m_ComboBoxOrigin.setSelectedIndex(0);
    m_ComboBoxType.setSelectedIndex(0);

    // apply filter again
    if (!getFilter().isMatchAll()) {
      search();
    }
    else {
      m_TableModel = new SpreadSheetTableModel(m_Original.toSpreadSheet());
      m_Table.setModel(m_TableModel);
    }
    
    updateMenu();
  }
  
  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  @Override
  public Class[] getSendToClasses() {
    return new Class[]{String.class, JTable.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve an item for
   * @return		true if an object is available for sending
   */
  @Override
  public boolean hasSendToItem(Class[] cls) {
    return SendToActionUtils.isAvailable(new Class[]{String.class, JTable.class}, cls);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve an item for
   * @return		the item to send, null if nothing available at the
   * 			moment
   */
  @Override
  public Object getSendToItem(Class[] cls) {
    Object	result;
    
    result = null;
    
    if (m_Original != null) {
      if (SendToActionUtils.isAvailable(JTable.class, cls)) {
	result = m_Table;
	if (m_Table.getRowCount() == 0)
	  result = null;
      }
      else if (SendToActionUtils.isAvailable(String.class, cls)) {
	result = m_TableModel.toSpreadSheet().toString();
      }
    }
    
    return result;
  }
}
