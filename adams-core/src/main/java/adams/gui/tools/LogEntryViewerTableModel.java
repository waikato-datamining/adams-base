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
 * LogEntryViewerModel.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import adams.db.LogEntry;
import adams.gui.core.AbstractBaseTableModel;
import adams.gui.core.CustomSearchTableModel;
import adams.gui.core.SearchParameters;

/**
 * The table model for displaying LogEntry objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogEntryViewerTableModel
  extends AbstractBaseTableModel
  implements CustomSearchTableModel {

  /** for serialization. */
  private static final long serialVersionUID = -7031190404664584447L;

  /** the underlying data. */
  protected Vector<LogEntry> m_Data;

  /**
   * Initializes the model with no data.
   */
  public LogEntryViewerTableModel() {
    this(new Vector<LogEntry>());
  }

  /**
   * Initializes the model with the specified data.
   *
   * @param data	the data to display
   */
  public LogEntryViewerTableModel(Vector<LogEntry> data) {
    super();

    m_Data = new Vector<LogEntry>(data);
  }

  /**
   * Returns the underlying data.
   *
   * @return		the data
   */
  public Vector<LogEntry> getData() {
    return m_Data;
  }

  /**
   * Returns the number of rows.
   *
   * @return		the number of rows
   */
  public int getRowCount() {
    if (m_Data == null)
      return 0;
    else
      return m_Data.size();
  }

  /**
   * Returns the number of columns in the table.
   * <pre>
   * Host
   * IP
   * DB-ID
   * Generation
   * Type
   * Statuts
   * Source
   * </pre>
   *
   * @return		the number of columns, always 7
   */
  public int getColumnCount() {
    return 7;
  }

  /**
   * Returns the name of the column.
   *
   * @param column	the column to retrieve the name for
   * @return		the name of the column
   */
  public String getColumnName(int column) {
    if (column == 0)
      return "Host";
    else if (column == 1)
      return "IP";
    else if (column == 2)
      return "DB-ID";
    else if (column == 3)
      return "Generation";
    else if (column == 4)
      return "Type";
    else if (column == 5)
      return "Status";
    else if (column == 6)
      return "Source";
    else
      throw new IllegalArgumentException("Illegal column index: " + column);
  }

  /**
   * Returns the LogEntry at the given position.
   *
   * @param row		the row in the table
   * @return		the entry
   */
  public LogEntry getLogEntryAt(int row) {
    return m_Data.get(row);
  }

  /**
   * Returns the value at the given position.
   *
   * @param row		the row in the table
   * @param column	the column in the table
   * @return		the value
   */
  public Object getValueAt(int row, int column) {
    LogEntry	entry;

    entry = m_Data.get(row);

    if (column == 0)
      return entry.getHost();
    else if (column == 1)
      return entry.getIP();
    else if (column == 2)
      return entry.getDatabaseID();
    else if (column == 3)
      return entry.getGenerationAsString();
    else if (column == 4)
      return entry.getType();
    else if (column == 5)
      return entry.getStatus();
    else if (column == 6)
      return entry.getSource();
    else
      throw new IllegalArgumentException("Illegal column index: " + column);
  }

  /**
   * Returns the class for the column.
   *
   * @param column	the column to retrieve the class for
   * @return		the class
   */
  public Class getColumnClass(int column) {
    if (column == 2)
      return Integer.class;
    else
      return String.class;
  }

  /**
   * Tests whether the search matches the specified row.
   *
   * @param params	the search parameters
   * @param row		the row of the underlying, unsorted model
   * @return		true if the search matches this row
   */
  public boolean isSearchMatch(SearchParameters params, int row) {
    LogEntry	entry;
    String[]	values;

    entry  = m_Data.get(row);
    values = new String[]{
	entry.getHost(),
	entry.getIP(),
	entry.getGenerationAsString(),
	entry.getType(),
	entry.getStatus(),
	entry.getSource()};

    if (params.isInteger() && (params.matches(entry.getDatabaseID()))) {
      return true;
    }
    else {
      for (String value: values) {
	if (params.matches(value))
	  return true;
      }
    }

    return false;
  }

  /**
   * Removes all entries.
   */
  public void clear() {
    synchronized(m_Data) {
      m_Data.clear();
    }
    fireTableDataChanged();
  }

  /**
   * Adds the log entry to the model (appended at the end).
   *
   * @param entry	the entry to add
   */
  public void add(LogEntry entry) {
    add(entry, false);
  }

  /**
   * Adds the log entry to the model. Entry can be inserted at the end or
   * in sorted fashion.
   *
   * @param entry	the entry to add
   * @param sort	whether to sort the data
   */
  public void add(LogEntry entry, boolean sort) {
    synchronized(m_Data) {
      m_Data.add(entry);
    }
    if (sort)
      Collections.sort(m_Data);
    fireTableDataChanged();
  }

  /**
   * Adds the log entries to the model (appended at the end).
   *
   * @param entries	the entries to add
   */
  public void addAll(Collection<LogEntry> entries) {
    addAll(entries, false);
  }

  /**
   * Adds the log entries to the model. Entries can be inserted at the end or
   * in sorted fashion.
   *
   * @param entries	the entries to add
   * @param sort	whether to sort the data
   */
  public void addAll(Collection<LogEntry> entries, boolean sort) {
    synchronized(m_Data) {
      m_Data.addAll(entries);
      if (sort)
	Collections.sort(m_Data);
    }
    fireTableDataChanged();
  }
}
