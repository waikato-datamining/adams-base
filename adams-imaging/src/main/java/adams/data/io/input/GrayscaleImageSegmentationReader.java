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
 * GrayscaleImageSegmentationReader.java
 * Copyright (C) 2022-2024 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageHelper;
import adams.data.io.output.GrayscaleImageSegmentationWriter;
import adams.data.io.output.ImageSegmentationAnnotationWriter;
import adams.data.statistics.StatUtils;
import adams.flow.container.ImageSegmentationContainer;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The layers are stored as grayscale values, with 0 being the background.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GrayscaleImageSegmentationReader
    extends AbstractCustomPNGAnnotationImageSegmentationReader {

  private static final long serialVersionUID = -5567473437385041915L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The layers are stored as grayscale values, with 0 being the background.";
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return the writer, null if none available
   */
  @Override
  public ImageSegmentationAnnotationWriter getCorrespondingWriter() {
    return new GrayscaleImageSegmentationWriter();
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Grayscale image segmentation";
  }

  /**
   * Reads the image segmentation annotations.
   *
   * @param file the file to read from
   * @return the annotations
   */
  @Override
  protected ImageSegmentationContainer doReadLayers(PlaceholderFile file) {
    ImageSegmentationContainer	result;
    BufferedImage 		baseImage;
    BufferedImage 		pngImage;
    int[] 			pngPixels;
    int[]			unique;
    int[] 			uniqueGray;
    TIntSet 			uniqueGraySet;
    Map<String,BufferedImage> 	layerImages;
    int[][]			layerPixels;
    int				i;
    int				n;
    int				white;
    int				maxLayer;
    String			layerName;

    baseImage     = BufferedImageHelper.read(file).toBufferedImage();
    pngImage      = readPNG(file);
    pngPixels     = BufferedImageHelper.getPixels(pngImage);
    unique        = StatUtils.uniqueValues(pngPixels);
    Arrays.sort(unique);
    uniqueGray = new int[unique.length];
    maxLayer      = 0;
    for (i = 0; i < unique.length; i++) {
      uniqueGray[i] = unique[i] & 0xFF;
      maxLayer      = Math.max(maxLayer, uniqueGray[i]);
    }
    uniqueGraySet = new TIntHashSet(uniqueGray);
    if (isLoggingEnabled())
      getLogger().info("Unique colors: #=" + unique.length + ", values=" + Utils.arrayToString(unique) + ", gray=" + Utils.arrayToString(uniqueGray));

    // separate pixels
    white       = Color.WHITE.getRGB();
    layerPixels = new int[maxLayer + 1][pngPixels.length];
    for (n = 0; n <= maxLayer; n++) {
      for (i = 0; i < pngPixels.length; i++) {
	if ((pngPixels[i] & 0xFF) == n)
	  layerPixels[n][i] = white;
      }
    }

    // create images
    layerImages = new HashMap<>();
    n           = 0;
    for (i = 0; i <= maxLayer; i++) {
      if (m_SkipFirstLayer && (i == 0))
	continue;
      if (n < m_LayerNames.length)
	layerName = m_LayerNames[n].getValue();
      else
	layerName = "layer-" + Utils.padLeft("" + (n+1), '0', 3);
      if (uniqueGraySet.contains(i)) {
	layerImages.put(layerName, new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_RGB));
	layerImages.get(layerName).setRGB(0, 0, baseImage.getWidth(), baseImage.getHeight(), layerPixels[i], 0, baseImage.getWidth());
      }
      n++;
    }

    result = new ImageSegmentationContainer(file.getName(), baseImage, layerImages);
    return result;
  }
}
