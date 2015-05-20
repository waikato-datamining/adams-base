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
 * AbstractOnlineFlowTest.java
 * Copyright (C) 2011-2013 University of Waikato
 */

package adams.flow;


/**
 * Abstract Test class for flow actors that need an internet connection.
 * <br><br>
 * It is possible to disable the execution of the test, in case no internet
 * connection is available, using the "adams.test.flow.offline" property.
 * E.g.: <code>-Dadams.test.flow.offline=true</code>.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see #isOffline()
 * @see #PROPERTY_OFFLINE
 */
public abstract class AbstractOnlineFlowTest
  extends AbstractFlowTest {

  /** the system property for specifying the dump file for the flow. */
  public final static String PROPERTY_OFFLINE = "adams.test.flow.offline";

  /** whether we are offline. */
  protected boolean m_Offline;

  /** whether the information about online/offline tests has been output. */
  protected static boolean m_OfflineInfoOutput;

  /**
   * Constructs the <code>AbstractFlowTest</code>. Called by subclasses.
   *
   * @param name the name of the test class
   */
  public AbstractOnlineFlowTest(String name) {
    super(name);

    m_Offline = Boolean.getBoolean(PROPERTY_OFFLINE);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    if (!m_OfflineInfoOutput) {
      m_OfflineInfoOutput = true;
      System.err.println(
	  "Execution of tests that require internet connection can be turned off.\n"
        + "You can do this using the following JVM parameter:\n"
	+ "  -D" + PROPERTY_OFFLINE + "=true");
    }
  }

  /**
   * Returns whether the test is run in offline-mode.
   */
  protected boolean isOffline() {
    return m_Offline;
  }

  /**
   * Tests an example actor setup, but only if internet connection is
   * available.
   *
   * @see		#isOffline()
   * @see 		#PROPERTY_OFFLINE
   */
  @Override
  public void testActor() {
    if (isOffline())
      return;

    super.testActor();
  }
}
