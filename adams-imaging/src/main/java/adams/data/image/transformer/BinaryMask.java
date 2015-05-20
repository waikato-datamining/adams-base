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
 * BinaryMask.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer;

import java.awt.Color;
import java.awt.image.BufferedImage;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;

/**
 <!-- globalinfo-start -->
 * Generates a binary image from the input and uses this mask to determine which pixels get replaced by the specified replacement color.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-threshold &lt;int&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;The threshold that determines whether a grayscale pixel will become black 
 * &nbsp;&nbsp;&nbsp;(below) or white (equal to or above).
 * &nbsp;&nbsp;&nbsp;default: 128
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 255
 * </pre>
 * 
 * <pre>-replacement-type &lt;REPLACE_WHITE_PIXELS|REPLACE_BLACK_PIXELS&gt; (property: replacementType)
 * &nbsp;&nbsp;&nbsp;The type of replacement to perform.
 * &nbsp;&nbsp;&nbsp;default: REPLACE_WHITE_PIXELS
 * </pre>
 * 
 * <pre>-replacement-color &lt;java.awt.Color&gt; (property: replacementColor)
 * &nbsp;&nbsp;&nbsp;The color to replace pixels selected by the replacement type with.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7706 $
 */
public class BinaryMask
  extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /**
   * Determines what pixels to replace.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 7706 $
   */
  public enum ReplacementType {
    /** replace the white pixels. */
    REPLACE_WHITE_PIXELS,
    /** replace the black pixels. */
    REPLACE_BLACK_PIXELS
  }
  
  /** the (grayscale) threshold to use. */
  protected int m_Threshold;

  /** what pixels to replace. */
  protected ReplacementType m_ReplacementType;
  
  /** the color to replace the pixels with. */
  protected Color m_ReplacementColor;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Generates a binary image from the input and uses this mask to "
	+ "determine which pixels get replaced by the specified replacement "
	+ "color.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"threshold", "threshold",
	128, 0, 255);

    m_OptionManager.add(
	"replacement-type", "replacementType",
	ReplacementType.REPLACE_WHITE_PIXELS);

    m_OptionManager.add(
	"replacement-color", "replacementColor",
	Color.WHITE);
  }

  /**
   * Sets the threshold.
   *
   * @param value	the threshold
   */
  public void setThreshold(int value) {
    if ((value >= 0) && (value <= 255)) {
      m_Threshold = value;
      reset();
    }
    else {
      getLogger().severe("Threshold has to be 0 <= x <= 255, provided: " + value);
    }
  }

  /**
   * Returns the threshold.
   *
   * @return		the threshold
   */
  public int getThreshold() {
    return m_Threshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String thresholdTipText() {
    return "The threshold that determines whether a grayscale pixel will become black (below) or white (equal to or above).";
  }

  /**
   * Sets the type of replacement to perform.
   *
   * @param value	the type
   */
  public void setReplacementType(ReplacementType value) {
    m_ReplacementType = value;
    reset();
  }

  /**
   * Returns the type of replacement to perform.
   *
   * @return		the type
   */
  public ReplacementType getReplacementType() {
    return m_ReplacementType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String replacementTypeTipText() {
    return "The type of replacement to perform.";
  }

  /**
   * Sets the color to replace the selected pixels with.
   *
   * @param value	the color
   */
  public void setReplacementColor(Color value) {
    m_ReplacementColor = value;
    reset();
  }

  /**
   * Returns the color to replace the selected pixels with.
   *
   * @return		the color
   */
  public Color getReplacementColor() {
    return m_ReplacementColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String replacementColorTipText() {
    return "The color to replace pixels selected by the replacement type with.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "threshold", m_Threshold, "threshold: ");
    result += QuickInfoHelper.toString(this, "replacementType", m_ReplacementType, ", type: ");
    result += QuickInfoHelper.toString(this, "replacementColor", m_ReplacementColor, ", color: ");
    
    return result;
  }
  
  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[]	result;
    Binary			binary;
    BufferedImageContainer[]	mask;
    BufferedImage		image;
    BufferedImage		bimage;
    int				x;
    int				y;
    int				width;
    int				height;
    int				color;
    int				current;

    // generate binary mask
    binary = new Binary();
    binary.setThreshold(m_Threshold);
    mask = binary.transform(img);
    bimage = mask[0].getImage();

    // replace pixels
    image  = img.getImage();
    width  = image.getWidth();
    height = image.getHeight();
    color  = m_ReplacementColor.getRGB();
    for (y = 0; y < height; y++) {
      for (x = 0; x < width; x++) {
	current = bimage.getRGB(x, y) & 0x00FFFFFF;
	switch (m_ReplacementType) {
	  case REPLACE_WHITE_PIXELS:
	    if (current > 0)
	      image.setRGB(x, y, color);
	    break;
	  case REPLACE_BLACK_PIXELS:
	    if (current == 0)
	      image.setRGB(x, y, color);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled replacement type: " + m_ReplacementType);
	}
      }
    }
    
    // create output
    result = new BufferedImageContainer[1];
    result[0] = (BufferedImageContainer) img.getHeader();
    result[0].setImage(image);
    
    return result;
  }
}
