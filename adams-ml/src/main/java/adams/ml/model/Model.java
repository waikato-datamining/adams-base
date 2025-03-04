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

/**
 * Model.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.model;

import adams.ml.data.Dataset;
import adams.ml.data.DatasetInfo;

import java.io.Serializable;

/**
 * Ancestor for models.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface Model
  extends Serializable {

  /**
   * Returns information about the dataset used for building the model.
   *
   * @return		the information
   */
  public DatasetInfo getDatasetInfo();

  /**
   * Gets a short string description of the model.
   *
   * @return		the description, null if none available
   */
  public String getModelDescription();

  /**
   * Checks whether the dataset is compatible with the model.
   *
   * @param data	the dataset to check
   * @return		null if compatible, otherwise error message why not
   */
  public String isCompatible(Dataset data);
}
