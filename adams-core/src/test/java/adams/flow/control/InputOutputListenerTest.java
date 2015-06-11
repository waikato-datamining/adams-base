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
 * InputOutputListenerTest.java
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
 * Test for InputOutputListener actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class InputOutputListenerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public InputOutputListenerTest(String name) {
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
    return new TestSuite(InputOutputListenerTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[6];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors callableactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) callableactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors3 = new adams.flow.core.AbstractActor[3];

      // Flow.CallableActors.Display
      adams.flow.sink.Display display4 = new adams.flow.sink.Display();
      argOption = (AbstractArgumentOption) display4.getOptionManager().findByProperty("x");
      display4.setX((Integer) argOption.valueOf("-3"));
      argOption = (AbstractArgumentOption) display4.getOptionManager().findByProperty("writer");
      adams.data.io.output.NullWriter nullwriter7 = new adams.data.io.output.NullWriter();
      display4.setWriter(nullwriter7);

      actors3[0] = display4;

      // Flow.CallableActors.input
      adams.flow.control.Sequence sequence8 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence8.getOptionManager().findByProperty("name");
      sequence8.setName((java.lang.String) argOption.valueOf("input"));
      argOption = (AbstractArgumentOption) sequence8.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors10 = new adams.flow.core.AbstractActor[3];

      // Flow.CallableActors.input.Convert
      adams.flow.transformer.Convert convert11 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert11.getOptionManager().findByProperty("conversion");
      adams.data.conversion.DoubleToString doubletostring13 = new adams.data.conversion.DoubleToString();
      argOption = (AbstractArgumentOption) doubletostring13.getOptionManager().findByProperty("numDecimals");
      doubletostring13.setNumDecimals((Integer) argOption.valueOf("3"));
      doubletostring13.setFixedDecimals(true);

      convert11.setConversion(doubletostring13);

      actors10[0] = convert11;

      // Flow.CallableActors.input.StringInsert
      adams.flow.transformer.StringInsert stringinsert15 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) stringinsert15.getOptionManager().findByProperty("position");
      stringinsert15.setPosition((adams.core.Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert15.getOptionManager().findByProperty("value");
      stringinsert15.setValue((adams.core.base.BaseString) argOption.valueOf(">>> "));
      actors10[1] = stringinsert15;

      // Flow.CallableActors.input.DumpFile
      adams.flow.sink.DumpFile dumpfile18 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile18.getOptionManager().findByProperty("outputFile");
      dumpfile18.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile18.setAppend(true);

      actors10[2] = dumpfile18;
      sequence8.setActors(actors10);

      actors3[1] = sequence8;

      // Flow.CallableActors.output
      adams.flow.control.Sequence sequence20 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence20.getOptionManager().findByProperty("name");
      sequence20.setName((java.lang.String) argOption.valueOf("output"));
      argOption = (AbstractArgumentOption) sequence20.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors22 = new adams.flow.core.AbstractActor[3];

      // Flow.CallableActors.output.Convert
      adams.flow.transformer.Convert convert23 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert23.getOptionManager().findByProperty("conversion");
      adams.data.conversion.DoubleToString doubletostring25 = new adams.data.conversion.DoubleToString();
      argOption = (AbstractArgumentOption) doubletostring25.getOptionManager().findByProperty("numDecimals");
      doubletostring25.setNumDecimals((Integer) argOption.valueOf("3"));
      doubletostring25.setFixedDecimals(true);

      convert23.setConversion(doubletostring25);

      actors22[0] = convert23;

      // Flow.CallableActors.output.StringInsert
      adams.flow.transformer.StringInsert stringinsert27 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) stringinsert27.getOptionManager().findByProperty("position");
      stringinsert27.setPosition((adams.core.Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert27.getOptionManager().findByProperty("value");
      stringinsert27.setValue((adams.core.base.BaseString) argOption.valueOf("<<< "));
      actors22[1] = stringinsert27;

      // Flow.CallableActors.output.DumpFile
      adams.flow.sink.DumpFile dumpfile30 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile30.getOptionManager().findByProperty("outputFile");
      dumpfile30.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile30.setAppend(true);

      actors22[2] = dumpfile30;
      sequence20.setActors(actors22);

      actors3[2] = sequence20;
      callableactors2.setActors(actors3);

      actors1[0] = callableactors2;

      // Flow.SetVariable
      adams.flow.standalone.SetVariable setvariable32 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable32.getOptionManager().findByProperty("variableName");
      setvariable32.setVariableName((adams.core.VariableName) argOption.valueOf("mean"));
      argOption = (AbstractArgumentOption) setvariable32.getOptionManager().findByProperty("variableValue");
      setvariable32.setVariableValue((adams.core.base.BaseText) argOption.valueOf("0"));
      actors1[1] = setvariable32;

      // Flow.SetVariable-1
      adams.flow.standalone.SetVariable setvariable35 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable35.getOptionManager().findByProperty("name");
      setvariable35.setName((java.lang.String) argOption.valueOf("SetVariable-1"));
      argOption = (AbstractArgumentOption) setvariable35.getOptionManager().findByProperty("variableName");
      setvariable35.setVariableName((adams.core.VariableName) argOption.valueOf("stdev"));
      argOption = (AbstractArgumentOption) setvariable35.getOptionManager().findByProperty("variableValue");
      setvariable35.setVariableValue((adams.core.base.BaseText) argOption.valueOf("1.0"));
      actors1[2] = setvariable35;

      // Flow.ForLoop
      adams.flow.source.ForLoop forloop39 = new adams.flow.source.ForLoop();
      argOption = (AbstractArgumentOption) forloop39.getOptionManager().findByProperty("loopLower");
      forloop39.setLoopLower((Integer) argOption.valueOf("-200"));
      argOption = (AbstractArgumentOption) forloop39.getOptionManager().findByProperty("loopUpper");
      forloop39.setLoopUpper((Integer) argOption.valueOf("200"));
      actors1[3] = forloop39;

      // Flow.MathExpression
      adams.flow.transformer.MathExpression mathexpression42 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) mathexpression42.getOptionManager().findByProperty("expression");
      mathexpression42.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X/33"));
      actors1[4] = mathexpression42;

      // Flow.InputOutputListener
      adams.flow.control.InputOutputListener inputoutputlistener44 = new adams.flow.control.InputOutputListener();
      argOption = (AbstractArgumentOption) inputoutputlistener44.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors45 = new adams.flow.core.AbstractActor[1];

      // Flow.InputOutputListener.MathExpression-1
      adams.flow.transformer.MathExpression mathexpression46 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) mathexpression46.getOptionManager().findByProperty("name");
      mathexpression46.setName((java.lang.String) argOption.valueOf("MathExpression-1"));
      argOption = (AbstractArgumentOption) mathexpression46.getOptionManager().findByProperty("expression");
      mathexpression46.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("1/sqrt(2*PI*pow(@{stdev},2))*exp(-1*pow(X-@{mean},2)/(2*@{stdev}))"));
      actors45[0] = mathexpression46;
      inputoutputlistener44.setActors(actors45);

      inputoutputlistener44.setOnInput(true);

      argOption = (AbstractArgumentOption) inputoutputlistener44.getOptionManager().findByProperty("inputDestination");
      inputoutputlistener44.setInputDestination((adams.flow.core.CallableActorReference) argOption.valueOf("input"));
      inputoutputlistener44.setOnOutput(true);

      argOption = (AbstractArgumentOption) inputoutputlistener44.getOptionManager().findByProperty("outputDestination");
      inputoutputlistener44.setOutputDestination((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors1[5] = inputoutputlistener44;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener52 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener52);

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

