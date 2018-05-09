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
 * Grayscale.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.image.luminance.AbstractLuminanceParameters;
import adams.data.image.luminance.BT601;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Generates a grayscale images using the specified luminance parameters.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-luminance &lt;adams.data.image.luminance.AbstractLuminanceParameters&gt; (property: luminance)
 * &nbsp;&nbsp;&nbsp;Supplies the luminance parameters for the grayscale conversion.
 * &nbsp;&nbsp;&nbsp;default: adams.data.image.luminance.BT601
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Grayscale
  extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8400999643470579756L;

  /** the luminance parameters to use. */
  protected AbstractLuminanceParameters m_Luminance;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a grayscale images using the specified luminance parameters.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "luminance", "luminance",
      new BT601());
  }

  /**
   * Sets the luminance scheme.
   *
   * @param value 	the scheme
   */
  public void setLuminance(AbstractLuminanceParameters value) {
    m_Luminance = value;
    reset();
  }

  /**
   * Returns the luminance scheme.
   *
   * @return 		the scheme
   */
  public AbstractLuminanceParameters getLuminance() {
    return m_Luminance;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String luminanceTipText() {
    return "Supplies the luminance parameters for the grayscale conversion.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "luminance", m_Luminance, "luminance: ");
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
    BufferedImage 		orig;
    BufferedImage 		gray;
    int				x;
    int				y;
    int 			rgbOrig;
    int				rgbGray;
    double[]			luminance;

    result    = new BufferedImageContainer[1];
    result[0] = (BufferedImageContainer) img.getHeader();
    luminance = m_Luminance.getParameters();
    orig      = img.toBufferedImage();
    gray      = new BufferedImage(orig.getWidth(), orig.getHeight(), BufferedImage.TYPE_INT_RGB);
    result[0].setImage(gray);
    for (x = 0; x < orig.getWidth(); x++) {
      for (y = 0; y < orig.getHeight(); y++) {
        rgbOrig = orig.getRGB(x, y);
        rgbGray = (int)(((rgbOrig & 0xFF0000) >>> 16) * luminance[0] + ((rgbOrig & 0xFF00) >>> 8) * luminance[1] + (rgbOrig & 0xFF) * luminance[2]);
	gray.setRGB(x, y, (rgbGray << 16) + (rgbGray << 8) + rgbGray);
      }
    }

    return result;
  }
}
