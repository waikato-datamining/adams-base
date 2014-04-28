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
 *    LWLIntervalEstimator.java
 *    Copyright (C) 1999, 2002, 2003, 2009, 2010 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.classifiers.lazy;

import weka.classifiers.Classifier;
import weka.classifiers.IntervalEstimator;
import weka.core.Instance;
import weka.core.RevisionUtils;

/**
 <!-- globalinfo-start -->
 * Locally weighted learning. Uses an instance-based algorithm to assign instance weights which are then used by a specified WeightedInstancesHandler.<br/>
 * Can do classification (e.g. using naive Bayes) or regression (e.g. using linear regression).<br/>
 * <br/>
 * For more info, see<br/>
 * <br/>
 * Eibe Frank, Mark Hall, Bernhard Pfahringer: Locally Weighted Naive Bayes. In: 19th Conference in Uncertainty in Artificial Intelligence, 249-256, 2003.<br/>
 * <br/>
 * C. Atkeson, A. Moore, S. Schaal (1996). Locally weighted learning. AI Review..
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;inproceedings{Frank2003,
 *    author = {Eibe Frank and Mark Hall and Bernhard Pfahringer},
 *    booktitle = {19th Conference in Uncertainty in Artificial Intelligence},
 *    pages = {249-256},
 *    publisher = {Morgan Kaufmann},
 *    title = {Locally Weighted Naive Bayes},
 *    year = {2003}
 * }
 *
 * &#64;article{Atkeson1996,
 *    author = {C. Atkeson and A. Moore and S. Schaal},
 *    journal = {AI Review},
 *    title = {Locally weighted learning},
 *    year = {1996}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre> -A
 *  The nearest neighbour search algorithm to use (default: weka.core.neighboursearch.LinearNNSearch).
 * </pre>
 *
 * <pre> -K &lt;number of neighbours&gt;
 *  Set the number of neighbours used to set the kernel bandwidth.
 *  (default all)</pre>
 *
 * <pre> -U &lt;number of weighting method&gt;
 *  Set the weighting kernel shape to use. 0=Linear, 1=Epanechnikov,
 *  2=Tricube, 3=Inverse, 4=Gaussian.
 *  (default 0 = Linear)</pre>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.trees.DecisionStump)</pre>
 *
 * <pre>
 * Options specific to classifier weka.classifiers.trees.DecisionStump:
 * </pre>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 <!-- options-end -->
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @author Ashraf M. Kibriya (amk14[at-the-rate]cs[dot]waikato[dot]ac[dot]nz)
 * @version $Revision$
 */
public class LWLIntervalEstimator
  extends LWLSynchro
  implements IntervalEstimator {

  /** for serialization. */
  private static final long serialVersionUID = 4368796865814525074L;

  /**
   * Set the base learner, which must implement IntervalEstimator.
   *
   * @param value 	the classifier to use.
   * @see		IntervalEstimator
   */
  public void setClassifier(Classifier value) {
    if (value instanceof IntervalEstimator)
      super.setClassifier(value);
    else
      System.err.println("Base classifier must be an IntervalEstimator - ignored!");
  }

  /**
   * Returns an N * 2 array, where N is the number of prediction
   * intervals. In each row, the first element contains the lower
   * boundary of the corresponding prediction interval and the second
   * element the upper boundary.
   *
   * @param inst the instance to make the prediction for.
   * @param confidenceLevel the percentage of cases that the interval should cover.
   * @return an array of prediction intervals
   * @exception Exception if the intervals can't be computed
   */
  public synchronized double[][] predictIntervals(Instance inst, double confidenceLevel) throws Exception {
    // default model?
    if (m_ZeroR != null)
      return new double[0][];

    if (m_Train.numInstances() == 0)
      throw new Exception("No training instances!");

    build(inst);

    if (m_Debug) {
      System.out.println("Interval instance: " + inst);
      System.out.println("Built base classifier:\n" + m_Classifier.toString());
    }

    return ((IntervalEstimator) m_Classifier).predictIntervals(inst, confidenceLevel);
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for executing this classifier.
   *
   * @param args 	the options, use -h to display all
   */
  public static void main(String[] args) {
    runClassifier(new LWLIntervalEstimator(), args);
  }
}
