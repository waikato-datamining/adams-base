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
 * DatabaseContainer.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.data;

import adams.core.base.BasePassword;
import adams.data.conversion.SpreadSheetToWekaInstances;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.sql.DefaultTypeMapper;
import adams.data.spreadsheet.sql.Reader;
import adams.db.DatabaseConnection;
import adams.db.SQLF;
import adams.db.SQLIntf;
import weka.core.Instances;
import weka.experiment.InstanceQuery;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.logging.Level;

/**
 * Dataset loaded from database.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DatabaseContainer
  extends AbstractDataContainer {

  private static final long serialVersionUID = 6267905940957451551L;

  /** the database URL. */
  protected String m_URL;

  /** the query used to load the data. */
  protected String m_Query;

  /** the user. */
  protected String m_User;

  /** the password. */
  protected String m_Password;

  /**
   * Loads the data using the specified url/query.
   *
   * @param url		the JDBC URL
   * @param user	the database user
   * @param pw		the password
   * @param query	the query used
   */
  public DatabaseContainer(String url, String user, String pw, String query) {
    super();
    try {
      DatabaseConnection conn = new DatabaseConnection(url, user, new BasePassword(pw));
      SQLIntf sql = SQLF.getSingleton(conn);
      Reader reader = new Reader(new DefaultTypeMapper(), DenseDataRow.class);
      ResultSet rs = sql.getResultSet(query);
      SpreadSheet sheet = reader.read(rs);
      SpreadSheetToWekaInstances conv = new SpreadSheetToWekaInstances();
      conv.setInput(sheet);
      String msg = conv.convert();
      if (msg != null)
        throw new IllegalStateException(msg);
      m_Data     = conv.getOutput(Instances.class);
      m_URL      = url;
      m_Query    = query;
      m_User     = user;
      m_Password = pw;
      conv.cleanUp();
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Failed to load data from DB: " + url, e);
    }
  }

  /**
   * Returns the source of the data item.
   *
   * @return		the source
   */
  @Override
  public String getSource() {
    if (m_URL == null)
      return "<unknown>";
    else
      return m_User + ":" + m_Password.replaceAll(".", "*") + "@" + m_URL + " using " + m_Query;
  }

  /**
   * Whether it is possible to reload this item.
   *
   * @return		true if reloadable
   */
  @Override
  public boolean canReload() {
    return (m_URL != null);
  }

  /**
   * Reloads the data.
   *
   * @return		true if successfully reloaded
   */
  @Override
  protected boolean doReload() {
    InstanceQuery 	instq;

    try {
      instq = new InstanceQuery();
      instq.setDatabaseURL(m_URL);
      instq.setUsername(m_User);
      instq.setPassword(m_Password);
      instq.setQuery(m_Query);
      m_Data = instq.retrieveInstances();
      return true;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to reload from database: " + m_URL, e);
      return false;
    }
  }

  /**
   * Returns the data to store in the undo.
   *
   * @return		the undo point
   */
  protected Serializable[] getUndoData() {
    return new Serializable[]{
      m_Data,
      m_Modified,
      m_URL,
      m_User,
      m_Password,
      m_Query
    };
  }

  /**
   * Restores the data from the undo point.
   *
   * @param data	the undo point
   */
  protected void applyUndoData(Serializable[] data) {
    m_Data     = (Instances) data[0];
    m_Modified = (Boolean) data[1];
    m_URL      = (String) data[2];
    m_User     = (String) data[3];
    m_Password = (String) data[4];
    m_Query    = (String) data[5];
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    super.cleanUp();
    m_URL      = null;
    m_User     = null;
    m_Password = null;
    m_Query    = null;
  }
}
