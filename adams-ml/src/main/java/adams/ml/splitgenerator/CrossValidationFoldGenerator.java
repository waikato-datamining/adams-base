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
package adams.ml.splitgenerator;

/**
 * Interface for generating cross-validation folds.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <I> the input data
 * @param <O> the output data
 */
public interface CrossValidationFoldGenerator<I,O>
  extends SplitGenerator<I,O> {

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
   * Returns the cross-validation indices.
   *
   * @return		the indices
   */
  public int[] crossValidationIndices();
}
