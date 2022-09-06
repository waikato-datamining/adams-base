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
 * ReorderAttributes.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.preprocesstab.attributeselaction;

import adams.gui.core.BaseButton;
import adams.gui.core.BaseListWithButtons;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Reorder;

import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows the user to reorder the attributes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ReorderAttributes
  extends AbstractSelectedAttributesAction {

  private static final long serialVersionUID = -6999154336311879167L;

  /**
   * Instantiates the action.
   */
  public ReorderAttributes() {
    super();
    setName("Reorder attributes");
    setIcon("sort-ascending.png");
    setAsynchronous(true);
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(
      (getOwner() != null)
	&& !isBusy()
	&& (getSelectedRows().length == 1)
	&& m_Owner.canStartExecution());
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    DataContainer 		cont;
    Instances 			data;
    Instances			filtered;
    DefaultListModel<String>	model;
    int				i;
    BaseListWithButtons		list;
    final BaseButton		buttonUp;
    final BaseButton		buttonDown;
    ApprovalDialog		dialog;
    Map<String,Integer> 	indices;
    StringBuilder		indicesStr;
    Reorder			reorder;
    WekaInvestigatorDataEvent	event;

    cont     = getSelectedData()[0];
    data     = cont.getData();
    model    = new DefaultListModel<>();
    for (i = 0; i < data.numAttributes(); i++)
      model.addElement(data.attribute(i).name());
    list       = new BaseListWithButtons(model);
    buttonUp   = new BaseButton(ImageManager.getIcon("arrow_up.gif"));
    buttonUp.addActionListener((ActionEvent ae) -> list.moveUp());
    list.addToButtonsPanel(buttonUp);
    buttonDown = new BaseButton(ImageManager.getIcon("arrow_down.gif"));
    buttonDown.addActionListener((ActionEvent ae) -> list.moveDown());
    list.addToButtonsPanel(buttonDown);
    list.addListSelectionListener((ListSelectionEvent ae) -> {
      buttonUp.setEnabled(list.canMoveUp());
      buttonDown.setEnabled(list.canMoveDown());
    });
    if (getOwner().getParentDialog() != null)
      dialog = new ApprovalDialog(getOwner().getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(getOwner().getParentFrame(), true);
    dialog.setTitle(getName());
    dialog.getContentPane().add(list, BorderLayout.CENTER);
    dialog.setSize(GUIHelper.getDefaultSmallDialogDimension());
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    // build attribute index string
    indices = new HashMap<>();
    for (i = 0; i < data.numAttributes(); i++)
      indices.put(data.attribute(i).name(), i+1);
    indicesStr = new StringBuilder();
    for (i = 0; i < model.getSize(); i++) {
      if (i > 0)
        indicesStr.append(",");
      indicesStr.append(indices.get(model.get(i)));
    }

    // filter data
    try {
      reorder = new Reorder();
      reorder.setAttributeIndices(indicesStr.toString());
      reorder.setInputFormat(data);
      filtered = Filter.useFilter(data, reorder);
      cont.addUndoPoint("Reordering attributes");
      cont.setData(filtered);
      event = new WekaInvestigatorDataEvent(
	getOwner().getOwner(),
	WekaInvestigatorDataEvent.ROWS_MODIFIED,
	new int[]{getSelectedRows()[0]});
      getOwner().fireDataChange(event);
    }
    catch (Exception ex) {
      logError("Failed to reorder attributes!", ex, "Reorder failed");
    }
  }
}
