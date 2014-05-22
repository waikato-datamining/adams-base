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
 * JAITransformer.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import adams.data.image.BufferedImageContainer;
import adams.data.jai.transformer.AbstractJAITransformer;
import adams.data.jai.transformer.PassThrough;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.goe.GenericObjectEditor;
import adams.gui.goe.GenericObjectEditor.GOEPanel;

/**
 * Allows the user to apply a JAI transformer to the selected images in the ImageViewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JAITransformer
  extends AbstractSelectedImagesFilter {

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
    return "JAI transformer...";
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
    m_Editor.setClassType(AbstractJAITransformer.class);
    m_Editor.setCanChangeClassInDialog(true);
    if (hasLastSetup())
      m_Editor.setValue(getLastSetup());
    else
      m_Editor.setValue(new PassThrough());
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
   * @return		the processed image
   */
  @Override
  protected BufferedImage filter(BufferedImage image) {
    BufferedImage		result;
    AbstractJAITransformer	transformer;
    BufferedImageContainer	input;
    BufferedImageContainer[]	transformed;

    result = null;

    setLastSetup(m_Editor.getValue());
    transformer = (AbstractJAITransformer) m_Editor.getValue();
    input       = new BufferedImageContainer();
    input.setImage(image);
    transformed = transformer.transform(input);
    if (transformed.length == 0)
      m_FilterError = "No filtered image generated!";
    if (transformed.length > 1)
      getLogger().warning("Generated more than one image, using only first one.");
    if (transformed.length >= 1)
      result = transformed[0].getImage();

    return result;
  }
}
