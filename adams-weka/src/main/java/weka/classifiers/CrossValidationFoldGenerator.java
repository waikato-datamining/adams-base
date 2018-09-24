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
 * CrossValidationFoldGenerator.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;

/**
 * Interface for generating cross-validation folds.
 * <br><br>
 * The template for the relation name accepts the following placeholders:
 * @ = original relation name, $T = type (train/test), $N = current fold number
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface CrossValidationFoldGenerator
  extends SplitGenerator {

  /** the placeholder for the (original) relation name. */
  public final static String PLACEHOLDER_ORIGINAL = "@";

  /** the placeholder for "train" or "test" type. */
  public final static String PLACEHOLDER_TYPE = "$T";

  /** the placeholder for the current fold number. */
  public final static String PLACEHOLDER_CURRENTFOLD = "$N";

  /**
   * Sets the number of folds to use.
   *
   * @param value	the number of folds, less than 2 for LOO
   */
  public void setNumFolds(int value);

  /**
   * Returns the number of folds.
   * 
   * @return		the number of folds
   */
  public int getNumFolds();

  /**
   * Returns the actual number of folds used (eg when using LOO).
   *
   * @return		the actual number of folds
   */
  public int getActualNumFolds();

  /**
   * Sets whether to randomize the data.
   *
   * @param value	true if to randomize the data
   */
  public void setRandomize(boolean value);

  /**
   * Returns whether to randomize the data.
   *
   * @return		true if to randomize the data
   */
  public boolean getRandomize();

  /**
   * Sets whether to stratify the data (nominal class).
   *
   * @param value	whether to stratify the data (nominal class)
   */
  public void setStratify(boolean value);

  /**
   * Returns whether to stratify the data (in case of nominal class).
   * 
   * @return		true if to stratify
   */
  public boolean getStratify();

  /**
   * Sets the template for the relation name.
   *
   * @param value	the template
   */
  public void setRelationName(String value);

  /**
   * Returns the relation name template.
   * 
   * @return		the template
   */
  public String getRelationName();

  /**
   * Returns the cross-validation indices.
   *
   * @return		the indices
   */
  public int[] crossValidationIndices();
}
