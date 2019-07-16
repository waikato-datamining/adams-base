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
 * CrossValidationGenerator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.splitgenerator.generic.crossvalidation;

import adams.core.logging.CustomLoggingLevelObject;
import adams.data.binning.Binnable;
import adams.data.binning.operation.Wrapping;
import adams.data.splitgenerator.generic.core.Subset;
import adams.data.splitgenerator.generic.randomization.DefaultRandomization;
import adams.data.splitgenerator.generic.randomization.Randomization;
import adams.data.splitgenerator.generic.stratification.DefaultStratification;
import adams.data.splitgenerator.generic.stratification.Stratification;
import gnu.trove.list.TIntList;

import java.util.ArrayList;
import java.util.List;

/**
 * For generating cross-validation splits.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CrossValidationGenerator
  extends CustomLoggingLevelObject {

  private static final long serialVersionUID = 6906260013695977045L;

  /** the number of folds to use. */
  protected int m_NumFolds;

  /** the randomization scheme. */
  protected Randomization m_Randomization;

  /** the stratification scheme. */
  protected Stratification m_Stratification;

  /**
   * Initializes the cross-validation.
   */
  public CrossValidationGenerator() {
    m_NumFolds       = 10;
    m_Randomization  = new DefaultRandomization();
    m_Stratification = new DefaultStratification();
    reset();
  }

  /**
   * Resets the scheme.
   */
  public void reset() {
    m_Randomization.reset();
    m_Stratification.reset();
  }

  /**
   * Sets the number of folds to use.
   *
   * @param value	the number of folds, LOO if <2
   */
  public void setNumFolds(int value) {
    m_NumFolds = value;
    reset();
  }

  /**
   * Returns the number of folds in use.
   *
   * @return		the number of folds, LOO if <2
   */
  public int getNumFolds() {
    return m_NumFolds;
  }

  /**
   * Sets the randomization scheme to use.
   * 
   * @param value	the scheme
   */
  public void setRandomization(Randomization value) {
    m_Randomization = value;
    reset();
  }

  /**
   * Returns the randomization scheme in use.
   * 
   * @return		the scheme
   */
  public Randomization getRandomization() {
    return m_Randomization;
  }

  /**
   * Sets the stratification scheme to use.
   * 
   * @param value	the scheme
   */
  public void setStratification(Stratification value) {
    m_Stratification = value;
    reset();
  }

  /**
   * Returns the stratification scheme in use.
   * 
   * @return		the scheme
   */
  public Stratification getStratification() {
    return m_Stratification;
  }

  /**
   * Creates the training set for one fold of a cross-validation on the dataset.
   *
   * @param numFolds the number of folds in the cross-validation. Must be
   *          greater than 1.
   * @param numFold 0 for the first fold, 1 for the second, ...
   * @return the training set
   * @throws IllegalArgumentException if the number of folds is less than 2 or
   *           greater than the number of instances.
   */
  protected <T> List<Binnable<T>> trainCV(List<Binnable<T>> data, int numFolds, int numFold) {
    List<Binnable<T>> 	result;
    int 		numInstForFold;
    int 		first;
    int 		offset;
    int 		i;

    if (numFolds < 2)
      throw new IllegalArgumentException("Number of folds must be at least 2!");
    if (numFolds > data.size())
      throw new IllegalArgumentException("Can't have more folds than instances!");

    numInstForFold = data.size() / numFolds;
    if (numFold < data.size() % numFolds) {
      numInstForFold++;
      offset = numFold;
    }
    else {
      offset = data.size() % numFolds;
    }

    result = new ArrayList<>();
    first  = numFold * (data.size() / numFolds) + offset;
    //copyInstances(0, train, first);
    for (i = 0; i < first; i++)
      result.add(data.get(i));
    //copyInstances(first + numInstForFold, train, data.size() - first - numInstForFold);
    for (i = 0; i < data.size() - first - numInstForFold; i++)
      result.add(data.get(first + numInstForFold + i));

    return result;
  }

  /**
   * Creates the training set for one fold of a cross-validation on the dataset.
   * The data is subsequently randomized based on the given random number
   * generator.
   *
   * @param numFolds the number of folds in the cross-validation. Must be
   *          greater than 1.
   * @param numFold 0 for the first fold, 1 for the second, ...
   * @param random the random number generator
   * @return the training set
   * @throws IllegalArgumentException if the number of folds is less than 2 or
   *           greater than the number of instances.
   */
  protected <T> List<Binnable<T>> trainCV(List<Binnable<T>> data, int numFolds, int numFold, Randomization random) {
    List<Binnable<T>> 	result;

    result = trainCV(data, numFolds, numFold);
    random.randomize(result);
    return result;
  }

  /**
   * Creates the test set for one fold of a cross-validation on the dataset.
   *
   * @param numFolds the number of folds in the cross-validation. Must be
   *          greater than 1.
   * @param numFold 0 for the first fold, 1 for the second, ...
   * @return the test set as a set of weighted instances
   * @throws IllegalArgumentException if the number of folds is less than 2 or
   *           greater than the number of instances.
   */
  protected <T> List<Binnable<T>> testCV(List<Binnable<T>> data, int numFolds, int numFold) {
    List<Binnable<T>> 	result;
    int 		numInstForFold;
    int 		first;
    int 		offset;
    int 		i;

    if (numFolds < 2)
      throw new IllegalArgumentException("Number of folds must be at least 2!");
    if (numFolds > data.size())
      throw new IllegalArgumentException("Can't have more folds than instances!");

    numInstForFold = data.size() / numFolds;
    if (numFold < data.size() % numFolds) {
      numInstForFold++;
      offset = numFold;
    }
    else {
      offset = data.size() % numFolds;
    }

    result = new ArrayList<>();
    first  = numFold * (data.size() / numFolds) + offset;
    // copyInstances(first, test, numInstForFold);
    for (i = 0; i < numInstForFold; i++)
      result.add(data.get(first + i));

    return result;
  }

  /**
   * Generates cross-validation fold pairs.
   * Temporarily adds the original index to the Binnable meta-data, using {@link Wrapping#TMP_INDEX} as key.
   *
   * @param data	the data to generate the pairs from
   * @param <T>		the payload type
   * @return		the fold pairs
   */
  public <T> List<FoldPair<Binnable<T>>> generate(List<Binnable<T>> data) {
    List<FoldPair<Binnable<T>>>	result;
    List<Binnable<T>> 		trainData;
    TIntList			trainIndices;
    Subset<Binnable<T>> 	train;
    List<Binnable<T>> 		testData;
    TIntList			testIndices;
    Subset<Binnable<T>> 	test;
    int				i;
    int				n;
    int				folds;

    result = new ArrayList<>();

    // actual number of folds
    if (m_NumFolds < 2)
      folds = data.size();
    else
      folds = m_NumFolds;

    // add tmp index
    data = Wrapping.addTmpIndex(data);

    // randomize/stratify
    data = m_Randomization.randomize(data);
    data = m_Stratification.stratify(data, folds);

    // generate pairs
    for (i = 0; i < folds; i++) {
      // train
      trainData    = trainCV(data, folds, i, m_Randomization);
      trainIndices = Wrapping.getTmpIndices(trainData);
      train        = new Subset<>(trainData, trainIndices);

      // test
      testData    = testCV(data, folds, i);
      testIndices = Wrapping.getTmpIndices(testData);
      test        = new Subset<>(testData, testIndices);

      result.add(new FoldPair<>(i, train, test));
    }

    // remove tmp index
    Wrapping.removeTmpIndex(data);

    return result;
  }
}
