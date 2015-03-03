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
 * DefaultSimpleCSVReportReader.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.data.report.Report;

/**
 * Default implementation of a report reader of the simple CSV format.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultSimpleCSVReportReader
  extends AbstractSimpleCSVReportReader<Report> {

  /** for serialization. */
  private static final long serialVersionUID = 3515661897286794584L;

  /**
   * Returns a new instance of the report class in use.
   *
   * @return		the new (empty) report
   */
  public Report newInstance() {
    return new Report();
  }
}
