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
import adams.gui.core.AbstractNamedHistoryPanel;
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
   * @return		null if successful, otherwise error message
   */
  public abstract String canEvaluate(Clusterer clusterer);

  /**
   * Evaluates the clusterer and returns the generated evaluation object.
   *
   * @param history	the history to add the result to
   * @return		the generate history item
   * @throws Exception	if evaluation fails
   */
  protected abstract ResultItem doEvaluate(Clusterer clusterer, AbstractNamedHistoryPanel<ResultItem> history) throws Exception;

  /**
   * Evaluates the clusterer and returns the generated evaluation object.
   *
   * @param history	the history to add the result to
   * @return		the generate history item
   * @throws Exception	if evaluation fails
   */
  public ResultItem evaluate(Clusterer clusterer, AbstractNamedHistoryPanel<ResultItem> history) throws Exception {
    ResultItem result;
    StopWatch   watch;

    watch = new StopWatch();
    watch.start();
    result = doEvaluate(clusterer, history);
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
    return ClassLister.getSingleton().getClasses(AbstractClustererEvaluation.class);
  }
}
