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
 * Average.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance.multirowprocessor.processor;

import adams.data.statistics.StatUtils;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

/**
 * Computes the average of the numeric attributes defined in the range.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Average
  extends AbstractRangeBasedSelectionProcessor {

  private static final long serialVersionUID = 273226641478995302L;

  /**
   * Returns a string describing the processor.
   *
   * @return a description suitable for displaying in the explorer/experimenter
   * gui
   */
  @Override
  public String globalInfo() {
    return "Computes the average of the numeric attributes defined in the range.\n"
	+ "In case of attributes outside the range or non-numeric ones, the values "
	+ "of the first row are used in the output.";
  }

  /**
   * Returns the format for the output data.
   *
   * @param data the input data
   * @throws Exception if fails to determine output format
   * @return the output format
   */
  @Override
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
    List<Instance>	result;
    double[]		values;
    int			i;
    int[]		indices;
    int			n;
    double[]		data;

    if (rows.size() < 2)
      return rows;

    m_Range.setData(rows.get(0).dataset());
    indices = m_Range.getIntIndices();
    if (indices.length == 0) {
      debugMsg("[WARNING] No attributes to work on? Range: " + m_Range.getRange());
      return rows;
    }

    result = new ArrayList<>();

    values = rows.get(0).toDoubleArray();
    data   = new double[rows.size()];
    for (i = 0; i < indices.length; i++) {
      if (!rows.get(0).attribute(indices[i]).isNumeric())
        continue;
      for (n = 0; n < rows.size(); n++)
        data[n] = rows.get(n).value(indices[i]);
      values[indices[i]] = StatUtils.mean(data);
    }
    result.add(new DenseInstance(1.0, values));

    return result;
  }
}
