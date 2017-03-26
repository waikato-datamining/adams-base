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
 * SubRange.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekaevaluationpostprocessor;

import adams.flow.container.WekaEvaluationContainer;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Generates an Evaluation object based on the actual class values that fall within the specified min&#47;max range.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-min &lt;double&gt; (property: min)
 * &nbsp;&nbsp;&nbsp;The minimum value to include.
 * &nbsp;&nbsp;&nbsp;default: -Infinity
 * </pre>
 * 
 * <pre>-max &lt;double&gt; (property: max)
 * &nbsp;&nbsp;&nbsp;The maximum value to include.
 * &nbsp;&nbsp;&nbsp;default: Infinity
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SubRange
  extends AbstractWekaEvaluationPostProcessor {

  private static final long serialVersionUID = -1598212513856588223L;

  /** the minimum value to include. */
  protected double m_Min;

  /** the maximum value to include. */
  protected double m_Max;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates an Evaluation object based on the actual class values that "
	+ "fall within the specified min/max range.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "min", "min",
      Double.NEGATIVE_INFINITY);

    m_OptionManager.add(
      "max", "max",
      Double.POSITIVE_INFINITY);
  }

  /**
   * Sets the minimum value to include.
   *
   * @param value	the minimum
   */
  public void setMin(double value) {
    m_Min = value;
    reset();
  }

  /**
   * Returns the minimum value to include.
   *
   * @return		the minimum
   */
  public double getMin() {
    return m_Min;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minTipText() {
    return "The minimum value to include.";
  }

  /**
   * Sets the maximum value to include.
   *
   * @param value	the maximum
   */
  public void setMax(double value) {
    m_Max = value;
    reset();
  }

  /**
   * Returns the maximum value to include.
   *
   * @return		the maximum
   */
  public double getMax() {
    return m_Max;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxTipText() {
    return "The maximum value to include.";
  }

  /**
   * Checks the container whether it can be processed.
   *
   * @param cont	the container to check
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(WekaEvaluationContainer cont) {
    String	result;
    Evaluation	eval;

    result = super.check(cont);

    if (result == null) {
      eval = (Evaluation) cont.getValue(WekaEvaluationContainer.VALUE_EVALUATION);
      if (!eval.getHeader().classAttribute().isNumeric())
	result = "Class attribute is not numeric!";
    }

    return result;
  }

  /**
   * Post-processes the evaluation container.
   *
   * @param cont	the container to post-process
   * @return		the generated evaluation containers
   */
  @Override
  protected List<WekaEvaluationContainer> doPostProcess(WekaEvaluationContainer cont) {
    List<WekaEvaluationContainer>	result;
    Evaluation				eval;
    Prediction				pred;
    TIntList 				indices;
    int					i;

    result  = new ArrayList<>();
    indices = new TIntArrayList();
    eval    = (Evaluation) cont.getValue(WekaEvaluationContainer.VALUE_EVALUATION);
    for (i = 0; i < eval.predictions().size(); i++) {
      pred = eval.predictions().get(i);
      if ((pred.actual() >= m_Min) && (pred.actual() <= m_Max))
	indices.add(i);
    }
    result.add(newContainer("-min:" + m_Min + ",max:" + m_Max, cont, indices));

    return result;
  }
}
