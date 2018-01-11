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
   * Initializes the result item.
   *
   * @param evaluator	the current evaluator
   * @param search 	the current search
   * @return		the initialized history item
   * @throws Exception	if initialization fails
   */
  public abstract ResultItem init(ASEvaluation evaluator, ASSearch search) throws Exception;

  /**
   * Performs attribute selections and updates the result item.
   *
   * @param evaluator	the current evaluator
   * @param search 	the current search
   * @param item 	the result item to update
   * @throws Exception	if evaluation fails
   */
  protected abstract void doEvaluate(ASEvaluation evaluator, ASSearch search, ResultItem item) throws Exception;

  /**
   * Performs attribute selections and updates the result item.
   *
   * @param evaluator	the current evaluator
   * @param search 	the current search
   * @param item 	the result item to update
   * @throws Exception	if evaluation fails
   */
  public void evaluate(ASEvaluation evaluator, ASSearch search, ResultItem item) throws Exception {
    StopWatch 	watch;

    watch = new StopWatch();
    watch.start();
    doEvaluate(evaluator, search, item);
    watch.stop();
    if (item.hasRunInformation())
      item.getRunInformation().add("Total time", (watch.getTime() / 1000.0) + "s");
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
