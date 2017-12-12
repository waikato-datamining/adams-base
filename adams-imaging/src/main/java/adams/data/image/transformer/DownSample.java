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
 * DownSample.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Generates a smaller image by taken every nth pixel (on the x and y axis).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-nth &lt;int&gt; (property: nthPixel)
 * &nbsp;&nbsp;&nbsp;Only every n-th pixel will be output.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DownSample
  extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8400999643470579756L;

  /** the n-th pixel to use. */
  protected int m_NthPixel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a smaller image by taken every nth pixel (on the x and y axis).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "nth", "nthPixel",
      1, 1, null);
  }

  /**
   * Sets the nth pixel setting.
   *
   * @param value 	the nth pixel to use
   */
  public void setNthPixel(int value) {
    if (getOptionManager().isValid("nthPixel", value)) {
      m_NthPixel = value;
      reset();
    }
  }

  /**
   * Returns the nth pixel setting.
   *
   * @return 		the nth pixel
   */
  public int getNthPixel() {
    return m_NthPixel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nthPixelTipText() {
    return "Only every n-th pixel will be output.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "nthPixel", m_NthPixel, "nth: ");
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
    BufferedImage 		large;
    BufferedImage 		small;
    int				x;
    int				y;

    result    = new BufferedImageContainer[1];
    result[0] = (BufferedImageContainer) img.getHeader();
    large     = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_INT_ARGB);
    small     = new BufferedImage(large.getWidth() / m_NthPixel, large.getHeight() / m_NthPixel, BufferedImage.TYPE_INT_ARGB);
    result[0].setImage(small);
    for (x = 0; x < large.getWidth(); x = x + m_NthPixel) {
      for (y = 0; y < large.getHeight(); y = y + m_NthPixel)
        small.setRGB(x / m_NthPixel, y / m_NthPixel, large.getRGB(x, y));
    }

    return result;
  }
}
