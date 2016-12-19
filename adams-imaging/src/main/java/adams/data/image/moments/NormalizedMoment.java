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
 * NormalizedMoment.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.moments;

/**
 <!-- globalinfo-start -->
 * The normalized moment.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-background-value &lt;java.awt.Color&gt; (property: backgroundValue)
 * &nbsp;&nbsp;&nbsp;The background color to use.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 * 
 * <pre>-p &lt;int&gt; (property: P)
 * &nbsp;&nbsp;&nbsp;The exponent for x.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 * 
 * <pre>-q &lt;int&gt; (property: Q)
 * &nbsp;&nbsp;&nbsp;The exponent for y.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author sjb90
 * @version $Revision$
 */
public class NormalizedMoment
  extends AbstractBufferedImageMoment {

  private static final long serialVersionUID = -7708665286209625811L;

  /** exponent for x. */
  protected int m_P;

  /** exponent for y. */
  protected int m_Q;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The normalized moment.";
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    m_OptionManager.add("p", "P", 0);
    m_OptionManager.add("q", "Q", 0);
  }

  /**
   * Sets the exponent for x.
   *
   * @param value	the exponent
   */
  public void setP(int value) {
    m_P = value;
    reset();
  }

  /**
   * Returns the exponent for x.
   *
   * @return		the exponent
   */
  public int getP() {
    return m_P;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String PTipText() {
    return "The exponent for x.";
  }

  /**
   * Sets the exponent for y.
   *
   * @param value	the exponent
   */
  public void setQ(int value) {
    m_Q = value;
    reset();
  }

  /**
   * Returns the exponent for y.
   *
   * @return		the exponent
   */
  public int getQ() {
    return m_Q;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String QTipText() {
    return "The exponent for y.";
  }

  @Override
  protected double doCalculate(boolean[][] img) {
    return MomentHelper.normalCentralMoment(img, m_P, m_Q);
  }
}
