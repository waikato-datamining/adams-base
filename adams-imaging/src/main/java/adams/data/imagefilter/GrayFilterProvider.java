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
 * GrayFilterProvider.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagefilter;

import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;

import javax.swing.GrayFilter;

/**
 <!-- globalinfo-start -->
 * Generates a javax.swing.GrayFilter image filter.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-brighten-pixels &lt;boolean&gt; (property: brightenPixels)
 * &nbsp;&nbsp;&nbsp;If enabled, the pixels get brightened.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-gray-percentage &lt;int&gt; (property: grayPercentage)
 * &nbsp;&nbsp;&nbsp;The gray percentage: 0 = lightest, 100 = darkest.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 100
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GrayFilterProvider
  extends AbstractImageFilterProvider {

  /** for serialization. */
  private static final long serialVersionUID = -370613834879391406L;

  /** whether to brighten the pixels. */
  protected boolean m_BrightenPixels;
  
  /** the gray percentage. */
  protected int m_GrayPercentage;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a " + GrayFilter.class.getName() + " image filter.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "brighten-pixels", "brightenPixels",
	    false);

    m_OptionManager.add(
	    "gray-percentage", "grayPercentage",
	    0, 0, 100);
  }

  /**
   * Sets whether to brighten the pixels.
   *
   * @param value	true if to brighten
   */
  public void setBrightenPixels(boolean value) {
    m_BrightenPixels = value;
    reset();
  }

  /**
   * Returns whether to brighten the pixels.
   *
   * @return		true if to brighten
   */
  public boolean getBrightenPixels() {
    return m_BrightenPixels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String brightenPixelsTipText() {
    return "If enabled, the pixels get brightened.";
  }

  /**
   * Sets the gray percentage (0 lightest, 100 darkest).
   *
   * @param value	the percentage (0-100)
   */
  public void setGrayPercentage(int value) {
    if ((value >= 0) && (value <= 100)) {
      m_GrayPercentage = value;
      reset();
    }
    else {
      getLogger().warning("Gray percentage must be 0 <= x <= 100, provided: " + value);
    }
  }

  /**
   * Returns the gray percentage (0 lightest, 100 darkest).
   *
   * @return		the percentage (0-100)
   */
  public int getGrayPercentage() {
    return m_GrayPercentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String grayPercentageTipText() {
    return "The gray percentage: 0 = lightest, 100 = darkest.";
  }

  /**
   * Generates the actor {@link ImageFilter} instance.
   * 
   * @param img		the buffered image to filter
   * @return		the image filter instance
   */
  @Override
  protected ImageFilter doGenerate(BufferedImage img) {
    return new GrayFilter(m_BrightenPixels, m_GrayPercentage);
  }
}
