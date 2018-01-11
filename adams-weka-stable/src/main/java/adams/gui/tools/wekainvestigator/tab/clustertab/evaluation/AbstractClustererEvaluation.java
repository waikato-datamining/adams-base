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
 * AbstractClustererEvaluation.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.clustertab.evaluation;

import adams.core.ClassLister;
import adams.gui.tools.wekainvestigator.evaluation.AbstractEvaluation;
import adams.gui.tools.wekainvestigator.tab.ClusterTab;
import adams.gui.tools.wekainvestigator.tab.clustertab.ResultItem;
import org.apache.commons.lang.time.StopWatch;
import weka.clusterers.Clusterer;

/**
 * Ancestor for clusterer evaluation setups.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractClustererEvaluation
  extends AbstractEvaluation<ClusterTab, ResultItem> {

  private static final long serialVersionUID = -5847790432092994409L;

  /**
   * Tests whether the clusterer can be evaluated.
   *
   * @param clusterer	the current clusterer
   * @return		null if successful, otherwise error message
   */
  public abstract String canEvaluate(Clusterer clusterer);

  /**
   * Initializes the result item.
   *
   * @param clusterer	the current clusterer
   * @return		the initialized history item
   * @throws Exception	if initialization fails
   */
  public abstract ResultItem init(Clusterer clusterer) throws Exception;

  /**
   * Evaluates the clusterer and updates the result item.
   *
   * @param clusterer	the current clusterer
   * @param item	the item to update
   * @throws Exception	if evaluation fails
   */
  protected abstract void doEvaluate(Clusterer clusterer, ResultItem item) throws Exception;

  /**
   * Evaluates the clusterer and updates the result item.
   *
   * @param clusterer	the current clusterer
   * @param item	the item to update
   * @throws Exception	if evaluation fails
   */
  public void evaluate(Clusterer clusterer, ResultItem item) throws Exception {
    StopWatch   watch;

    watch = new StopWatch();
    watch.start();
    doEvaluate(clusterer, item);
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
    return ClassLister.getSingleton().getClasses(AbstractClustererEvaluation.class);
  }
}
