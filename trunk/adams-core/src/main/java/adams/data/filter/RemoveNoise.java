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
 * RemoveNoise.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.List;

import adams.core.Mergeable;
import adams.data.container.DataContainer;
import adams.data.noise.AbstractDenoiser;

/**
 <!-- globalinfo-start -->
 * A filter that removes noise from the data with a user-supplied noise level algorithm.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-denoiser &lt;gcms.data.noise.AbstractDenoiser [options]&gt; (property: denoiser)
 *         The denoiser algorithm to use for removing the noise from the data.
 *         default: gcms.data.noise.MedianHeightDifference -factor-sd 2.0 -thr2 -1.0 -savitzky "gcms.data.filter.SavitzkyGolay -polynomial 2 -derivative 0 -left 3 -right 3 -optimize 15" -derivative "gcms.data.filter.Derivative -order 2 -scaling -1.0"
 * </pre>
 *
 * <pre>-invert (property: invert)
 *         If set to true, non-noise will be removed instead of noise.
 * </pre>
 *
 * Default options for gcms.data.noise.MedianHeightDifference (-denoiser/denoiser):
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-zero (property: zeroNoise)
 *         If set to true, the abundances of noisy points will be set to zero instead
 *          of being completely removed from the data.
 * </pre>
 *
 * <pre>-factor-sd &lt;double&gt; (property: thresholdSdFactor)
 *         The factor to multiply the height median (thr_sd) with in order to obtain
 *          the threshold below signals are considered being noise.
 *         default: 2
 * </pre>
 *
 * <pre>-thr2 &lt;double&gt; (property: threshold2)
 *         A user-supplied threshold (= thr_2) that is applied to the abundances of
 *          the original input data, use -1 to disable.
 *         default: -1.0
 * </pre>
 *
 * <pre>-savitzky &lt;gcms.data.filter.AbstractFilter [options]&gt; (property: savitzkyGolay)
 *         The Savitzky-Golay filter setup to use in smoothing the data beforehand.
 *         default: gcms.data.filter.SavitzkyGolay -polynomial 2 -derivative 0 -left 3 -right 3 -optimize 15
 * </pre>
 *
 * <pre>-derivative &lt;gcms.data.filter.AbstractFilter [options]&gt; (property: derivative)
 *         The Derivative filter to use (always uses 2nd derivative).
 *         default: gcms.data.filter.Derivative -order 2 -scaling -1.0
 * </pre>
 *
 * <pre>-regions (property: recordRegions)
 *         If set to true, the elution regions will be recorded as well.
 * </pre>
 *
 * <pre>Default options for gcms.data.filter.SavitzkyGolay (-savitzky/savitzkyGolay):
 * </pre>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-polynomial &lt;int&gt; (property: polynomialOrder)
 *         The polynomial order to use, must be at least 2.
 *         default: 2
 * </pre>
 *
 * <pre>-derivative &lt;int&gt; (property: derivativeOrder)
 *         The order of the derivative to use, &gt;= 0.
 *         default: 1
 * </pre>
 *
 * <pre>-left &lt;int&gt; (property: numPointsLeft)
 *         The number of points left of a data point, &gt;= 0.
 *         default: 3
 * </pre>
 *
 * <pre>-right &lt;int&gt; (property: numPointsRight)
 *         The number of points right of a data point, &gt;= 0.
 *         default: 3
 * </pre>
 *
 * <pre>-optimize &lt;int&gt; (property: optimizeWindowSize)
 *         The maximum window size to optimize (odd, positive number); uses the Durbin-Watson
 *          statistic to determine the best window size, ie, number of points left
 *         and right of the points being smoothed; the data must be oscillating around
 *          zero, ie, passed through the Derivative filter.
 *         default: -1
 * </pre>
 *
 * <pre></pre>
 *
 * <pre>Default options for gcms.data.filter.Derivative (-derivative/derivative):
 * </pre>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-order &lt;int&gt; (property: order)
 *         The order of the derivative to calculate.
 *         default: 1
 * </pre>
 *
 * <pre>-scaling &lt;double&gt; (property: scalingRange)
 *         The range to scale the abundances to after each derivation step; use 0 to
 *          turn off and -1 to set it to the input range.
 *         default: 0.0
 * </pre>
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data to process
 */
public class RemoveNoise<T extends DataContainer & Mergeable>
  extends AbstractFilter<T> {

  /** for serialization. */
  private static final long serialVersionUID = 4225773526265205077L;

  /** the noise level algorithm. */
  protected AbstractDenoiser m_Denoiser;

  /** indicates whether to invert behavior, i.e., to remove non-noise instead
   * of noise. */
  protected boolean m_Invert;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A filter that removes noise from the data with a user-supplied "
      + "noise level algorithm.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "denoiser", "denoiser",
	    new adams.data.noise.PassThrough());

    m_OptionManager.add(
	    "invert", "invert",
	    false);
  }

  /**
   * Sets the denoiser algorithm.
   *
   * @param value 	the algorithm
   */
  public void setDenoiser(AbstractDenoiser value) {
    m_Denoiser = value;
    m_Denoiser.setRecordRegions(true);
    reset();
  }

  /**
   * Returns the current denoiser algorithm.
   *
   * @return 		the algorithm
   */
  public AbstractDenoiser getDenoiser() {
    return m_Denoiser;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String denoiserTipText() {
    return "The denoiser algorithm to use for removing the noise from the data.";
  }

  /**
   * Sets whether the behavior should be inverted, i.e., removing non-noise.
   *
   * @param value 	true if non-noise should be removed
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether non-noise is removed (= true) or the actual noise.
   *
   * @return 		true if non-noise is removed
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If set to true, non-noise will be removed instead of noise.";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected T processData(T data) {
    T			result;
    AbstractDenoiser<T>	denoiser;
    List<T>		regions;
    int			i;

    denoiser = (AbstractDenoiser<T>) m_Denoiser.shallowCopy(true);
    denoiser.setRecordRegions(true);
    result = denoiser.denoise(data);
    if (m_Invert) {
      result  = (T) result.getHeader();
      regions = denoiser.getRegions();
      for (i = 0; i < regions.size(); i++)
	result.mergeWith(regions.get(i));
    }
    denoiser.destroy();

    if (isLoggingEnabled())
      getLogger().info(
	  "Number of points: input=" + data.size()
	  + ", output=" + result.size());

    return result;
  }
}
