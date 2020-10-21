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
 * IndividualImageSegmentationLayerWriter.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageHelper;
import adams.data.io.input.AbstractImageSegmentationAnnotationReader;
import adams.data.io.input.IndividualImageSegmentationLayerReader;
import adams.flow.container.ImageSegmentationContainer;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Uses a JPG as base image and indexed PNG files for the individual layers (0 = background, 1 = annotation).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IndividualImageSegmentationLayerWriter
  extends AbstractImageSegmentationAnnotationWriter {

  private static final long serialVersionUID = 8630734382383387883L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses a JPG as base image and indexed PNG files for the individual layers (0 = background, 1 = annotation).";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Individual image segmentation layers";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"jpg"};
  }

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return getFormatExtensions()[0];
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  @Override
  public AbstractImageSegmentationAnnotationReader getCorrespondingReader() {
    return new IndividualImageSegmentationLayerReader();
  }

  /**
   * Writes the image segmentation annotations.
   *
   * @param file	the file to write to
   * @param annotations the annotations to write
   * @return		null if successfully written, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, ImageSegmentationContainer annotations) {
    String			result;
    String			prefix;
    Map<String,BufferedImage> 	layers;
    PlaceholderFile		layerFile;

    prefix = FileUtils.replaceExtension(file.getAbsolutePath(), "");

    if (isLoggingEnabled())
      getLogger().info("Writing base image to: " + file);
    result = BufferedImageHelper.write(annotations.getValue(ImageSegmentationContainer.VALUE_BASE, BufferedImage.class), file);
    if (result != null)
      result = "Failed to write base image: " + result;

    if ((result == null) && (annotations.hasValue(ImageSegmentationContainer.VALUE_LAYERS))) {
      layers = (Map<String,BufferedImage>) annotations.getValue(ImageSegmentationContainer.VALUE_LAYERS, Map.class);
      for (String label: layers.keySet()) {
        layerFile = new PlaceholderFile(prefix + "-" + label + ".png");
	if (isLoggingEnabled())
	  getLogger().info("Writing layer '" + label + "' to: " + layerFile);
	result = BufferedImageHelper.write(layers.get(label), layerFile);
	if (result != null) {
	  result = "Failed to write layer '" + label + "': " + result;
	  break;
	}
      }
    }

    if ((result != null) && isLoggingEnabled())
      getLogger().severe(result);

    return result;
  }
}
