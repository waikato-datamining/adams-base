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
 * AbstractAttributeSelectionEvaluation.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.attseltab.evaluation;

import adams.core.ClassLister;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.tools.wekainvestigator.evaluation.AbstractEvaluation;
import adams.gui.tools.wekainvestigator.tab.AttributeSelectionTab;
import adams.gui.tools.wekainvestigator.tab.attseltab.ResultItem;
import org.apache.commons.lang.time.StopWatch;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;

/**
 * Ancestor for attribute selection evaluation setups.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAttributeSelectionEvaluation
  extends AbstractEvaluation<AttributeSelectionTab, ResultItem> {

  private static final long serialVersionUID = -5847790432092994409L;

  /**
   * Tests whether attribute selection can be performed.
   *
   * @return		null if successful, otherwise error message
   */
  public abstract String canEvaluate(ASEvaluation evaluator, ASSearch search);

  /**
   * Performs attribute selections and returns the generated evaluation object.
   *
   * @param history	the history to add the result to
   * @return		the generate history item
   * @throws Exception	if evaluation fails
   */
  protected abstract ResultItem doEvaluate(ASEvaluation evaluator, ASSearch search, AbstractNamedHistoryPanel<ResultItem> history) throws Exception;

  /**
   * Performs attribute selections and returns the generated evaluation object.
   *
   * @param history	the history to add the result to
   * @return		the generate history item
   * @throws Exception	if evaluation fails
   */
  public ResultItem evaluate(ASEvaluation evaluator, ASSearch search, AbstractNamedHistoryPanel<ResultItem> history) throws Exception {
    ResultItem 	result;
    StopWatch 	watch;

    watch = new StopWatch();
    watch.start();
    result = doEvaluate(evaluator, search, history);
    watch.stop();
    if (result.hasRunInformation())
      result.getRunInformation().add("Total time", (watch.getTime() / 1000.0) + "s");

    return result;
  }

  /**
   * Returns the available actions.
   *
   * @return		the action classnames
   */
  public static Class[] getEvaluations() {
    return ClassLister.getSingleton().getClasses(AbstractAttributeSelectionEvaluation.class);
  }
}
