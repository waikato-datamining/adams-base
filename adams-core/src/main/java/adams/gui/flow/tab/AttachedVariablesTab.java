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
 * AttachedVariablesTab.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tab;

import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.processor.ListAllVariables;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTableWithButtons;
import adams.gui.core.ImageManager;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.tree.Node;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;

/**
 * Allows user to list variables and their values attached to the selected actor.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AttachedVariablesTab
  extends AbstractTabChangeAwareEditorTab
  implements SelectionAwareEditorTab {

  private static final long serialVersionUID = 1745841596971673114L;

  /** the button for refreshing the variable list. */
  protected BaseButton m_ButtonRefresh;

  /** for listing all the variables. */
  protected BaseTableWithButtons m_TableVariables;

  /** the model for the variables. */
  protected DefaultTableModel m_ModelVariables;

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Attached variables";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ModelVariables = newModel();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    // table
    m_ButtonRefresh = new BaseButton("Refresh", ImageManager.getIcon("refresh.gif"));
    m_ButtonRefresh.addActionListener(e -> refreshVariables());

    m_TableVariables = new BaseTableWithButtons(m_ModelVariables);
    m_TableVariables.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_TableVariables.addToButtonsPanel(m_ButtonRefresh);

    add(new BaseScrollPane(m_TableVariables), BorderLayout.CENTER);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    clear();
    updateButtons();
  }

  /**
   * Returns an empty table model.
   *
   * @return		the model
   */
  protected DefaultTableModel newModel() {
    return new DefaultTableModel(new String[0][2], new String[]{"Name", "Value"});
  }

  /**
   * Clears all the content.
   */
  protected void clear() {
    m_ModelVariables = newModel();
    m_TableVariables.setModel(m_ModelVariables);
    m_TableVariables.setOptimalColumnWidth();
  }

  /**
   * Updates the enabled state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonRefresh.setEnabled(
      (getCurrentPanel() != null)
	&& (getCurrentPanel().getRunningFlow() != null));
  }

  /**
   * Refreshes the variables.
   */
  protected void refreshVariables() {
    ListAllVariables	list;
    Node 		selNode;
    Actor 		runningFlow;
    Actor		runningActor;

    clear();

    if (getCurrentPanel() == null)
      return;
    if (getCurrentPanel().getTree().getSelectedActors().length != 1)
      return;
    runningFlow = getCurrentPanel().getRunningFlow();
    if (runningFlow == null)
      return;

    selNode      = getCurrentPanel().getTree().getSelectedNode();
    runningActor = ActorUtils.locate(selNode.getFullName(), runningFlow, true, true);
    if (runningActor == null)
      return;

    list = new ListAllVariables();
    list.process(runningActor);
    for (String item : list.getVariables())
      m_ModelVariables.addRow(new String[]{item, runningActor.getVariables().get(item)});

    m_TableVariables.setOptimalColumnWidth();
  }

  /**
   * Notifies the tab of the currently selected flow panel.
   *
   * @param panel	the new panel
   */
  @Override
  public void flowPanelChanged(FlowPanel panel) {
    updateButtons();
    refreshVariables();
  }

  /**
   * Notifies the tab of the currently selected actors.
   *
   * @param paths	the selected paths
   * @param actors	the currently selected actors
   */
  @Override
  public void actorSelectionChanged(TreePath[] paths, Actor[] actors) {
    updateButtons();
    refreshVariables();
  }
}
