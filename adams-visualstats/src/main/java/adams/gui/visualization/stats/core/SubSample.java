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
 * SubSample.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.core;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.StatUtils;

/**
 * Class for taking a smaller sample of a dataset. Used by the matrix class.
 *
 * @author msf8
 * @version $Revision$
 */
public class SubSample {

  /** Instances to plot */
  protected SpreadSheet m_Data;

  /**Percentage of data to take */
  protected double m_Percentage;

  /**
   * constructor
   * @param inst			Instances for plotting
   * @param percent		Percent of data for sub sample
   */
  public SubSample(SpreadSheet inst, double percent) {
    m_Data = inst;
    m_Percentage = percent;
  }

  /**
   * Take a sample of the dataset
   * @return				Instances containing specified instances of the original instances
   * @throws Exception
   */
  public SpreadSheet sample() throws Exception {
    SpreadSheet		result;
    int[]		indices;

    result  = m_Data.getHeader();
    indices = StatUtils.subsample(m_Data.getRowCount(), m_Percentage / 100, 42).toArray();
    for (int index: indices)
      result.addRow().assign(m_Data.getRow(index));

    return result;
  }
}