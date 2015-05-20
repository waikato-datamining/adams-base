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
 * AbstractSpreadSheetViewerCheckBoxMenuItemAction.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.menu;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import adams.core.Properties;
import adams.gui.action.AbstractPropertiesCheckBoxMenuItemAction;
import adams.gui.application.Child;
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.tools.SpreadSheetViewerPanel;
import adams.gui.tools.spreadsheetviewer.TabbedPane;

/**
 * Ancestor for checkbox menu item actions in the spreadsheet viewer.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSpreadSheetViewerCheckBoxMenuItemAction
  extends AbstractPropertiesCheckBoxMenuItemAction<SpreadSheetViewerPanel, GenericObjectEditorDialog>
  implements SpreadSheetViewerAction {
  
  /** for serialization. */
  private static final long serialVersionUID = -6842831257705457783L;
  
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
   * Returns the tabbed pane of the viewer.
   * 
   * @return		the tabbed pane
   */
  protected TabbedPane getTabbedPane() {
    return m_State.getTabbedPane();
  }
  
  /**
   * Returns whether a sheet is selected.
   * 
   * @return		true if selected
   */
  protected boolean isSheetSelected() {
    return (getTabbedPane().getTabCount() > 0) && (getTabbedPane().getSelectedIndex() != -1);
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
