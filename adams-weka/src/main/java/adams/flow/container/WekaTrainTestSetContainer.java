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
 * WekaTrainTestSetContainer.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import weka.core.Instances;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container for storing train and test set.
 * <br><br>
 * Optionally, random seed, fold number and fold count can be stored as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaTrainTestSetContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = 7650097276531433711L;

  /** the identifier for the training data. */
  public final static String VALUE_TRAIN = "Train";

  /** the identifier for the test data. */
  public final static String VALUE_TEST = "Test";

  /** the identifier for the random seed. */
  public final static String VALUE_SEED = "Seed";

  /** the identifier for the fold number. */
  public final static String VALUE_FOLD_NUMBER = "FoldNumber";

  /** the identifier for the fold count. */
  public final static String VALUE_FOLD_COUNT = "FoldCount";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public WekaTrainTestSetContainer() {
    this(null, null);
  }

  /**
   * Initializes the container.
   *
   * @param train	the training set
   * @param test	the test data
   */
  public WekaTrainTestSetContainer(Instances train, Instances test) {
    this(train, test, null);
  }

  /**
   * Initializes the container.
   *
   * @param train	the training set
   * @param test	the test data
   * @param seed	the seed value, can be null
   */
  public WekaTrainTestSetContainer(Instances train, Instances test, Long seed) {
    this(train, test, seed, null, null);
  }

  /**
   * Initializes the container.
   *
   * @param train	the training set
   * @param test	the test data
   * @param seed	the seed value, can be null
   * @param foldNumber	the fold number
   * @param foldCount	the fold count
   */
  public WekaTrainTestSetContainer(Instances train, Instances test, Long seed, Integer foldNumber, Integer foldCount) {
    super();

    store(VALUE_TRAIN, train);
    store(VALUE_TEST, test);
    store(VALUE_SEED, seed);
    store(VALUE_FOLD_NUMBER, foldNumber);
    store(VALUE_FOLD_COUNT, foldCount);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_TRAIN, "training set; " + Instances.class.getName());
    addHelp(VALUE_TEST, "test set; " + Instances.class.getName());
    addHelp(VALUE_SEED, "seed value; " + Long.class.getName());
    addHelp(VALUE_FOLD_NUMBER, "current fold (1-based); " + Integer.class.getName());
    addHelp(VALUE_FOLD_COUNT, "total number of folds; " + Integer.class.getName());
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String>	result;

    result = new ArrayList<String>();

    result.add(VALUE_TRAIN);
    result.add(VALUE_TEST);
    result.add(VALUE_SEED);
    result.add(VALUE_FOLD_NUMBER);
    result.add(VALUE_FOLD_COUNT);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return   (hasValue(VALUE_TRAIN) && hasValue(VALUE_TEST) && !hasValue(VALUE_SEED) && !hasValue(VALUE_FOLD_NUMBER) && !hasValue(VALUE_FOLD_COUNT))
           | (hasValue(VALUE_TRAIN) && hasValue(VALUE_TEST) &&  hasValue(VALUE_SEED) && !hasValue(VALUE_FOLD_NUMBER) && !hasValue(VALUE_FOLD_COUNT))
           | (hasValue(VALUE_TRAIN) && hasValue(VALUE_TEST) &&  hasValue(VALUE_SEED) &&  hasValue(VALUE_FOLD_NUMBER) &&  hasValue(VALUE_FOLD_COUNT));
  }
}
