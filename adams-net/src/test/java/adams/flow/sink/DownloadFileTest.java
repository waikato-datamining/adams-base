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

import adams.core.base.BaseURL;
import adams.core.net.InetUtils;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractOnlineFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for DownloadFile actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
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
    AbstractArgumentOption	argOption;
    String			host;

    Flow flow = new Flow();

    host = "github.com";
    if (!InetUtils.hasConnection(host, 3000))
      System.err.println("No connection to host '" + host + "'? Trying anyway...");
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[2];
      adams.flow.source.URLSupplier tmp2 = new adams.flow.source.URLSupplier();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("URLs");
      tmp2.setURLs(new BaseURL[]{(adams.core.base.BaseURL) argOption.valueOf("https://" + host + "/waikato-datamining/adams-base/blob/master/README.md")});

      tmp1[0] = tmp2;
      adams.flow.sink.DownloadFile tmp4 = new adams.flow.sink.DownloadFile();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("outputFile");
      tmp4.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/out.html"));

      tmp1[1] = tmp4;
      flow.setActors(tmp1);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }

    return flow;
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

