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
 * TryCatchTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.data.io.input.SingleStringTextReader;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for TryCatch actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class TryCatchTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TryCatchTest(String name) {
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
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
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
    return new TestSuite(TryCatchTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.FileSupplier tmp2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("files");
      tmp2.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${CWD}/some/where.txt")});

      tmp1[0] = tmp2;
      adams.flow.control.TryCatch tmp4 = new adams.flow.control.TryCatch();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("try");
      adams.flow.control.SubProcess tmp6 = new adams.flow.control.SubProcess();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("name");
      tmp6.setName((java.lang.String) argOption.valueOf("try"));

      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp8 = new adams.flow.core.AbstractActor[1];
      adams.flow.transformer.TextFileReader tmp9 = new adams.flow.transformer.TextFileReader();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("outputType");
      tmp9.setReader(new SingleStringTextReader());

      tmp8[0] = tmp9;
      tmp6.setActors(tmp8);

      tmp4.setTry(tmp6);

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("catch");
      adams.flow.source.StringConstants tmp12 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("name");
      tmp12.setName((java.lang.String) argOption.valueOf("catch"));

      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] tmp14 = new adams.core.base.BaseString[1];
      tmp14[0] = (adams.core.base.BaseString) argOption.valueOf("broken");
      tmp12.setStrings(tmp14);

      tmp4.setCatch(tmp12);

      tmp1[1] = tmp4;
      adams.flow.sink.DumpFile tmp15 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("outputFile");
      tmp15.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp1[2] = tmp15;
      flow.setActors(tmp1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("errorHandling");
      flow.setErrorHandling((adams.flow.control.Flow.ErrorHandling) argOption.valueOf("ACTORS_DECIDE_TO_STOP_ON_ERROR"));

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

