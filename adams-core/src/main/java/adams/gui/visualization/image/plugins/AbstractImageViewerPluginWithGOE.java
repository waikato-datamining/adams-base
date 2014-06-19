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
 * AbstractImageViewerPluginWithGOE.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import java.awt.Dialog.ModalityType;

import adams.gui.goe.GenericObjectEditorDialog;

/**
 * Ancestor for plugins that require a GOE.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractImageViewerPluginWithGOE
  extends AbstractImageViewerPlugin {

  /** for serialization. */
  private static final long serialVersionUID = 4879131668314193846L;

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
   * Processes the image.
   */
  protected abstract String process();
  
  /**
   * Executes the plugin.
   *
   * @return		null if OK, otherwise error message. Using an empty 
   * 			string will suppress the error message display and
   * 			the creation of a log entry.
   */
  @Override
  protected String doExecute() {
    String			result;
    GenericObjectEditorDialog	dialog;

    result = null;
    if (m_CurrentPanel.getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentDialog());
    else
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentFrame());
    dialog.getGOEEditor().setClassType(getEditorType());
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
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
    result = process();
    
    return result;
  }

}
