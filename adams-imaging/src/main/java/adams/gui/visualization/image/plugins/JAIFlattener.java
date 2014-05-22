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
 * JAIFlattener.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import weka.core.Instance;
import adams.data.image.BufferedImageContainer;
import adams.data.jai.flattener.AbstractJAIFlattener;
import adams.data.jai.flattener.Histogram;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.goe.GenericObjectEditor;
import adams.gui.goe.GenericObjectEditor.GOEPanel;

/**
 * Allows the user to apply a JAI flattner to an image in the ImageViewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7713 $
 */
public class JAIFlattener
  extends AbstractSelectedImagesFlattener {

  /** for serialization. */
  private static final long serialVersionUID = -3146372359577147914L;

  /** the GOE editor with the transformer. */
  protected GenericObjectEditor m_Editor;

  /**
   * Returns the text for the menu item to create.
   *
   * @return		the text
   */
  @Override
  public String getCaption() {
    return "JAI flattener...";
  }
  
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
   * Creates the panel with the configuration (return null to suppress display).
   * 
   * @return		the generated panel, null to suppress
   */
  @Override
  protected JPanel createConfigurationPanel(final ApprovalDialog dialog) {
    JPanel	result;
    
    m_Editor = new GenericObjectEditor();
    m_Editor.setClassType(AbstractJAIFlattener.class);
    m_Editor.setCanChangeClassInDialog(true);
    if (hasLastSetup())
      m_Editor.setValue(getLastSetup());
    else
      m_Editor.setValue(new Histogram());
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
   * Filters the image.
   *
   * @param image	the image to filter
   * @return		the generated instances
   */
  @Override
  protected Instance[] flatten(BufferedImage image) {
    weka.core.Instance[]	result;
    AbstractJAIFlattener	flattener;
    BufferedImageContainer	input;
    weka.core.Instance[]	flattened;

    result = null;

    setLastSetup(m_Editor.getValue());
    flattener = (AbstractJAIFlattener) m_Editor.getValue();
    input       = new BufferedImageContainer();
    input.setImage(image);
    flattened = flattener.flatten(input);
    if (flattened.length == 0)
      m_FilterError = "No instances generated!";
    if (flattened.length > 0)
      result = flattened;

    return result;
  }
}
