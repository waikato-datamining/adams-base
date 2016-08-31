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
 * DatabaseContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.data;

import weka.experiment.InstanceQuery;

import java.util.logging.Level;

/**
 * Dataset loaded from database.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
      InstanceQuery instq = new InstanceQuery();
      instq.setDatabaseURL(url);
      instq.setUsername(user);
      instq.setPassword(pw);
      instq.setQuery(query);
      m_Data     = instq.retrieveInstances();
      m_URL      = url;
      m_Query    = query;
      m_User     = user;
      m_Password = pw;
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
}
