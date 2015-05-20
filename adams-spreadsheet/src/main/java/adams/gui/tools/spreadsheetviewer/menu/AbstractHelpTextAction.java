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
 * AbstractHelpTextAction.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.menu;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import adams.core.Properties;
import adams.gui.action.AbstractPropertiesMenuItemAction;
import adams.gui.application.Child;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.TextDialog;
import adams.gui.tools.SpreadSheetViewerPanel;

/**
 * Ancestor for help dialogs that display simple text content.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractHelpTextAction
  extends AbstractPropertiesMenuItemAction<SpreadSheetViewerPanel, TextDialog>
  implements SpreadSheetViewerAction {
  
  /** for serialization. */
  private static final long serialVersionUID = -4567753721410600783L;

  /**
   * Returns the underlying properties.
   * 
   * @return		the properties
   */
  @Override
  protected Properties getProperties() {
    return SpreadSheetViewerPanel.getPropertiesMenu();
  }

  /**
   * Tries to determine the parent frame.
   *
   * @return		the parent frame if one exists or null if not
   */
  protected Frame getParentFrame() {
    return GUIHelper.getParentFrame(m_State);
  }

  /**
   * Tries to determine the parent dialog.
   *
   * @return		the parent dialog if one exists or null if not
   */
  protected Dialog getParentDialog() {
    return GUIHelper.getParentDialog(m_State);
  }

  /**
   * Tries to determine the parent child window/frame.
   *
   * @return		the parent child window/frame if one exists or null if not
   */
  protected Child getParentChild() {
    return GUIHelper.getParentChild(m_State);
  }

  /**
   * Returns the help text to display.
   * 
   * @return		the help content
   */
  protected abstract String getHelpContent();
  
  /**
   * Creates a new dialog.
   * 
   * @return		the dialog
   */
  @Override
  protected TextDialog createDialog() {
    TextDialog 	result;
    
    if (getParentDialog() != null)
      result = new TextDialog(getParentDialog());
    else
      result = new TextDialog(getParentFrame());
    result.setDialogTitle(getTitle().replaceAll("[.]*$", ""));
    result.setContent(getHelpContent());
    result.setEditable(false);
    result.setDefaultCloseOperation(TextDialog.HIDE_ON_CLOSE);
    result.setSize(800, 600);
    GUIHelper.setSizeAndLocation(result);
    result.setLocationRelativeTo(m_State);
    
    return result;
  }
  
  /**
   * Invoked when an action occurs.
   * 
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    getDialog().setVisible(true);
  }
  
  /**
   * Invoked when an action occurs (hook method after executing the actual action code).
   * <br><br>
   * Updates the menu.
   * 
   * @param e		the event
   */
  @Override
  protected void postActionPerformed(ActionEvent e) {
    super.postActionPerformed(e);
    m_State.updateMenu();
  }
}
