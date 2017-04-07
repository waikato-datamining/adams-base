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

import adams.core.Utils;
import adams.core.base.BaseInterval;
import adams.flow.container.WekaEvaluationContainer;
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
 * <pre>-range &lt;adams.core.base.BaseInterval&gt; [-range ...] (property: ranges)
 * &nbsp;&nbsp;&nbsp;The ranges to include.
 * &nbsp;&nbsp;&nbsp;default: 
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

  /** the intervals. */
  protected BaseInterval[] m_Ranges;

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
      "range", "ranges",
      new BaseInterval[0]);
  }

  /**
   * Sets the ranges to include.
   *
   * @param value	the ranges
   */
  public void setRanges(BaseInterval[] value) {
    m_Ranges = value;
    reset();
  }

  /**
   * Returns the ranges to include.
   *
   * @return		the ranges
   */
  public BaseInterval[] getRanges() {
    return m_Ranges;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rangesTipText() {
    return "The ranges to include.";
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
    int					n;
    String				relName;

    result  = new ArrayList<>();
    indices = new TIntArrayList();
    eval    = (Evaluation) cont.getValue(WekaEvaluationContainer.VALUE_EVALUATION);
    for (i = 0; i < eval.predictions().size(); i++) {
      pred = eval.predictions().get(i);
      for (n = 0; n < m_Ranges.length; n++) {
	if (m_Ranges[n].isInside(pred.actual())) {
	  indices.add(i);
	  break;
	}
      }
    }
    if (m_Ranges.length == 1)
      relName = m_Ranges[0].getValue();
    else
      relName = Utils.arrayToString(m_Ranges);
    result.add(newContainer("-" + relName, cont, indices));

    return result;
  }
}
