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
 * AbstractRowSelection.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance.multirowprocessor.selection;

import weka.core.Instances;
import weka.filters.unsupervised.instance.multirowprocessor.AbstractMultiRowProcessorPlugin;

import java.util.List;

/**
 * Ancestor for row selection schemes.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRowSelection
  extends AbstractMultiRowProcessorPlugin {

  private static final long serialVersionUID = -3561684420376456445L;

  /**
   * Hook method for performing checks.
   *
   * @param data	the data to check
   * @return		null if checks passed, otherwise error message
   */
  protected String check(Instances data) {
    if ((data == null) || (data.numInstances() == 0))
      return "No data provided!";
    return null;
  }

  /**
   * Returns the list of row indices generated from the data.
   *
   * @param data	the data to generate row selections from
   * @return		the list of selections
   * @throws Exception	if checks or selection failed
   */
  protected abstract List<int[]> doSelectRows(Instances data) throws Exception;

  /**
   * Returns the list of row indices generated from the data.
   *
   * @param data	the data to generate row selections from
   * @return		the list of selections
   * @throws Exception	if checks or selection failed
   */
  public List<int[]> selectRows(Instances data) throws Exception {
    String	msg;

    msg = check(data);
    if (msg != null)
      throw new Exception(msg);

    return doSelectRows(data);
  }
}
