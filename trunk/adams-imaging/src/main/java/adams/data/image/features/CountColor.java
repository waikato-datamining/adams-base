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
 * CountColor.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.features;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.report.DataType;
import adams.gui.core.ColorHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Counts the occurrences of a specific color (alpha channel gets ignored).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-converter &lt;adams.data.featureconverter.AbstractFeatureConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The feature converter to use to produce the output data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featureconverter.SpreadSheet -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.SpreadSheet
 * </pre>
 * 
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to add to the output.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-notes &lt;adams.core.base.BaseString&gt; [-notes ...] (property: notes)
 * &nbsp;&nbsp;&nbsp;The notes to add as attributes to the generated data, eg 'PROCESS INFORMATION'
 * &nbsp;&nbsp;&nbsp;.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color to count.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 * 
 * <pre>-use-percentage &lt;boolean&gt; (property: usePercentage)
 * &nbsp;&nbsp;&nbsp;If enabled, a percentage is output (0-1) rather than an absolute count.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision: 9196 $
 */
public class CountColor
  extends AbstractBufferedImageFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /** the color to count. */
  protected Color m_Color;

  /** whether to return the percentage instead of absolute count. */
  protected boolean m_UsePercentage;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Counts the occurrences of a specific color (alpha channel gets ignored).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color", "color",
      Color.WHITE);

    m_OptionManager.add(
      "use-percentage", "usePercentage",
      false);
  }

  /**
   * Sets the color to count.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color to count.
   *
   * @return		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color to count.";
  }

  /**
   * Sets whether to output percentage instead of absolute count.
   *
   * @param value	the color
   */
  public void setUsePercentage(boolean value) {
    m_UsePercentage = value;
    reset();
  }

  /**
   * Returns whether to output percentage instead of absolute count.
   *
   * @return		true if to output percentage
   */
  public boolean getUsePercentage() {
    return m_UsePercentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String usePercentageTipText() {
    return "If enabled, a percentage is output (0-1) rather than an absolute count.";
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(BufferedImageContainer img) {
    HeaderDefinition	result;

    result = new HeaderDefinition();
    result.add("Count-" + ColorHelper.toHex(m_Color), DataType.NUMERIC);

    return result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(BufferedImageContainer img) {
    List<Object>[]		result;
    BufferedImage		image;
    int[]			pixels;
    int				i;
    int				count;
    int				color;

    image     = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_4BYTE_ABGR);
    count     = 0;
    color     = m_Color.getRGB() & 0x00FFFFFF;
    pixels    = BufferedImageHelper.getPixels(image);
    // remove alpha channel
    for (i = 0; i < pixels.length; i++) {
      if ((pixels[i] & 0x00FFFFFF) == color)
	count++;
    }

    result    = new List[1];
    result[0] = new ArrayList<>();
    if (m_UsePercentage)
      result[0].add((double) count / (double) pixels.length);
    else
      result[0].add(count);

    return result;
  }
}
