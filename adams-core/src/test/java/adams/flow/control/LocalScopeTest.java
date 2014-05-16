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
 * LocalScopeTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
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
 * Test for LocalScope actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class LocalScopeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public LocalScopeTest(String name) {
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
    return new TestSuite(LocalScopeTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[5];

      // Flow.Start
      adams.flow.source.Start start2 = new adams.flow.source.Start();
      actors1[0] = start2;

      // Flow.SetVariable
      adams.flow.transformer.SetVariable setvariable3 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableName");
      setvariable3.setVariableName((adams.core.VariableName) argOption.valueOf("blah"));
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableValue");
      setvariable3.setVariableValue((java.lang.String) argOption.valueOf("BLAH"));
      actors1[1] = setvariable3;

      // Flow.SetVariable-1
      adams.flow.transformer.SetVariable setvariable6 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable6.getOptionManager().findByProperty("name");
      setvariable6.setName((java.lang.String) argOption.valueOf("SetVariable-1"));
      argOption = (AbstractArgumentOption) setvariable6.getOptionManager().findByProperty("variableName");
      setvariable6.setVariableName((adams.core.VariableName) argOption.valueOf("hello"));
      argOption = (AbstractArgumentOption) setvariable6.getOptionManager().findByProperty("variableValue");
      setvariable6.setVariableValue((java.lang.String) argOption.valueOf("WORLD"));
      actors1[2] = setvariable6;

      // Flow.LocalScope
      adams.flow.control.LocalScope localscope10 = new adams.flow.control.LocalScope();
      argOption = (AbstractArgumentOption) localscope10.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors11 = new adams.flow.core.AbstractActor[3];

      // Flow.LocalScope.Start
      adams.flow.source.Start start12 = new adams.flow.source.Start();
      actors11[0] = start12;

      // Flow.LocalScope.SetVariable
      adams.flow.transformer.SetVariable setvariable13 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable13.getOptionManager().findByProperty("variableName");
      setvariable13.setVariableName((adams.core.VariableName) argOption.valueOf("blah"));
      argOption = (AbstractArgumentOption) setvariable13.getOptionManager().findByProperty("variableValue");
      setvariable13.setVariableValue((java.lang.String) argOption.valueOf("blah"));
      actors11[1] = setvariable13;

      // Flow.LocalScope.SetVariable-1
      adams.flow.transformer.SetVariable setvariable16 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable16.getOptionManager().findByProperty("name");
      setvariable16.setName((java.lang.String) argOption.valueOf("SetVariable-1"));
      argOption = (AbstractArgumentOption) setvariable16.getOptionManager().findByProperty("variableName");
      setvariable16.setVariableName((adams.core.VariableName) argOption.valueOf("hello"));
      argOption = (AbstractArgumentOption) setvariable16.getOptionManager().findByProperty("variableValue");
      setvariable16.setVariableValue((java.lang.String) argOption.valueOf("world"));
      actors11[2] = setvariable16;
      localscope10.setActors(actors11);

      localscope10.setPropagateVariables(true);

      argOption = (AbstractArgumentOption) localscope10.getOptionManager().findByProperty("variablesRegExp");
      localscope10.setVariablesRegExp((adams.core.base.BaseRegExp) argOption.valueOf("h.*"));
      actors1[3] = localscope10;

      // Flow.Trigger
      adams.flow.control.Trigger trigger21 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger21.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors22 = new adams.flow.core.AbstractActor[3];

      // Flow.Trigger.ListVariables
      adams.flow.source.ListVariables listvariables23 = new adams.flow.source.ListVariables();
      argOption = (AbstractArgumentOption) listvariables23.getOptionManager().findByProperty("regExp");
      listvariables23.setRegExp((adams.core.base.BaseRegExp) argOption.valueOf("[^f].*"));
      actors22[0] = listvariables23;

      // Flow.Trigger.SetVariable
      adams.flow.transformer.SetVariable setvariable25 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable25.getOptionManager().findByProperty("variableName");
      setvariable25.setVariableName((adams.core.VariableName) argOption.valueOf("v"));
      actors22[1] = setvariable25;

      // Flow.Trigger.Trigger
      adams.flow.control.Trigger trigger27 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger27.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors28 = new adams.flow.core.AbstractActor[2];

      // Flow.Trigger.Trigger.CombineVariables
      adams.flow.source.CombineVariables combinevariables29 = new adams.flow.source.CombineVariables();
      argOption = (AbstractArgumentOption) combinevariables29.getOptionManager().findByProperty("expression");
      combinevariables29.setExpression((adams.core.base.BaseText) argOption.valueOf("@{v}: @{@{v}}"));
      actors28[0] = combinevariables29;

      // Flow.Trigger.Trigger.DumpFile
      adams.flow.sink.DumpFile dumpfile31 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile31.getOptionManager().findByProperty("outputFile");
      dumpfile31.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile31.setAppend(true);
      actors28[1] = dumpfile31;
      trigger27.setActors(actors28);

      actors22[2] = trigger27;
      trigger21.setActors(actors22);

      actors1[4] = trigger21;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener34 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener34);

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

