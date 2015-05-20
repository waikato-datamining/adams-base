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
 * AbstractSelectedImagesFilterWithGOE.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import adams.gui.dialog.ApprovalDialog;
import adams.gui.goe.GenericObjectEditor;
import adams.gui.goe.GenericObjectEditor.GOEPanel;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ancestor for image filters that process the selected images using
 * a setup from a {@link GenericObjectEditor}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSelectedImagesFilterWithGOE
  extends AbstractSelectedImagesFilter {
  
  /** for serialization. */
  private static final long serialVersionUID = 3705344456737400966L;
  
  /** the GOE editor with the transformer. */
  protected GenericObjectEditor m_Editor;
  
  /**
   * Returns whether the dialog has an approval button.
   * 
   * @return		true if approval button visible
   */
  @Override
  protected boolean hasApprovalButton() {
    return false;
  }
  
  /**
   * Returns whether the dialog has a cancel button.
   * 
   * @return		true if cancel button visible
   */
  @Override
  protected boolean hasCancelButton() {
    return false;
  }
  
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
   * Creates the panel with the configuration (return null to suppress display).
   * 
   * @return		the generated panel, null to suppress
   */
  @Override
  protected JPanel createConfigurationPanel(final ApprovalDialog dialog) {
    JPanel	result;
    
    m_Editor = new GenericObjectEditor();
    m_Editor.setClassType(getEditorType());
    m_Editor.setCanChangeClassInDialog(getCanChangeClassInDialog());
    if (hasLastSetup())
      m_Editor.setValue(getLastSetup());
    else
      m_Editor.setValue(getDefaultValue());
    result = new JPanel(new BorderLayout());
    result.add(m_Editor.getCustomEditor(), BorderLayout.CENTER);

    ((GOEPanel) m_Editor.getCustomEditor()).addOkListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	dialog.getApproveButton().doClick();
      }
    });
    ((GOEPanel) m_Editor.getCustomEditor()).addCancelListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	dialog.getCancelButton().doClick();
      }
    });
    
    return result;
  }

  /**
   * Initializes the processing.
   * <br><br>
   * Sets the last setup.
   *
   * @return		null if successful, otherwise error message
   * @see		#setLastSetup(Object)
   */
  @Override
  protected String processInit() {
    String	result;

    result = super.processInit();

    if (result == null)
      setLastSetup(m_Editor.getValue());

    return result;
  }
}
