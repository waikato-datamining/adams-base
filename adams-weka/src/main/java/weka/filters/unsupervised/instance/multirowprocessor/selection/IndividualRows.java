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
 * IndividualRows.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance.multirowprocessor.selection;

import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

/**
 * Just selects each row by itself.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class IndividualRows
  extends AbstractRowSelection {

  private static final long serialVersionUID = -8519118208205929299L;

  /**
   * Returns a string describing the row selection scheme.
   *
   * @return a description suitable for displaying in the explorer/experimenter
   * gui
   */
  @Override
  public String globalInfo() {
    return "Just selects each row by itself.";
  }

  /**
   * Returns the list of row indices generated from the data.
   *
   * @param data the data to generate row selections from
   * @throws Exception if checks or selection failed
   * @return the list of selections
   */
  @Override
  protected List<int[]> doSelectRows(Instances data) throws Exception {
    ArrayList<int[]>	result;
    int			i;

    result = new ArrayList<>();
    for (i = 0; i < data.numInstances(); i++)
      result.add(new int[]{i});

    return result;
  }
}
