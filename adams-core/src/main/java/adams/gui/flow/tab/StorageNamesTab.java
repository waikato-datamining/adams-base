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
 * StorageNamesTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tab;

import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.processor.ListAllStorageNames;
import adams.flow.processor.ListStorageUsage;
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
 * Allows user to list storage names in flow.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StorageNamesTab
  extends AbstractTabChangeAwareEditorTab {

  private static final long serialVersionUID = 1745841596971673114L;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the button for refreshing the variable list. */
  protected JButton m_ButtonRefresh;

  /** the button for locating the usages. */
  protected JButton m_ButtonUsages;

  /** for listing all the names. */
  protected BaseListWithButtons m_ListStorageNames;

  /** the model for the names. */
  protected DefaultListModel<String> m_ModelStorageNames;

  /** the panel for the usages. */
  protected JPanel m_PanelUsages;

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Storage names";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ModelStorageNames = new DefaultListModel<>();
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
    m_ButtonRefresh.addActionListener(e -> refreshStorageNames());
    m_ButtonUsages = new JButton("Usages", GUIHelper.getIcon("glasses.gif"));
    m_ButtonUsages.addActionListener(e -> findUsages());

    m_ListStorageNames = new BaseListWithButtons(m_ModelStorageNames);
    m_ListStorageNames.addToButtonsPanel(m_ButtonRefresh);
    m_ListStorageNames.addToButtonsPanel(m_ButtonUsages);
    m_ListStorageNames.addListSelectionListener(e -> updateButtons());
    m_ListStorageNames.setDoubleClickButton(m_ButtonUsages);

    panel.add(new BaseScrollPane(m_ListStorageNames), BorderLayout.CENTER);

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
   * Clears all the content.
   */
  protected void clear() {
    m_ModelStorageNames.clear();
    m_SplitPane.setBottomComponentHidden(true);
    m_PanelUsages.removeAll();
  }

  /**
   * Refreshes the storage names.
   */
  protected void refreshStorageNames() {
    ListAllStorageNames list;

    clear();
    list = new ListAllStorageNames();
    list.process(getCurrentPanel().getCurrentFlow());
    for (String item: list.getStorageNames())
      m_ModelStorageNames.addElement(item);
  }

  /**
   * Updates the usage of the currently selected item.
   */
  protected void findUsages() {
    ListStorageUsage 	list;
    Actor 		actor;

    actor = getCurrentPanel().getCurrentFlow();
    if (actor instanceof Flow)
      ((Flow) actor).setParentComponent(getCurrentPanel());
    list = new ListStorageUsage();
    list.setName("" + m_ListStorageNames.getSelectedValue());
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
    m_ButtonUsages.setEnabled(m_ListStorageNames.getSelectedIndices().length == 1);
  }

  /**
   * Notifies the tab of the currently selected flow panel.
   *
   * @param panel	the new panel
   */
  @Override
  public void flowPanelChanged(FlowPanel panel) {
    clear();
  }
}
