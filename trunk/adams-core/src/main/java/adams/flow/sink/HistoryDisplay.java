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
 * HistoryDisplay.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.VariableNameNoUpdate;
import adams.flow.core.Token;
import adams.gui.core.AbstractNamedHistoryPanel.HistoryEntrySelectionEvent;
import adams.gui.core.AbstractNamedHistoryPanel.HistoryEntrySelectionListener;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.BufferHistoryPanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.TextEditorPanel;

/**
 <!-- globalinfo-start -->
 * Actor that outputs any object that arrives at its input port via the 'toString()' method in a separate 'history' entry.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: HistoryDisplay
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 640
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 480
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-font &lt;java.awt.Font&gt; (property: font)
 * &nbsp;&nbsp;&nbsp;The font of the dialog.
 * &nbsp;&nbsp;&nbsp;default: Monospaced-PLAIN-12
 * </pre>
 * 
 * <pre>-always-clear &lt;boolean&gt; (property: alwaysClear)
 * &nbsp;&nbsp;&nbsp;If enabled, the display is always cleared before processing a token.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-caret-at-start &lt;boolean&gt; (property: caretAtStart)
 * &nbsp;&nbsp;&nbsp;If set to true, then the caret will be positioned by default at the start 
 * &nbsp;&nbsp;&nbsp;and not the end (can be changed in dialog: View -&gt; Position caret at start
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-num-tokens &lt;int&gt; (property: numTokens)
 * &nbsp;&nbsp;&nbsp;The number of tokens to accept per history entry.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-entry-name-variable &lt;adams.core.VariableNameNoUpdate&gt; (property: entryNameVariable)
 * &nbsp;&nbsp;&nbsp;The variable to use for naming the entries; gets ignored if variable not 
 * &nbsp;&nbsp;&nbsp;available; an existing history entry gets replaced if a new one with the 
 * &nbsp;&nbsp;&nbsp;same name gets added.
 * &nbsp;&nbsp;&nbsp;default: entryNameVariable
 * </pre>
 * 
 * <pre>-allow-merge &lt;boolean&gt; (property: allowMerge)
 * &nbsp;&nbsp;&nbsp;If enabled then entries with the same name (ie when using 'entryNameVariable'
 * &nbsp;&nbsp;&nbsp;) get merged.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-allow-search &lt;boolean&gt; (property: allowSearch)
 * &nbsp;&nbsp;&nbsp;Whether to allow the user to search the entries.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HistoryDisplay
  extends AbstractTextualDisplay {

  /** for serialization. */
  private static final long serialVersionUID = 3365817040968234289L;

  /**
   * Represents a panel with a history on the left and the displayed text
   * on the right.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class HistorySplitPanel
    extends BasePanel {

    /** for serialization. */
    private static final long serialVersionUID = 5121061351955687610L;

    /** the owning HistoryDisplay component. */
    protected HistoryDisplay m_Owner;

    /** the split pane for the components. */
    protected JSplitPane m_SplitPane;
    
    /** the status bar. */
    protected BaseStatusBar m_StatusBar;

    /** the history panel. */
    protected BufferHistoryPanel m_History;

    /** the actual text area. */
    protected TextEditorPanel m_TextPanel;

    /** the format for the dates. */
    protected SimpleDateFormat m_Format;

    /**
     * Initializes the split pane.
     *
     * @param owner		the owning TextDisplay
     */
    public HistorySplitPanel(HistoryDisplay owner) {
      super(new BorderLayout());

      m_Owner   = owner;
      m_Format  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

      m_SplitPane = new JSplitPane();
      add(m_SplitPane, BorderLayout.CENTER);

      m_TextPanel = new TextEditorPanel();
      m_TextPanel.setTextFont(owner.getFont());
      m_TextPanel.setEditable(false);
      m_SplitPane.setBottomComponent(m_TextPanel);

      m_History = new BufferHistoryPanel();
      m_History.setTextArea(m_TextPanel.getTextArea());
      m_History.addHistoryEntrySelectionListener(new HistoryEntrySelectionListener() {
	@Override
	public void historyEntrySelected(HistoryEntrySelectionEvent e) {
	  m_StatusBar.setStatus(Utils.flatten(e.getNames(), ", "));
	}
      });
      m_SplitPane.setTopComponent(m_History);

      m_SplitPane.setResizeWeight(0.1);
      m_SplitPane.setDividerLocation(Math.max(150, m_History.getPreferredSize().width));
      
      m_StatusBar = new BaseStatusBar();
      m_StatusBar.setMouseListenerActive(true);
      add(m_StatusBar, BorderLayout.SOUTH);
    }

    /**
     * Returns the owner of this history panel.
     *
     * @return		the owning TextDisplay component
     */
    public HistoryDisplay getOwner() {
      return m_Owner;
    }

    /**
     * Returns the underlying text panel.
     *
     * @return		the text editor
     */
    public TextEditorPanel getTextPanel() {
      return m_TextPanel;
    }

    /**
     * Removes all entries.
     */
    public void clear() {
      m_History.clear();
      m_TextPanel.clear();
    }

    /**
     * Returns the number of results.
     *
     * @return		the number of results
     */
    public int count() {
      return m_History.count();
    }

    /**
     * Returns the underlying history panel.
     *
     * @return		the panel
     */
    public BufferHistoryPanel getHistory() {
      return m_History;
    }

    /**
     * Adds the given text.
     *
     * @param result	the text to add
     */
    public void addResult(String result) {
      addResult(new StringBuilder(result));
    }

    /**
     * Adds the given text.
     *
     * @param result	the text to add
     */
    public synchronized void addResult(StringBuilder result) {
      String	baseID;
      String	id;
      String	var;

      // determine a unique ID
      var = m_Owner.getEntryNameVariable().getValue();
      if (m_Owner.getVariables().has(var)) {
	id = m_Owner.getVariables().get(var);
      }
      else {
	synchronized(m_Format) {
	  baseID = m_Format.format(new Date());
	}
	id = m_History.newEntryName(baseID);
      }

      // add result
      if (m_History.hasEntry(id) && m_Owner.getAllowMerge())
	appendResult(result, m_History.indexOfEntry(id));
      else
	m_History.addEntry(id, result);

      // select this entry immediately
      m_History.setSelectedEntry(id);
    }

    /**
     * Appends the given text to the last index.
     * Creates a new entry if none yet available.
     *
     * @param result	the text to append
     */
    public void appendResult(String result) {
      appendResult(result, -1);
    }

    /**
     * Appends the given text to the specified index.
     * Creates a new entry if none yet available.
     *
     * @param result	the text to append
     * @param index	the 0-based index of the history element to append to, -1 for last
     */
    public void appendResult(String result, int index) {
      appendResult(new StringBuilder(result), index);
    }

    /**
     * Appends the given text to the last index.
     * Creates a new entry if none yet available.
     *
     * @param result	the text to append
     */
    public synchronized void appendResult(StringBuilder result) {
      appendResult(result, -1);
    }

    /**
     * Appends the given text to the last index. 
     * Creates a new entry if none yet available.
     *
     * @param result	the text to append
     * @param index	the 0-based index of the history element to append to, -1 for last
     */
    public synchronized void appendResult(StringBuilder result, int index) {
      int	actualIndex;
      
      actualIndex = index;
      if (actualIndex < 0)
	actualIndex = m_History.count() - 1;
      
      // nothing to append to?
      if ((actualIndex < 0) || (actualIndex >= m_History.count())) {
	addResult(result);
	return;
      }

      m_History.getEntry(actualIndex).append("\n").append(result);
      m_History.setSelectedIndex(actualIndex);
    }
    
    /**
     * Sets whether the entry list is searchable.
     * 
     * @param value	true if to make the list searchable
     */
    public void setAllowSearch(boolean value) {
      m_History.setAllowSearch(value);
    }
    
    /**
     * Returns whether the entry list is searchable.
     * 
     * @return		true if list is searchable
     */
    public boolean getAllowSearch() {
      return m_History.getAllowSearch();
    }
  }

  /** the key for storing the current count in the backup. */
  public final static String BACKUP_CURRENTCOUNT = "current count";
  
  /** the number of tokens to accept for a single entry. */
  protected int m_NumTokens;

  /** the history panel. */
  protected HistorySplitPanel m_HistoryPanel;

  /** the print menu item. */
  protected JMenuItem m_MenuItemFilePrint;

  /** the copy menu item. */
  protected JMenuItem m_MenuItemEditCopy;

  /** the select all menu item. */
  protected JMenuItem m_MenuItemEditSelectAll;

  /** the font menu item. */
  protected JMenuItem m_MenuItemViewFont;

  /** the "caret position" menu item. */
  protected JCheckBoxMenuItem m_MenuItemViewCaret;
  
  /** the current count of tokens. */
  protected int m_CurrentCount;
  
  /** whether to allow merging of panel content. */
  protected boolean m_AllowMerge;

  /** whether to allow searching. */
  protected boolean m_AllowSearch;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Actor that outputs any object that arrives at its input port via "
      + "the 'toString()' method in a separate 'history' entry.";
  }

  /** whether to position the caret by default at the start or at the end. */
  protected boolean m_CaretAtStart;

  /** the variable to use for naming the entries. */
  protected VariableNameNoUpdate m_EntryNameVariable;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "caret-at-start", "caretAtStart",
	    false);
    
    m_OptionManager.add(
	    "num-tokens", "numTokens",
	    1, -1, null);
    
    m_OptionManager.add(
	    "entry-name-variable", "entryNameVariable",
	    new VariableNameNoUpdate("entryNameVariable"));

    m_OptionManager.add(
	    "allow-merge", "allowMerge",
	    false);

    m_OptionManager.add(
	    "allow-search", "allowSearch",
	    false);
  }

  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_CurrentCount = 0;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    
    pruneBackup(BACKUP_CURRENTCOUNT);
  }
  
  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();
    result.put(BACKUP_CURRENTCOUNT, m_CurrentCount);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_CURRENTCOUNT)) {
      m_CurrentCount = (Integer) state.get(BACKUP_CURRENTCOUNT);
      state.remove(BACKUP_CURRENTCOUNT);
    }

    super.restoreState(state);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "caretAtStart", m_CaretAtStart, "caret at start", ", ");
    result += QuickInfoHelper.toString(this, "numTokens", m_NumTokens, ", # tokens: ");
    result += QuickInfoHelper.toString(this, "entryNameVariable", m_EntryNameVariable, ", entry name var: ");
    result += QuickInfoHelper.toString(this, "allowMerge", m_AllowMerge, "merge", ", ");
    result += QuickInfoHelper.toString(this, "allowSearch", m_AllowSearch, "searchable", ", ");

    return result;
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 640;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 480;
  }

  /**
   * Sets whether to position the caret at the start or at the end (default).
   *
   * @param value	if true then the caret will be positioned at start
   */
  public void setCaretAtStart(boolean value) {
    m_CaretAtStart = value;
    reset();
  }

  /**
   * Returns whether the caret is positioned at the start instead of the end.
   *
   * @return		true if caret positioned at start
   */
  public boolean isCaretAtStart() {
    return m_CaretAtStart;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String caretAtStartTipText() {
    return
        "If set to true, then the caret will be positioned by default at the "
      + "start and not the end (can be changed in dialog: View -> Position caret at start).";
  }

  /**
   * Sets the number of tokens to accept per entry before creating a new entry.
   *
   * @param value	the panel provider to use
   */
  public void setNumTokens(int value) {
    if (value >= -1) {
      m_NumTokens = value;
      reset();
    }
    else {
      getLogger().warning("Number of tokens must be -1/0 (for unlimited) or greater than 0, provided: " + value);
    }
  }

  /**
   * Returns the number of tokens to accept per entry before creating a new entry.
   *
   * @return		the number of tokens
   */
  public int getNumTokens() {
    return m_NumTokens;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numTokensTipText() {
    return "The number of tokens to accept per history entry.";
  }

  /**
   * Sets the variable name which value gets used to name the entries. Gets
   * ignored if variable does not exist.
   *
   * @param value	the variable name
   */
  public void setEntryNameVariable(VariableNameNoUpdate value) {
    m_EntryNameVariable = value;
    reset();
  }

  /**
   * Returns the variable name which value gets used to name the entries.
   * Gets ignored if variable does not exist.
   *
   * @return		the variable name
   */
  public VariableNameNoUpdate getEntryNameVariable() {
    return m_EntryNameVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String entryNameVariableTipText() {
    return "The variable to use for naming the entries; gets ignored if variable not available; an existing history entry gets replaced if a new one with the same name gets added.";
  }

  /**
   * Sets whether to enable merging of content in case of same name.
   *
   * @param value 	true if to allow merge
   * @see		#setEntryNameVariable(VariableNameNoUpdate)
   */
  public void setAllowMerge(boolean value) {
    m_AllowMerge = value;
    reset();
  }

  /**
   * Returns whether to enable merging of content in case of same name.
   *
   * @return 		true if to allow merge
   * @see		#setEntryNameVariable(VariableNameNoUpdate)
   */
  public boolean getAllowMerge() {
    return m_AllowMerge;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String allowMergeTipText() {
    return 
	"If enabled then entries with the same name (ie when using "
	+ "'entryNameVariable') get merged.";
  }

  /**
   * Sets whether to allow the user to search the entries.
   *
   * @param value 	true if to allow search
   */
  public void setAllowSearch(boolean value) {
    m_AllowSearch = value;
    reset();
  }

  /**
   * Returns whether to allow the user to search the entries.
   *
   * @return 		true if to allow search
   */
  public boolean getAllowSearch() {
    return m_AllowSearch;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String allowSearchTipText() {
    return "Whether to allow the user to search the entries.";
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_HistoryPanel != null)
      m_HistoryPanel.clear();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    m_HistoryPanel = new HistorySplitPanel(this);
    m_HistoryPanel.setAllowSearch(m_AllowSearch);
    m_HistoryPanel.getHistory().setCaretAtStart(m_CaretAtStart);
    m_HistoryPanel.getHistory().addHistoryEntrySelectionListener(new HistoryEntrySelectionListener() {
      public void historyEntrySelected(HistoryEntrySelectionEvent e) {
	updateMenu();
      }
    });

    return m_HistoryPanel;
  }

  /**
   * Creates the "File" menu.
   *
   * @return		the generated menu
   */
  @Override
  protected JMenu createFileMenu() {
    JMenu	result;
    JMenuItem	menuitem;
    int		pos;

    result = super.createFileMenu();

    // File/Print
    menuitem = new JMenuItem("Print...");
    menuitem.setMnemonic('P');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed P"));
    menuitem.setIcon(GUIHelper.getIcon("print.gif"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_HistoryPanel.getTextPanel().printText();
      }
    });
    pos = indexOfMenuItem(result, m_MenuItemFileClose);
    result.insertSeparator(pos);
    result.insert(menuitem, pos);
    m_MenuItemFilePrint = menuitem;

    return result;
  }

  /**
   * Creates the "Edit" menu.
   *
   * @return		the menu
   */
  protected JMenu createEditMenu() {
    JMenu			result;
    JMenuItem			menuitem;
    final TextEditorPanel	fPanel;

    fPanel = m_HistoryPanel.getTextPanel();

    // Edit
    result = new JMenu("Edit");
    result.setMnemonic('E');
    result.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	updateMenu();
      }
    });

    // Edit/Copy
    menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
    menuitem.setMnemonic('C');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed C"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	fPanel.copy();
      }
    });
    result.add(menuitem);
    m_MenuItemEditCopy = menuitem;

    // Edit/Select all
    menuitem = new JMenuItem("Select all", GUIHelper.getEmptyIcon());
    menuitem.setMnemonic('S');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed A"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	fPanel.selectAll();
      }
    });
    result.addSeparator();
    result.add(menuitem);
    m_MenuItemEditSelectAll = menuitem;

    return result;
  }

  /**
   * Creates the "Edit" menu.
   *
   * @return		the menu
   */
  protected JMenu createViewMenu() {
    JMenu	result;
    JMenuItem	menuitem;

    // View
    result = new JMenu("View");
    result.setMnemonic('V');
    result.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	updateMenu();
      }
    });

    // View/Font
    menuitem = new JMenuItem("Choose font...");
    result.add(menuitem);
    menuitem.setIcon(GUIHelper.getIcon("font.png"));
    menuitem.setMnemonic('f');
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_HistoryPanel.getTextPanel().selectFont();
      }
    });
    m_MenuItemViewFont = menuitem;

    // View/Caret
    menuitem = new JCheckBoxMenuItem("Position caret at start");
    result.addSeparator();
    result.add(menuitem);
    menuitem.setMnemonic('s');
    menuitem.setSelected(m_CaretAtStart);
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_HistoryPanel.getHistory().setCaretAtStart(m_MenuItemViewCaret.isSelected());
      }
    });
    m_MenuItemViewCaret = (JCheckBoxMenuItem) menuitem;

    return result;
  }

  /**
   * Assembles the menu bar.
   *
   * @return		the menu bar
   */
  @Override
  protected JMenuBar createMenuBar() {
    JMenuBar	result;

    result = super.createMenuBar();
    result.add(createEditMenu());
    result.add(createViewMenu());

    return result;
  }

  /**
   * updates the enabled state of the menu items.
   */
  @Override
  protected void updateMenu() {
    TextEditorPanel	panel;

    if (m_MenuBar == null)
      return;

    super.updateMenu();

    panel = m_HistoryPanel.getTextPanel();

    // File
    m_MenuItemFileClear.setEnabled(m_HistoryPanel.count() > 0);

    // Edit
    m_MenuItemEditCopy.setEnabled(panel.canCopy());
  }

  /**
   * Whether "clear" is supported and shows up in the menu.
   *
   * @return		always true
   */
  @Override
  protected boolean supportsClear() {
    return true;
  }

  /**
   * Clears the display.
   */
  @Override
  protected void clear() {
    m_HistoryPanel.clear();
  }

  /**
   * Returns the text to save.
   *
   * @return		the text, null if no text available
   */
  @Override
  public String supplyText() {
    String	result;
    int		index;

    result = null;

    if (m_HistoryPanel != null) {
      index  = m_HistoryPanel.getHistory().getSelectedIndex();
      if (index != -1)
	result = m_HistoryPanel.getHistory().getEntry(index).toString();
    }

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.Object.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Object.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    boolean	newPanel;
    
    newPanel = false;
    if ((m_NumTokens > 0) && (m_CurrentCount % m_NumTokens == 0))
      newPanel = true;
    else if ((m_NumTokens <= 0) && (m_CurrentCount == 0))
      newPanel = true;

    synchronized(m_HistoryPanel) {
      if (newPanel)
	m_HistoryPanel.addResult(token.getPayload().toString());
      else
	m_HistoryPanel.appendResult(token.getPayload().toString());
    }
    
    m_CurrentCount++;
  }

  /**
   * Removes all graphical components.
   */
  @Override
  protected void cleanUpGUI() {
    if (m_HistoryPanel != null)
      m_HistoryPanel.clear();

    super.cleanUpGUI();
  }
}
