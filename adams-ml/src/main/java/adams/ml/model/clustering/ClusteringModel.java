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
 * ClusteringModel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.model.clustering;

import adams.data.spreadsheet.Row;
import adams.ml.model.Model;

/**
 * Interface for clustering models.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ClusteringModel
  extends Model {

  /**
   * Returns the cluster index for the given row.
   *
   * @param row		the row to classify
   * @return		the cluster index
   * @throws Exception	if prediction fails
   */
  public int cluster(Row row) throws Exception;

  /**
   * Returns the cluster distribution for the given row.
   *
   * @param row		the row to generate the class distribution for
   * @return		the cluster distribution
   * @throws Exception	if prediction fails
   */
  public double[] distribution(Row row) throws Exception;
}
