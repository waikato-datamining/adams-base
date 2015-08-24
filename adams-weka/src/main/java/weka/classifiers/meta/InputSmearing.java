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
 * InputSmearing.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Extended version of weka.classifiers.meta.Bagging, which allows input smearing of numeric attributes.<br/>
 * Class for bagging a classifier to reduce variance. Can do classification and regression depending on the base learner. <br/>
 * <br/>
 * For more information, see<br/>
 * <br/>
 * Leo Breiman (1996). Bagging predictors. Machine Learning. 24(2):123-140.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;article{Breiman1996,
 *    author = {Leo Breiman},
 *    journal = {Machine Learning},
 *    number = {2},
 *    pages = {123-140},
 *    title = {Bagging predictors},
 *    volume = {24},
 *    year = {1996}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -stddev &lt;number&gt;
 *  The standard deviation to use for the smearing (default 1.0)</pre>
 * 
 * <pre> -P
 *  Size of each bag, as a percentage of the
 *  training set size. (default 100)</pre>
 * 
 * <pre> -O
 *  Calculate the out of bag error.</pre>
 * 
 * <pre> -represent-copies-using-weights
 *  Represent copies of instances using weights rather than explicitly.</pre>
 * 
 * <pre> -S &lt;num&gt;
 *  Random number seed.
 *  (default 1)</pre>
 * 
 * <pre> -num-slots &lt;num&gt;
 *  Number of execution slots.
 *  (default 1 - i.e. no parallelism)
 *  (use 0 to auto-detect number of cores)</pre>
 * 
 * <pre> -I &lt;num&gt;
 *  Number of iterations.
 *  (default 10)</pre>
 * 
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.trees.REPTree)</pre>
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
 * Options specific to classifier weka.classifiers.trees.REPTree:
 * </pre>
 * 
 * <pre> -M &lt;minimum number of instances&gt;
 *  Set minimum number of instances per leaf (default 2).</pre>
 * 
 * <pre> -V &lt;minimum variance for split&gt;
 *  Set minimum numeric class variance proportion
 *  of train variance for split (default 1e-3).</pre>
 * 
 * <pre> -N &lt;number of folds&gt;
 *  Number of folds for reduced error pruning (default 3).</pre>
 * 
 * <pre> -S &lt;seed&gt;
 *  Seed for random data shuffling (default 1).</pre>
 * 
 * <pre> -P
 *  No pruning.</pre>
 * 
 * <pre> -L
 *  Maximum tree depth (default -1, no maximum)</pre>
 * 
 * <pre> -I
 *  Initial class value count (default 0)</pre>
 * 
 * <pre> -R
 *  Spread initial count over all class values (i.e. don't use 1 per value)</pre>
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
 * Options after -- are passed to the designated classifier.<p>
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @author Eibe Frank (eibe at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InputSmearing
  extends Bagging {

  private static final long serialVersionUID = 8040692114355993432L;

  /** the standard variation to use. */
  protected double m_StdDev = 1.0;

  /**
   * Returns a string describing classifier.
   *
   * @return a description suitable for
   * displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Extended version of " + Bagging.class.getName() + ", which allows "
	+ "input smearing of numeric attributes.\n"
	+ super.globalInfo();
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<Option>();

    result.addElement(new Option(
      "\tThe standard deviation to use for the smearing (default 1.0)",
      "stddev", 1, "-stddev <number>"));

    result.addAll(Collections.list(super.listOptions()));

    return result.elements();
  }


  /**
   * Parses a given list of options.
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String 	tmpStr;

    tmpStr = Utils.getOption("stddev", options);
    if (!tmpStr.isEmpty())
      setStdDev(Double.parseDouble(tmpStr));
    else
      setStdDev(1.0);

    super.setOptions(options);

    Utils.checkForRemainingOptions(options);
  }

  /**
   * Gets the current settings of the Classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector<String> options = new Vector<String>();

    options.add("-stddev");
    options.add("" + getStdDev());

    Collections.addAll(options, super.getOptions());

    return options.toArray(new String[options.size()]);
  }

  /**
   * Gets the standard deviation to use for input smearing.
   *
   * @return the standard deviation
   */
  public double getStdDev() {
    return m_StdDev;
  }

  /**
   * Sets the standard deviation to use for input smearing.
   *
   * @param value the standard deviation.
   */
  public void setStdDev(double value) {
    m_StdDev = value;
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String stdDevTipText() {
    return "The standard deviation to use for the input smearing.";
  }

  /**
   * Returns a training set for a particular iteration.
   *
   * @param iteration the number of the iteration for the requested training set.
   * @return the training set for the supplied iteration number
   * @throws Exception if something goes wrong when generating a training set.
   */
  @Override
  protected synchronized Instances getTrainingSet(int iteration) throws Exception {
    Instances bagData = super.getTrainingSet(iteration);
    Random rnd = new Random(m_Seed + iteration);

    for (Instance inst: bagData) {
      for (int i = 0; i < inst.numAttributes(); i++) {
	if (inst.attribute(i).type() == Attribute.NUMERIC) {
	  inst.setValue(i, inst.value(i) + rnd.nextGaussian() * m_StdDev);
	}
      }
    }

    return bagData;
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 10470 $");
  }

  /**
   * Main method for testing this class.
   *
   * @param args the options
   */
  public static void main(String[] args) {
    runClassifier(new InputSmearing(), args);
  }
}
