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
 * VariablesTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tab;

import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.processor.ListAllVariables;
import adams.flow.processor.ListVariableUsage;
import adams.gui.core.BaseListWithButtons;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.GUIHelper;
import adams.gui.flow.FlowPanel;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Allows user to list variables in flow.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariablesTab
  extends AbstractTabChangeAwareEditorTab {

  private static final long serialVersionUID = 1745841596971673114L;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the button for refreshing the variable list. */
  protected JButton m_ButtonRefresh;

  /** the button for locating the usages. */
  protected JButton m_ButtonUsages;

  /** for listing all the variables. */
  protected BaseListWithButtons m_ListVariables;

  /** the model for the variables. */
  protected DefaultListModel<String> m_ModelVariables;

  /** the panel for the usages. */
  protected JPanel m_PanelUsages;

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Variables";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ModelVariables = new DefaultListModel<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel 	panel;

    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPane.setResizeWeight(0.5);
    add(m_SplitPane, BorderLayout.CENTER);

    panel = new JPanel(new BorderLayout());
    m_SplitPane.setTopComponent(panel);

    // list
    m_ButtonRefresh = new JButton("Refresh", GUIHelper.getIcon("refresh.gif"));
    m_ButtonRefresh.addActionListener(e -> refreshVariables());
    m_ButtonUsages = new JButton("Usages", GUIHelper.getIcon("glasses.gif"));
    m_ButtonUsages.addActionListener(e -> findUsages());

    m_ListVariables = new BaseListWithButtons(m_ModelVariables);
    m_ListVariables.addToButtonsPanel(m_ButtonRefresh);
    m_ListVariables.addToButtonsPanel(m_ButtonUsages);
    m_ListVariables.addListSelectionListener(e -> updateButtons());
    m_ListVariables.setDoubleClickButton(m_ButtonUsages);

    panel.add(new BaseScrollPane(m_ListVariables), BorderLayout.CENTER);

    // usages
    m_PanelUsages = new JPanel(new BorderLayout());
    m_SplitPane.setBottomComponent(m_PanelUsages);
    m_SplitPane.setBottomComponentHidden(true);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateButtons();
  }

  /**
   * Refreshes the variables.
   */
  protected void refreshVariables() {
    ListAllVariables	list;

    list = new ListAllVariables();
    list.process(getCurrentPanel().getCurrentFlow());
    m_ModelVariables.clear();
    for (String item: list.getVariables())
      m_ModelVariables.addElement(item);
  }

  /**
   * Updates the usage of the currently selected item.
   */
  protected void findUsages() {
    ListVariableUsage	list;
    AbstractActor 	actor;

    actor = getCurrentPanel().getCurrentFlow();
    if (actor instanceof Flow)
      ((Flow) actor).setParentComponent(getCurrentPanel());
    list = new ListVariableUsage();
    list.setName("" + m_ListVariables.getSelectedValue());
    list.process(actor);

    m_PanelUsages.removeAll();
    if (list.hasGraphicalOutput()) {
      m_PanelUsages.add(list.getGraphicalOutput());
      m_SplitPane.setBottomComponentHidden(false);
    }
    else {
      m_SplitPane.setBottomComponentHidden(true);
    }
    invalidate();
    revalidate();
    doLayout();
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonRefresh.setEnabled(true);
    m_ButtonUsages.setEnabled(m_ListVariables.getSelectedIndices().length == 1);
  }

  /**
   * Notifies the tab of the currently selected flow panel.
   *
   * @param panel	the new panel
   */
  @Override
  public void flowPanelChanged(FlowPanel panel) {
    m_ModelVariables.clear();
  }
}
