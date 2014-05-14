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
 * TestHelper.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.test;

import adams.core.base.BasePassword;
import adams.core.io.FileUtils;
import adams.data.container.DataContainer;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;

/**
 * A helper class specific to the adams project.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <I> the type of input data
 * @param <O> the type of output data
 */
public class TestHelper<I extends DataContainer, O>
  extends AbstractTestHelper<I, O> {

  /**
   * Initializes the helper class.
   *
   * @param owner	the owning test case
   * @param dataDir	the data directory to use
   */
  public TestHelper(AdamsTestCase owner, String dataDir) {
    super(owner, dataDir);
  }

  /**
   * Dummy, does nothing.
   *
   * @param filename	the filename to load (without path)
   * @return		always null
   */
  @Override
  public I load(String filename) {
    return null;
  }

  /**
   * Dummy, just write the string returned by the container's toString()
   * method to the file.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		always true
   */
  @Override
  public boolean save(O data, String filename) {
    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), data.toString(), false);
  }

  /**
   * Returns the database connection.
   *
   * @param url		the URL to use
   * @param user	the database user
   * @param password	the database password
   */
  @Override
  public AbstractDatabaseConnection getDatabaseConnection(String url, String user, BasePassword password) {
    m_DatabaseConnection = DatabaseConnection.getSingleton(url, user, password);
    return m_DatabaseConnection;
  }

  /**
   * Tries to connect to the database.
   *
   * @param url		the URL to use
   * @param user	the database user
   * @param password	the database password
   */
  @Override
  public void connect(String url, String user, BasePassword password) {
    String	lastError;

    m_DatabaseConnection = DatabaseConnection.getSingleton(url, user, password);
    lastError            = m_DatabaseConnection.getLastConnectionError();
    if (!m_DatabaseConnection.isConnected()) {
      try {
	m_DatabaseConnection.connect();
      }
      catch (Exception e) {
	e.printStackTrace();
      }
    }
    if (!m_DatabaseConnection.isConnected()) {
      if (m_DatabaseConnection.getLastConnectionError().length() > 0)
	lastError = m_DatabaseConnection.getLastConnectionError();
      throw new IllegalStateException(
	  "Failed to connect to database:\n"
	  + m_DatabaseConnection.toStringShort() + " (" + lastError + ")");
    }
  }
}
