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

/**
 * WhitePatch.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer.whitebalance;

import java.awt.image.BufferedImage;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.image.BufferedImageHelper;

/**
 <!-- globalinfo-start -->
 * Basic algorithm that is based on the Retinex theory of visual color constancy, which argues that perceived white is associated with the maximum cone signals. This is known as the white world assumption.<br>
 * <br>
 * For more information see:<br>
 * Edmund Y. Lam, George S. K. Fung (2009). Single-Sensor Imaging: Methods and Applications for Digital Cameras.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6652 $
 */
public class WhitePatch
  extends AbstractWhiteBalanceAlgorithm
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -867061196522097584L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Basic algorithm that is based on the Retinex theory of visual color constancy, "
	+ "which argues that perceived white is associated with the maximum cone signals. "
	+ "This is known as the white world assumption.\n\n"
	+ "For more information see:\n"
	+ getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.INBOOK);
    result.setValue(Field.AUTHOR, "Edmund Y. Lam and George S. K. Fung");
    result.setValue(Field.CHAPTER, "10: Automatic White Balancing in Digital Photography");
    result.setValue(Field.TITLE, "Single-Sensor Imaging: Methods and Applications for Digital Cameras");
    result.setValue(Field.PAGES, "267-294");
    result.setValue(Field.YEAR, "2009");
    result.setValue(Field.PUBLISHER, "Taylor & Francis Group LLC");
    result.setValue(Field.PDF, "http://courses.cs.washington.edu/courses/cse467/08au/labs/l5/whiteBalance.pdf");

    return result;
  }

  /**
   * Performs the actual white balancing.
   * 
   * @param img		the image to process
   * @return		the processed image
   */
  @Override
  protected BufferedImage doBalance(BufferedImage img) {
    int		width;
    int		height;
    double	r;
    double	g;
    double	b;
    int[][]	pixels;
    int		i;
    double	alpha;
    double	beta;
    int		x;
    int		y;
    int[]	split;
    
    img    = BufferedImageHelper.convert(img, BufferedImage.TYPE_4BYTE_ABGR);
    width  = img.getWidth();
    height = img.getHeight();
    
    // calculate averages
    r      = 0.0;
    g      = 0.0;
    b      = 0.0;
    pixels = BufferedImageHelper.getRGBPixels(img);
    for (i = 0; i < pixels.length; i++) {
      r = Math.max(pixels[i][0], r);
      g = Math.max(pixels[i][1], g);
      b = Math.max(pixels[i][2], b);
    }

    // calculate corrections
    alpha = g / r;
    beta  = g / b;
    
    // correct pixels
    for (y = 0; y < height; y++) {
      for (x = 0; x < width; x++) {
	split = BufferedImageHelper.split(img.getRGB(x, y));
	split[0] *= alpha;
	split[2] *= beta;
	img.setRGB(x, y, BufferedImageHelper.combine(split));
      }
    }
    
    return img;
  }
}
