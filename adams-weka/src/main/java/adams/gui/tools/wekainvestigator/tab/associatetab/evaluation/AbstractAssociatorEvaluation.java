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
 * AbstractAssociatorEvaluation.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.associatetab.evaluation;

import adams.core.ClassLister;
import adams.gui.tools.wekainvestigator.evaluation.AbstractEvaluation;
import adams.gui.tools.wekainvestigator.tab.AssociateTab;
import adams.gui.tools.wekainvestigator.tab.associatetab.ResultItem;
import org.apache.commons.lang.time.StopWatch;
import weka.associations.Associator;

/**
 * Ancestor for associator evaluation setups.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAssociatorEvaluation
  extends AbstractEvaluation<AssociateTab, ResultItem> {

  private static final long serialVersionUID = -5847790432092994409L;

  /**
   * Tests whether the associator can be evaluated.
   *
   * @param associator	the current associator
   * @return		null if successful, otherwise error message
   */
  public abstract String canEvaluate(Associator associator);

  /**
   * Initializes the result item.
   *
   * @param associator	the current associator
   * @return		the initialized history item
   * @throws Exception	if initialization fails
   */
  public abstract ResultItem init(Associator associator) throws Exception;

  /**
   * Evaluates the associator and updates the result item.
   *
   * @param associator	the current associator
   * @param item	the item to update
   * @throws Exception	if evaluation fails
   */
  protected abstract void doEvaluate(Associator associator, ResultItem item) throws Exception;

  /**
   * Evaluates the associator and updates the result item.
   *
   * @param associator	the current associator
   * @param item	the item to update
   * @throws Exception	if evaluation fails
   */
  public void evaluate(Associator associator, ResultItem item) throws Exception {
    StopWatch   watch;

    watch = new StopWatch();
    watch.start();
    doEvaluate(associator, item);
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
    return ClassLister.getSingleton().getClasses(AbstractAssociatorEvaluation.class);
  }
}
