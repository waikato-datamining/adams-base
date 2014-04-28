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
 * MutableReportHandler.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.data.report;

/**
 * Interface for data containers that can set a report as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of report to handle
 */
public interface MutableReportHandler<T extends Report>
  extends ReportHandler<T> {

  /**
   * Sets a new report.
   *
   * @param value	the new report
   */
  public void setReport(T value);
}
