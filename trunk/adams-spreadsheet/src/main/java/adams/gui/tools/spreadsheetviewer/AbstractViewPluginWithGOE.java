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
 * AbstractViewPluginWithGOE.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer;

import java.awt.Dialog.ModalityType;

import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BasePanel;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * Ancestor for plugins that present a GOE dialog.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractViewPluginWithGOE
  extends AbstractViewPlugin {

  /** for serialization. */
  private static final long serialVersionUID = 6346501571908274956L;

  /**
   * Returns the class to use as type (= superclass) in the GOE.
   * 
   * @return		the class
   */
  protected abstract Class getEditorType();
  
  /**
   * Returns the default object to use in the GOE if no last setup is yet
   * available.
   * 
   * @return		the object
   */
  protected abstract Object getDefaultValue();

  /**
   * Returns whether the class can be changed in the GOE.
   * 
   * @return		true if class can be changed by the user
   */
  protected boolean getCanChangeClassInDialog() {
    return true;
  }
  
  /**
   * Performs the actual generation of the information.
   * 
   * @param sheet	the sheet to process
   * @return		the generated information panel
   */
  protected abstract BasePanel generatePanel(SpreadSheet sheet);
  
  /**
   * Interacts with the user.
   * 
   * @param sheet	the sheet to process
   * @return		the generated information panel
   */
  @Override
  protected BasePanel doGenerate(SpreadSheet sheet) {
    BasePanel			result;
    GenericObjectEditorDialog	dialog;

    result = null;
    if (m_CurrentPanel.getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentDialog());
    else
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentFrame());
    dialog.getGOEEditor().setClassType(getEditorType());
    dialog.getGOEEditor().setCanChangeClassInDialog(getCanChangeClassInDialog());
    if (hasLastSetup())
      dialog.setCurrent(getLastSetup());
    else
      dialog.setCurrent(getDefaultValue());
    dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
    dialog.setLocationRelativeTo(m_CurrentPanel);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION) {
      m_CanceledByUser = true;
      return result;
    }

    setLastSetup(dialog.getCurrent());
    
    result = generatePanel(sheet);

    return result;
  }
}
