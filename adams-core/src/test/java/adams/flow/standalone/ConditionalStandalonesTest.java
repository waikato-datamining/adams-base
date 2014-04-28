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
 * ConditionalStandalonesTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseString;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for ConditionalStandalones actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class ConditionalStandalonesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ConditionalStandalonesTest(String name) {
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
    return new TestSuite(ConditionalStandalonesTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[4];

      // Flow.ConditionalStandalones
      adams.flow.standalone.ConditionalStandalones conditionalstandalones2 = new adams.flow.standalone.ConditionalStandalones();
      argOption = (AbstractArgumentOption) conditionalstandalones2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors3 = new adams.flow.core.AbstractActor[2];

      // Flow.ConditionalStandalones.SetVariable
      adams.flow.standalone.SetVariable setvariable4 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("variableName");
      setvariable4.setVariableName((adams.core.VariableName) argOption.valueOf("a"));
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("variableValue");
      setvariable4.setVariableValue((java.lang.String) argOption.valueOf("yes"));
      actors3[0] = setvariable4;

      // Flow.ConditionalStandalones.SetVariable-1
      adams.flow.standalone.SetVariable setvariable7 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable7.getOptionManager().findByProperty("name");
      setvariable7.setName((java.lang.String) argOption.valueOf("SetVariable-1"));
      argOption = (AbstractArgumentOption) setvariable7.getOptionManager().findByProperty("variableName");
      setvariable7.setVariableName((adams.core.VariableName) argOption.valueOf("b"));
      argOption = (AbstractArgumentOption) setvariable7.getOptionManager().findByProperty("variableValue");
      setvariable7.setVariableValue((java.lang.String) argOption.valueOf("yes"));
      actors3[1] = setvariable7;
      conditionalstandalones2.setActors(actors3);

      argOption = (AbstractArgumentOption) conditionalstandalones2.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.Expression expression12 = new adams.flow.condition.bool.Expression();
      conditionalstandalones2.setCondition(expression12);

      actors1[0] = conditionalstandalones2;

      // Flow.ConditionalStandalones-1
      adams.flow.standalone.ConditionalStandalones conditionalstandalones13 = new adams.flow.standalone.ConditionalStandalones();
      argOption = (AbstractArgumentOption) conditionalstandalones13.getOptionManager().findByProperty("name");
      conditionalstandalones13.setName((java.lang.String) argOption.valueOf("ConditionalStandalones-1"));
      argOption = (AbstractArgumentOption) conditionalstandalones13.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors15 = new adams.flow.core.AbstractActor[2];

      // Flow.ConditionalStandalones-1.SetVariable
      adams.flow.standalone.SetVariable setvariable16 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable16.getOptionManager().findByProperty("variableName");
      setvariable16.setVariableName((adams.core.VariableName) argOption.valueOf("a"));
      argOption = (AbstractArgumentOption) setvariable16.getOptionManager().findByProperty("variableValue");
      setvariable16.setVariableValue((java.lang.String) argOption.valueOf("yes"));
      actors15[0] = setvariable16;

      // Flow.ConditionalStandalones-1.SetVariable-1
      adams.flow.standalone.SetVariable setvariable19 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable19.getOptionManager().findByProperty("name");
      setvariable19.setName((java.lang.String) argOption.valueOf("SetVariable-1"));
      argOption = (AbstractArgumentOption) setvariable19.getOptionManager().findByProperty("variableName");
      setvariable19.setVariableName((adams.core.VariableName) argOption.valueOf("b"));
      argOption = (AbstractArgumentOption) setvariable19.getOptionManager().findByProperty("variableValue");
      setvariable19.setVariableValue((java.lang.String) argOption.valueOf("yes"));
      actors15[1] = setvariable19;
      conditionalstandalones13.setActors(actors15);

      argOption = (AbstractArgumentOption) conditionalstandalones13.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.Expression expression24 = new adams.flow.condition.bool.Expression();
      argOption = (AbstractArgumentOption) expression24.getOptionManager().findByProperty("expression");
      expression24.setExpression((adams.parser.BooleanExpressionText) argOption.valueOf("false"));
      conditionalstandalones13.setCondition(expression24);

      actors1[1] = conditionalstandalones13;

      // Flow.CombineVariables
      adams.flow.source.CombineVariables combinevariables26 = new adams.flow.source.CombineVariables();
      argOption = (AbstractArgumentOption) combinevariables26.getOptionManager().findByProperty("expression");
      combinevariables26.setExpression(new BaseString("@{a}-@{b}"));
      actors1[2] = combinevariables26;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile28 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile28.getOptionManager().findByProperty("outputFile");
      dumpfile28.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors1[3] = dumpfile28;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener31 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener31);

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

