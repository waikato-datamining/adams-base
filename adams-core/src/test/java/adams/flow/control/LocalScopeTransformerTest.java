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
 * LocalScopeTransformerTest.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.base.BaseText;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.Actor;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for LocalScopeTransformer actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class LocalScopeTransformerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public LocalScopeTransformerTest(String name) {
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
    return new TestSuite(LocalScopeTransformerTest.class);
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
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[5];

      // Flow.Start
      adams.flow.source.Start start2 = new adams.flow.source.Start();
      actors1[0] = start2;

      // Flow.SetVariable
      adams.flow.transformer.SetVariable setvariable3 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableName");
      setvariable3.setVariableName((adams.core.VariableName) argOption.valueOf("blah"));
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableValue");
      setvariable3.setVariableValue((BaseText) argOption.valueOf("BLAH"));
      actors1[1] = setvariable3;

      // Flow.SetVariable-1
      adams.flow.transformer.SetVariable setvariable6 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable6.getOptionManager().findByProperty("name");
      setvariable6.setName((java.lang.String) argOption.valueOf("SetVariable-1"));
      argOption = (AbstractArgumentOption) setvariable6.getOptionManager().findByProperty("variableName");
      setvariable6.setVariableName((adams.core.VariableName) argOption.valueOf("hello"));
      argOption = (AbstractArgumentOption) setvariable6.getOptionManager().findByProperty("variableValue");
      setvariable6.setVariableValue((BaseText) argOption.valueOf("WORLD"));
      actors1[2] = setvariable6;

      // Flow.LocalScopeTransformer
      adams.flow.control.LocalScopeTransformer localscopetransformer10 = new adams.flow.control.LocalScopeTransformer();
      argOption = (AbstractArgumentOption) localscopetransformer10.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors11 = new adams.flow.core.Actor[2];

      // Flow.LocalScopeTransformer.LocalScopeTransformer.SetVariable
      adams.flow.transformer.SetVariable setvariable12 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable12.getOptionManager().findByProperty("variableName");
      setvariable12.setVariableName((adams.core.VariableName) argOption.valueOf("blah"));
      argOption = (AbstractArgumentOption) setvariable12.getOptionManager().findByProperty("variableValue");
      setvariable12.setVariableValue((BaseText) argOption.valueOf("blah"));
      actors11[0] = setvariable12;

      // Flow.LocalScopeTransformer.LocalScopeTransformer.SetVariable-1
      adams.flow.transformer.SetVariable setvariable15 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable15.getOptionManager().findByProperty("name");
      setvariable15.setName((java.lang.String) argOption.valueOf("SetVariable-1"));
      argOption = (AbstractArgumentOption) setvariable15.getOptionManager().findByProperty("variableName");
      setvariable15.setVariableName((adams.core.VariableName) argOption.valueOf("hello"));
      argOption = (AbstractArgumentOption) setvariable15.getOptionManager().findByProperty("variableValue");
      setvariable15.setVariableValue((BaseText) argOption.valueOf("world"));
      actors11[1] = setvariable15;
      localscopetransformer10.setActors(actors11);

      localscopetransformer10.setPropagateVariables(true);

      argOption = (AbstractArgumentOption) localscopetransformer10.getOptionManager().findByProperty("variablesRegExp");
      localscopetransformer10.setVariablesRegExp((adams.core.base.BaseRegExp) argOption.valueOf("h.*"));
      actors1[3] = localscopetransformer10;

      // Flow.Trigger
      adams.flow.control.Trigger trigger20 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger20.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors21 = new adams.flow.core.Actor[3];

      // Flow.Trigger.ListVariables
      adams.flow.source.ListVariables listvariables22 = new adams.flow.source.ListVariables();
      actors21[0] = listvariables22;

      // Flow.Trigger.SetVariable
      adams.flow.transformer.SetVariable setvariable23 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable23.getOptionManager().findByProperty("variableName");
      setvariable23.setVariableName((adams.core.VariableName) argOption.valueOf("v"));
      actors21[1] = setvariable23;

      // Flow.Trigger.Trigger
      adams.flow.control.Trigger trigger25 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger25.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors26 = new adams.flow.core.Actor[2];

      // Flow.Trigger.Trigger.CombineVariables
      adams.flow.source.CombineVariables combinevariables27 = new adams.flow.source.CombineVariables();
      argOption = (AbstractArgumentOption) combinevariables27.getOptionManager().findByProperty("expression");
      combinevariables27.setExpression((adams.core.base.BaseText) argOption.valueOf("@{v}: @{@{v}}"));
      actors26[0] = combinevariables27;

      // Flow.Trigger.Trigger.DumpFile
      adams.flow.sink.DumpFile dumpfile29 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile29.getOptionManager().findByProperty("outputFile");
      dumpfile29.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile29.setAppend(true);

      actors26[1] = dumpfile29;
      trigger25.setActors(actors26);

      actors21[2] = trigger25;
      trigger20.setActors(actors21);

      actors1[4] = trigger20;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener32 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener32);

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

