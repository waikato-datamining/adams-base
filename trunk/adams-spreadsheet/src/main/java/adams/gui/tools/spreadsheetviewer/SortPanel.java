/*
 * SortPanel.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.spreadsheetviewer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JButton;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.RowComparator;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.event.SortSetupEvent;
import adams.gui.event.SortSetupEvent.EventType;
import adams.gui.event.SortSetupListener;

/**
 * Panel that allows users to sort a spreadsheet over an arbitrary number
 * of columns.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SortPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 7929780439384161250L;

  /** the spreadsheet that forms the basis for the sorting. */
  protected SpreadSheet m_Sheet;

  /** the list of definitions to use. */
  protected List<SortDefinitionPanel> m_Panels;

  /** the gridlayout in use. */
  protected GridLayout m_Layout;

  /** the panel holding the defintion panels. */
  protected BasePanel m_PanelDefinitions;

  /** the panel for the buttons. */
  protected BasePanel m_PanelButtons;

  /** the button for resetting the definitions. */
  protected JButton m_ButtonReset;

  /** the button for a new sort definition. */
  protected JButton m_ButtonAdd;

  /** the column names. */
  protected List<String> m_ColumnNames;

  /** the listeners for changes in the setup. */
  protected HashSet<SortSetupListener> m_SortSetupListeners;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Panels             = new ArrayList<SortDefinitionPanel>();
    m_ColumnNames        = new ArrayList<String>();
    m_SortSetupListeners = new HashSet<SortSetupListener>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_Layout = new GridLayout(0, 1);
    m_PanelDefinitions = new BasePanel(m_Layout);
    add(m_PanelDefinitions, BorderLayout.CENTER);

    m_PanelButtons = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    add(m_PanelButtons, BorderLayout.NORTH);

    m_ButtonReset = new JButton(GUIHelper.getIcon("new.gif"));
    m_ButtonReset.setToolTipText("Click to reset the conditions");
    m_ButtonReset.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	resetDefinitions();
      }
    });
    m_PanelButtons.add(m_ButtonReset);

    m_ButtonAdd = new JButton(GUIHelper.getIcon("add.gif"));
    m_ButtonAdd.setToolTipText("Click to add a condition");
    m_ButtonAdd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	addDefinition();
      }
    });
    m_PanelButtons.add(m_ButtonAdd);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    reset();
  }

  /**
   * Sets the spreadsheet to use.
   *
   * @param value	the spreadsheet to use
   * @return		true if the panel was reset
   */
  public boolean setSpreadSheet(SpreadSheet value) {
    boolean	mustReset;
    
    mustReset = (m_Sheet == null) || (m_Sheet.equalsHeader(value) != null);
    m_Sheet = value;
    if (mustReset)
      reset();
    
    return mustReset;
  }

  /**
   * Returns the spreadsheet to use.
   *
   * @return		the spreadsheet in use, null if none set
   */
  public SpreadSheet getSpreadSheet() {
    return m_Sheet;
  }

  /**
   * Returns the column names in use.
   *
   * @return		the column names
   */
  public List<String> getColumnNames() {
    return m_ColumnNames;
  }

  /**
   * Resets the definitions.
   */
  public void resetDefinitions() {
    SortDefinitionPanel	panel;

    m_Panels.clear();
    panel = new SortDefinitionPanel(this);
    m_Panels.add(panel);

    update();
    notifySortSetupListeners(new SortSetupEvent(this, null, EventType.RESET));
  }

  /**
   * Adds a new definition.
   */
  public void addDefinition() {
    SortDefinitionPanel	panel;

    panel = new SortDefinitionPanel(this);
    m_Panels.add(panel);

    update();
    notifySortSetupListeners(new SortSetupEvent(this, panel, EventType.ADD));
  }

  /**
   * Removes the panel from the list of sort definitions.
   *
   * @param panel	the panel to remove
   */
  public void removeDefinition(SortDefinitionPanel panel) {
    if (m_Panels.remove(panel)) {
      update();
      notifySortSetupListeners(new SortSetupEvent(this, panel, EventType.REMOVE));
    }
  }

  /**
   * Removes all sort definition panels.
   */
  public void reset() {
    m_Panels.clear();
    m_ColumnNames.clear();
    if (m_Sheet != null) {
      for (Cell cell: m_Sheet.getHeaderRow().cells())
	m_ColumnNames.add(cell.getContent());
    }
    update();
    notifySortSetupListeners(new SortSetupEvent(this, null, EventType.RESET));
  }
  
  /**
   * Checks whether the panel is the first one.
   * 
   * @param panel	the panel to check
   * @return		true if the first one
   */
  public boolean isFirstDefinition(SortDefinitionPanel panel) {
    return (m_Panels.size() > 0) && (m_Panels.get(0) == panel);
  }
  
  /**
   * Checks whether the panel is the last one.
   * 
   * @param panel	the panel to check
   * @return		true if the last one
   */
  public boolean isLastDefinition(SortDefinitionPanel panel) {
    return (m_Panels.size() > 0) && (m_Panels.get(m_Panels.size() - 1) == panel);
  }
  
  /**
   * Moves the panel up/down.
   * 
   * @param panel	the panel to move
   * @param up		if true, gets moved up, otherwise down
   * @return		true if successfully moved
   */
  public boolean moveDefinition(SortDefinitionPanel panel, boolean up) {
    boolean	result;
    int		index;    
    
    result = false;
    index  = m_Panels.indexOf(panel);
    
    if (up && !isFirstDefinition(panel)) {
      m_Panels.remove(index);
      m_Panels.add(index - 1, panel);
      result = true;
    }
    else if (!up && !isLastDefinition(panel)) {
      m_Panels.remove(index);
      m_Panels.add(index + 1, panel);
      result = true;
    }
    
    update();
    notifySortSetupListeners(new SortSetupEvent(this, panel, EventType.MOVED));
    
    return result;
  }

  /**
   * Updates the display.
   */
  protected void update() {
    m_PanelDefinitions.removeAll();
    m_Layout.setRows(m_Panels.size());
    for (SortDefinitionPanel panel: m_Panels)
      m_PanelDefinitions.add(panel);
    revalidate();
    if (getParentDialog() != null)
      getParentDialog().pack();
    for (SortDefinitionPanel panel: m_Panels)
      panel.updateButtons();
  }

  /**
   * Checks whether the setup is valid, i.e., no name used twice, at least
   * one sorting definition.
   *
   * @return		true if valid
   */
  public boolean isValidSetup() {
    HashSet<String>	names;

    names = new HashSet<String>();
    for (SortDefinitionPanel panel: m_Panels) {
      if (names.contains(panel.getColumnName()))
	return false;
      names.add(panel.getColumnName());
    }

    return (names.size() > 0);
  }

  /**
   * Returns a comparator for sorting the spreadsheet.
   *
   * @return		the comparator, null if not valid setup
   */
  public RowComparator getComparator() {
    RowComparator	result;
    int[]		indices;
    boolean[]	ascending;
    int			i;
    SortDefinitionPanel	panel;

    if (!isValidSetup())
      return null;

    indices   = new int[m_Panels.size()];
    ascending = new boolean[m_Panels.size()];
    for (i = 0; i < m_Panels.size(); i++) {
      panel        = m_Panels.get(i);
      indices[i]   = m_ColumnNames.indexOf(panel.getColumnName());
      ascending[i] = panel.isAscending();
    }

    result = new RowComparator(indices, ascending);

    return result;
  }

  /**
   * Adds the specified listener.
   *
   * @param l		the listener to add
   */
  public void addSortSetupListener(SortSetupListener l) {
    m_SortSetupListeners.add(l);
  }

  /**
   * Removes the specified listener.
   *
   * @param l		the listener to remove
   */
  public void removeSortSetupListener(SortSetupListener l) {
    m_SortSetupListeners.remove(l);
  }

  /**
   * Notifies all listeners with the specified event.
   *
   * @param e		the event to send
   */
  public void notifySortSetupListeners(SortSetupEvent e) {
    for (SortSetupListener l: m_SortSetupListeners)
      l.sortSetupChanged(e);
  }
}
