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
 * ReportProviderByDBID.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import adams.data.report.Field;
import adams.data.report.Report;

import java.util.List;

/**
 * Interface for table classes that handle the reports of a project.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of report to handle
 * @see AbstractIndexedTable
 */
public interface ReportProviderByDBID<T extends Report>
  extends ReportProvider<T, Integer> {

  /**
   * Stores the report. Removes a previously existing report.
   *
   * @param parent_id	the parent_id of the report
   * @param report	the report
   * @return		true if successfully inserted/updated
   */
  public boolean store(Integer parent_id, T report);

  /**
   * Stores the report. Either updates or inserts the fields.
   *
   * @param parent_id		the parent_id of the report
   * @param report		the report
   * @param removeExisting	whether to remove existing an already existing
   * 				report before storing it (has precedence over
   * 				"merge")
   * @param merge		whether to merge the existing and the current
   * @param overwrite		fields to overwrite if in "merge" mode
   * @return			true if successfully inserted/updated
   */
  public boolean store(Integer parent_id, T report, boolean removeExisting, boolean merge, Field[] overwrite);

  /**
   * Loads the report from the database.
   *
   * @param parent_id	the ID of parent data container
   * @return		the report
   */
  public T load(Integer parent_id);

  /**
   * Checks whether the report exists in the database.
   *
   * @param parent_id	the ID of parent data container
   * @return		true if the report exists
   */
  public boolean exists(Integer parent_id);

  /**
   * Removes the report from the database.
   *
   * @param parent_id	the ID of the parent data container
   * @return		true if successfully removed
   */
  public boolean remove(Integer parent_id);

  /**
   * Return a list (Vector) of IDs of data containers that match the defined
   * conditions.
   *
   * @param conditions	the conditions that the conatiners must meet
   * @return		list of container IDs
   */
  public List<String> getIDs(AbstractConditions conditions);

  /**
   * Return a list (Vector) of columns of data containers that match the defined
   * conditions.
   *
   * @param columns	the columns to retrieve.
   * @param conditions	the conditions that the containers must meet
   * @return		list of columns (tab-separated)
   */
  public List<String> getIDs(String[] columns, AbstractConditions conditions);

  /**
   * Return a list of database IDs of data containers that match the defined
   * conditions.
   *
   * @param conditions	the conditions that the conatiners must meet
   * @return		list of database IDs
   */
  public List<Integer> getDBIDs(AbstractConditions conditions);
}
