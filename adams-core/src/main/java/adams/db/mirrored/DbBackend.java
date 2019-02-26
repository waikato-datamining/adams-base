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
 * DbBackend.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.db.mirrored;

import adams.core.Utils;
import adams.db.AbstractDatabaseConnection;
import adams.db.AbstractDbBackend;
import adams.db.DatabaseConnection;
import adams.db.JdbcUrl;
import adams.db.LogIntf;
import adams.db.SQLIntf;

/**
 * MySQL Spectral backend.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DbBackend
  extends AbstractDbBackend {

  private static final long serialVersionUID = -8233202811908896313L;

  /** the database connections of the mirrors. */
  protected AbstractDatabaseConnection[] m_Mirrors;

  /** the urls the mirrors are for. */
  protected JdbcUrl[] m_MirrorsForUrls;

  /** the backends. */
  protected adams.db.DbBackend[] m_Backends;

  /** the default backend. */
  protected adams.db.DbBackend m_NonMirroredBackend;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Mirrored Spectral backend.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "mirror", "mirrors",
      new AbstractDatabaseConnection[0]);

    m_OptionManager.add(
      "mirror-for-url", "mirrorsForUrls",
      new JdbcUrl[0]);

    m_OptionManager.add(
      "backend", "backends",
      new adams.db.DbBackend[0]);

    m_OptionManager.add(
      "non-mirrored-backend", "nonMirroredBackend",
      new adams.db.mysql.DbBackend());
  }

  /**
   * Sets the mirror connections to use.
   *
   * @param value	the connections
   */
  public void setMirrors(AbstractDatabaseConnection[] value) {
    m_Mirrors = value;
    m_MirrorsForUrls = (JdbcUrl[]) Utils.adjustArray(m_MirrorsForUrls, m_Mirrors.length, new JdbcUrl());
    m_Backends = (adams.db.DbBackend[]) Utils.adjustArray(m_Backends, m_Mirrors.length, new adams.db.mysql.DbBackend());
    reset();
  }

  /**
   * Returns the mirror connections in use.
   *
   * @return		the connections
   */
  public AbstractDatabaseConnection[] getMirrors() {
    return m_Mirrors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String mirrorsTipText() {
    return "The connections for the mirrors.";
  }

  /**
   * Sets the URLs that the mirrors are for.
   *
   * @param value	the urls
   */
  public void setMirrorsForUrls(JdbcUrl[] value) {
    m_MirrorsForUrls = value;
    m_Mirrors = (AbstractDatabaseConnection[]) Utils.adjustArray(m_Mirrors, m_MirrorsForUrls.length, DatabaseConnection.getSingleton());
    m_Backends = (adams.db.DbBackend[]) Utils.adjustArray(m_Backends, m_Mirrors.length, new adams.db.mysql.DbBackend());
    reset();
  }

  /**
   * Returns the URLs that the mirrors are for.
   *
   * @return		the urls
   */
  public JdbcUrl[] getMirrorsForUrls() {
    return m_MirrorsForUrls;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String mirrorsForUrlsTipText() {
    return "The URLs that the mirrors correspond to.";
  }

  /**
   * Sets the backends to use.
   *
   * @param value	the backends
   */
  public void setBackends(adams.db.DbBackend[] value) {
    m_Backends = value;
    m_MirrorsForUrls = (JdbcUrl[]) Utils.adjustArray(m_MirrorsForUrls, m_Backends.length, new JdbcUrl());
    m_Mirrors = (AbstractDatabaseConnection[]) Utils.adjustArray(m_Mirrors, m_Backends.length, DatabaseConnection.getSingleton());
    reset();
  }

  /**
   * Returns the backends in use.
   *
   * @return		the backends
   */
  public adams.db.DbBackend[] getBackends() {
    return m_Backends;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backendsTipText() {
    return "The backends for the mirrors.";
  }

  /**
   * Sets the backend to use for non-mirrored connections.
   *
   * @param value	the backend
   */
  public void setNonMirroredBackend(adams.db.DbBackend value) {
    m_NonMirroredBackend = value;
    reset();
  }

  /**
   * Returns the backend to use for non-mirrored connections.
   *
   * @return		the backend
   */
  public adams.db.DbBackend getNonMirroredBackend() {
    return m_NonMirroredBackend;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nonMirroredBackendTipText() {
    return "The backend to use for non-mirrored connections.";
  }

  /**
   * Returns the corresponding mirror for the connection URL provided.
   *
   * @param conn	the URL to get the mirror for
   * @return		the mirror, null if no mirror
   */
  protected AbstractDatabaseConnection getMirrorFor(AbstractDatabaseConnection conn) {
    int		i;

    for (i = 0; i < m_MirrorsForUrls.length; i++) {
      if (m_MirrorsForUrls[i].getValue().equals(conn.getURL()))
        return m_Mirrors[i];
    }

    return null;
  }

  /**
   * Returns the corresponding backend for the connection URL provided.
   *
   * @param conn	the URL to get the backend for
   * @return		the backend, null if no backend
   */
  protected adams.db.DbBackend getBackendFor(AbstractDatabaseConnection conn) {
    int		i;

    for (i = 0; i < m_MirrorsForUrls.length; i++) {
      if (m_MirrorsForUrls[i].getValue().equals(conn.getURL()))
        return m_Backends[i];
    }

    return null;
  }

  /**
   * Checks whether connection can be mirrored.
   *
   * @param conn	the connection to check
   * @return		true if can be mirrored
   */
  protected boolean canMirror(AbstractDatabaseConnection conn) {
    return (getMirrorFor(conn) != null)
      && (getBackendFor(conn) != null);
  }

  /**
   * Returns the handler for the spectrum table.
   *
   * @param conn	the database connection
   * @return		the handler
   */
  @Override
  public SQLIntf getSQL(AbstractDatabaseConnection conn) {
    if (canMirror(conn))
      return SQL.getSingleton(conn, getBackendFor(conn).getSQL(getMirrorFor(conn)));
    else
      return m_NonMirroredBackend.getSQL(conn);
  }

  /**
   * Returns the handler for the sample data table.
   *
   * @param conn	the database connection
   * @return		the handler
   */
  @Override
  public LogIntf getLog(AbstractDatabaseConnection conn) {
    if (canMirror(conn))
      return LogT.getSingleton(conn, getBackendFor(conn).getLog(getMirrorFor(conn)));
    else
      return m_NonMirroredBackend.getLog(conn);
  }
}
