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
 * GrayscaleImageSegmentationWriter.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageHelper;
import adams.data.io.input.GrayscaleImageSegmentationReader;
import adams.data.io.input.ImageSegmentationAnnotationReader;
import adams.flow.container.ImageSegmentationContainer;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * The layers get stored as grayscale values, with 0 being the background.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GrayscaleImageSegmentationWriter
    extends AbstractImplicitBackgroundPNGAnnotationImageSegmentationWriter {

  private static final long serialVersionUID = 3566330074754565825L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The layers get stored as grayscale values, with 0 being the background.";
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return the reader, null if none available
   */
  @Override
  public ImageSegmentationAnnotationReader getCorrespondingReader() {
    return new GrayscaleImageSegmentationReader();
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return new GrayscaleImageSegmentationReader().getFormatDescription();
  }

  /**
   * Writes the image segmentation annotations.
   *
   * @param file        the file to write to
   * @param annotations the annotations to write
   * @return null if successfully written, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, ImageSegmentationContainer annotations) {
    BufferedImage		baseImage;
    String[]			layerNames;
    Map<String,BufferedImage> 	layers;
    int[]			combPixels;
    int[]			currPixels;
    BufferedImage		combImage;
    int				i;
    int				n;
    int				offset;

    baseImage  = (BufferedImage) annotations.getValue(ImageSegmentationContainer.VALUE_BASE);
    layers     = (Map<String,BufferedImage>) annotations.getValue(ImageSegmentationContainer.VALUE_LAYERS);
    layerNames = getLayerNames(annotations);
    combPixels = new int[baseImage.getWidth() * baseImage.getHeight()];
    offset     = m_ImplicitBackground ? 1 : 0;
    for (i = 0; i < layerNames.length; i++) {
      if (!layers.containsKey(layerNames[i]))
	continue;
      currPixels = BufferedImageHelper.getPixels(layers.get(layerNames[i]));
      for (n = 0; n < currPixels.length; n++) {
	if ((currPixels[n] & 0x00FFFFFF) > 0)
	  combPixels[n] = i+offset;
      }
    }
    combImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    combImage.getRaster().setPixels(0, 0, combImage.getWidth(), combImage.getHeight(), combPixels);

    return BufferedImageHelper.write(combImage, getAnnotationFile(file));
  }
}
