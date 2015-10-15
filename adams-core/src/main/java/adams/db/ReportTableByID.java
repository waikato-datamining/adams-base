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
 * ReportTableByID.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import adams.core.Utils;
import adams.data.id.IDHandler;
import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

import java.util.Vector;
import java.util.logging.Level;

/**
 * Abstract ancestor for classes that provide access to reports stored in
 * tables.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <R> the type of reports to handle
 * @param <F> the type of fields to handle
 */
public abstract class ReportTableByID<R extends Report & IDHandler, F extends AbstractField>
  extends AbstractIndexedTable
  implements FieldProvider<F>, ReportProviderByID<R> {

  /** for serialization. */
  private static final long serialVersionUID = -1143611181126566480L;

  /**
   * Initializes the table class with the given table name.
   *
   * @param dbcon	the database context this table is used in
   * @param tableName	the name of the table
   */
  public ReportTableByID(AbstractDatabaseConnection dbcon, String tableName) {
    super(dbcon, tableName);
  }

  /**
   * Creates a new Field object.
   *
   * @param name	the name of the field
   * @param type	the type of the field
   * @return		the generated field
   * @throws Exception	if the field type is not handled
   */
  protected Field createField(String name, String type) throws Exception {
    Field	result;

    if (type.equals("B"))
      result = new Field(name, DataType.BOOLEAN);
    else if (type.equals("S"))
      result = new Field(name, DataType.STRING);
    else if (type.equals("N"))
      result = new Field(name, DataType.NUMERIC);
    else
      throw new IllegalStateException("Unhandled type '" + type + "'!");

    return result;
  }

  /**
   * Parses the given string according to the given field's type.
   *
   * @param field	the field that specifies the type
   * @param s		the value to parse
   * @return		the parsed value
   */
  protected Object parse(AbstractField field, String s) {
    if (field.getDataType() == DataType.BOOLEAN)
      return Boolean.parseBoolean(s);
    else if (field.getDataType() == DataType.NUMERIC)
      return Utils.toDouble(s);
    else
      return Field.fixString(s);
  }

  /**
   * Returns all available fields.
   *
   * @return		the list of fields
   */
  public Vector<F> getFields() {
    return getFields(null);
  }

  /**
   * Removes the report from the database.
   *
   * @param report	the report to delete (and obtain the parent ID from)
   * @return		true if successfully removed
   */
  public boolean remove(R report) {
    return remove(report.getID());
  }

  /**
   * Removes the quantitation report from the database.
   *
   * @param id		ID of chromatogram
   * @return		true if successfully removed
   */
  public boolean remove(String id) {
    boolean	result;
    String	sql;

    // build SQL statement
    sql =   "DELETE "
      + "FROM " + getTableName() + " "
      + "WHERE ID = " + backquote(id);

    // execute SQL
    try {
      execute(sql);
      result = true;
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed to remove: " + sql, e);
    }

    return result;
  }

  /**
   * Stores the report. Always removes an already existing report
   * first.
   *
   * @param id	the id of the report
   * @param report	the report
   * @return		true if successfully inserted
   */
  public boolean store(String id, R report) {
    return store(id, report, true, false, new Field[]{});
  }

  /**
   * Stores the report. Report can be merged with already existing one or
   * the existing one can be removed from the database first.
   *
   * @param id		the id of the report
   * @param report		the report
   * @param removeExisting	whether to remove existing an already existing
   * 				report before storing it (has precedence over
   * 				"merge")
   * @param merge		whether to merge the existing and the current
   * @param overwrite		fields to overwrite if in "merge" mode
   * @return			true if successfully inserted/updated
   */
  public boolean store(String id, R report, boolean removeExisting, boolean merge, Field[] overwrite) {
    boolean	result;
    boolean	exists;
    R		reportOld;
    boolean	canSave;
    int		i;

    result  = false;
    exists  = exists(id);
    canSave = !exists;

    if (exists) {
      getLogger().info("Report already exists for: " + id);
      if (removeExisting) {
	getLogger().info("Removing old report for: " + id);
	remove(id);
	canSave = true;
      }
      else if (merge) {
	getLogger().info("Merging with old report: " + id);
	reportOld = load(id);
	reportOld.mergeWith(report);
	reportOld.setValue(new Field(Report.FIELD_DUMMYREPORT, DataType.BOOLEAN), false);
	// fields to overwrite?
	if (overwrite != null) {
	  for (i = 0; i < overwrite.length; i++) {
	    try {
	      if (report.hasValue(overwrite[i]))
		reportOld.setValue(overwrite[i], report.getValue(overwrite[i]));
	    }
	    catch (Exception e) {
	      getLogger().log(Level.SEVERE, "Error overwriting field '" + overwrite[i] + "' in report '" + report.getDatabaseID() + "':", e);
	    }
	  }
	}
	report  = reportOld;
	canSave = true;
      }
      else {
	getLogger().info("Not saving report for: " + id);
	id = null;
      }
    }

    if (canSave) {
      getLogger().info("Storing in DB: " + id);
      result = doStore(id, report);
    }

    return result;
  }

  /**
   * Stores the report. Either updates or inserts the fields.
   *
   * @param id	the id of the report
   * @param report	the report
   * @return		true if successfully inserted
   */
  protected abstract boolean doStore(String id, R report);
}
