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

import java.awt.Dialog.ModalityType;
import java.awt.image.BufferedImage;

import weka.core.Instance;
import adams.data.image.BufferedImageContainer;
import adams.data.jai.flattener.AbstractJAIFlattener;
import adams.data.jai.flattener.Histogram;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * Allows the user to apply a JAI flattner to an image in the ImageViewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7713 $
 */
public class JAIFlattener
  extends AbstractImageFlattener {

  /** for serialization. */
  private static final long serialVersionUID = -3146372359577147914L;

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
   * Filters the image.
   *
   * @param image	the image to filter
   * @return		the generated instances
   */
  @Override
  protected Instance[] flatten(BufferedImage image) {
    weka.core.Instance[]	result;
    GenericObjectEditorDialog	dialog;
    AbstractJAIFlattener	flattener;
    BufferedImageContainer	input;
    weka.core.Instance[]	flattened;

    result = null;
    if (m_CurrentPanel.getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentDialog());
    else
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentFrame());
    dialog.getGOEEditor().setClassType(AbstractJAIFlattener.class);
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

    setLastSetup(dialog.getCurrent());
    flattener = (AbstractJAIFlattener) dialog.getCurrent();
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
