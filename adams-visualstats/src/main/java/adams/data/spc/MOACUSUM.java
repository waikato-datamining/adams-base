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
 * MOACUSUM.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 * Copyright (C) 2014 - 2015 Apache Software Foundation
 */

package adams.data.spc;

import adams.core.License;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.annotation.MixedCopyright;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * CUSUM (cumulative sum control chart) is a sequential analysis technique developed by E. S. Page of the University of Cambridge.<br>
 * <br>
 * For more information see:<br>
 * E. S. Page. CUSUM. URL https:&#47;&#47;en.wikipedia.org&#47;wiki&#47;CUSUM.<br>
 * This version is based on MOA's drift detector:<br>
 * https:&#47;&#47;github.com&#47;apache&#47;incubator-samoa&#47;blob&#47;9b178f63152e5b4c262e0f3ed28e77667832fc98&#47;samoa-api&#47;src&#47;main&#47;java&#47;org&#47;apache&#47;samoa&#47;moa&#47;classifiers&#47;core&#47;driftdetection&#47;CusumDM.java
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-min-num-values &lt;int&gt; (property: minNumValues)
 * &nbsp;&nbsp;&nbsp;The minimum number of values to process before testing.
 * &nbsp;&nbsp;&nbsp;default: 30
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-delta &lt;double&gt; (property: delta)
 * &nbsp;&nbsp;&nbsp;The delta parameter.
 * &nbsp;&nbsp;&nbsp;default: 0.05
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-lambda &lt;double&gt; (property: lambda)
 * &nbsp;&nbsp;&nbsp;The threshold for the test.
 * &nbsp;&nbsp;&nbsp;default: 4.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
  author = "Manuel Baena (mbaena@lcc.uma.es)",
  copyright = "2014 - 2015 Apache Software Foundation",
  url = "https://github.com/apache/incubator-samoa/blob/9b178f63152e5b4c262e0f3ed28e77667832fc98/samoa-api/src/main/java/org/apache/samoa/moa/classifiers/core/driftdetection/CusumDM.java",
  license = License.APACHE2
)
public class MOACUSUM
  extends AbstractControlChart
  implements IndividualsControlChart {

  private static final long serialVersionUID = -8104165645635976186L;

  /** the minimum number of values before testing. */
  protected int m_MinNumValues;

  /** the delta parameter. */
  protected double m_Delta;

  /** the lambda parameter. */
  protected double m_Lambda;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "CUSUM (cumulative sum control chart) is a sequential analysis technique "
        + "developed by E. S. Page of the University of Cambridge.\n\n"
        + "For more information see:\n"
        + getTechnicalInformation() + "\n"
        + "This version is based on MOA's drift detector:\n"
        + "https://github.com/apache/incubator-samoa/blob/9b178f63152e5b4c262e0f3ed28e77667832fc98/samoa-api/src/main/java/org/apache/samoa/moa/classifiers/core/driftdetection/CusumDM.java";
  }

  /**
   * Returns the chart name.
   *
   * @return		the chart name
   */
  @Override
  public String getName() {
    return "MOACUSUM";
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "E. S. Page");
    result.setValue(Field.TITLE, "CUSUM");
    result.setValue(Field.URL, "https://en.wikipedia.org/wiki/CUSUM");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "min-num-values", "minNumValues",
      30, 1, null);

    m_OptionManager.add(
      "delta", "delta",
      0.05, 0.0, null);

    m_OptionManager.add(
      "lambda", "lambda",
      4.0, 0.0, null);
  }

  /**
   * Sets the minimum number of values to process before testing.
   *
   * @param value	the number of values
   */
  public void setMinNumValues(int value) {
    if (getOptionManager().isValid("minNumValues", value)) {
      m_MinNumValues = value;
      reset();
    }
  }

  /**
   * Returns the minimum number of values to process before testing.
   *
   * @return		the number of values
   */
  public int getMinNumValues() {
    return m_MinNumValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minNumValuesTipText() {
    return "The minimum number of values to process before testing.";
  }

  /**
   * Sets the delta parameter.
   *
   * @param value	the delta
   */
  public void setDelta(double value) {
    if (getOptionManager().isValid("delta", value)) {
      m_Delta = value;
      reset();
    }
  }

  /**
   * Returns the delta parameter.
   *
   * @return		the delta
   */
  public double getDelta() {
    return m_Delta;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String deltaTipText() {
    return "The delta parameter.";
  }

  /**
   * Sets the threshold for the test.
   *
   * @param value	the threshold
   */
  public void setLambda(double value) {
    if (getOptionManager().isValid("lambda", value)) {
      m_Lambda = value;
      reset();
    }
  }

  /**
   * Returns the threshold for the test.
   *
   * @return		the threshold
   */
  public double getLambda() {
    return m_Lambda;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lambdaTipText() {
    return "The threshold for the test.";
  }

  /**
   * Calculates center/lower/upper limit.
   *
   * @param data	the data to use for the calculation
   * @return		center/lower/upper
   */
  @Override
  public List<Limits> calculate(Number[] data) {
    List<Limits>	result;
    int			i;

    result = new ArrayList<>();
    for (i = 0; i < data.length; i++)
      result.add(new Limits(0.0, m_Lambda / 2, m_Lambda));

    return result;
  }

  /**
   * Prepares the data.
   *
   * @param data	the data to prepare
   * @return		the prepared/processed data
   */
  @Override
  public double[] prepare(Number[] data) {
    TDoubleList 	result;
    double		x_mean;
    double 		shi;
    double 		slo;
    double		x;
    int			i;

    result = new TDoubleArrayList();

    x_mean = 0.0;
    shi    = 0.0;
    slo    = 0.0;
    for (i = 0; i < data.length; i++) {
      x      = data[i].doubleValue();
      x_mean = x_mean + (x - x_mean) / (i + 1.0);
      shi    = Math.max(0, shi + x - x_mean - m_Delta);
      slo    = Math.max(0, slo + x_mean - m_Delta - x);
      if ((i + 1) >= m_MinNumValues) {
	result.add(Math.max(shi, slo));
	if (isLoggingEnabled())
	  getLogger().info((i+1) + ". " + "x=" + Math.max(shi, slo) + ", slo=" + slo + ", shi=" + shi);
      }
    }

    return result.toArray();
  }
}
