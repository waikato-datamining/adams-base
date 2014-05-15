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
 * CombineVariablesTest.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseText;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for CombineVariables actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class CombineVariablesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CombineVariablesTest(String name) {
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
    return new TestSuite(CombineVariablesTest.class);
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
      setvariable3.setVariableName((adams.core.VariableName) argOption.valueOf("x"));

      abstractactor1[1] = setvariable3;

      // Flow.Trigger
      adams.flow.control.Trigger trigger5 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger5.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor6 = new adams.flow.core.AbstractActor[3];

      // Flow.Trigger.ForLoop
      adams.flow.source.ForLoop forloop7 = new adams.flow.source.ForLoop();
      abstractactor6[0] = forloop7;

      // Flow.Trigger.SetVariable
      adams.flow.transformer.SetVariable setvariable8 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable8.getOptionManager().findByProperty("variableName");
      setvariable8.setVariableName((adams.core.VariableName) argOption.valueOf("y"));

      abstractactor6[1] = setvariable8;

      // Flow.Trigger.Trigger
      adams.flow.control.Trigger trigger10 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger10.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor11 = new adams.flow.core.AbstractActor[2];

      // Flow.Trigger.Trigger.CombineVariables
      adams.flow.source.CombineVariables combinevariables12 = new adams.flow.source.CombineVariables();
      argOption = (AbstractArgumentOption) combinevariables12.getOptionManager().findByProperty("expression");
      combinevariables12.setExpression(new BaseText("@{x}/@{y}"));

      abstractactor11[0] = combinevariables12;

      // Flow.Trigger.Trigger.DumpFile
      adams.flow.sink.DumpFile dumpfile14 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile14.getOptionManager().findByProperty("outputFile");
      dumpfile14.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      dumpfile14.setAppend(true);

      abstractactor11[1] = dumpfile14;
      trigger10.setActors(abstractactor11);

      abstractactor6[2] = trigger10;
      trigger5.setActors(abstractactor6);

      abstractactor1[2] = trigger5;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener17 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener17);

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

