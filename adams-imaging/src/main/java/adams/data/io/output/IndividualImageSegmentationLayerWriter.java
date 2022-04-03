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
 * Copyright (C) 2020-2022 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.io.input.ImageSegmentationAnnotationReader;
import adams.data.io.input.IndividualImageSegmentationLayerReader;
import adams.flow.container.ImageSegmentationContainer;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

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

  /** whether to skip writing base image. */
  protected boolean m_SkipBaseImage;

  /** whether to skip images with only background. */
  protected boolean m_SkipEmptyLayers;

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
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"skip-base-image", "skipBaseImage",
	false);

    m_OptionManager.add(
	"skip-empty-layers", "skipEmptyLayers",
	false);
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
  public ImageSegmentationAnnotationReader getCorrespondingReader() {
    return new IndividualImageSegmentationLayerReader();
  }

  /**
   * Sets whether to skip writing the base image.
   *
   * @param value 	true if to skip
   */
  public void setSkipBaseImage(boolean value) {
    m_SkipBaseImage = value;
    reset();
  }

  /**
   * Returns whether to skip writing the base image.
   *
   * @return 		true if to skip
   */
  public boolean getSkipBaseImage() {
    return m_SkipBaseImage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipBaseImageTipText() {
    return "If enabled, the base image is not written to disk (eg when updating only the layers).";
  }

  /**
   * Sets whether to skip writing empty layers (ie only background).
   *
   * @param value 	true if to skip
   */
  public void setSkipEmptyLayers(boolean value) {
    m_SkipEmptyLayers = value;
    reset();
  }

  /**
   * Returns whether to skip writing empty layers (ie only background).
   *
   * @return 		true if to skip
   */
  public boolean getSkipEmptyLayers() {
    return m_SkipEmptyLayers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipEmptyLayersTipText() {
    return "If enabled, layers that consist only of background are not included in the output.";
  }

  /**
   * Writes the file to disk.
   *
   * @param image	the image to write
   * @param file	the file to write to
   * @param binary	whether to make binary
   * @return		null if successful, otherwise error message
   */
  protected String writeFile(BufferedImage image, PlaceholderFile file, boolean binary) {
    AbstractImageWriter		writer;
    BufferedImageContainer	cont;

    if (binary)
      image = BufferedImageHelper.convert(image, BufferedImage.TYPE_BYTE_BINARY);

    cont   = new BufferedImageContainer();
    cont.setImage(image);

    if (binary)
      writer = new JAIImageWriter();
    else
      writer = new ApacheCommonsImageWriter();

    return writer.write(file, cont);
  }

  /**
   * Counts the distinct colors in the image.
   *
   * @param image	the image to process
   * @return		the count
   */
  protected int countColors(BufferedImage image) {
    TIntSet	colors;
    int[]	pixels;
    int		i;

    colors = new TIntHashSet();
    pixels    = BufferedImageHelper.getPixels(image);
    for (i = 0; i < pixels.length; i++)
      colors.add(pixels[i]);

    return colors.size();
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

    result = null;
    prefix = FileUtils.replaceExtension(file.getAbsolutePath(), "");

    if (!m_SkipBaseImage) {
      if (isLoggingEnabled())
	getLogger().info("Writing base image to: " + file);
      result = writeFile(annotations.getValue(ImageSegmentationContainer.VALUE_BASE, BufferedImage.class), file, false);
      if (result != null)
	result = "Failed to write base image: " + result;
    }

    if ((result == null) && (annotations.hasValue(ImageSegmentationContainer.VALUE_LAYERS))) {
      layers = (Map<String,BufferedImage>) annotations.getValue(ImageSegmentationContainer.VALUE_LAYERS, Map.class);
      for (String label: layers.keySet()) {
	if (m_SkipEmptyLayers) {
	  if (countColors(layers.get(label)) == 1) {
	    if (isLoggingEnabled())
	      getLogger().info("Layer '" + label + "' is empty, skipping!");
	    continue;
	  }
	}
	layerFile = new PlaceholderFile(prefix + "-" + label + ".png");
	if (isLoggingEnabled())
	  getLogger().info("Writing layer '" + label + "' to: " + layerFile);
	result = writeFile(layers.get(label), layerFile, true);
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
