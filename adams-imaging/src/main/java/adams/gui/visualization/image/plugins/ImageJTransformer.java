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
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import ij.ImagePlus;

import java.awt.Dialog.ModalityType;
import java.awt.image.BufferedImage;
import java.util.Date;

import adams.data.imagej.ImagePlusContainer;
import adams.data.imagej.transformer.AbstractImageJTransformer;
import adams.data.imagej.transformer.PassThrough;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * Allows the user to apply an ImageJ transformer to an image in the ImageViewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageJTransformer
  extends AbstractImageFilter {

  /** for serialization. */
  private static final long serialVersionUID = 9108452366270377935L;

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
   * Filters the image.
   *
   * @param image	the image to filter
   * @return		the processed image
   */
  @Override
  protected BufferedImage filter(BufferedImage image) {
    BufferedImage		result;
    GenericObjectEditorDialog	dialog;
    AbstractImageJTransformer	transformer;
    String			title;
    ImagePlusContainer		input;
    ImagePlusContainer[]	transformed;

    result = null;
    if (m_CurrentPanel.getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentDialog());
    else
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentFrame());
    dialog.getGOEEditor().setClassType(AbstractImageJTransformer.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    if (hasLastSetup())
      dialog.setCurrent(getLastSetup());
    else
      dialog.setCurrent(new PassThrough());
    dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
    dialog.setLocationRelativeTo(m_CurrentPanel);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION) {
      m_CanceledByUser = true;
      return result;
    }

    setLastSetup(dialog.getCurrent());
    if (m_CurrentPanel.getCurrentFile() != null)
      title = m_CurrentPanel.getCurrentFile().toString();
    else
      title = "" + new Date();
    transformer = (AbstractImageJTransformer) dialog.getCurrent();
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
