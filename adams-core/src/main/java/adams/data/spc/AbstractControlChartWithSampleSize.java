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
 * AbstractControlChartWithSampleSize.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.spc;

/**
 * Ancestor for control charts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractControlChartWithSampleSize
  extends AbstractControlChart {

  private static final long serialVersionUID = 6551757750425372768L;

  /** the sample size. */
  protected int m_SampleSize;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "sample-size", "sampleSize",
      getDefaultSampleSize(),
      getDefaultSampleSizeLowerLimit(),
      getDefaultSampleSizeUpperLimit());
  }

  /**
   * Returns the default lower limit for the sample size.
   *
   * @return		the default lower limit
   */
  protected Number getDefaultSampleSizeLowerLimit() {
    return 1;
  }

  /**
   * Returns the default upper limit for the sample size.
   *
   * @return		the default upper limit
   */
  protected Number getDefaultSampleSizeUpperLimit() {
    return null;
  }

  /**
   * Returns the default sample size.
   *
   * @return		the default size
   */
  protected abstract int getDefaultSampleSize();

  /**
   * Returns the chart name.
   *
   * @return		the chart name
   */
  public abstract String getName();

  /**
   * Sets the sample size.
   *
   * @param value	the sample size
   */
  public void setSampleSize(int value) {
    if ((getDefaultSampleSizeLowerLimit() != null) && (value < getDefaultSampleSizeLowerLimit().intValue())) {
      getLogger().warning("Sample size must be at least " + getDefaultSampleSizeLowerLimit() + ", provided: " + value);
      return;
    }
    if ((getDefaultSampleSizeUpperLimit() != null) && (value > getDefaultSampleSizeUpperLimit().intValue())) {
      getLogger().warning("Sample size must be at most " + getDefaultSampleSizeUpperLimit() + ", provided: " + value);
      return;
    }
    m_SampleSize = value;
    reset();
  }

  /**
   * Returns the sample size.
   *
   * @return		the sample size
   */
  public int getSampleSize() {
    return m_SampleSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sampleSizeTipText() {
    return "The sample size to use.";
  }
}
