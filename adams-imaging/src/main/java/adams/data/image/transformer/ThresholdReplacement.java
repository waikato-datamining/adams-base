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
 * ThresholdReplacement.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Replaces pixels of the image that fall below or above (depending on configuration) a user defined threshold in the grayscale space with the supplied replacement color.<br>
 * Can be replaced to remove dark or light splotches.
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
 * &nbsp;&nbsp;&nbsp;The threshold (in grayscale space) that determines whether pixel gets replaced.
 * &nbsp;&nbsp;&nbsp;default: 128
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 255
 * </pre>
 * 
 * <pre>-type &lt;REMOVE_BELOW|REMOVE_ABOVE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;Defines how the threshold is interpreted.
 * &nbsp;&nbsp;&nbsp;default: REMOVE_BELOW
 * </pre>
 * 
 * <pre>-replacement-color &lt;java.awt.Color&gt; (property: replacementColor)
 * &nbsp;&nbsp;&nbsp;The color to use as replacement.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7706 $
 */
public class ThresholdReplacement
  extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /**
   * How to interpret the threshold.
   */
  public enum ThresholdType {
    REMOVE_BELOW,
    REMOVE_ABOVE
  }

  /** the (grayscale) threshold to use. */
  protected int m_Threshold;

  /** whether to remove below or above threshold. */
  protected ThresholdType m_Type;

  /** the replacement color. */
  protected Color m_ReplacementColor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Replaces pixels of the image that fall below or above (depending on "
          + "configuration) a user defined threshold in the grayscale space "
          + "with the supplied replacement color.\n"
          + "Can be replaced to remove dark or light splotches.";
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
	"type", "type",
	ThresholdType.REMOVE_BELOW);

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
    if (getOptionManager().isValid("threshold", value)) {
      m_Threshold = value;
      reset();
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
    return "The threshold (in grayscale space) that determines whether pixel gets replaced.";
  }

  /**
   * Sets the type of removal.
   *
   * @param value	the type of removal
   */
  public void setType(ThresholdType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of removal.
   *
   * @return		the type of removal
   */
  public ThresholdType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String typeTipText() {
    return "Defines how the threshold is interpreted.";
  }

  /**
   * Sets the replacement color.
   *
   * @param value	the color
   */
  public void setReplacementColor(Color value) {
    m_ReplacementColor = value;
    reset();
  }

  /**
   * Returns the replacement color.
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
    return "The color to use as replacement.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "threshold", m_Threshold, "threshold: ");
    result += QuickInfoHelper.toString(this, "type", m_Type, ", type: ");
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
    int 			width;
    int 			height;
    BufferedImage 		imageGray;
    BufferedImage		imageNew;
    int				x;
    int				y;
    int[]			channels;
    int				replace;

    width     = img.getWidth();
    height    = img.getHeight();
    result    = new BufferedImageContainer[1];
    result[0] = (BufferedImageContainer) img.getHeader();
    imageGray = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_BYTE_GRAY);
    imageNew  = BufferedImageHelper.deepCopy(img.getImage());
    replace   = m_ReplacementColor.getRGB();

    for (y = 0; y < height; y++) {
      for (x = 0; x < width; x++) {
	channels = BufferedImageHelper.split(imageGray.getRGB(x, y));
	switch (m_Type) {
	  case REMOVE_ABOVE:
	    if (channels[0] > m_Threshold)
	      imageNew.setRGB(x, y, replace);
	    break;
	  case REMOVE_BELOW:
	    if (channels[0] < m_Threshold)
	      imageNew.setRGB(x, y, replace);
	    break;
	}
      }
    }

    result[0].setImage(imageNew);

    return result;
  }
}
