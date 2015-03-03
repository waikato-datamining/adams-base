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
 * AbstractCrossvalidatedInstanceEvaluator.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.evaluator;

import java.util.Random;
import java.util.Vector;

import weka.core.Instance;
import weka.core.Instances;

/**
 * Ancestor for evalutors that use cross-validation for initialization.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of container
 */
public abstract class AbstractCrossvalidatedInstanceEvaluator<T extends AbstractCrossvalidatedInstanceEvaluator.EvaluationContainer>
  extends AbstractDatasetInstanceEvaluator {

  /** for serialization. */
  private static final long serialVersionUID = 5958214723994891350L;

  /**
   * Container for storing the evaluation results.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static abstract class EvaluationContainer
    implements Comparable {

    /** the Instance the container is for. */
    protected Instance m_Instance;

    /**
     * Initializes the container.
     *
     * @param inst	the Instance the container is for
     */
    public EvaluationContainer(Instance inst) {
      super();

      m_Instance = inst;
    }

    /**
     * Returns the stored Instance.
     *
     * @return		the instance
     */
    public Instance getInstance() {
      return m_Instance;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p/>
     * Only compares the commandlines of the two objects.
     *
     * @param o 	the object to be compared.
     * @return  	a negative integer, zero, or a positive integer as this object
     *			is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException 	if the specified object's type prevents it
     *         				from being compared to this object.
     */
    public abstract int compareTo(Object o);

    /**
     * Returns whether the two objects are the same.
     *
     * @param o		the object to be compared
     * @return		true if the object is the same as this one
     */
    public boolean equals(Object o) {
      return (compareTo(o) == 0);
    }

    /**
     * Returns a string representation of the stored data.
     *
     * @return		the represention
     */
    public abstract String toString();
  }

  /** the number of folds for cross-validation. */
  protected int m_Folds;

  /** the random seed for cross-valiation. */
  protected int m_Seed;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "folds", "folds",
	    2);

    m_OptionManager.add(
	    "seed", "seed",
	    1);
  }

  /**
   * Sets the number of folds for cross-validation.
   *
   * @param value 	the number of folds (<2 turns off cross-validation)
   */
  public void setFolds(int value) {
    m_Folds = value;
    reset();
  }

  /**
   * Returns the number of folds for cross-validation.
   *
   * @return 		the number of folds (<2 turns off cross-validation)
   */
  public int getFolds() {
    return m_Folds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String foldsTipText() {
    return "The number of folds to use for cross-validation; cross-validation gets turned off below a value of 2.";
  }

  /**
   * Sets the seed value for cross-validation.
   *
   * @param value 	the seed
   */
  public void setSeed(int value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value for cross-validation.
   *
   * @return 		the seed
   */
  public int getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value for randomizing the data for cross-validation.";
  }

  /**
   * Finds the threshold based on the collected data.
   *
   * @param evals	the collected evaluation containers
   * @return		null if everything OK, error message otherwise
   */
  protected abstract String findThreshold(Vector<T> evals);

  /**
   * Performs an evaluation on the given train and test set.
   *
   * @param train	the training set
   * @param test	the test set
   * @return		the generated evaluation container
   */
  protected abstract Vector<T> evaluate(Instances train, Instances test);

  /**
   * Finds the user-defined threshold and sets other internal variables
   * accordingly.
   *
   * @return		null if everything OK, error message otherwise
   */
  protected String findThreshold() {
    String	result;
    Vector<T>	evals;
    Instances	data;
    Instances	train;
    Instances	test;
    int		i;
    Random	rand;

    result = null;

    // randomize data
    rand = new Random(m_Seed);
    data = new Instances(m_Data);
    data.randomize(rand);

    // collect the evaluations
    evals = new Vector<T>();
    if (m_Folds < 2) {
      evals.addAll(evaluate(data, data));
    }
    else {
      if (data.classAttribute().isNominal())
	data.stratify(m_Folds);
      for (i = 0; i < m_Folds; i++) {
	train = data.trainCV(m_Folds, i, rand);
	test  = data.testCV(m_Folds, i);
	evals.addAll(evaluate(train, test));
      }
    }

    result = findThreshold(evals);

    return result;
  }
}
