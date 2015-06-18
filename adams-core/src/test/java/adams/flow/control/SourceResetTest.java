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
 * SourceResetTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for SourceReset actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SourceResetTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SourceResetTest(String name) {
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
    return new TestSuite(SourceResetTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[3];

      // Flow.ForLoop
      adams.flow.source.ForLoop forloop2 = new adams.flow.source.ForLoop();
      argOption = (AbstractArgumentOption) forloop2.getOptionManager().findByProperty("loopUpper");
      forloop2.setLoopUpper((Integer) argOption.valueOf("3"));
      actors1[0] = forloop2;

      // Flow.SetVariable
      adams.flow.transformer.SetVariable setvariable4 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("variableName");
      setvariable4.setVariableName((adams.core.VariableName) argOption.valueOf("monitor"));
      actors1[1] = setvariable4;

      // Flow.Trigger
      adams.flow.control.Trigger trigger6 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger6.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors7 = new adams.flow.core.AbstractActor[2];

      // Flow.Trigger.SourceReset
      adams.flow.control.SourceReset sourcereset8 = new adams.flow.control.SourceReset();
      argOption = (AbstractArgumentOption) sourcereset8.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors9 = new adams.flow.core.AbstractActor[1];

      // Flow.Trigger.SourceReset.RandomNumberGenerator
      adams.flow.source.RandomNumberGenerator randomnumbergenerator10 = new adams.flow.source.RandomNumberGenerator();
      argOption = (AbstractArgumentOption) randomnumbergenerator10.getOptionManager().findByProperty("generator");
      adams.data.random.JavaRandomInt javarandomint12 = new adams.data.random.JavaRandomInt();
      randomnumbergenerator10.setGenerator(javarandomint12);

      argOption = (AbstractArgumentOption) randomnumbergenerator10.getOptionManager().findByProperty("maxNum");
      randomnumbergenerator10.setMaxNum((Integer) argOption.valueOf("5"));
      actors9[0] = randomnumbergenerator10;
      sourcereset8.setActors(actors9);

      argOption = (AbstractArgumentOption) sourcereset8.getOptionManager().findByProperty("variableName");
      sourcereset8.setVariableName((adams.core.VariableName) argOption.valueOf("monitor"));
      actors7[0] = sourcereset8;

      // Flow.Trigger.DumpFile
      adams.flow.sink.DumpFile dumpfile15 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile15.getOptionManager().findByProperty("outputFile");
      dumpfile15.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile15.setAppend(true);

      actors7[1] = dumpfile15;
      trigger6.setActors(actors7);

      actors1[2] = trigger6;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener18 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener18);

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

