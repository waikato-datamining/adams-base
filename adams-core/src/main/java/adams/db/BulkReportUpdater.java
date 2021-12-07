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
 * BulkReportUpdater.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import adams.data.report.DataType;
import adams.data.report.Report;

/**
 * Interface for table classes that handle the reports of a project.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of report to handle
 * @see AbstractIndexedTable
 */
public interface BulkReportUpdater<T extends Report>
    extends DatabaseConnectionProvider {

  /**
   * Stores the records. Removes any previously existing reference values.
   *
   * @param records	the report
   * @param types	the data types to import
   * @param skipFields 	the fields to skip (regular expression), null to accept all
   * @param batchSize   the maximum number of records in one batch
   * @return		true if successfully inserted/updated
   */
  public boolean bulkStore(T[] records, DataType[] types, String skipFields, int batchSize);

  /**
   * Interrupts a currently running bulk store, if possible.
   */
  public void stopBulkStore();
}
