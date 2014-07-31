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
 * ImageJFeatureGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import java.awt.image.BufferedImage;

import weka.core.Instance;
import adams.core.option.OptionUtils;
import adams.data.conversion.BufferedImageToImageJ;
import adams.data.image.BufferedImageContainer;
import adams.data.imagej.ImagePlusContainer;
import adams.data.imagej.features.AbstractImageJFeatureGenerator;
import adams.data.imagej.features.Histogram;

/**
 * Allows the user to apply a ImageJ feature generator to an image in the ImageViewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7713 $
 */
public class ImageJFeatureGenerator
  extends AbstractSelectedImagesFlattener {

  /** for serialization. */
  private static final long serialVersionUID = 6721788085343201024L;

  /**
   * Returns the text for the menu item to create.
   *
   * @return		the text
   */
  @Override
  public String getCaption() {
    return "ImageJ feature generator...";
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
    return AbstractImageJFeatureGenerator.class;
  }
  
  /**
   * Returns the default object to use in the GOE if no last setup is yet
   * available.
   * 
   * @return		the object
   */
  @Override
  protected Object getDefaultValue() {
    return new Histogram();
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
   * @return		the generated instances
   */
  @Override
  protected Instance[] flatten(BufferedImage image) {
    weka.core.Instance[]	result;
    AbstractImageJFeatureGenerator	flattener;
    weka.core.Instance[]	flattened;
    BufferedImageContainer	input;
    BufferedImageToImageJ	conv;

    result = null;

    input = new BufferedImageContainer();
    input.setImage(image);
    conv = new BufferedImageToImageJ();
    conv.setInput(input);
    if ((m_FilterError = conv.convert()) == null) {
      setLastSetup(m_Editor.getValue());
      flattener = (AbstractImageJFeatureGenerator) m_Editor.getValue();
      flattened = flattener.generate((ImagePlusContainer) conv.getOutput());
      if (flattened.length == 0)
        m_FilterError = "No instances generated!";
      if (flattened.length > 0)
        result = flattened;
    }
    conv.cleanUp();

    return result;
  }
}
