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
 * ShowTriggers.java
 * Copyright (C) 2012-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.flow.core.ActorUtils;
import adams.flow.core.EventHelper;
import adams.gui.action.AbstractBaseAction;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTableWithButtons;
import adams.gui.core.GUIHelper;
import adams.gui.core.KeyValuePairTableModel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.TextDialog;
import adams.gui.flow.FlowEditorPanel;
import adams.gui.flow.FlowPanel;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Shows a dialog that lists all currently active triggers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ShowTriggers
  extends AbstractFlowEditorMenuItem {

  /** for serialization. */
  private static final long serialVersionUID = 4736251620576615831L;

  /**
   * Returns the name of the menu to list this item under.
   * 
   * @return		the name of the menu
   */
  @Override
  public String getMenu() {
    return FlowEditorPanel.MENU_VIEW;
  }

  /**
   * Returns a table model with the current triggers.
   * 
   * @return		the table model, null in case of an error
   */
  protected KeyValuePairTableModel getTabelModel() {
    KeyValuePairTableModel	result;
    List<String>		groups;
    Set<TriggerKey> 		triggers;
    List<String>		list;
    String[][]			data;
    FlowPanel 			panel;
    int				i;
    int				n;
    int				id;
    
    result = null;
    list   = new ArrayList<>();

    for (n = 0; n < getOwner().getFlowPanels().getPanelCount(); n++) {
      panel = getOwner().getFlowPanels().getPanelAt(n);
      if (!panel.isRunning())
        continue;
      id = ActorUtils.getFlowID(panel.getLastFlow());
      if (id == -1)
        continue;

      try {
	groups = EventHelper.getDefaultScheduler(id).getTriggerGroupNames();
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to get trigger group names:", e);
	return result;
      }

      for (String group: groups) {
	try {
	  triggers = EventHelper.getDefaultScheduler(id).getTriggerKeys(GroupMatcher.triggerGroupEquals(group));
	  for (TriggerKey key: triggers)
	    list.add(group + "\t" + key.getName());
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to get trigger names for group '" + group + "':", e);
	}
      }
    }

    data = new String[list.size()][2];
    for (i = 0; i < list.size(); i++)
      data[i] = list.get(i).split("\t");

    return new KeyValuePairTableModel(data, new String[]{"Group", "Trigger"});
  }
  
  /**
   * Creates the action to use.
   * 
   * @return		the action
   */
  @Override
  protected AbstractBaseAction newAction() {
    return new AbstractBaseAction("Show triggers") {
      private static final long serialVersionUID = 868738932723881336L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	final BaseTableWithButtons table = new BaseTableWithButtons(getTabelModel());
	final BaseButton viewButton = new BaseButton("View");
	viewButton.setMnemonic('V');
	viewButton.setEnabled(false);
	viewButton.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    int row = table.getSelectedRow();
	    if (row == -1)
	      return;
	    TextDialog editor = new TextDialog();
	    editor.setEditable(false);
	    editor.setContent("Group: " + table.getValueAt(row, 0) + "\n" + "Trigger: " + table.getValueAt(row, 1));
	    editor.setSize(GUIHelper.getDefaultTinyDialogDimension());
	    editor.setLocationRelativeTo(table);
	    editor.setVisible(true);
	  }
	});
	table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
	table.setOptimalColumnWidth();
	table.addToButtonsPanel(viewButton);
	table.setDoubleClickButton(viewButton);
	table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
	  @Override
	  public void valueChanged(ListSelectionEvent e) {
	    viewButton.setEnabled(table.getSelectedRowCount() == 1);
	  }
	});
	ApprovalDialog dlg;
	if (getOwner().getParentDialog() != null)
	  dlg = ApprovalDialog.getInformationDialog(getOwner().getParentDialog(), ModalityType.MODELESS);
	else
	  dlg = ApprovalDialog.getInformationDialog(getOwner().getParentFrame(), false);
	dlg.setDefaultCloseOperation(BaseDialog.DISPOSE_ON_CLOSE);
	dlg.setTitle("Triggers");
	dlg.getContentPane().add(table);
	dlg.pack();
	dlg.setLocationRelativeTo(getOwner());
	dlg.setVisible(true);
      }
    };
  }

  /**
   * Updating the action, based on the current status of the owner.
   */
  @Override
  public void update() {
    m_Action.setEnabled(getOwner().isAnyRunning());
  }
}
