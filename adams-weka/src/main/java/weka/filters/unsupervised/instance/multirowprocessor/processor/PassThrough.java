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
 * PassThrough.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance.multirowprocessor.processor;

import weka.core.Instance;
import weka.core.Instances;

import java.util.List;

/**
 * Just passes through the data.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PassThrough
  extends AbstractSelectionProcessor {

  private static final long serialVersionUID = 6646211858982714315L;

  /**
   * Returns a string describing the processor.
   *
   * @return a description suitable for displaying in the explorer/experimenter
   * gui
   */
  @Override
  public String globalInfo() {
    return "Just passes through the data.";
  }

  /**
   * Returns the format for the output data.
   *
   * @param data	the input data
   * @return		the output format
   * @throws Exception	if fails to determine output format
   */
  public Instances generateOutputFormat(Instances data) throws Exception {
    return new Instances(data, 0);
  }

  /**
   * Returns the list of row indices generated from the data.
   *
   * @param rows the rows to process
   * @throws Exception if checks or selection failed
   * @return the list of selections
   */
  @Override
  protected List<Instance> doProcessRows(List<Instance> rows) throws Exception {
    return rows;
  }
}
