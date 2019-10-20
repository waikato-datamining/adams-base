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
 * SimpleSubRange.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekaevaluationpostprocessor;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseInterval;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Generates an Evaluation object based on the actual class values that fall within the specified interval ranges.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-minimum &lt;double&gt; (property: minimum)
 * &nbsp;&nbsp;&nbsp;The minimum value that the values must satisfy; use NaN (not a number) to
 * &nbsp;&nbsp;&nbsp;ignore minimum.
 * &nbsp;&nbsp;&nbsp;default: NaN
 * </pre>
 *
 * <pre>-minimum-included &lt;boolean&gt; (property: minimumIncluded)
 * &nbsp;&nbsp;&nbsp;If enabled, then the minimum value gets included (testing '&lt;=' rather than
 * &nbsp;&nbsp;&nbsp;'&lt;').
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-maximum &lt;double&gt; (property: maximum)
 * &nbsp;&nbsp;&nbsp;The maximum value that the values must satisfy; use NaN (not a number) to
 * &nbsp;&nbsp;&nbsp;ignore maximum.
 * &nbsp;&nbsp;&nbsp;default: NaN
 * </pre>
 *
 * <pre>-maximum-included &lt;boolean&gt; (property: maximumIncluded)
 * &nbsp;&nbsp;&nbsp;If enabled, then the maximum value gets included (testing '&gt;=' rather than
 * &nbsp;&nbsp;&nbsp;'&gt;').
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SimpleSubRange
  extends AbstractNumericClassPostProcessor {

  private static final long serialVersionUID = -1598212513856588223L;

  /** the placeholder for NaN. */
  public final static String NAN = "NaN";

  /** the minimum value. */
  protected double m_Minimum;

  /** whether the minimum value is included. */
  protected boolean m_MinimumIncluded;

  /** the maximum value. */
  protected double m_Maximum;

  /** whether the maximum value is included. */
  protected boolean m_MaximumIncluded;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates an Evaluation object based on the actual class values that "
	+ "fall within the specified interval ranges.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "minimum", "minimum",
      Double.NaN);

    m_OptionManager.add(
      "minimum-included", "minimumIncluded",
      false);

    m_OptionManager.add(
      "maximum", "maximum",
      Double.NaN);

    m_OptionManager.add(
      "maximum-included", "maximumIncluded",
      false);
  }

  /**
   * Sets the minimum.
   *
   * @param value	the minimum
   */
  public void setMinimum(double value) {
    m_Minimum = value;
    reset();
  }

  /**
   * Returns the minimum.
   *
   * @return		the minimum
   */
  public double getMinimum() {
    return m_Minimum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minimumTipText() {
    return
	"The minimum value that the values must satisfy; use " + NAN + " (not a "
	+ "number) to ignore minimum.";
  }

  /**
   * Sets whether to exclude the minimum.
   *
   * @param value	true to exclude minimum
   */
  public void setMinimumIncluded(boolean value) {
    m_MinimumIncluded = value;
    reset();
  }

  /**
   * Returns whether the minimum is included.
   *
   * @return		true if minimum included
   */
  public boolean getMinimumIncluded() {
    return m_MinimumIncluded;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minimumIncludedTipText() {
    return "If enabled, then the minimum value gets included (testing '<=' rather than '<').";
  }

  /**
   * Sets the maximum.
   *
   * @param value	the maximum
   */
  public void setMaximum(double value) {
    m_Maximum = value;
    reset();
  }

  /**
   * Returns the maximum.
   *
   * @return		the maximum
   */
  public double getMaximum() {
    return m_Maximum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maximumTipText() {
    return
	"The maximum value that the values must satisfy; use " + NAN + " (not a "
	+ "number) to ignore maximum.";
  }

  /**
   * Sets whether to exclude the maximum.
   *
   * @param value	true to exclude maximum
   */
  public void setMaximumIncluded(boolean value) {
    m_MaximumIncluded = value;
    reset();
  }

  /**
   * Returns whether the maximum is included.
   *
   * @return		true if maximum included
   */
  public boolean getMaximumIncluded() {
    return m_MaximumIncluded;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maximumIncludedTipText() {
    return "If enabled, then the maximum value gets included (testing '>=' rather than '>').";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = m_MinimumIncluded ? "[" : "(";
    result += QuickInfoHelper.toString(this, "minimum", m_Minimum);
    result += ";";
    result += QuickInfoHelper.toString(this, "maximum", m_Maximum);
    result += m_MaximumIncluded ? "]": ")";

    return result;
  }

  /**
   * Post-processes the evaluation.
   *
   * @param eval	the Evaluation to post-process
   * @return		the generated evaluations
   */
  @Override
  protected List<Evaluation> doPostProcess(Evaluation eval) {
    List<Evaluation>	result;
    Prediction		pred;
    TIntList 		indices;
    int			i;
    BaseInterval	interval;
    double		value;
    boolean		add;

    result  = new ArrayList<>();
    indices = new TIntArrayList();
    for (i = 0; i < eval.predictions().size(); i++) {
      pred  = eval.predictions().get(i);
      value = pred.actual();
      add   = true;
      if (!Double.isNaN(m_Minimum)) {
	if (m_MinimumIncluded) {
	  if (value < m_Minimum)
	    add = false;
	}
	else {
	  if (value <= m_Minimum)
	    add = false;
	}
      }
      if (!Double.isNaN(m_Maximum)) {
	if (m_MaximumIncluded) {
	  if (value > m_Maximum)
	    add = false;
	}
	else {
	  if (value >= m_Maximum)
	    add = false;
	}
      }
      if (add)
	indices.add(i);
    }
    interval = new BaseInterval(m_Minimum, m_MinimumIncluded, m_Maximum, m_MaximumIncluded);
    result.add(newEvaluation("-" + interval, eval, indices));

    return result;
  }
}
