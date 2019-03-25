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
 * AbstractSplitter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.weka.datasetsplitter;

import adams.core.option.AbstractOptionHandler;
import weka.core.Instances;

/**
 * Parent class for different methods of splitting a dataset into
 * smaller datasets.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public abstract class AbstractSplitter extends AbstractOptionHandler {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = -3970687323224324655L;

  /**
   * Splits the given dataset into a number of other datasets. Should be
   * implemented by sub-classes to perform actual splitting.
   *
   * @param dataset	The dataset to split.
   * @return	An array of datasets resulting from the split.
   */
  public abstract Instances[] split(Instances dataset);

}
