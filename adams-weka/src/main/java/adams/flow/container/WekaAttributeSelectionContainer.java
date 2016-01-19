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
 * WekaAttributeSelectionContainer.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import adams.data.spreadsheet.SpreadSheet;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.AttributeTransformer;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container for storing results from attribute selection.
 * <br><br>
 * In case of performing cross-validation: seed and fold count get stored as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaAttributeSelectionContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = -5748659421111306695L;

  /** the identifier for the training data. */
  public final static String VALUE_TRAIN = "Train";

  /** the identifier for the reduced data. */
  public final static String VALUE_REDUCED = "Reduced";

  /** the identifier for the transformed data. */
  public final static String VALUE_TRANSFORMED = "Transformed";

  /** the identifier for the evaluation object. */
  public final static String VALUE_EVALUATION = "Evaluation";

  /** the identifier for the statistics object. */
  public final static String VALUE_STATISTICS = "Statistics";

  /** the identifier for the Remove filter setup. */
  public final static String VALUE_SELECTEDATTRIBUTES = "Selected attributes";

  /** the identifier for the random seed. */
  public final static String VALUE_SEED = "Seed";

  /** the identifier for the fold count. */
  public final static String VALUE_FOLD_COUNT = "FoldCount";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public WekaAttributeSelectionContainer() {
    this(null, null, null, null, null, null, null, null);
  }

  /**
   * Initializes the container.
   *
   * @param train	the training set
   * @param reduced	the reduced data
   * @param transformed	the transformed data
   * @param eval	the attribute selection evaluation object
   * @param statistics	the spreadsheet with the statistics
   * @param range	the range string of selected attributes
   */
  public WekaAttributeSelectionContainer(Instances train, Instances reduced, Instances transformed, AttributeSelection eval, SpreadSheet statistics, String range) {
    this(train, reduced, transformed, eval, statistics, range, null, null);
  }

  /**
   * Initializes the container.
   *
   * @param train	the training set
   * @param reduced	the reduced data
   * @param transformed	the transformed data
   * @param eval	the attribute selection evaluation object
   * @param seed	the seed value, can be null
   * @param foldCount	the fold count
   */
  public WekaAttributeSelectionContainer(Instances train, Instances reduced, Instances transformed, AttributeSelection eval, Long seed, Integer foldCount) {
    this(train, reduced, transformed, eval, null, null, seed, foldCount);
  }

  /**
   * Initializes the container.
   *
   * @param train	the training set
   * @param reduced	the reduced data
   * @param transformed	the transformed data
   * @param eval	the attribute selection evaluation object
   * @param statistics	the spreadsheet with the statistics
   * @param range	the range string of selected attributes
   * @param seed	the seed value, can be null
   * @param foldCount	the fold count
   */
  protected WekaAttributeSelectionContainer(Instances train, Instances reduced, Instances transformed, AttributeSelection eval, SpreadSheet statistics, String range, Long seed, Integer foldCount) {
    super();

    store(VALUE_TRAIN, train);
    if (reduced != null)
      store(VALUE_REDUCED, reduced);
    if (transformed != null)
      store(VALUE_TRANSFORMED, transformed);
    store(VALUE_EVALUATION, eval);
    if (statistics != null)
      store(VALUE_STATISTICS, statistics);
    if (range != null)
      store(VALUE_SELECTEDATTRIBUTES, range);
    store(VALUE_SEED, seed);
    store(VALUE_FOLD_COUNT, foldCount);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_TRAIN, "training set; " + Instances.class.getName());
    addHelp(VALUE_REDUCED, "reduced dataset; " + Instances.class.getName());
    addHelp(VALUE_TRANSFORMED, "transformed dataset (if " + AttributeTransformer.class.getName() + "); " + Instances.class.getName());
    addHelp(VALUE_EVALUATION, "attribute selection evaluation object; " + AttributeSelection.class.getName());
    addHelp(VALUE_STATISTICS, "spreadsheet with the statistics; " + SpreadSheet.class.getName());
    addHelp(VALUE_SELECTEDATTRIBUTES, "range string of selected attributes (1-based indices); " + String.class.getName());
    addHelp(VALUE_SEED, "seed value (cross-validation); " + Long.class.getName());
    addHelp(VALUE_FOLD_COUNT, "fold (cross-validation); " + Integer.class.getName());
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
    result.add(VALUE_REDUCED);
    result.add(VALUE_TRANSFORMED);
    result.add(VALUE_EVALUATION);
    result.add(VALUE_STATISTICS);
    result.add(VALUE_SELECTEDATTRIBUTES);
    result.add(VALUE_SEED);
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
    return   (hasValue(VALUE_TRAIN) && hasValue(VALUE_EVALUATION) && !hasValue(VALUE_SEED) && !hasValue(VALUE_FOLD_COUNT))
           | (hasValue(VALUE_TRAIN) && hasValue(VALUE_EVALUATION) &&  hasValue(VALUE_SEED) &&  hasValue(VALUE_FOLD_COUNT));
  }
}
