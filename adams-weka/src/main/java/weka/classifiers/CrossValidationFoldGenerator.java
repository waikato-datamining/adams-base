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
   * Returns the number of folds.
   * 
   * @return		the number of folds
   */
  public int getNumFolds();

  /**
   * Returns whether to stratify the data (in case of nominal class).
   * 
   * @return		true if to stratify
   */
  public boolean getStratify();

  /**
   * Returns the relation name template.
   * 
   * @return		the template
   */
  public String getRelationName();
}
