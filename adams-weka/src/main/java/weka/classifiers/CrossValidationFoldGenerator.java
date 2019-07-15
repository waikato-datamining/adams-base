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
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;

import adams.data.splitgenerator.StratifiableSplitGenerator;
import adams.flow.container.WekaTrainTestSetContainer;
import weka.core.Instances;

/**
 * Interface for generating cross-validation folds.
 * <br><br>
 * The template for the relation name accepts the following placeholders:
 * @ = original relation name, $T = type (train/test), $N = current fold number
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface CrossValidationFoldGenerator
  extends SplitGenerator,
  adams.data.splitgenerator.CrossValidationFoldGenerator<Instances,WekaTrainTestSetContainer>,
  StratifiableSplitGenerator<Instances,WekaTrainTestSetContainer> {

  /**
   * Sets the number of folds to use.
   *
   * @param value	the number of folds, less than 2 for LOO
   */
  @Override
  public void setNumFolds(int value);

  /**
   * Returns the number of folds.
   * 
   * @return		the number of folds
   */
  @Override
  public int getNumFolds();

  /**
   * Returns the actual number of folds used (eg when using LOO).
   *
   * @return		the actual number of folds
   */
  @Override
  public int getActualNumFolds();

  /**
   * Sets whether to randomize the data.
   *
   * @param value	true if to randomize the data
   */
  @Override
  public void setRandomize(boolean value);

  /**
   * Returns whether to randomize the data.
   *
   * @return		true if to randomize the data
   */
  @Override
  public boolean getRandomize();

  /**
   * Sets whether to stratify the data (nominal class).
   *
   * @param value	whether to stratify the data (nominal class)
   */
  @Override
  public void setStratify(boolean value);

  /**
   * Returns whether to stratify the data (in case of nominal class).
   * 
   * @return		true if to stratify
   */
  @Override
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
  @Override
  public int[] crossValidationIndices();
}
