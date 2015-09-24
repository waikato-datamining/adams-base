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
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import adams.core.base.BaseText;
import adams.data.image.BufferedImageContainer;
import adams.data.imagemagick.ImageMagickHelper;
import adams.flow.core.Token;
import adams.flow.transformer.ImageMagickTransformer;
import adams.gui.core.TextEditorPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.image.ImagePanel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

/**
 * Allows the user to apply ImageMagick commands.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageMagick
  extends AbstractSelectedImagesFilter {

  /** for serialization. */
  private static final long serialVersionUID = 3840263834155992337L;

  /** the editor with the commands. */
  protected TextEditorPanel m_Editor;

  /**
   * Returns the text for the menu to place the plugin beneath.
   *
   * @return		the menu
   */
  @Override
  public String getMenu() {
    return "Filter";
  }

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
   * Returns the icon name.
   *
   * @return		the name, null if none available
   */
  @Override
  public String getIconName() {
    return "imagemagick.png";
  }

  /**
   * Checks whether the plugin can be executed given the specified image panel.
   * <br><br>
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
   * Creates the panel with the configuration (return null to suppress display).
   * 
   * @return		the generated panel, null to suppress
   */
  @Override
  protected JPanel createConfigurationPanel(final ApprovalDialog dialog) {
    JPanel		result;
    
    result = new JPanel(new BorderLayout());
    m_Editor = new TextEditorPanel();
    if (hasLastSetup())
      m_Editor.setContent((String) getLastSetup());
    else
      m_Editor.setContent("");
    result.add(new JLabel("Please enter the commands"), BorderLayout.NORTH);
    result.add(m_Editor, BorderLayout.CENTER);
    
    return result;
  }

  /**
   * Creates the log message.
   * 
   * @return		the message, null if none available
   */
  @Override
  protected String createLogEntry() {
    return getClass().getSimpleName() + ": " + m_Editor.getContent();
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
    BufferedImageContainer	input;
    ImageMagickTransformer	transformer;

    result = null;

    setLastSetup(m_Editor.getContent());
    transformer = new ImageMagickTransformer();
    transformer.setCommands(new BaseText(m_Editor.getContent()));
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
