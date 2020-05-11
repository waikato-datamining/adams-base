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
 * GrayOrIndexedColorizer.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.image.transformer;

import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.CustomColorProvider;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Colorizes grayscale or indexed images using the specified color provider.<br>
 * Other images types get converted to grayscale first.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-color-provider &lt;adams.gui.visualization.core.ColorProvider&gt; (property: colorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider to use for coloring in the grayscale image.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.CustomColorProvider -color #ffff00 -color #0000ff -color #ff0000
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GrayOrIndexedColorizer
  extends AbstractBufferedImageTransformer {

  private static final long serialVersionUID = 4183676541160281269L;

  /** the color provider for generating the colors. */
  protected ColorProvider m_ColorProvider;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Colorizes grayscale or indexed images using the specified color provider.\n"
      + "Other images types get converted to grayscale first.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color-provider", "colorProvider",
      getDefaultColorProvider());
  }

  /**
   * Returns the default color provider.
   *
   * @return		the default
   */
  protected ColorProvider getDefaultColorProvider() {
    CustomColorProvider result;
    result = new CustomColorProvider();
    result.setColors(new Color[]{Color.YELLOW, Color.BLUE, Color.RED});
    return result;
  }

  /**
   * Sets the color provider to use.
   *
   * @param value	the color provider
   */
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use.
   *
   * @return		the color provider
   */
  public ColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String colorProviderTipText() {
    return "The color provider to use for coloring in the grayscale image.";
  }

  /**
   * Performs the actual transforming of the image.
   *
   * @param img		the image to transform (can be modified, since it is a copy)
   * @return		the generated image(s)
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[]	result;
    int[]			oldPixels;
    int[]			newPixels;
    int[]			colors;
    int				i;
    TIntSet			unique;
    TIntIntMap 			map;
    BufferedImage		oldImg;
    BufferedImage		newImg;
    int				type;

    result    = new BufferedImageContainer[1];
    result[0] = (BufferedImageContainer) img.getHeader();

    // get correct image
    type = img.getContent().getType();
    switch (type) {
      case BufferedImage.TYPE_BYTE_BINARY:
      case BufferedImage.TYPE_BYTE_GRAY:
      case BufferedImage.TYPE_BYTE_INDEXED:
        oldImg = img.getContent();
        break;
      default:
        oldImg = BufferedImageHelper.convert(img.getContent(), BufferedImage.TYPE_BYTE_GRAY);
    }

    // determine unique colors
    oldPixels = BufferedImageHelper.getPixels(oldImg);
    unique    = new TIntHashSet(oldPixels);
    if (isLoggingEnabled())
      getLogger().info("# unique colors: " + unique.size());

    // create lookup
    m_ColorProvider.resetColors();
    colors = unique.toArray();
    Arrays.sort(colors);
    map = new TIntIntHashMap();
    for (int color: colors)
      map.put(color, m_ColorProvider.next().getRGB());
    if (isLoggingEnabled())
      getLogger().info("color map: " + map);

    // create new pixels
    newPixels = new int[oldPixels.length];
    for (i = 0; i < oldPixels.length; i++)
      newPixels[i] = map.get(oldPixels[i]);

    // create new image
    newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
    newImg.setRGB(0, 0, img.getWidth(), img.getHeight(), newPixels, 0, img.getWidth());
    result[0].setContent(newImg);

    return result;
  }
}
