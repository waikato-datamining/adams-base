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
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.ListHelper;
import adams.core.logging.LoggingLevel;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseSplitButton;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ImageManager;
import adams.gui.event.UndoEvent;
import adams.gui.event.UndoListener;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.datatable.action.AbstractEditableDataTableAction;
import adams.gui.tools.wekainvestigator.datatable.action.Save;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for tabs with modifiable data table.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInvestigatorTabWithEditableDataTable
  extends AbstractInvestigatorTabWithDataTable
  implements UndoListener {

  private static final long serialVersionUID = -94945456385486233L;

  /** the button for activating a dataset. */
  protected BaseFlatButton m_ButtonActivate;

  /** the button for removing a dataset. */
  protected BaseFlatButton m_ButtonRemove;

  /** the save button. */
  protected BaseFlatButton m_ButtonSave;

  /** the action button. */
  protected BaseSplitButton m_ButtonAction;

  /** the up button. */
  protected BaseFlatButton m_ButtonUp;

  /** the down button. */
  protected BaseFlatButton m_ButtonDown;

  /** the button for undoing changes. */
  protected BaseFlatButton m_ButtonUndo;

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

    m_ButtonActivate = new BaseFlatButton("Activate", ImageManager.getIcon("location.png"));
    m_ButtonActivate.addActionListener((ActionEvent e) -> activate(m_Table.getSelectedRow()));
    m_Table.addToButtonsPanel(m_ButtonActivate);

    m_ButtonRemove = new BaseFlatButton("Remove", ImageManager.getIcon("delete.gif"));
    m_ButtonRemove.addActionListener((ActionEvent e) -> removeData(m_Table.getSelectedRows()));
    m_Table.addToButtonsPanel(m_ButtonRemove);

    m_ButtonUndo = new BaseFlatButton("Undo", ImageManager.getIcon("undo.gif"));
    m_ButtonUndo.addActionListener((ActionEvent e) -> undo(m_Table.getSelectedRows()));
    m_Table.addToButtonsPanel(m_ButtonUndo);

    m_ButtonSave = new BaseFlatButton("Save", ImageManager.getIcon("save.gif"));
    m_Table.addToButtonsPanel(m_ButtonSave);

    m_ButtonAction = new BaseSplitButton("...", ImageManager.getEmptyIcon());
    m_ButtonAction.setButtonEnabled(true);
    for (AbstractEditableDataTableAction action: m_Actions) {
      if (action instanceof Save)
	m_ButtonSave.setAction(action);
      else
	m_ButtonAction.add(action);
    }
    m_Table.addToButtonsPanel(m_ButtonAction);

    panel = new JPanel(new GridLayout(1, 2));
    m_ButtonUp = new BaseFlatButton(ImageManager.getIcon("arrow_up.gif"));
    m_ButtonUp.addActionListener((ActionEvent e) -> {
      ListHelper.moveUp(getData(), getSelectedRows());
      fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.TABLE_CHANGED));
    });
    m_ButtonDown = new BaseFlatButton(ImageManager.getIcon("arrow_down.gif"));
    m_ButtonDown.addActionListener((ActionEvent e) -> {
      ListHelper.moveDown(getData(), getSelectedRows());
      fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.TABLE_CHANGED));
    });
    panel.add(m_ButtonUp);
    panel.add(m_ButtonDown);
    m_Table.addToButtonsPanel(panel);

    m_Table.getComponent().addCellPopupMenuListener((MouseEvent e) -> showDataTablePopup(e));
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
   * Notifies the tab that the data changed.
   *
   * @param e		the event
   */
  public void dataChanged(WekaInvestigatorDataEvent e) {
    super.dataChanged(e);
    for (DataContainer cont: getData())
      cont.getUndo().addUndoListener(this);
    updateButtons();
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
    int[] 	rows;
    boolean 	enabled;

    m_ButtonActivate.setEnabled(!isBusy() && m_Table.getSelectedRowCount() == 1);
    m_ButtonRemove.setEnabled(!isBusy() && m_Table.getSelectedRowCount() > 0);
    for (AbstractEditableDataTableAction action: m_Actions)
      action.update();
    m_ButtonUp.setEnabled(!isBusy() && m_Model.canMoveUp(getSelectedRows()));
    m_ButtonDown.setEnabled(!isBusy() && m_Model.canMoveDown(getSelectedRows()));

    rows = m_Table.getSelectedRows();
    enabled = false;
    for (int row: rows) {
      if (row < getData().size()) {
        DataContainer cont = getData().get(row);
        if (cont.isUndoSupported() && cont.getUndo().canUndo()) {
          enabled = true;
          break;
        }
      }
    }
    m_ButtonUndo.setEnabled(!isBusy() && enabled);
  }

  /**
   * An undo event, like add or remove, has occurred.
   *
   * @param e		the trigger event
   */
  public void undoOccurred(UndoEvent e) {
    updateButtons();
  }

  /**
   * Displays popup for table.
   *
   * @param e 		the event that triggered the popup
   */
  protected void showDataTablePopup(MouseEvent e) {
    BasePopupMenu	result;

    result = new BasePopupMenu();
    for (AbstractEditableDataTableAction action: m_Actions) {
      action.update();
      result.add(action);
    }
    result.show(e.getComponent(), e.getX(), e.getY());
  }
}
