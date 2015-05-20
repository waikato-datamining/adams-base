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
 * DisplayPanelManager.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.VariableNameNoUpdate;
import adams.core.Variables;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.TextFileChooser;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.core.AbstractNamedHistoryPanel.HistoryEntrySelectionEvent;
import adams.gui.core.AbstractNamedHistoryPanel.HistoryEntrySelectionListener;
import adams.gui.core.BaseFrame;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.print.JComponentWriter;
import adams.gui.print.JComponentWriterFileChooser;
import adams.gui.print.PNGWriter;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Actor that displays a 'history' of panels created by the selected panel provider. The provider can be an actor that generates classifier errors, for instance.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * &nbsp;&nbsp;&nbsp;java.awt.image.BufferedImage<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImage<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: DisplayPanelManager
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
 * <pre>-provider &lt;adams.flow.sink.DisplayPanelProvider&gt; (property: panelProvider)
 * &nbsp;&nbsp;&nbsp;The actor for generating the display panels.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.ImageViewer -writer adams.gui.print.NullWriter -selection-processor adams.gui.visualization.image.selection.NullProcessor -image-overlay adams.gui.visualization.image.NullOverlay
 * </pre>
 * 
 * <pre>-num-tokens &lt;int&gt; (property: numTokens)
 * &nbsp;&nbsp;&nbsp;The number of tokens to accept per created panel.
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
 * &nbsp;&nbsp;&nbsp;If enabled and the display panel is derived from adams.flow.sink.MergeableDisplayPanel 
 * &nbsp;&nbsp;&nbsp;then entries with the same name (ie when using 'entryNameVariable') get 
 * &nbsp;&nbsp;&nbsp;merged.
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
public class DisplayPanelManager
  extends AbstractDisplay
  implements MenuBarProvider, ComponentSupplier, TextSupplier, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 3365817040968234289L;

  /**
   * A history panel that keeps track of named DisplayPanel objects, e.g.,
   * containing experiments results.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class DisplayPanelHistoryPanel
    extends AbstractNamedHistoryPanel<DisplayPanel> {

    /** for serialization. */
    private static final long serialVersionUID = 1704390033157269580L;

    /** the panel to display the results in. */
    protected BasePanel m_Panel;

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_Panel = null;
    }

    /**
     * Sets the panel to display the results in.
     *
     * @param value	the panel to display
     */
    public void setPanel(BasePanel value) {
      m_Panel = value;
    }

    /**
     * Displays the specified entry.
     *
     * @param name	the name of the entry, can be null to clear display
     */
    @Override
    protected void updateEntry(String name) {
      m_Panel.removeAll();

      if (name != null) {
        // update panel
        if (hasEntry(name)) {
          m_Panel.add((JPanel) getEntry(name));
          m_Panel.getParent().invalidate();
          m_Panel.getParent().validate();
          m_Panel.getParent().repaint();
        }
      }
    }

    /**
     * Removes the specified entry.
     *
     * @param name	the name of the entry
     * @return		the entry that was stored under this name or null if
     * 			no entry was stored with this name
     */
    @Override
    public DisplayPanel removeEntry(String name) {
      DisplayPanel	result;

      result = super.removeEntry(name);
      if (result != null)
	result.cleanUp();

      return result;
    }
  }

  /**
   * Represents a panel with a history on the left and the displayed panel
   * on the right.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class HistorySplitPanel
    extends BasePanel {

    /** for serialization. */
    private static final long serialVersionUID = 5121061351955687610L;

    /** the owning DisplayPanelDisplay component. */
    protected DisplayPanelManager m_Owner;

    /** the split pane for the components. */
    protected JSplitPane m_SplitPane;
    
    /** the status bar. */
    protected BaseStatusBar m_StatusBar;

    /** the history panel. */
    protected DisplayPanelHistoryPanel m_History;

    /** the actual panel for displaying the other panels. */
    protected BasePanel m_Panel;

    /** the format for the dates. */
    protected SimpleDateFormat m_Format;

    /**
     * Initializes the split pane.
     *
     * @param owner		the owning TextDisplay
     */
    public HistorySplitPanel(DisplayPanelManager owner) {
      super(new BorderLayout());

      m_Owner   = owner;
      m_Format  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

      m_SplitPane = new JSplitPane();
      add(m_SplitPane, BorderLayout.CENTER);

      m_Panel = new BasePanel(new BorderLayout());
      if (owner.getPanelProvider().displayPanelRequiresScrollPane())
	m_SplitPane.setBottomComponent(new BaseScrollPane(m_Panel));
      else
	m_SplitPane.setBottomComponent(m_Panel);

      m_History = new DisplayPanelHistoryPanel();
      m_History.setPanel(m_Panel);
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
    public DisplayPanelManager getOwner() {
      return m_Owner;
    }

    /**
     * Removes all entries.
     */
    public void clear() {
      m_History.clear();
      m_Panel.removeAll();
      m_Panel.repaint();
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
    public DisplayPanelHistoryPanel getHistory() {
      return m_History;
    }

    /**
     * Adds the given text.
     *
     * @param result	the text to add
     */
    public synchronized void addResult(DisplayPanel result) {
      String	baseID;
      String	id;
      String	var;
      boolean	add;

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
      add = true;
      if (m_History.hasEntry(id) && m_Owner.getAllowMerge()) {
	if (result instanceof MergeableDisplayPanel) {
	  ((MergeableDisplayPanel) m_History.getEntry(id)).mergeWith((MergeableDisplayPanel) result);
	  add = false;
	}
	else {
	  m_Owner.getLogger().warning(
	      result.getClass().getName() + " does not implement " 
		  + MergeableDisplayPanel.class.getName() + ", merging not possible!");
	}
      }

      if (add)
	m_History.addEntry(id, result);

      // select this entry immediately
      m_History.setSelectedEntry(id);
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

  /** the key for storing the current panel in the backup. */
  public final static String BACKUP_CURRENTPANEL = "current panel";

  /** the history panel. */
  protected HistorySplitPanel m_HistoryPanel;

  /** the actor to use for generating panels. */
  protected DisplayPanelProvider m_PanelProvider;

  /** the variable to use for naming the entries. */
  protected VariableNameNoUpdate m_EntryNameVariable;
  
  /** the number of tokens to accept for a single panel. */
  protected int m_NumTokens;
  
  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "clear" menu item. */
  protected JMenuItem m_MenuItemFileClear;

  /** the "save as" menu item. */
  protected JMenuItem m_MenuItemFileSaveAs;

  /** the "exit" menu item. */
  protected JMenuItem m_MenuItemFileClose;

  /** the filedialog for loading/saving flows. */
  protected transient JComponentWriterFileChooser m_ComponentFileChooser;

  /** the filedialog for loading/saving flows. */
  protected transient TextFileChooser m_TextFileChooser;
  
  /** the current count of tokens. */
  protected int m_CurrentCount;
  
  /** the current panel, if any. */
  protected DisplayPanel m_CurrentPanel;
  
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
        "Actor that displays a 'history' of panels created by the selected "
      + "panel provider. The provider can be an actor that generates classifier "
      + "errors, for instance.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "provider", "panelProvider",
	    new ImageViewer());
    
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
    m_CurrentPanel = null;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    
    pruneBackup(BACKUP_CURRENTCOUNT);
    pruneBackup(BACKUP_CURRENTPANEL);
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
    if (m_CurrentPanel != null)
      result.put(BACKUP_CURRENTPANEL, m_CurrentPanel);

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

    if (state.containsKey(BACKUP_CURRENTPANEL)) {
      m_CurrentPanel = (DisplayPanel) state.get(BACKUP_CURRENTPANEL);
      state.remove(BACKUP_CURRENTPANEL);
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
    result += QuickInfoHelper.toString(this, "panelProvider", m_PanelProvider, ", provider: ");
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
   * Sets the panel provider to use for generating the panels.
   *
   * @param value	the panel provider to use
   */
  public void setPanelProvider(DisplayPanelProvider value) {
    m_PanelProvider = value;
    reset();
  }

  /**
   * Returns the panel provider in use for generating the panels.
   *
   * @return		the panel provider in use
   */
  public DisplayPanelProvider getPanelProvider() {
    return m_PanelProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String panelProviderTipText() {
    return "The actor for generating the display panels.";
  }

  /**
   * Sets the number of tokens to accept per panel before creating a new panel.
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
   * Returns the number of tokens to accept per panel before creating a new panel.
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
    return "The number of tokens to accept per created panel.";
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
   * Sets whether to enable merging of panel content in case of same name.
   *
   * @param value 	true if to allow merge
   * @see		#setEntryNameVariable(VariableNameNoUpdate)
   */
  public void setAllowMerge(boolean value) {
    m_AllowMerge = value;
    reset();
  }

  /**
   * Returns whether to enable merging of panel content in case of same name.
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
	"If enabled and the display panel is derived from " 
	+ MergeableDisplayPanel.class.getName() + " then entries with the "
	+ "same name (ie when using 'entryNameVariable') get merged.";
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
   * Updates the Variables instance in use.
   * <br><br>
   * Use with caution!
   *
   * @param value	the instance to use
   */
  @Override
  protected void forceVariables(Variables value) {
    super.forceVariables(value);
    m_PanelProvider.setVariables(value);
  }

  /**
   * Returns the currently selected panel.
   *
   * @return		the panel or null if none selected
   */
  protected JPanel getSelectedPanel() {
    JPanel	result;
    int		index;

    result = null;
    if (m_HistoryPanel != null) {
      index  = m_HistoryPanel.getHistory().getSelectedIndex();
      if (index != -1)
	result = (JPanel) m_HistoryPanel.getHistory().getEntry(index);
    }

    return result;
  }

  /**
   * Returns the current panel.
   *
   * @return		the current panel, can be null
   */
  public JComponent supplyComponent() {
    JComponent	result;
    JPanel	panel;

    result = null;

    if (m_PanelProvider instanceof ComponentSupplier) {
      panel = getSelectedPanel();
      if (panel != null)
	result = ((ComponentSupplier) panel).supplyComponent();
    }

    return result;
  }

  /**
   * Returns a custom file filter for the file chooser.
   * 
   * @return		the file filter, null if to use default one
   */
  public ExtensionFileFilter getCustomTextFileFilter() {
    if (m_PanelProvider instanceof TextSupplier)
      return ((TextSupplier) getSelectedPanel()).getCustomTextFileFilter();
    else
      return null;
  }

  /**
   * Supplies the text.
   *
   * @return		the text, null if none available
   */
  public String supplyText() {
    String	result;
    JPanel	panel;

    result = null;

    if (m_PanelProvider instanceof TextSupplier) {
      panel = getSelectedPanel();
      if (panel != null)
	result = ((TextSupplier) panel).supplyText();
    }

    return result;
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
    HistorySplitPanel	result;

    result         = new HistorySplitPanel(this);
    result.setAllowSearch(m_AllowSearch);
    m_HistoryPanel = result;

    return result;
  }

  /**
   * Creates the actual frame.
   *
   * @param panel	the panel to display in the frame
   * @return		the created frame
   */
  @Override
  protected BaseFrame doCreateFrame(BasePanel panel) {
    BaseFrame	result;
    ImageIcon	icon;
    
    result = super.doCreateFrame(panel);
    
    icon = GUIHelper.getIcon(m_PanelProvider.getClass());
    if (icon != null)
      result.setIconImage(icon.getImage());
    
    return result;
  }
  
  /**
   * Returns (and initializes if necessary) the file chooser for the components.
   * 
   * @return		the file chooser
   */
  protected JComponentWriterFileChooser getComponentFileChooser() {
    if (m_ComponentFileChooser == null)
      m_ComponentFileChooser = new JComponentWriterFileChooser();
    
    return m_ComponentFileChooser;
  }
  
  /**
   * Returns (and initializes if necessary) the file chooser for the text.
   * 
   * @return		the file chooser
   */
  protected TextFileChooser getTextFileChooser() {
    TextFileChooser	fileChooser;
    ExtensionFileFilter	filter;
    
    if (m_TextFileChooser == null) {
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
      m_TextFileChooser = fileChooser;
    }
    
    return m_TextFileChooser;
  }
  
  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class, java.awt.image.BufferedImage.class, adams.data.image.AbstractImage.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    if ((m_PanelProvider != null) && (m_PanelProvider instanceof InputConsumer))
      return ((InputConsumer) m_PanelProvider).accepts();
    else
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

    if (newPanel) {
      synchronized(m_HistoryPanel) {
	m_CurrentPanel = m_PanelProvider.createDisplayPanel(null);
	m_HistoryPanel.addResult(m_CurrentPanel);
      }
    }
    
    if (m_CurrentPanel != null)
      m_CurrentPanel.display(token);

    m_CurrentCount++;
  }

  /**
   * Assembles the menu bar.
   *
   * @return		the menu bar
   */
  protected JMenuBar createMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenuItem	menuitem;

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

    // File/Clear
    menuitem = new JMenuItem("Clear");
    menu.add(menuitem);
    menuitem.setMnemonic('l');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
    menuitem.setIcon(GUIHelper.getIcon("new.gif"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	clear();
      }
    });
    m_MenuItemFileClear = menuitem;

    // File/Save As
    menuitem = new JMenuItem("Save as...");
    menu.add(menuitem);
    menuitem.setMnemonic('a');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
    menuitem.setIcon(GUIHelper.getIcon("save.gif"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	saveAs();
      }
    });
    m_MenuItemFileSaveAs = menuitem;

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
    m_MenuItemFileClose = menuitem;

    return result;
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    if (m_MenuBar == null) {
      m_MenuBar = createMenuBar();
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

    m_MenuItemFileSaveAs.setEnabled(
      (    (m_PanelProvider instanceof ComponentSupplier)
        || (m_PanelProvider instanceof TextSupplier) )
      && (getSelectedPanel() != null));

    m_MenuItemFileClear.setEnabled(m_HistoryPanel.count() > 0);
  }

  /**
   * Clears the display.
   */
  protected void clear() {
    m_HistoryPanel.clear();
  }

  /**
   * Saves the setups.
   */
  protected void saveAs() {
    int			retVal;
    JComponentWriter	writer;
    String		filename;

    filename = m_HistoryPanel.getHistory().getSelectedEntry();
    filename = FileUtils.createFilename(filename, "");
    if (m_PanelProvider instanceof TextSupplier) {
      filename = filename + "." + getTextFileChooser().getDefaultExtension();
      getTextFileChooser().setSelectedFile(new File(filename));
      retVal = getTextFileChooser().showSaveDialog(m_HistoryPanel);
      if (retVal != BaseFileChooser.APPROVE_OPTION)
	return;

      String msg = FileUtils.writeToFileMsg(
        getTextFileChooser().getSelectedFile().getAbsolutePath(),
        supplyText(),
        false,
        getTextFileChooser().getEncoding());

      if (msg != null)
        getLogger().severe("Error saving text to '" + getTextFileChooser().getSelectedFile() + "':\n" + msg);
    }
    else if (m_PanelProvider instanceof ComponentSupplier) {
      getComponentFileChooser().setSelectedFile(new File(filename));
      retVal = getComponentFileChooser().showSaveDialog(m_HistoryPanel);
      if (retVal != BaseFileChooser.APPROVE_OPTION)
	return;

      writer = getComponentFileChooser().getWriter();
      writer.setComponent(supplyComponent());
      try {
	writer.toOutput();
      }
      catch (Exception e) {
	handleException("Error saving panel to '" + writer.getFile() + "': ", e);
      }
    }
  }

  /**
   * Closes the dialog or frame.
   */
  protected void close() {
    m_HistoryPanel.closeParent();
  }

  /**
   * Removes all graphical components.
   */
  @Override
  protected void cleanUpGUI() {
    if (m_HistoryPanel != null)
      m_HistoryPanel.clear();

    m_MenuBar              = null;
    m_MenuItemFileClear    = null;
    m_MenuItemFileSaveAs   = null;
    m_MenuItemFileClose    = null;

    super.cleanUpGUI();
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  public Class[] getSendToClasses() {
    if (m_PanelProvider instanceof ComponentSupplier)
      return new Class[]{PlaceholderFile.class, JComponent.class};
    else if (m_PanelProvider instanceof TextSupplier)
      return new Class[]{String.class};
    else
      return new Class[]{String.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve the item for
   * @return		true if an object is available for sending
   */
  public boolean hasSendToItem(Class[] cls) {
    if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
      if (m_PanelProvider instanceof ComponentSupplier)
	return (supplyComponent() != null);
      else
	return false;
    }

    if (SendToActionUtils.isAvailable(new Class[]{PlaceholderFile.class, String.class}, cls)) {
      if (m_PanelProvider instanceof ComponentSupplier)
	return (supplyComponent() != null);
      else if (m_PanelProvider instanceof TextSupplier)
	return ((supplyText() != null) && (supplyText().length() > 0));
    }

    return false;
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  public Object getSendToItem(Class[] cls) {
    Object		result;
    JComponent		comp;
    PNGWriter		writer;

    result = null;

    if (SendToActionUtils.isAvailable(new Class[]{PlaceholderFile.class, String.class}, cls)) {
      if (m_PanelProvider instanceof ComponentSupplier) {
	comp = supplyComponent();
	if (comp != null) {
	  result = SendToActionUtils.nextTmpFile("actor-" + getName(), "png");
	  writer = new PNGWriter();
	  writer.setFile((PlaceholderFile) result);
	  writer.setComponent(comp);
	  try {
	    writer.generateOutput();
	  }
	  catch (Exception e) {
	    handleException("Failed to write image to " + result + ":", e);
	    result = null;
	  }
	}
      }
      if (m_PanelProvider instanceof TextSupplier) {
	result = supplyText();
      }
    }
    else if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
      if (m_PanelProvider instanceof ComponentSupplier)
	result = supplyComponent();
    }

    return result;
  }
}
