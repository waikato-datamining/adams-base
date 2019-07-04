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
 * GenericCrossValidation.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.ml.data;

import adams.data.binning.Binnable;
import adams.data.binning.operation.Randomize;
import adams.data.binning.operation.Stratify;
import com.github.fracpete.javautils.enumerate.Enumerated;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * For generating cross-validation splits.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GenericCrossValidation {

  /** the temporary index stored in the binnable meta-data. */
  public static final String TMP_INDEX = "$$$tmpindex$$$";

  /**
   * Container for a single fold item (train or test).
   *
   * @param <T>	the type of wrapped data
   */
  public static class FoldItem<T>
    implements Serializable {

    private static final long serialVersionUID = 3833693505441351845L;

    /** the data. */
    protected List<T> m_Data;

    /** the original indices (can be null). */
    protected TIntList m_OriginalIndices;

    /**
     * Initializes the container.
     *
     * @param data		the data
     * @param originalIndices	the indices
     */
    public FoldItem(List<T> data, TIntList originalIndices) {
      m_Data            = new ArrayList<>(data);
      m_OriginalIndices = new TIntArrayList(originalIndices);
    }

    /**
     * Returns the data.
     *
     * @return		the data
     */
    public List<T> getData() {
      return m_Data;
    }

    /**
     * Returns the original indices.
     *
     * @return		the indices
     */
    public TIntList getOriginalIndices() {
      return m_OriginalIndices;
    }
  }

  /**
   * Combines train and test data.
   *
   * @param <T> the type of wrapped data
   */
  public static class FoldPair<T>
    implements Serializable {

    private static final long serialVersionUID = -7911202345550167880L;

    /** the index. */
    protected int m_Index;

    /** the training data. */
    protected FoldItem<T> m_Train;

    /** the test data. */
    protected FoldItem<T> m_Test;

    /**
     * Initializes the fold pair.
     *
     * @param index 	the index
     * @param train	the training data
     * @param test	the test data
     */
    public FoldPair(int index, FoldItem<T> train, FoldItem<T> test) {
      m_Index = index;
      m_Train = train;
      m_Test  = test;
    }

    /**
     * Returns the index.
     *
     * @return		the index
     */
    public int getIndex() {
      return m_Index;
    }

    /**
     * Returns the training data.
     *
     * @return		the data
     */
    public FoldItem<T> getTrain() {
      return m_Train;
    }

    /**
     * Returns the test data.
     *
     * @return		the data
     */
    public FoldItem<T> getTest() {
      return m_Test;
    }

    /**
     * Returns a short string representation.
     *
     * @return		the representation
     */
    @Override
    public String toString() {
      return m_Index + ": train=" + getTrain().getData().size() + ", test=" + getTest().getData().size();
    }
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
  protected static <T> List<Binnable<T>> trainCV(List<Binnable<T>> data, int numFolds, int numFold) {
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
  protected static <T> List<Binnable<T>> trainCV(List<Binnable<T>> data, int numFolds, int numFold, Random random) {
    List<Binnable<T>> 	result;

    result = trainCV(data, numFolds, numFold);
    Randomize.randomizeData(result, random);
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
  protected static <T> List<Binnable<T>> testCV(List<Binnable<T>> data, int numFolds, int numFold) {
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
   * Temporarily adds the original index to the Binnable meta-data, using {@link #TMP_INDEX} as key.
   *
   * @param data	the data to generate the pairs from
   * @param folds	the number of folds to use
   * @param seed	the seed for randomization
   * @param <T>		the payload type
   * @return		the fold pairs
   * @see		#crossValidate(List, int, Random)
   */
  public static <T> List<FoldPair<Binnable<T>>> crossValidate(List<Binnable<T>> data, int folds, long seed) {
    return crossValidate(data, folds, new Random(seed));
  }

  /**
   * Generates cross-validation fold pairs.
   * Temporarily adds the original index to the Binnable meta-data, using {@link #TMP_INDEX} as key.
   *
   * @param data	the data to generate the pairs from
   * @param folds	the number of folds to use
   * @param random	the random number generation for randomizing the data
   * @param <T>		the payload type
   * @return		the fold pairs
   */
  public static <T> List<FoldPair<Binnable<T>>> crossValidate(List<Binnable<T>> data, int folds, Random random) {
    List<FoldPair<Binnable<T>>>	result;
    List<Binnable<T>> 		trainData;
    TIntList			trainIndices;
    FoldItem<Binnable<T>>	train;
    List<Binnable<T>> 		testData;
    TIntList			testIndices;
    FoldItem<Binnable<T>>	test;
    int				i;
    int				n;

    result = new ArrayList<>();

    // add tmp index
    for (Enumerated<Binnable<T>> d: enumerate(data))
      d.value.addMetaData(TMP_INDEX, d.index);

    // randomize/stratify
    data = Randomize.randomizeData(data, random);
    data = Stratify.stratify(data, folds);

    // generate pairs
    for (i = 0; i < folds; i++) {
      // train
      trainData = trainCV(data, folds, i, random);
      trainIndices = new TIntArrayList();
      for (n = 0; n < trainData.size(); n++)
	trainIndices.add((Integer) trainData.get(n).getMetaData(TMP_INDEX));
      train = new FoldItem<>(trainData, trainIndices);

      // test
      testData = testCV(data, folds, i);
      testIndices  = new TIntArrayList();
      for (n = 0; n < testData.size(); n++)
	testIndices.add((Integer) testData.get(n).getMetaData(TMP_INDEX));
      test = new FoldItem<>(testData, testIndices);

      result.add(new FoldPair<>(i, train, test));
    }

    // remove tmp index
    for (Binnable<T> item: data)
      item.removeMetaData(TMP_INDEX);

    return result;
  }
}
