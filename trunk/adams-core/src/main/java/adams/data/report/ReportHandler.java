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
 * ReportHandler.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.data.report;

/**
 * Interface for data containers that handle reports.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of the report
 */
public interface ReportHandler<T extends Report> {

  /**
   * Checks whether a report is present.
   *
   * @return		true if a report is present
   */
  public boolean hasReport();

  /**
   * Returns the report.
   *
   * @return		the report, can be null if none available
   */
  public T getReport();
}
