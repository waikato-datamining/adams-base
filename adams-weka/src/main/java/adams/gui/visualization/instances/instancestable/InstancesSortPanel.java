/*
 * InstancesSortPanel.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.instances.instancestable;

import adams.data.instances.InstanceComparator;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.ImageManager;
import adams.gui.event.InstancesSortSetupEvent;
import adams.gui.event.InstancesSortSetupEvent.EventType;
import adams.gui.event.InstancesSortSetupListener;
import weka.core.Instances;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Panel that allows users to sort instances over an arbitrary number
 * of columns.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class InstancesSortPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 7929780439384161250L;

  /** the instances that forms the basis for the sorting. */
  protected Instances m_Data;

  /** the list of definitions to use. */
  protected List<InstancesSortDefinitionPanel> m_Panels;

  /** the gridlayout in use. */
  protected GridLayout m_Layout;

  /** the panel holding the defintion panels. */
  protected BasePanel m_PanelDefinitions;

  /** the panel for the buttons. */
  protected BasePanel m_PanelButtons;

  /** the button for resetting the definitions. */
  protected BaseButton m_ButtonReset;

  /** the button for a new sort definition. */
  protected BaseButton m_ButtonAdd;

  /** the column names. */
  protected List<String> m_ColumnNames;

  /** the listeners for changes in the setup. */
  protected HashSet<InstancesSortSetupListener> m_InstancesSortSetupListeners;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Panels             = new ArrayList<>();
    m_ColumnNames        = new ArrayList<>();
    m_InstancesSortSetupListeners = new HashSet<>();
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

    m_ButtonReset = new BaseButton(ImageManager.getIcon("new.gif"));
    m_ButtonReset.setToolTipText("Click to reset the conditions");
    m_ButtonReset.addActionListener((ActionEvent e) -> resetDefinitions());
    m_PanelButtons.add(m_ButtonReset);

    m_ButtonAdd = new BaseButton(ImageManager.getIcon("add.gif"));
    m_ButtonAdd.setToolTipText("Click to add a condition");
    m_ButtonAdd.addActionListener((ActionEvent e) -> addDefinition());
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
   * Sets the Instances to use.
   *
   * @param value	the Instances to use
   * @return		true if the panel was reset
   */
  public boolean setInstances(Instances value) {
    boolean	mustReset;
    
    mustReset = (m_Data == null) || (m_Data.equalHeadersMsg(value) != null);
    m_Data = value;
    if (mustReset)
      reset();
    
    return mustReset;
  }

  /**
   * Returns the Instances to use.
   *
   * @return		the Instances in use, null if none set
   */
  public Instances getInstances() {
    return m_Data;
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
    InstancesSortDefinitionPanel panel;

    m_Panels.clear();
    panel = new InstancesSortDefinitionPanel(this);
    m_Panels.add(panel);

    update();
    notifyInstancesSortSetupListeners(new InstancesSortSetupEvent(this, null, EventType.RESET));
  }

  /**
   * Adds a new definition.
   */
  public void addDefinition() {
    InstancesSortDefinitionPanel panel;

    panel = new InstancesSortDefinitionPanel(this);
    m_Panels.add(panel);

    update();
    notifyInstancesSortSetupListeners(new InstancesSortSetupEvent(this, panel, EventType.ADD));
  }

  /**
   * Removes the panel from the list of sort definitions.
   *
   * @param panel	the panel to remove
   */
  public void removeDefinition(InstancesSortDefinitionPanel panel) {
    if (m_Panels.remove(panel)) {
      update();
      notifyInstancesSortSetupListeners(new InstancesSortSetupEvent(this, panel, EventType.REMOVE));
    }
  }

  /**
   * Removes all sort definition panels.
   */
  public void reset() {
    m_Panels.clear();
    m_ColumnNames.clear();
    if (m_Data != null) {
      for (int i = 0; i < m_Data.numAttributes(); i++)
	m_ColumnNames.add(m_Data.attribute(i).name());
    }
    update();
    notifyInstancesSortSetupListeners(new InstancesSortSetupEvent(this, null, EventType.RESET));
  }
  
  /**
   * Checks whether the panel is the first one.
   * 
   * @param panel	the panel to check
   * @return		true if the first one
   */
  public boolean isFirstDefinition(InstancesSortDefinitionPanel panel) {
    return (m_Panels.size() > 0) && (m_Panels.get(0) == panel);
  }
  
  /**
   * Checks whether the panel is the last one.
   * 
   * @param panel	the panel to check
   * @return		true if the last one
   */
  public boolean isLastDefinition(InstancesSortDefinitionPanel panel) {
    return (m_Panels.size() > 0) && (m_Panels.get(m_Panels.size() - 1) == panel);
  }
  
  /**
   * Moves the panel up/down.
   * 
   * @param panel	the panel to move
   * @param up		if true, gets moved up, otherwise down
   * @return		true if successfully moved
   */
  public boolean moveDefinition(InstancesSortDefinitionPanel panel, boolean up) {
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
    notifyInstancesSortSetupListeners(new InstancesSortSetupEvent(this, panel, EventType.MOVED));
    
    return result;
  }

  /**
   * Updates the display.
   */
  protected void update() {
    m_PanelDefinitions.removeAll();
    m_Layout.setRows(m_Panels.size());
    for (InstancesSortDefinitionPanel panel: m_Panels)
      m_PanelDefinitions.add(panel);
    revalidate();
    if (getParentDialog() != null)
      getParentDialog().pack();
    for (InstancesSortDefinitionPanel panel: m_Panels)
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

    names = new HashSet<>();
    for (InstancesSortDefinitionPanel panel: m_Panels) {
      if (names.contains(panel.getColumnName()))
	return false;
      names.add(panel.getColumnName());
    }

    return (names.size() > 0);
  }

  /**
   * Returns a comparator for sorting the Instances.
   *
   * @return		the comparator, null if not valid setup
   */
  public InstanceComparator getComparator() {
    InstanceComparator	result;
    int[]		indices;
    boolean[]		ascending;
    int			i;
    InstancesSortDefinitionPanel panel;

    if (!isValidSetup())
      return null;

    indices   = new int[m_Panels.size()];
    ascending = new boolean[m_Panels.size()];
    for (i = 0; i < m_Panels.size(); i++) {
      panel        = m_Panels.get(i);
      indices[i]   = m_ColumnNames.indexOf(panel.getColumnName());
      ascending[i] = panel.isAscending();
    }

    result = new InstanceComparator(indices, ascending);

    return result;
  }

  /**
   * Adds the specified listener.
   *
   * @param l		the listener to add
   */
  public void addInstancesSortSetupListener(InstancesSortSetupListener l) {
    m_InstancesSortSetupListeners.add(l);
  }

  /**
   * Removes the specified listener.
   *
   * @param l		the listener to remove
   */
  public void removeInstancesSortSetupListener(InstancesSortSetupListener l) {
    m_InstancesSortSetupListeners.remove(l);
  }

  /**
   * Notifies all listeners with the specified event.
   *
   * @param e		the event to send
   */
  public void notifyInstancesSortSetupListeners(InstancesSortSetupEvent e) {
    for (InstancesSortSetupListener l: m_InstancesSortSetupListeners)
      l.sortSetupChanged(e);
  }
}
