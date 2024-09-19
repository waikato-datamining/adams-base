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
 * LocalScopeTeeTest.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.Index;
import adams.core.VariableName;
import adams.core.base.BaseString;
import adams.core.base.BaseText;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.DoubleToString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.execution.NullListener;
import adams.flow.sink.CallableSink;
import adams.flow.sink.DumpFile;
import adams.flow.source.ForLoop;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.Convert;
import adams.flow.transformer.MathExpression;
import adams.flow.transformer.SetVariable;
import adams.flow.transformer.StringInsert;
import adams.parser.MathematicalExpressionText;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for LocalScopeTee actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class LocalScopeTeeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public LocalScopeTeeTest(String name) {
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
    return new TestSuite(LocalScopeTeeTest.class);
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
      List<Actor> actors = new ArrayList<>();

      // Flow.CallableActors
      CallableActors callableactors = new CallableActors();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.CallableActors.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile.setAppend(true);
      actors2.add(dumpfile);
      callableactors.setActors(actors2.toArray(new Actor[0]));

      actors.add(callableactors);

      // Flow.ForLoop
      ForLoop forloop = new ForLoop();
      argOption = (AbstractArgumentOption) forloop.getOptionManager().findByProperty("loopLower");
      forloop.setLoopLower((Integer) argOption.valueOf("-10"));
      actors.add(forloop);

      // Flow.LocalScopeTee
      LocalScopeTee localscopetee = new LocalScopeTee();
      List<Actor> actors3 = new ArrayList<>();

      // Flow.LocalScopeTee.SetVariable
      SetVariable setvariable = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableName");
      setvariable.setVariableName((VariableName) argOption.valueOf("mean"));
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableValue");
      setvariable.setVariableValue((BaseText) argOption.valueOf("-2.0"));
      actors3.add(setvariable);

      // Flow.LocalScopeTee.SetVariable-1
      SetVariable setvariable2 = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("name");
      setvariable2.setName((String) argOption.valueOf("SetVariable-1"));
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableName");
      setvariable2.setVariableName((VariableName) argOption.valueOf("stdev"));
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableValue");
      setvariable2.setVariableValue((BaseText) argOption.valueOf("1.0"));
      actors3.add(setvariable2);

      // Flow.LocalScopeTee.MathExpression
      MathExpression mathexpression = new MathExpression();
      argOption = (AbstractArgumentOption) mathexpression.getOptionManager().findByProperty("expression");
      mathexpression.setExpression((MathematicalExpressionText) argOption.valueOf("X/33"));
      actors3.add(mathexpression);

      // Flow.LocalScopeTee.MathExpression-1
      MathExpression mathexpression2 = new MathExpression();
      argOption = (AbstractArgumentOption) mathexpression2.getOptionManager().findByProperty("name");
      mathexpression2.setName((String) argOption.valueOf("MathExpression-1"));
      argOption = (AbstractArgumentOption) mathexpression2.getOptionManager().findByProperty("expression");
      mathexpression2.setExpression((MathematicalExpressionText) argOption.valueOf("1/sqrt(2*PI*pow(@{stdev},2))*exp(-1*pow(X-@{mean},2)/(2*@{stdev}))"));
      actors3.add(mathexpression2);

      // Flow.LocalScopeTee.Convert
      Convert convert = new Convert();
      DoubleToString doubletostring = new DoubleToString();
      argOption = (AbstractArgumentOption) doubletostring.getOptionManager().findByProperty("numDecimals");
      doubletostring.setNumDecimals((Integer) argOption.valueOf("3"));
      convert.setConversion(doubletostring);

      actors3.add(convert);

      // Flow.LocalScopeTee.StringInsert
      StringInsert stringinsert = new StringInsert();
      argOption = (AbstractArgumentOption) stringinsert.getOptionManager().findByProperty("position");
      stringinsert.setPosition((Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert.getOptionManager().findByProperty("value");
      stringinsert.setValue((BaseString) argOption.valueOf("mean=@{mean}/stdev=@{stdev}: "));
      stringinsert.setValueContainsVariable(true);

      actors3.add(stringinsert);

      // Flow.LocalScopeTee.CallableSink
      CallableSink callablesink = new CallableSink();
      argOption = (AbstractArgumentOption) callablesink.getOptionManager().findByProperty("callableName");
      callablesink.setCallableName((CallableActorReference) argOption.valueOf("DumpFile"));
      actors3.add(callablesink);
      localscopetee.setActors(actors3.toArray(new Actor[0]));

      actors.add(localscopetee);

      // Flow.LocalScopeTee (2)
      LocalScopeTee localscopetee2 = new LocalScopeTee();
      argOption = (AbstractArgumentOption) localscopetee2.getOptionManager().findByProperty("name");
      localscopetee2.setName((String) argOption.valueOf("LocalScopeTee (2)"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.LocalScopeTee (2).SetVariable-1
      SetVariable setvariable3 = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("name");
      setvariable3.setName((String) argOption.valueOf("SetVariable-1"));
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableName");
      setvariable3.setVariableName((VariableName) argOption.valueOf("mean"));
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableValue");
      setvariable3.setVariableValue((BaseText) argOption.valueOf("2.0"));
      actors4.add(setvariable3);

      // Flow.LocalScopeTee (2).SetVariable-1 (2)
      SetVariable setvariable4 = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("name");
      setvariable4.setName((String) argOption.valueOf("SetVariable-1 (2)"));
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("variableName");
      setvariable4.setVariableName((VariableName) argOption.valueOf("stdev"));
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("variableValue");
      setvariable4.setVariableValue((BaseText) argOption.valueOf("1.0"));
      actors4.add(setvariable4);

      // Flow.LocalScopeTee (2).MathExpression
      MathExpression mathexpression3 = new MathExpression();
      argOption = (AbstractArgumentOption) mathexpression3.getOptionManager().findByProperty("expression");
      mathexpression3.setExpression((MathematicalExpressionText) argOption.valueOf("X/33"));
      actors4.add(mathexpression3);

      // Flow.LocalScopeTee (2).MathExpression-1
      MathExpression mathexpression4 = new MathExpression();
      argOption = (AbstractArgumentOption) mathexpression4.getOptionManager().findByProperty("name");
      mathexpression4.setName((String) argOption.valueOf("MathExpression-1"));
      argOption = (AbstractArgumentOption) mathexpression4.getOptionManager().findByProperty("expression");
      mathexpression4.setExpression((MathematicalExpressionText) argOption.valueOf("1/sqrt(2*PI*pow(@{stdev},2))*exp(-1*pow(X-@{mean},2)/(2*@{stdev}))"));
      actors4.add(mathexpression4);

      // Flow.LocalScopeTee (2).Convert
      Convert convert2 = new Convert();
      DoubleToString doubletostring2 = new DoubleToString();
      argOption = (AbstractArgumentOption) doubletostring2.getOptionManager().findByProperty("numDecimals");
      doubletostring2.setNumDecimals((Integer) argOption.valueOf("3"));
      convert2.setConversion(doubletostring2);

      actors4.add(convert2);

      // Flow.LocalScopeTee (2).StringInsert
      StringInsert stringinsert2 = new StringInsert();
      argOption = (AbstractArgumentOption) stringinsert2.getOptionManager().findByProperty("position");
      stringinsert2.setPosition((Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert2.getOptionManager().findByProperty("value");
      stringinsert2.setValue((BaseString) argOption.valueOf("mean=@{mean}/stdev=@{stdev}: "));
      stringinsert2.setValueContainsVariable(true);

      actors4.add(stringinsert2);

      // Flow.LocalScopeTee (2).CallableSink
      CallableSink callablesink2 = new CallableSink();
      argOption = (AbstractArgumentOption) callablesink2.getOptionManager().findByProperty("callableName");
      callablesink2.setCallableName((CallableActorReference) argOption.valueOf("DumpFile"));
      actors4.add(callablesink2);
      localscopetee2.setActors(actors4.toArray(new Actor[0]));

      actors.add(localscopetee2);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

      NullManager nullmanager = new NullManager();
      flow.setFlowRestartManager(nullmanager);

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

