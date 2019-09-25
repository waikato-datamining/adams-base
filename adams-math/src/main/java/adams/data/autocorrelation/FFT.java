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
 * FFT.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.autocorrelation;

import adams.core.License;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.core.annotation.MixedCopyright;
import adams.data.utils.AutoCorrelation;

/**
 <!-- globalinfo-start -->
 * Uses FFT approach to autocorrelation.<br>
 * <br>
 * For more information see:<br>
 * WikiPedia. Autocorrelation.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;misc{missing_id,
 *    author = {WikiPedia},
 *    title = {Autocorrelation},
 *    HTTP = {https:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Autocorrelation}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-normalize &lt;boolean&gt; (property: normalize)
 * &nbsp;&nbsp;&nbsp;Whether to normalize the data.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
  author = "Gene - http://stackoverflow.com/users/1161878/gene",
  license = License.CC_BY_SA_3,
  url = "http://stackoverflow.com/a/12453487/4698227"
)
public class FFT
  extends AbstractAutoCorrelation
  implements TechnicalInformationHandler {

  private static final long serialVersionUID = 3413225445542399689L;

  /** whether to normalize. */
  protected boolean m_Normalize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Uses FFT approach to autocorrelation.\n\n"
      + "For more information see:\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "normalize", "normalize",
	    false);
  }

  /**
   * Sets whether to normalize the data.
   *
   * @param value	true if to normalize
   */
  public void setNormalize(boolean value) {
    m_Normalize = value;
    reset();
  }

  /**
   * Returns whether to normalize the data.
   *
   * @return		true if to normalize
   */
  public boolean getNormalize() {
    return m_Normalize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String normalizeTipText() {
    return "Whether to normalize the data.";
  }

  /**
   * Returns technical information on autocorrelation.
   *
   * @return		the technical information
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    return AutoCorrelation.getTechnicalInformation();
  }

  /**
   * Hook method for checks.
   *
   * @param data	the data to check
   */
  @Override
  protected void check(double[] data) {
    super.check(data);
    if (data.length % 2 != 0)
      throw new IllegalStateException(
	"Length of signal array must be even, provided: " + data.length);
  }

  /**
   * Performs the actual autocorrelation on the data.
   *
   * @param data	the data to process
   * @return		the processed data
   */
  @Override
  protected double[] doCorrelate(double[] data) {
    return AutoCorrelation.fft(data, m_Normalize);
  }
}
