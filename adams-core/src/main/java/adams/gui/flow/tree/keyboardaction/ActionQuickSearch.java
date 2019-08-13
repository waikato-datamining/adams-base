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
 * ActionQuickSearch.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.keyboardaction;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.gui.action.AbstractBaseAction;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseShortcut;
import adams.gui.core.ClassQuickSearchPanel;
import adams.gui.core.GUIHelper;
import adams.gui.flow.menu.AbstractFlowEditorMenuItem;
import adams.gui.flow.menu.FlowEditorAction;
import adams.gui.flow.tree.StateContainer;
import adams.gui.flow.tree.menu.TreePopupAction;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Allows the user to search for actions in menu items for editor and tree popup.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ActionQuickSearch
  extends AbstractKeyboardAction {

  private static final long serialVersionUID = 8171584749198453214L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to search for actions in menu items for editor and tree popup.";
  }

  /**
   * Returns the default shortcut of the action.
   *
   * @return 		the default
   */
  @Override
  protected BaseShortcut getDefaultShortcut() {
    return new BaseShortcut("ctrl SPACE");
  }

  /**
   * Performs the actual execution of the action.
   *
   * @param state	the current state
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String doExecute(StateContainer state) {
    final BaseDialog		dialog;
    final ClassQuickSearchPanel	panel;
    List<Class> 		classes;

    if (state.tree.getParentDialog() != null)
      dialog = new BaseDialog(state.tree.getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new BaseDialog(state.tree.getParentFrame(), true);

    classes = new ArrayList<>();
    classes.addAll(Arrays.asList(ClassLister.getSingleton().getClasses(TreePopupAction.class)));
    classes.addAll(Arrays.asList(ClassLister.getSingleton().getClasses(FlowEditorAction.class)));
    classes.addAll(Arrays.asList(ClassLister.getSingleton().getClasses(AbstractFlowEditorMenuItem.class)));
    Collections.sort(classes, new Comparator<Class>() {
      @Override
      public int compare(Class o1, Class o2) {
	return o1.getName().compareTo(o2.getName());
      }
    });
    panel = new ClassQuickSearchPanel();
    panel.setClasses(classes.toArray(new Class[0]));
    panel.addCancelListener((ChangeEvent e) -> dialog.setVisible(false));
    panel.addSelectionListener((ListSelectionEvent e) -> {
      Class cls = panel.getSelectedClass();
      if (cls == null)
        return;

      // tree popup
      if (ClassLocator.matches(TreePopupAction.class, cls)) {
        try {
	  TreePopupAction action = (TreePopupAction) cls.newInstance();
	  action.update(state);
	  if (action.isEnabled()) {
	    dialog.setVisible(false);
	    action.actionPerformed(null);
	  }
	  else {
	    GUIHelper.showErrorMessage(
	      state.tree.getParent(),
	      "TreePopup action is not enabled for current state: " + Utils.classToString(cls));
	  }
	}
	catch (Exception ex) {
	  GUIHelper.showErrorMessage(
	    state.tree.getParent(),
	    "Failed to instantiate/execute TreePopup action class: " + Utils.classToString(cls));
	}
      }
      // floweditor action
      else if (ClassLocator.matches(FlowEditorAction.class, cls)) {
        try {
	  FlowEditorAction action = (FlowEditorAction) cls.newInstance();
	  action.update(state.tree.getEditor());
	  if (action.isEnabled()) {
	    dialog.setVisible(false);
	    action.actionPerformed(null);
	  }
	  else {
	    GUIHelper.showErrorMessage(
	      state.tree.getParent(),
	      "Flow editor action is not enabled for current state: " + Utils.classToString(cls));
	  }
	}
	catch (Exception ex) {
	  GUIHelper.showErrorMessage(
	    state.tree.getParent(),
	    "Failed to instantiate/execute menu item class: " + Utils.classToString(cls));
	}
      }
      // floweditor action
      else if (ClassLocator.matches(AbstractFlowEditorMenuItem.class, cls)) {
        try {
	  AbstractFlowEditorMenuItem menuitem = (AbstractFlowEditorMenuItem) cls.newInstance();
	  menuitem.setOwner(state.tree.getOwner().getEditor());
	  menuitem.updateAction();
	  AbstractBaseAction action = menuitem.getAction();
	  if (action.isEnabled()) {
	    dialog.setVisible(false);
	    action.actionPerformed(null);
	  }
	  else {
	    GUIHelper.showErrorMessage(
	      state.tree.getParent(),
	      "Menu item is not enabled for current state: " + Utils.classToString(cls));
	  }
	}
	catch (Exception ex) {
	  GUIHelper.showErrorMessage(
	    state.tree.getParent(),
	    "Failed to instantiate/execute menu item class: " + Utils.classToString(cls));
	}
      }
      // unknown
      else {
        GUIHelper.showErrorMessage(
          state.tree.getParent(),
	  "Unhandled quick action class: " + Utils.classToString(cls));
      }
    });

    dialog.setDefaultCloseOperation(BaseDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Action quick search");
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setSize(GUIHelper.getDefaultSmallDialogDimension());
    dialog.setLocationRelativeTo(state.tree.getParent());
    dialog.setVisible(true);
    return null;
  }
}
