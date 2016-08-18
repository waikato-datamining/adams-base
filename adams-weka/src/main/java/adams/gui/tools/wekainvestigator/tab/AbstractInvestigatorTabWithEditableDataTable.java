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
 * AbstractInvestigatorTabWithEditableDataTable.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.logging.LoggingLevel;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.tools.wekainvestigator.datatable.action.AbstractEditableDataTableAction;
import adams.gui.tools.wekainvestigator.datatable.action.Export;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideSplitButton;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for tabs with modifiable data table.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInvestigatorTabWithEditableDataTable
  extends AbstractInvestigatorTabWithDataTable {

  private static final long serialVersionUID = -94945456385486233L;

  /** the button for removing a dataset. */
  protected JideButton m_ButtonRemove;

  /** the action button. */
  protected JideSplitButton m_ButtonAction;

  /** the up button. */
  protected JideButton m_ButtonUp;

  /** the down button. */
  protected JideButton m_ButtonDown;

  /** the available actions. */
  protected List<AbstractEditableDataTableAction> m_Actions;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    Class[]			classes;
    AbstractEditableDataTableAction action;

    super.initialize();

    m_Owner          = null;
    m_Actions        = new ArrayList<>();
    classes          = AbstractEditableDataTableAction.getActions();
    for (Class cls: classes) {
      try {
	action = (AbstractEditableDataTableAction) cls.newInstance();
	action.setOwner(this);
	m_Actions.add(action);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, "Failed to instantiate action: " + cls.getName(), e);
      }
    }
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    m_ButtonRemove = new JideButton("Remove", GUIHelper.getIcon("delete.gif"));
    m_ButtonRemove.setButtonStyle(JideButton.TOOLBOX_STYLE);
    m_ButtonRemove.addActionListener((ActionEvent e) -> removeData(m_Table.getSelectedRows()));
    m_Table.addToButtonsPanel(m_ButtonRemove);

    m_ButtonAction = new JideSplitButton();
    m_ButtonAction.setAlwaysDropdown(false);
    m_ButtonAction.setButtonEnabled(true);
    m_ButtonAction.setButtonStyle(JideSplitButton.TOOLBOX_STYLE);
    for (AbstractEditableDataTableAction action: m_Actions) {
      if (action instanceof Export)
	m_ButtonAction.setAction(action);
      else
	m_ButtonAction.add(action);
    }
    m_Table.addToButtonsPanel(m_ButtonAction);

    panel = new JPanel(new GridLayout(1, 2));
    m_ButtonUp = new JideButton(GUIHelper.getIcon("arrow_up.gif"));
    m_ButtonUp.setButtonStyle(JideButton.TOOLBOX_STYLE);
    m_ButtonUp.addActionListener((ActionEvent e) -> {
      final int indices[] = m_Model.moveUp(getSelectedRows());
      SwingUtilities.invokeLater(() -> m_Table.setSelectedRows(indices));
    });
    m_ButtonDown = new JideButton(GUIHelper.getIcon("arrow_down.gif"));
    m_ButtonDown.setButtonStyle(JideButton.TOOLBOX_STYLE);
    m_ButtonDown.addActionListener((ActionEvent e) -> {
      final int[] indices = m_Model.moveDown(getSelectedRows());
      SwingUtilities.invokeLater(() -> m_Table.setSelectedRows(indices));
    });
    panel.add(m_ButtonUp);
    panel.add(m_ButtonDown);
    m_Table.addToButtonsPanel(panel);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateButtons();
  }

  /**
   * Returns whether a readonly table is used.
   *
   * @return		true if readonly
   */
  protected boolean hasReadOnlyTable() {
    return false;
  }

  /**
   * Gets called when the user changes the selection.
   */
  protected void dataTableSelectionChanged() {
    updateButtons();
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonRemove.setEnabled(m_Table.getSelectedRowCount() > 0);
    for (AbstractEditableDataTableAction action: m_Actions)
      action.update();
    m_ButtonUp.setEnabled(m_Model.canMoveUp(getSelectedRows()));
    m_ButtonDown.setEnabled(m_Model.canMoveDown(getSelectedRows()));
  }
}
