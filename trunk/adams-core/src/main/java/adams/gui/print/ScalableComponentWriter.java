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
  *    ScalableComponentWriter.java
  *    Copyright (C) 2005,2009,2013 University of Waikato, Hamilton, New Zealand
  *
  */

package adams.gui.print;

/**
 * Abstract ancestor for scalable writers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class ScalableComponentWriter
  extends JComponentWriter {

  /** for serialization. */
  private static final long serialVersionUID = -2075028313807733655L;

  /** the x scale factor. */
  protected double m_xScale;

  /** the y scale factor. */
  protected double m_yScale;

  /** whether scaling is enabled. */
  protected boolean m_ScalingEnabled;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "scaling", "scalingEnabled",
	    false);

    m_OptionManager.add(
	    "scale-x", "XScale",
	    1.0);

    m_OptionManager.add(
	    "scale-y", "YScale",
	    1.0);
  }

  /**
   * sets whether to enable scaling.
   *
   * @param value whether scaling is enabled
   */
  public void setScalingEnabled(boolean value) {
    m_ScalingEnabled = value;
  }

  /**
   * Whether scaling is enabled or ignored.
   *
   * @return 		true if scaling is enabled
   */
  public boolean getScalingEnabled() {
    return m_ScalingEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scalingEnabledTipText() {
    return "If set to true, then scaling will be used.";
  }

  /**
   * Sets the scale factor.
   *
   * @param value 	the scale factor for the x-axis
   */
  public void setXScale(double value) {
    if (getScalingEnabled())
      m_xScale = value;
    else
      m_xScale = 1.0;

    if (isLoggingEnabled())
      getLogger().info("xScale = " + m_xScale + ", yScale = " + m_yScale);
  }

  /**
   * returns the scale factor for the x-axis.
   *
   * @return 		the scale factor for the x-axis
   */
  public double getXScale() {
    return m_xScale;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XScaleTipText() {
    return "The scaling factor for the X-axis.";
  }

  /**
   * sets the Y scale factor.
   *
   * @param value 	the scale factor for the y-axis
   */
  public void setYScale(double value) {
    if (getScalingEnabled())
      m_yScale = value;
    else
      m_yScale = 1.0;

    if (isLoggingEnabled())
      getLogger().info("xScale = " + m_xScale + ", yScale = " + m_yScale);
  }

  /**
   * returns the scale factor for the y-axis.
   *
   * @return 		the scale factor for the y-axis
   */
  public double getYScale() {
    return m_xScale;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YScaleTipText() {
    return "The scaling factor for the Y axis.";
  }
}
