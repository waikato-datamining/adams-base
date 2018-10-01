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
 * MultiTargetRegressionModel.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.ml.model.regression;

import adams.data.spreadsheet.Row;
import adams.ml.model.Model;

import java.util.Map;

/**
 * Interface for regression models.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface MultiTargetRegressionModel
  extends Model {

  /**
   * Returns the regressions for the given row.
   *
   * @param row		the row to make predictions for
   * @return		the prediction per class attribute
   * @throws Exception	if prediction fails
   */
  public Map<String,Double> classify(Row row) throws Exception;
}
