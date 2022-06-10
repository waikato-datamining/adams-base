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
 * IndexedPNGImageSegmentationReader.java
 * Copyright (C) 2020-2022 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageHelper;
import adams.data.io.output.ImageSegmentationAnnotationWriter;
import adams.data.io.output.IndexedPNGImageSegmentationWriter;
import adams.flow.container.ImageSegmentationContainer;
import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.ImageLineByte;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReader;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * The layers are stored in the blue channel, with 0 being the background.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IndexedPNGImageSegmentationReader
  extends AbstractPNGAnnotationImageSegmentationReader {

  private static final long serialVersionUID = -5567473437385041915L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The first palette index of the PNG is assumed to be the background, for the remainder the supplied layer names are used.";
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return the writer, null if none available
   */
  @Override
  public ImageSegmentationAnnotationWriter getCorrespondingWriter() {
    return new IndexedPNGImageSegmentationWriter();
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Indexed PNG image segmentation";
  }

  /**
   * Turns the PNG into layers.
   *
   * @param file	the PNG file to read
   * @return		the layers
   */
  protected Map<String,BufferedImage> readLayers(File file) {
    Map<String,BufferedImage>	result;
    int[]			pixels;
    int[]			layerPixels;
    BufferedImage		image;
    PngReader 			reader;
    int				i;
    int				n;
    int				idx;
    IImageLine 			line;
    ImageLineByte 		lineByte;
    ImageLineInt 		lineInt;
    int				color;
    int 			maxIndex;
    int				black;
    int				white;

    result = new HashMap<>();

    try {
      reader = new PngReader(file.getAbsoluteFile());
      if (isLoggingEnabled())
	getLogger().info(reader.imgInfo.toString());

      if (!reader.imgInfo.indexed) {
        getLogger().severe("PNG is not indexed: " + file);
        return result;
      }

      if (reader.imgInfo.channels != 1) {
        getLogger().severe("Expected one channel, but found " + reader.imgInfo.channels + " in: " + file);
        return result;
      }

      // read indexed pixels
      pixels   = new int[reader.imgInfo.cols * reader.imgInfo.rows];
      maxIndex = 0;
      for (n = 0; n < reader.imgInfo.rows; n++) {
        line = reader.readRow();
	if (line instanceof ImageLineByte) {
	  lineByte = (ImageLineByte) reader.readRow(n);
	  for (i = 0; i < reader.imgInfo.cols; i++) {
	    color = lineByte.getElem(i);
	    pixels[i + n * reader.imgInfo.cols] = color;
	    maxIndex = Math.max(maxIndex, color);
	  }
	}
	else {
	  lineInt = (ImageLineInt) reader.readRow(n);
	  for (i = 0; i < reader.imgInfo.cols; i++) {
	    color = lineInt.getElem(i);
	    pixels[i + n * reader.imgInfo.cols] = color;
	    maxIndex = Math.max(maxIndex, color);
	  }
	}
      }

      // create layers
      black = Color.BLACK.getRGB();
      white = Color.WHITE.getRGB();
      idx   = 0;
      for (n = 0; n <= maxIndex; n++) {
        if (m_SkipFirstLayer && (n == 0))
          continue;
	layerPixels = new int[pixels.length];
	for (i = 0; i < layerPixels.length; i++) {
	  if (pixels[i] == n)
	    layerPixels[i] = white;
	  else
	    layerPixels[i] = black;
	}
	image = new BufferedImage(reader.imgInfo.cols, reader.imgInfo.rows, BufferedImage.TYPE_INT_RGB);
	image.setRGB(0, 0, image.getWidth(), image.getHeight(), layerPixels, 0, image.getWidth());
	if (idx >= m_LayerNames.length)
	  result.put("layer-" + (idx+1), image);
	else
	  result.put(m_LayerNames[idx].getValue(), image);
	idx++;
      }
    }
    catch (Exception e) {
      result = null;
      getLogger().log(Level.SEVERE, "Failed to read PNG: " + file, e);
    }

    return result;
  }

  /**
   * Reads the image segmentation annotations.
   *
   * @param file the file to read from
   * @return the annotations
   */
  @Override
  protected ImageSegmentationContainer doRead(PlaceholderFile file) {
    ImageSegmentationContainer	result;
    File			png;
    BufferedImage 		baseImage;
    Map<String,BufferedImage> 	layerImages;

    baseImage   = BufferedImageHelper.read(file).toBufferedImage();
    png         = FileUtils.replaceExtension(file, ".png");
    layerImages = readLayers(png);
    result      = new ImageSegmentationContainer(file.getName(), baseImage, layerImages);
    return result;
  }
}
