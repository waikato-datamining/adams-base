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
 * RandomSplitGenerator.java
 * Copyright (C) 2012-2019 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;

import adams.flow.container.WekaTrainTestSetContainer;
import weka.core.Instances;

/**
 * Interface for generators of random splits of datasets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface RandomSplitGenerator
  extends SplitGenerator, adams.data.splitgenerator.RandomSplitGenerator<Instances, WekaTrainTestSetContainer> {

  /**
   * Sets the split percentage.
   *
   * @param value	the percentage (0-1)
   */
  @Override
  public void setPercentage(double value);

  /**
   * Returns the split percentage.
   *
   * @return		the percentage (0-1)
   */
  @Override
  public double getPercentage();

  /**
   * Sets whether to preserve the order.
   *
   * @param value	true if to preserve order
   */
  @Override
  public void setPreserveOrder(boolean value);

  /**
   * Returns whether to preserve the order.
   *
   * @return		true if to preserve order
   */
  @Override
  public boolean getPreserveOrder();
}
