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
 * TimeseriesTestHelper.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.test;

import adams.core.Constants;
import adams.core.base.BasePassword;
import adams.data.DateFormatString;
import adams.data.io.input.SimpleTimeseriesReader;
import adams.data.io.output.SimpleTimeseriesWriter;
import adams.data.timeseries.Timeseries;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;

/**
 * A helper class specific to the project.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesTestHelper
  extends AbstractTestHelper<Timeseries, Timeseries> {

  /** the format for the timestamps in the regression tests (read). */
  protected DateFormatString m_RegressionTimestampFormatRead;

  /** the format for the timestamps in the regression tests (write). */
  protected DateFormatString m_RegressionTimestampFormatWrite;
  
  /**
   * Initializes the helper class.
   *
   * @param owner	the owning test case
   * @param dataDir	the data directory to use
   */
  public TimeseriesTestHelper(AdamsTestCase owner, String dataDir) {
    super(owner, dataDir);
    
    m_RegressionTimestampFormatRead  = new DateFormatString(Constants.TIMESTAMP_FORMAT_MSECS);
    m_RegressionTimestampFormatWrite = new DateFormatString(Constants.TIMESTAMP_FORMAT_MSECS);
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
   * Sets the timestamp format when reading input files for the regression
   * tests.
   * 
   * @param value	the format
   */
  public void setRegressionTimestampFormatRead(DateFormatString value) {
    m_RegressionTimestampFormatRead = value;
  }

  /**
   * Returns the timestamp format when reading input files for the regression
   * tests.
   * 
   * @return		the format
   */
  public DateFormatString getRegressionTimestampFormatRead() {
    return m_RegressionTimestampFormatRead;
  }

  /**
   * Sets the timestamp format when saving output files in the regression
   * tests.
   * 
   * @param value	the format
   */
  public void setRegressionTimestampFormatWrite(DateFormatString value) {
    m_RegressionTimestampFormatWrite = value;
  }

  /**
   * Returns the timestamp format when saving output files in the regression
   * tests.
   * 
   * @return		the format
   */
  public DateFormatString getRegressionTimestampFormatWrite() {
    return m_RegressionTimestampFormatWrite;
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

  /**
   * Loads the data to process using the SimpleTimeseriesReader.
   *
   * @param filename	the filename to load (without path)
   * @return		the data, null if it could not be loaded
   * @see		#getDataDirectory()
   * @see		SimpleTimeseriesReader
   */
  @Override
  public Timeseries load(String filename) {
    Timeseries			result;
    SimpleTimeseriesReader	reader;

    copyResourceToTmp(filename);

    result = null;
    reader = new SimpleTimeseriesReader();
    reader.setTimestampFormat(m_RegressionTimestampFormatRead);
    reader.setInput(new TmpFile(filename));
    if (reader.read().size() > 0)
      result = reader.read().get(0);
    reader.destroy();

    deleteFileFromTmp(filename);

    return result;
  }

  /**
   * Saves the data in the tmp directory using the SimpleTimeseriesWriter.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   * @see		SimpleTimeseriesWriter
   */
  @Override
  public boolean save(Timeseries data, String filename) {
    boolean			result;
    SimpleTimeseriesWriter	writer;
    TmpFile			output;

    writer = new SimpleTimeseriesWriter();
    writer.setTimestampFormat(m_RegressionTimestampFormatWrite);
    output = new TmpFile(filename);
    writer.setOutput(output);
    writer.write(data);
    result = output.exists();
    writer.destroy();

    return result;
  }
}
