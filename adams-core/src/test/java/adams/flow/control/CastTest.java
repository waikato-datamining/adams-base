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
 * CastTest.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.base.BaseClassname;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for Cast actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class CastTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CastTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.txt")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(CastTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.StringConstants tmp2 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] tmp3 = new adams.core.base.BaseString[5];
      tmp3[0] = (adams.core.base.BaseString) argOption.valueOf("1");
      tmp3[1] = (adams.core.base.BaseString) argOption.valueOf("2");
      tmp3[2] = (adams.core.base.BaseString) argOption.valueOf("3");
      tmp3[3] = (adams.core.base.BaseString) argOption.valueOf("4");
      tmp3[4] = (adams.core.base.BaseString) argOption.valueOf("5");
      tmp2.setStrings(tmp3);

      tmp1[0] = tmp2;
      adams.flow.control.Cast tmp4 = new adams.flow.control.Cast();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("classname");
      tmp4.setClassname((BaseClassname) argOption.valueOf("java.lang.String"));

      tmp1[1] = tmp4;
      adams.flow.sink.DumpFile tmp6 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("outputFile");
      tmp6.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp6.setAppend(true);

      tmp1[2] = tmp6;
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

