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
 * NoPreProcessing.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.report;

import adams.data.report.Report;

/**
 * Dummy, performs no pre-processing at all.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NoPreProcessing<T extends Report>
  extends AbstractReportPreProcessor<T> {

  /** for serialization. */
  private static final long serialVersionUID = -2567606165350541861L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, performs no pre-processing at all.";
  }

  /**
   * Performs the actual pre-processing.
   * 
   * @param data	the data to process
   * @return		the processed data
   */
  @Override
  protected T doPreProcess(T data) {
    return data;
  }
}
