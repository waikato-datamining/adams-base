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

/*
 * AbstractPNGAnnotationImageSegmentationWriter.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.flow.container.ImageSegmentationContainer;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Ancestor for image segmentation annotation writers that store the annotations in a single PNG file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractPNGAnnotationImageSegmentationWriter
  extends AbstractImageSegmentationAnnotationWriter
  implements ImageSegmentationAnnotationWriterWithLayerNames {

  private static final long serialVersionUID = 3566330074754565825L;

  /** the layer names. */
  protected BaseString[] m_LayerNames;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "layer-name", "layerNames",
      new BaseString[0]);
  }

  /**
   * Sets the names for the layers to use; outputs all if none specified.
   *
   * @param value	the names
   */
  public void setLayerNames(BaseString[] value) {
    m_LayerNames = value;
    reset();
  }

  /**
   * Returns the names for the layers to use; outputs all if none specified.
   *
   * @return		the names
   */
  public BaseString[] getLayerNames() {
    return m_LayerNames;
  }
  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"jpg"};
  }

  /**
   * Returns the default extension of the format.
   *
   * @return the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return "jpg";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String layerNamesTipText() {
    return "The names to of the layers to output; outputs all if none specified.";
  }

  /**
   * Hook method for performing checks before writing the data.
   *
   * @param file	the file to check
   * @param annotations the annotations to write
   * @return		null if no errors, otherwise error message
   */
  @Override
  protected String check(PlaceholderFile file, ImageSegmentationContainer annotations) {
    String			result;
    Map<String,BufferedImage> 	layers;

    result = super.check(file, annotations);

    if (result == null) {
      layers = (Map<String,BufferedImage>) annotations.getValue(ImageSegmentationContainer.VALUE_LAYERS);
      if ((layers == null) || (layers.size() == 0))
        result = "No layers in container!";
    }

    return result;
  }
}
