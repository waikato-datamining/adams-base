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
 * ImageMagick.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

import adams.core.ImageMagickHelper;
import adams.core.base.BaseText;
import adams.data.image.BufferedImageContainer;
import adams.flow.core.Token;
import adams.flow.transformer.ImageMagickTransformer;
import adams.gui.core.TextEditorPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.image.ImagePanel;

/**
 * Allows the user to apply ImageMagick commands.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageMagick
  extends AbstractImageFilter {

  /** for serialization. */
  private static final long serialVersionUID = 3840263834155992337L;

  /**
   * Returns the text for the menu item to create.
   *
   * @return		the text
   */
  @Override
  public String getCaption() {
    return "ImageMagick...";
  }

  /**
   * Checks whether the plugin can be executed given the specified image panel.
   * <p/>
   * Panel must be non-null and must contain an image. Also, ImageMagick must
   * be installed.
   *
   * @param panel	the panel to use as basis for decision
   * @return		true if plugin can be executed
   */
  @Override
  public boolean canExecute(ImagePanel panel) {
    return ImageMagickHelper.isConvertAvailable() && super.canExecute(panel);
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
    ApprovalDialog	dialog;
    TextEditorPanel		editor;
    BufferedImageContainer	input;
    ImageMagickTransformer	transformer;

    result = null;
    if (m_CurrentPanel.getParentDialog() != null)
      dialog = ApprovalDialog.getDialog(m_CurrentPanel.getParentDialog());
    else
      dialog = ApprovalDialog.getDialog(m_CurrentPanel.getParentFrame());
    editor = new TextEditorPanel();
    if (hasLastSetup())
      editor.setContent((String) getLastSetup());
    else
      editor.setContent("");
    dialog.setTitle("ImageMagick");
    dialog.getContentPane().add(editor, BorderLayout.CENTER);
    dialog.getContentPane().add(new JLabel("Please enter the commands"), BorderLayout.NORTH);
    dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
    dialog.setSize(400, 300);
    dialog.setLocationRelativeTo(m_CurrentPanel);
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION) {
      m_CanceledByUser = true;
      return result;
    }

    setLastSetup(editor.getContent());
    transformer = new ImageMagickTransformer();
    transformer.setCommands(new BaseText(editor.getContent()));
    m_FilterError = transformer.setUp();
    if (m_FilterError == null) {
      input = new BufferedImageContainer();
      input.setImage(image);
      transformer.input(new Token(input));
      m_FilterError = transformer.execute();
      if ((m_FilterError == null) && (transformer.hasPendingOutput()))
	result = ((BufferedImageContainer) transformer.output().getPayload()).getImage();
      transformer.wrapUp();
      transformer.cleanUp();
    }

    return result;
  }
}
