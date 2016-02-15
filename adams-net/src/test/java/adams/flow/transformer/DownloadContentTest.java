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
 * DownloadContentTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseURL;
import adams.core.net.InetUtils;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractOnlineFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.sink.DumpFile;

/**
 * Test for DownloadContent actor.
 *
 * @author fracpete
 * @version $Revision$
 */
public class DownloadContentTest
  extends AbstractOnlineFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DownloadContentTest(String name) {
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
    return new TestSuite(DownloadContentTest.class);
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

    host = "adams.cms.waikato.ac.nz";
    if (!InetUtils.hasConnection(host, 3000))
      System.err.println("No connection to host '" + host + "'? Trying anyway...");
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors = new adams.flow.core.Actor[3];
      adams.flow.source.URLSupplier url = new adams.flow.source.URLSupplier();
      argOption = (AbstractArgumentOption) url.getOptionManager().findByProperty("URLs");
      url.setURLs(new BaseURL[]{(adams.core.base.BaseURL) argOption.valueOf("https://" + host + "/index.html")});

      actors[0] = url;
      adams.flow.transformer.DownloadContent download = new adams.flow.transformer.DownloadContent();

      actors[1] = download;
      
      DumpFile df = new DumpFile();
      df.setOutputFile(new adams.core.io.PlaceholderFile("${TMP}/out.html"));
      
      actors[2] = df;
      
      flow.setActors(actors);
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

