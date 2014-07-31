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
 * GIMP.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.adams.transformer.whitebalance;

import java.awt.image.BufferedImage;

import weka.core.Utils;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.image.BufferedImageHelper;

/**
 <!-- globalinfo-start -->
 * Uses the algorithm for white balancing as used by GIMP.<br/>
 * The White Balance command automatically adjusts the colors by stretching the Red, Green and Blue channels separately. To do this, it discards pixel colors at each end of the Red, Green and Blue histograms which are used by only 0.05% (default) of the pixels in the image and stretches the remaining range as much as possible. The result is that pixel colors which occur very infrequently at the outer edges of the histograms (perhaps bits of dust, etc.) do not negatively influence the minimum and maximum values used for stretching the histograms. However, there may be hue shifts in the resulting image.<br/>
 * <br/>
 * For more information see:<br/>
 * GIMP. White Balance.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-threshold &lt;double&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;The threshold in percentage of pixels in the image below which to discard 
 * &nbsp;&nbsp;&nbsp;colors.
 * &nbsp;&nbsp;&nbsp;default: 0.05
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 100.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6652 $
 */
public class GIMP
  extends AbstractWhiteBalanceAlgorithm
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -867061196522097584L;

  /** the threshold below which pixels get discarded. */
  protected double m_Threshold;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Uses the algorithm for white balancing as used by GIMP.\n"
	+ "The White Balance command automatically adjusts the colors by "
	+ "stretching the Red, Green and Blue channels separately. To do "
	+ "this, it discards pixel colors at each end of the Red, Green and "
	+ "Blue histograms which are used by only 0.05% (default) of the "
	+ "pixels in the image and stretches the remaining range as much "
	+ "as possible. The result is that pixel colors which occur very "
	+ "infrequently at the outer edges of the histograms (perhaps bits "
	+ "of dust, etc.) do not negatively influence the minimum and "
	+ "maximum values used for stretching the histograms. However, there "
	+ "may be hue shifts in the resulting image.\n\n"
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

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "GIMP");
    result.setValue(Field.TITLE, "White Balance");
    result.setValue(Field.NOTE, "Version 2.8");
    result.setValue(Field.HTTP, "http://docs.gimp.org/2.8/en/gimp-layer-white-balance.html");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "threshold", "threshold",
	    0.05, 0.0, 100.0);
  }

  /**
   * Sets the threshold in percent (0-100) below which to discard colors.
   *
   * @param value	the threshold
   */
  public void setThreshold(double value) {
    if ((value >= 0) && (value <= 100)) {
      m_Threshold = value;
      reset();
    }
    else {
      getLogger().warning("Threshold must be 0 <= x <= 100, provided: " + value);
    }
  }

  /**
   * Returns the threshold in percent (0-100) below which to discard colors.
   *
   * @return		the threshold
   */
  public double getThreshold() {
    return m_Threshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String thresholdTipText() {
    return "The threshold in percentage of pixels in the image below which to discard colors.";
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
    int		x;
    int		y;
    int[][]	histo;
    int		min;
    int[]	first;
    int[]	last;
    int		i;
    int		n;
    int[][]	conversion;
    double	factor;
    int[]	split;
    
    img    = BufferedImageHelper.convert(img, BufferedImage.TYPE_4BYTE_ABGR);
    width  = img.getWidth();
    height = img.getHeight();
    
    // get RGBA histograms
    histo = BufferedImageHelper.histogram(img, false);
    if (isLoggingEnabled()) {
      getLogger().finer("R: " + Utils.arrayToString(histo[0]));
      getLogger().finer("G: " + Utils.arrayToString(histo[1]));
      getLogger().finer("B: " + Utils.arrayToString(histo[2]));
    }
    
    // determine which points to discard at either end
    min    = (int) Math.round((double) width * (double) height * m_Threshold / 100.0);
    first  = new int[3];
    last   = new int[3];
    for (i = 0; i < 3; i++) {
      // first
      for (n = 0; n < histo[i].length; n++) {
	if (histo[i][n] > min) {
	  first[i] = n;
	  break;
	}
      }
      // last
      for (n = histo[i].length - 1; n >= 0; n--) {
	if (histo[i][n] > min) {
	  last[i] = n;
	  break;
	}
      }
    }
    if (isLoggingEnabled()) {
      getLogger().finer("first: " + Utils.arrayToString(first));
      getLogger().finer("last: " + Utils.arrayToString(last));
    }

    // generate conversion matrices, "stretching" first-last region
    // onto 0-255 range:
    //
    //              ---
    // 0    18 ----/   \-- 202    255
    // |_____|/___________\|______|
    // |     |             |      |
    //      first         last
    
    conversion = new int[3][256];
    for (i = 0; i < 3; i++) {
      factor = 256.0 / (last[i] - first[i] + 1);
      for (n = 0; n < 256; n++) {
	if (n < first[i])
	  conversion[i][n] = 0;
	else if (n > last[i])
	  conversion[i][n] = 255;
	else
	  conversion[i][n] = (int) ((n - first[i]) * factor);
      }
    }
    if (isLoggingEnabled()) {
      getLogger().finer("R (conversion): " + Utils.arrayToString(conversion[0]));
      getLogger().finer("G (conversion): " + Utils.arrayToString(conversion[1]));
      getLogger().finer("B (conversion): " + Utils.arrayToString(conversion[2]));
    }

    // correct pixels
    for (y = 0; y < height; y++) {
      for (x = 0; x < width; x++) {
	split = BufferedImageHelper.split(img.getRGB(x, y));
	for (i = 0; i < conversion.length; i++)
	  split[i] = conversion[i][split[i]];
	img.setRGB(x, y, BufferedImageHelper.combine(split));
      }
    }
    
    return img;
  }
}
