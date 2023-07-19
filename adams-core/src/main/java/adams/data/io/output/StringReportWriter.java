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
 * StringReportWriter.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import adams.core.MessageCollection;
import adams.data.report.Report;

/**
 * Interface for report writers that can output the report as string.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface StringReportWriter<T extends Report>
  extends ReportWriter<T> {

  /**
   * Performs checks and converts the report to a string.
   *
   * @param data	the data to write
   * @param errors 	for collecting errors
   * @return		the generated data, null in case of failure
   */
  public String write(T data, MessageCollection errors);
}
