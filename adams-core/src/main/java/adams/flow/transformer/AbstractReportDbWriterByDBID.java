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
 * AbstractReportDbWriterByDBID.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.Constants;
import adams.data.report.Report;

/**
 * Abstract ancestor for actors that write reports to the database.
 * Uses database IDs.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 12299 $
 * @param <T> the type of report to handle
 */
public abstract class AbstractReportDbWriterByDBID<T extends Report>
  extends AbstractReportDbWriter<T, Integer> {

  private static final long serialVersionUID = 7715639177204568610L;

  /**
   * Extracts the ID from the report.
   *
   * @param report	the report to extract the ID from
   * @return		the ID
   */
  protected Integer extractID(T report) {
    return report.getDatabaseID();
  }

  /**
   * Checks whether the ID is valid.
   *
   * @param id		the ID to check
   * @return		true if valid
   */
  protected boolean checkID(Integer id) {
    return (id != Constants.NO_ID);
  }
}
