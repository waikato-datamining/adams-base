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
 * BlueChannelImageSegmentationWriter.java
 * Copyright (C) 2020-2022 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.base.BaseObject;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageHelper;
import adams.data.io.input.BlueChannelImageSegmentationReader;
import adams.data.io.input.ImageSegmentationAnnotationReader;
import adams.flow.container.ImageSegmentationContainer;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Map;

/**
 * The layers get stored in the blue channel, with 0 being the background.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BlueChannelImageSegmentationWriter
  extends AbstractPNGAnnotationImageSegmentationWriter {

  private static final long serialVersionUID = 3566330074754565825L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The layers get stored in the blue channel, with 0 being the background.";
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return the reader, null if none available
   */
  @Override
  public ImageSegmentationAnnotationReader getCorrespondingReader() {
    return new BlueChannelImageSegmentationReader();
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return new BlueChannelImageSegmentationReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new BlueChannelImageSegmentationReader().getFormatExtensions();
  }

  /**
   * Returns the default extension of the format.
   *
   * @return the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return new BlueChannelImageSegmentationReader().getDefaultFormatExtension();
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

    baseImage  = (BufferedImage) annotations.getValue(ImageSegmentationContainer.VALUE_BASE);
    layers     = (Map<String,BufferedImage>) annotations.getValue(ImageSegmentationContainer.VALUE_LAYERS);
    if (m_LayerNames.length == 0) {
      layerNames = layers.keySet().toArray(new String[0]);
      Arrays.sort(layerNames);
    }
    else {
      layerNames = BaseObject.toStringArray(m_LayerNames);
    }
    combPixels = new int[baseImage.getWidth() * baseImage.getHeight()];
    for (i = 0; i < layerNames.length; i++) {
      if (!layers.containsKey(layerNames[i]))
        continue;
      currPixels = BufferedImageHelper.getPixels(layers.get(layerNames[i]));
      for (n = 0; n < currPixels.length; n++) {
        if ((currPixels[n] & 0x00FFFFFF) > 0)
          combPixels[n] = 0xFF000000 | (i+1);
      }
    }
    combImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_RGB);
    combImage.setRGB(0, 0, combImage.getWidth(), combImage.getHeight(), combPixels, 0, combImage.getWidth());

    return BufferedImageHelper.write(combImage, file);
  }
}
