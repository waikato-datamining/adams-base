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
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageHelper;
import adams.data.io.output.AbstractImageSegmentationAnnotationWriter;
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
  extends AbstractImageSegmentationAnnotationReader {

  private static final long serialVersionUID = -5567473437385041915L;

  /** the layer names. */
  protected BaseString[] m_LayerNames;

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
   * Sets the names for the layers to use.
   *
   * @param value	the names
   */
  public void setLayerNames(BaseString[] value) {
    m_LayerNames = value;
    reset();
  }

  /**
   * Returns the names for the layers to use.
   *
   * @return		the names
   */
  public BaseString[] getLayerNames() {
    return m_LayerNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String layerNamesTipText() {
    return "The names to use for the layers; if additional layers should be present in the data, names get assigned automatically.";
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return the writer, null if none available
   */
  @Override
  public AbstractImageSegmentationAnnotationWriter getCorrespondingWriter() {
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
   * Hook method for performing checks before reading the data.
   *
   * @param file	the file to check
   * @return		null if no errors, otherwise error message
   */
  @Override
  protected String check(PlaceholderFile file) {
    String	result;
    File	png;

    result = super.check(file);

    if (result == null) {
      png = FileUtils.replaceExtension(file, ".png");
      if (!png.exists())
        result = "Associated PNG file with annotations is missing!";
    }

    return result;
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
    IImageLine 			line;
    ImageLineByte 		lineByte;
    ImageLineInt 		lineInt;
    int				color;
    int				maxLayer;
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
      maxLayer = 0;
      for (n = 0; n < reader.imgInfo.rows; n++) {
        line = reader.readRow();
	if (line instanceof ImageLineByte) {
	  lineByte = (ImageLineByte) reader.readRow(n);
	  for (i = 0; i < reader.imgInfo.cols; i++) {
	    color = lineByte.getElem(i);
	    pixels[i + n * reader.imgInfo.cols] = color;
	    maxLayer = Math.max(maxLayer, color);
	  }
	}
	else {
	  lineInt = (ImageLineInt) reader.readRow(n);
	  for (i = 0; i < reader.imgInfo.cols; i++) {
	    color = lineInt.getElem(i);
	    pixels[i + n * reader.imgInfo.cols] = color;
	    maxLayer = Math.max(maxLayer, color);
	  }
	}
      }

      // create layers
      black = Color.BLACK.getRGB();
      white = Color.WHITE.getRGB();
      for (n = 0; n < maxLayer + 1; n++) {
	layerPixels = new int[pixels.length];
	for (i = 0; i < layerPixels.length; i++) {
	  if (pixels[i] == n)
	    layerPixels[i] = white;
	  else
	    layerPixels[i] = black;
	}
	image = new BufferedImage(reader.imgInfo.cols, reader.imgInfo.rows, BufferedImage.TYPE_INT_RGB);
	image.setRGB(0, 0, image.getWidth(), image.getHeight(), layerPixels, 0, image.getWidth());
	if (n >= m_LayerNames.length)
	  result.put("layer-" + (n+1), image);
	else
	  result.put(m_LayerNames[n].getValue(), image);
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
