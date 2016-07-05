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
 * StringIndentTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.VariableName;
import adams.core.base.BaseString;
import adams.core.base.BaseText;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.condition.bool.Expression;
import adams.flow.control.ConditionalSubProcess;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.StringConstants;
import adams.flow.standalone.SetVariable;
import adams.parser.BooleanExpressionText;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for StringIndent actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class StringIndentTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StringIndentTest(String name) {
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
    return new TestSuite(StringIndentTest.class);
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

      // Flow.SetVariable
      SetVariable setvariable = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableName");
      setvariable.setVariableName((VariableName) argOption.valueOf("count"));
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableValue");
      setvariable.setVariableValue((BaseText) argOption.valueOf("0"));
      actors.add(setvariable);

      // Flow.StringConstants
      StringConstants stringconstants = new StringConstants();
      argOption = (AbstractArgumentOption) stringconstants.getOptionManager().findByProperty("strings");
      List<BaseString> strings = new ArrayList<>();
      strings.add((BaseString) argOption.valueOf("The quick brown fox "));
      strings.add((BaseString) argOption.valueOf("jumps over the lazy "));
      strings.add((BaseString) argOption.valueOf("dog."));
      stringconstants.setStrings(strings.toArray(new BaseString[0]));
      actors.add(stringconstants);

      // Flow.IncVariable
      IncVariable incvariable = new IncVariable();
      argOption = (AbstractArgumentOption) incvariable.getOptionManager().findByProperty("variableName");
      incvariable.setVariableName((VariableName) argOption.valueOf("count"));
      actors.add(incvariable);

      // Flow.StringIndent
      StringIndent stringindent = new StringIndent();
      argOption = (AbstractArgumentOption) stringindent.getOptionManager().findByProperty("indentation");
      stringindent.setIndentation((String) argOption.valueOf("  "));
      actors.add(stringindent);

      // Flow.ConditionalSubProcess
      ConditionalSubProcess conditionalsubprocess = new ConditionalSubProcess();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.ConditionalSubProcess.StringIndent
      StringIndent stringindent2 = new StringIndent();
      argOption = (AbstractArgumentOption) stringindent2.getOptionManager().findByProperty("indentation");
      stringindent2.setIndentation((String) argOption.valueOf("  "));
      actors2.add(stringindent2);
      conditionalsubprocess.setActors(actors2.toArray(new Actor[0]));

      Expression expression = new Expression();
      argOption = (AbstractArgumentOption) expression.getOptionManager().findByProperty("expression");
      expression.setExpression((BooleanExpressionText) argOption.valueOf("@{count} % 2 = 1"));
      conditionalsubprocess.setCondition(expression);

      actors.add(conditionalsubprocess);

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile.setAppend(true);

      actors.add(dumpfile);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

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

