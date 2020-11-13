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
 * MultiReportWriter.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.data.report.Report;

/**
 * Interface for report writers that write multiple reports into a single file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface MultiReportWriter<T extends Report>
  extends ReportWriter<T> {

  /**
   * Returns the class of the report that the writer supports.
   *
   * @return		the class
   */
  public Class getReportClass();

  /**
   * Performs checks and writes the data.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  public boolean write(T[] data);
}
