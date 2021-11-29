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
 * AbstractSelectionProcessor.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance.multirowprocessor.processor;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.instance.multirowprocessor.AbstractMultiRowProcessorPlugin;

import java.util.List;

/**
 * Ancestor for row selection processors.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSelectionProcessor
    extends AbstractMultiRowProcessorPlugin {

  private static final long serialVersionUID = -3561684420376456445L;

  /**
   * Returns the format for the output data.
   *
   * @param data	the input data
   * @return		the output format
   * @throws Exception	if fails to determine output format
   */
  public abstract Instances generateOutputFormat(Instances data) throws Exception;

  /**
   * Hook method for performing checks.
   *
   * @param rows	the data to check
   * @return		null if checks passed, otherwise error message
   */
  protected String check(List<Instance> rows) {
    if ((rows == null) || (rows.size() == 0))
      return "No data provided!";
    return null;
  }

  /**
   * Returns the list of row indices generated from the data.
   *
   * @param rows	the rows to process
   * @return		the list of selections
   * @throws Exception	if checks or selection failed
   */
  protected abstract List<Instance> doProcessRows(List<Instance> rows) throws Exception;

  /**
   * Returns the list of row indices generated from the data.
   *
   * @param rows	the rows to process
   * @throws Exception	if checks or selection failed
   */
  public List<Instance> processRows(List<Instance> rows) throws Exception {
    String	msg;

    msg = check(rows);
    if (msg != null)
      throw new Exception(msg);

    return doProcessRows(rows);
  }
}
