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
 * LeanMultiScheme.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;

/**
 <!-- globalinfo-start -->
 * Class for selecting a classifier from among several using cross validation on the training data or the performance on the training data. Performance is measured based on percent correct (classification) or mean-squared error (regression).<br/>
 * Only keeps the best classifier as trained model, never trains the array of specified classifiers.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -X &lt;number of folds&gt;
 *  Use cross validation for model selection using the
 *  given number of folds. (default 0, is to
 *  use training error)</pre>
 * 
 * <pre> -S &lt;num&gt;
 *  Random number seed.
 *  (default 1)</pre>
 * 
 * <pre> -B &lt;classifier specification&gt;
 *  Full class name of classifier to include, followed
 *  by scheme options. May be specified multiple times.
 *  (default: "weka.classifiers.rules.ZeroR")</pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 * <pre> 
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LeanMultiScheme
  extends MultiScheme {

  private static final long serialVersionUID = -4775540898817424109L;

  /**
   * Returns a string describing classifier
   * @return a description suitable for
   * displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return super.globalInfo() + "\n"
      + "Only keeps the best classifier as trained model, never trains the array of specified classifiers.";
  }

  /**
   * Gets a single classifier from the set of available classifiers.
   *
   * @param index the index of the classifier wanted
   * @return the Classifier
   */
  @Override
  public Classifier getClassifier(int index) {
    try {
      return AbstractClassifier.makeCopy(super.getClassifier(index));
    }
    catch (Exception e) {
      System.err.println("Failed to create copy of classifier #" + (index+1) + ":");
      e.printStackTrace();
      return null;
    }
  }
}
