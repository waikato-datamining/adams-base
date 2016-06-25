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
 * AbstractDatabaseTest.java
 * Copyright (C) 2009 University of Waikato
 */

package adams.test;

import adams.core.Properties;
import adams.core.base.BasePassword;
import adams.db.AbstractDatabaseConnection;
import adams.db.AbstractIndexedTable;

import java.util.Hashtable;

/**
 * Abstract Test class for flow actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDatabaseTestCase
  extends AdamsTestCase {

  /** property for an alternative test database props file. */
  public final static String PROPERTY_TESTDBPROPS = "adams.test.db.props";

  /** the properties. */
  protected Properties m_Properties;

  /** whether the tables got already initialized (JDBC URL &lt-&gt; true|false). */
  protected static Hashtable<String,Boolean> m_TablesInitialized;
  static {
    m_TablesInitialized = new Hashtable<String,Boolean>();
  }

  /**
   * Constructs the <code>AbstractDatabaseTest</code>. Called by subclasses.
   *
   * @param name the name of the test class
   */
  public AbstractDatabaseTestCase(String name) {
    super(name);
  }

  /**
   * Returns the name of the database properties file to use.
   *
   * @return		the filename
   */
  protected String getDatabasePropertiesFile() {
    return "adams/test/TestDatabase.props";
  }

  /**
   * Returns the properties for the flow tests.
   * If {@link #PROPERTY_TESTDBPROPS} is defined and points to a valid 
   * properties file, then the "file" parameter gets ignored.
   *
   * @param file	the file to load from the classpath, eg "adams/test/TestDatabase.props"
   * @return		the properties
   * @see		Properties#read(String)
   */
  protected synchronized Properties getDatabaseProperties(String file) {
    String	props;
    boolean	useDefault;
    
    if (m_Properties == null) {
      useDefault = true;
      props      = System.getProperty(PROPERTY_TESTDBPROPS);
      if (props != null) {
	try {
	  useDefault = false;
	  m_Properties = new Properties();
	  m_Properties.load(props);
	}
	catch (Exception e) {
	  System.err.println("Failed to read properties defined in " + PROPERTY_TESTDBPROPS + ": " + props);
	  e.printStackTrace();
	}
      }
      
      if (useDefault) {
	try {
	  m_Properties = Properties.read(file);
	}
	catch (Exception e) {
	  e.printStackTrace();
	  m_Properties = new Properties();
	}
      }
    }

    return m_Properties;
  }

  /**
   * Returns the properties for the flow tests.
   *
   * @return		the properties
   */
  public synchronized Properties getDatabaseProperties() {
    return getDatabaseProperties(getDatabasePropertiesFile());
  }

  /**
   * Returns the database URL.
   *
   * @return		the URL
   */
  public String getDatabaseURL() {
    return getDatabaseProperties().getProperty("DatabaseURL");
  }

  /**
   * Returns the database user.
   *
   * @return		the user
   */
  public String getDatabaseUser() {
    return getDatabaseProperties().getProperty("DatabaseUser");
  }

  /**
   * Returns the database password.
   *
   * @return		the password
   */
  public BasePassword getDatabasePassword() {
    return new BasePassword(getDatabaseProperties().getProperty("DatabasePassword"));
  }

  /**
   * Tries to connect to the database.
   */
  protected synchronized void connect() {
    m_TestHelper.connect(getDatabaseURL(), getDatabaseUser(), getDatabasePassword());
    initTables();
    postConnect();
  }

  /**
   * Hook method for actions after connecting to a database.
   * Calls the test helper's postConnect() method.
   */
  protected void postConnect() {
    m_TestHelper.postConnect();
  }

  /**
   * Reconnects to the default database.
   *
   * @see 		#getDatabasePropertiesFile()
   * @see		#reconnect(String)
   */
  protected synchronized void reconnect() {
    reconnect(getDatabasePropertiesFile());
  }

  /**
   * Reconnects to the databas setup stored in the specified props file.
   *
   * @param file	the props file with the database connection setup
   */
  protected synchronized void reconnect(String file) {
    m_Properties = null;
    getDatabaseProperties(file);
    connect();
  }

  /**
   * Initializes the tables if necessary.
   */
  protected void initTables() {
    if (!m_TablesInitialized.containsKey(getDatabaseURL()))
      m_TablesInitialized.put(getDatabaseURL(), false);

    if (!m_TablesInitialized.get(getDatabaseURL())) {
      AbstractIndexedTable.initTables(getDatabaseConnection());
      m_TablesInitialized.put(getDatabaseURL(), true);
    }
  }

  /**
   * Returns the database connection.
   */
  protected AbstractDatabaseConnection getDatabaseConnection() {
    return m_TestHelper.getDatabaseConnection(getDatabaseURL(), getDatabaseUser(), getDatabasePassword());
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    connect();

    // FIXME
    //GlobalDataContainerFilter.setFilter(
	//(AbstractFilter) OptionUtils.forCommandLine(AbstractFilter.class, getDatabaseProperties().getString("GlobalFilter")));
  }
}
