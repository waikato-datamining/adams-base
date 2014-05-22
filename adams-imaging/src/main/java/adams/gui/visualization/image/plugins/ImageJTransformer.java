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
 * ImageJTransformer.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import ij.ImagePlus;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.JPanel;

import adams.data.imagej.ImagePlusContainer;
import adams.data.imagej.transformer.AbstractImageJTransformer;
import adams.data.imagej.transformer.PassThrough;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.goe.GenericObjectEditor;
import adams.gui.goe.GenericObjectEditor.GOEPanel;

/**
 * Allows the user to apply an ImageJ transformer to an image in the ImageViewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageJTransformer
  extends AbstractSelectedImagesFilter {

  /** for serialization. */
  private static final long serialVersionUID = 9108452366270377935L;

  /** the GOE editor with the transformer. */
  protected GenericObjectEditor m_Editor;

  /**
   * Returns the text for the menu item to create.
   *
   * @return		the text
   */
  @Override
  public String getCaption() {
    return "ImageJ transformer...";
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
    m_Editor.setClassType(AbstractImageJTransformer.class);
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
    AbstractImageJTransformer	transformer;
    String			title;
    ImagePlusContainer		input;
    ImagePlusContainer[]	transformed;

    result = null;

    setLastSetup(m_Editor.getValue());
    if (m_CurrentPanel.getCurrentFile() != null)
      title = m_CurrentPanel.getCurrentFile().toString();
    else
      title = "" + new Date();
    transformer = (AbstractImageJTransformer) m_Editor.getValue();
    input       = new ImagePlusContainer();
    input.setImage(new ImagePlus(title, image));
    transformed = transformer.transform(input);
    if (transformed.length == 0)
      m_FilterError = "No filtered image generated!";
    if (transformed.length > 1)
      getLogger().severe("WARNING: Generated more than one image, using only first one.");
    if (transformed.length >= 1)
      result = transformed[0].getImage().getBufferedImage();

    return result;
  }
}
