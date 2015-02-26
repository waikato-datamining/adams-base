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
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import adams.core.option.OptionUtils;
import adams.data.imagej.ImagePlusContainer;
import adams.data.imagej.transformer.AbstractImageJTransformer;
import adams.data.imagej.transformer.PassThrough;
import ij.ImagePlus;

import java.awt.image.BufferedImage;
import java.util.Date;

/**
 * Allows the user to apply an ImageJ transformer to an image in the ImageViewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageJTransformer
  extends AbstractSelectedImagesFilterWithGOE {

  /** for serialization. */
  private static final long serialVersionUID = 9108452366270377935L;

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
    return "ImageJ transformer...";
  }

  /**
   * Returns the icon name.
   *
   * @return		the name, null if none available
   */
  @Override
  public String getIconName() {
    return "imagej.gif";
  }

  /**
   * Returns the class to use as type (= superclass) in the GOE.
   * 
   * @return		the class
   */
  @Override
  protected Class getEditorType() {
    return AbstractImageJTransformer.class;
  }
  
  /**
   * Returns the default object to use in the GOE if no last setup is yet
   * available.
   * 
   * @return		the object
   */
  @Override
  protected Object getDefaultValue() {
    return new PassThrough();
  }

  /**
   * Creates the log message.
   * 
   * @return		the message, null if none available
   */
  @Override
  protected String createLogEntry() {
    return getClass().getSimpleName() + ": " + OptionUtils.getCommandLine(m_Editor.getValue());
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
