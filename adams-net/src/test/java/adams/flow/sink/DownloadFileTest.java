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
 * DownloadFileTest.java
 * Copyright (C) 2011-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.env.Environment;
import adams.flow.AbstractOnlineFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for DownloadFile actor.
 * <br>
 * NB: Dummy test.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class DownloadFileTest
  extends AbstractOnlineFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DownloadFileTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.deleteFileFromTmp("out.html");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("out.html");

    super.tearDown();
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(DownloadFileTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    return new Flow();
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(adams.env.Environment.class);
    runTest(suite());
  }
}

