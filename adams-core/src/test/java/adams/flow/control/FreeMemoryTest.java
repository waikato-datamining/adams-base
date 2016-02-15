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
 * FreeMemoryTest.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.Actor;
import adams.flow.transformer.BaseName;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for FreeMemory actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class FreeMemoryTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public FreeMemoryTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("simple.report");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("simple.report");
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
    return new TestSuite(FreeMemoryTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[4];
      adams.flow.standalone.CallableActors tmp2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp3 = new adams.flow.core.Actor[1];
      adams.flow.sink.DumpFile tmp4 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("outputFile");
      tmp4.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp4.setAppend(true);

      tmp3[0] = tmp4;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      adams.flow.source.ForLoop tmp6 = new adams.flow.source.ForLoop();
      tmp1[1] = tmp6;
      adams.flow.transformer.SetVariable tmp7 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("variableName");
      tmp7.setVariableName((adams.core.VariableName) argOption.valueOf("seed"));

      tmp1[2] = tmp7;
      adams.flow.control.Trigger tmp9 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp10 = new adams.flow.core.Actor[4];
      adams.flow.source.FileSupplier tmp11 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("files");
      tmp11.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/simple.report")});

      tmp10[0] = tmp11;
      tmp10[1] = new BaseName();
      adams.flow.control.Tee tmp13 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp14 = new adams.flow.core.Actor[1];
      adams.flow.sink.CallableSink tmp15 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("callableName");
      tmp15.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));

      tmp14[0] = tmp15;
      tmp13.setActors(tmp14);

      tmp10[2] = tmp13;
      adams.flow.control.FreeMemory tmp17 = new adams.flow.control.FreeMemory();
      tmp10[3] = tmp17;
      tmp9.setActors(tmp10);

      tmp1[3] = tmp9;
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

