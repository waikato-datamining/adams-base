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
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageHelper;
import adams.data.io.input.AbstractImageSegmentationAnnotationReader;
import adams.data.io.input.IndexedPNGImageSegmentationReader;
import adams.flow.container.ImageSegmentationContainer;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Map;

/**
 * Each layer gets stored with a separate color in the palette.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IndexedPNGImageSegmentationWriter
  extends AbstractImageSegmentationAnnotationWriter {

  private static final long serialVersionUID = 3566330074754565825L;

  /** the layer names. */
  protected BaseString[] m_LayerNames;

  /** for supplying the palette colors. */
  protected ColorProvider m_ColorProvider;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Each layer gets stored with a separate color in the palette.";
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

    m_OptionManager.add(
      "color-provider", "colorProvider",
      new DefaultColorProvider());
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
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String layerNamesTipText() {
    return "The names to of the layers to output; outputs all if none specified.";
  }

  /**
   * Sets the color provider to use for the palette.
   *
   * @param value	the provider
   */
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use for the palette.
   *
   * @return		the provider
   */
  public ColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorProviderTipText() {
    return "The color provider to use for the palette.";
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return the reader, null if none available
   */
  @Override
  public AbstractImageSegmentationAnnotationReader getCorrespondingReader() {
    return new IndexedPNGImageSegmentationReader();
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return new IndexedPNGImageSegmentationReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new IndexedPNGImageSegmentationReader().getFormatExtensions();
  }

  /**
   * Returns the default extension of the format.
   *
   * @return the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return new IndexedPNGImageSegmentationReader().getDefaultFormatExtension();
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
    Map<String,BufferedImage> 	layers;
    String[]			layerNames;
    int[]			combPixels;
    int[]			currPixels;
    BufferedImage		combImage;
    int				i;
    int				n;
    int				color;
    int				black;

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
    black      = Color.BLACK.getRGB();
    Arrays.fill(combPixels, black);
    m_ColorProvider.resetColors();
    for (i = 0; i < layerNames.length; i++) {
      color = m_ColorProvider.next().getRGB();
      if (!layers.containsKey(layerNames[i]))
        continue;
      currPixels = BufferedImageHelper.getPixels(layers.get(layerNames[i]));
      for (n = 0; n < currPixels.length; n++) {
        if ((currPixels[n] & 0x00FFFFFF) > 0)
          combPixels[n] = 0xFF000000 | color;
      }
    }
    combImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_RGB);
    combImage.setRGB(0, 0, combImage.getWidth(), combImage.getHeight(), combPixels, 0, combImage.getWidth());
    combImage = BufferedImageHelper.convert(combImage, BufferedImage.TYPE_BYTE_INDEXED);

    return BufferedImageHelper.write(combImage, file);
  }
}
