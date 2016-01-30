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
 * LogTSQLite.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.db;

import adams.core.base.BaseDateTime;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * SQLite implementation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogTSQLite
  extends LogT {

  private static final long serialVersionUID = 6309576140124918549L;

  /**
   * The constructor.
   *
   * @param dbcon the database context this table is used in
   */
  protected LogTSQLite(AbstractDatabaseConnection dbcon) {
    super(dbcon);
  }

  /**
   * Loads log entries from the database that match the conditions.
   *
   * @param cond	the conditions for the entries to match
   * @return		the log entries
   */
  public List<LogEntry> load(LogEntryConditions cond) {
    List<LogEntry>	result;
    LogEntry		log;
    ResultSet rs;
    StringBuilder	sqlWhere;
    List<String>	where;
    int			i;

    result = new ArrayList<LogEntry>();

    cond.update();

    // translate conditions
    where = new ArrayList<String>();
    if (!cond.getHost().isEmpty() && !cond.getHost().isMatchAll())
      where.add("HOST REGEXP " + backquote(cond.getHost()));
    if (!cond.getIP().isEmpty() && !cond.getIP().isMatchAll())
      where.add("IP REGEXP " + backquote(cond.getIP()));
    if (!cond.getType().isEmpty() && !cond.getType().isMatchAll())
      where.add("TYPE REGEXP " + backquote(cond.getType()));
    if (!cond.getStatus().isEmpty() && !cond.getStatus().isMatchAll())
      where.add("STATUS REGEXP " + backquote(cond.getStatus()));
    if (!cond.getSource().isEmpty() && !cond.getSource().isMatchAll())
      where.add("SOURCE REGEXP " + backquote(cond.getSource()));
    if (!cond.getGenerationStartDate().equals(BaseDateTime.infinityPast()))
      where.add("GENERATION >= '" + cond.getGenerationStartDate().stringValue() + "'");
    if (!cond.getGenerationEndDate().equals(BaseDateTime.infinityFuture()))
      where.add("GENERATION <= '" + cond.getGenerationEndDate().stringValue() + "'");

    // generate sql
    sqlWhere = new StringBuilder();
    for (i = 0; i < where.size(); i++) {
      if (i > 0)
	sqlWhere.append(" AND ");
      sqlWhere.append(where.get(i));
    }
    if (cond.getLatest())
      sqlWhere.append(" ORDER BY GENERATION DESC");
    else
      sqlWhere.append(" ORDER BY GENERATION ASC");
    if (cond.getLimit() > -1)
      sqlWhere.append(" LIMIT " + cond.getLimit());

    // retrieve data
    rs = null;
    try {
      rs = select("*", sqlWhere.toString());
      while ((log = resultsetToObject(rs)) != null)
	result.add(log);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to load: " + sqlWhere, e);
    }
    finally {
      closeAll(rs);
    }

    Collections.sort(result);

    return result;
  }
}
