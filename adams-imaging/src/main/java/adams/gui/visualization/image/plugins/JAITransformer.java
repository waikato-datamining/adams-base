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

import java.awt.Dialog.ModalityType;
import java.awt.image.BufferedImage;

import adams.data.image.BufferedImageContainer;
import adams.data.jai.transformer.AbstractJAITransformer;
import adams.data.jai.transformer.PassThrough;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * Allows the user to apply a JAI transformer to an image in the ImageViewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JAITransformer
  extends AbstractImageFilter {

  /** for serialization. */
  private static final long serialVersionUID = -3146372359577147914L;

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
   * Filters the image.
   *
   * @param image	the image to filter
   * @return		the processed image
   */
  @Override
  protected BufferedImage filter(BufferedImage image) {
    BufferedImage		result;
    GenericObjectEditorDialog	dialog;
    AbstractJAITransformer	transformer;
    BufferedImageContainer	input;
    BufferedImageContainer[]	transformed;

    result = null;
    if (m_CurrentPanel.getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentDialog());
    else
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentFrame());
    dialog.getGOEEditor().setClassType(AbstractJAITransformer.class);
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
    transformer = (AbstractJAITransformer) dialog.getCurrent();
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
