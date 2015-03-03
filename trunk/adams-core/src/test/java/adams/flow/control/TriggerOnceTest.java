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
 * TriggerOnceTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for TriggerOnce actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class TriggerOnceTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TriggerOnceTest(String name) {
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
    return new TestSuite(TriggerOnceTest.class);
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
      adams.flow.core.AbstractActor[] abstractactor1 = new adams.flow.core.AbstractActor[3];

      // Flow.ForLoop
      adams.flow.source.ForLoop forloop2 = new adams.flow.source.ForLoop();
      abstractactor1[0] = forloop2;

      // Flow.SetVariable
      adams.flow.transformer.SetVariable setvariable3 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableName");
      setvariable3.setVariableName((adams.core.VariableName) argOption.valueOf("i"));

      abstractactor1[1] = setvariable3;

      // Flow.TriggerOnce
      adams.flow.control.TriggerOnce triggeronce5 = new adams.flow.control.TriggerOnce();
      argOption = (AbstractArgumentOption) triggeronce5.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor6 = new adams.flow.core.AbstractActor[2];

      // Flow.TriggerOnce.Variable
      adams.flow.source.Variable variable7 = new adams.flow.source.Variable();
      argOption = (AbstractArgumentOption) variable7.getOptionManager().findByProperty("variableName");
      variable7.setVariableName((adams.core.VariableName) argOption.valueOf("i"));

      abstractactor6[0] = variable7;

      // Flow.TriggerOnce.DumpFile
      adams.flow.sink.DumpFile dumpfile9 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile9.getOptionManager().findByProperty("outputFile");
      dumpfile9.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      dumpfile9.setAppend(true);

      abstractactor6[1] = dumpfile9;
      triggeronce5.setActors(abstractactor6);

      abstractactor1[2] = triggeronce5;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener12 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener12);

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

