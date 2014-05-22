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
 * ImageJFlattener.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import java.awt.Dialog.ModalityType;
import java.awt.image.BufferedImage;

import weka.core.Instance;
import adams.data.conversion.BufferedImageToImageJ;
import adams.data.image.BufferedImageContainer;
import adams.data.imagej.ImagePlusContainer;
import adams.data.imagej.flattener.AbstractImageJFlattener;
import adams.data.imagej.flattener.Histogram;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * Allows the user to apply a ImageJ flattner to an image in the ImageViewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7713 $
 */
public class ImageJFlattener
  extends AbstractImageFlattener {

  /** for serialization. */
  private static final long serialVersionUID = 6721788085343201024L;

  /**
   * Returns the text for the menu item to create.
   *
   * @return		the text
   */
  @Override
  public String getCaption() {
    return "ImageJ flattener...";
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
    GenericObjectEditorDialog	dialog;
    AbstractImageJFlattener	flattener;
    weka.core.Instance[]	flattened;
    BufferedImageContainer	input;
    BufferedImageToImageJ	conv;

    result = null;
    if (m_CurrentPanel.getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentDialog());
    else
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentFrame());
    dialog.getGOEEditor().setClassType(AbstractImageJFlattener.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    if (hasLastSetup())
      dialog.setCurrent(getLastSetup());
    else
      dialog.setCurrent(new Histogram());
    dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
    dialog.setLocationRelativeTo(m_CurrentPanel);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION) {
      m_CanceledByUser = true;
      return result;
    }

    input = new BufferedImageContainer();
    input.setImage(image);
    conv = new BufferedImageToImageJ();
    conv.setInput(input);
    if ((m_FilterError = conv.convert()) == null) {
      setLastSetup(dialog.getCurrent());
      flattener = (AbstractImageJFlattener) dialog.getCurrent();
      flattened = flattener.flatten((ImagePlusContainer) conv.getOutput());
      if (flattened.length == 0)
        m_FilterError = "No instances generated!";
      if (flattened.length > 0)
        result = flattened;
    }
    conv.cleanUp();

    return result;
  }
}
