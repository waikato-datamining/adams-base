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
 * SwitchedSourceTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.VariableName;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.Expression;
import adams.flow.control.Flow;
import adams.flow.control.Trigger;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.transformer.SetVariable;
import adams.parser.BooleanExpressionText;
import adams.parser.MathematicalExpressionText;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for SwitchedSource actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SwitchedSourceTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SwitchedSourceTest(String name) {
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
    return new TestSuite(SwitchedSourceTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  public Actor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      List<Actor> actors = new ArrayList<Actor>();

      // Flow.ForLoop
      ForLoop forloop = new ForLoop();
      actors.add(forloop);

      // Flow.SetVariable
      SetVariable setvariable = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableName");
      setvariable.setVariableName((VariableName) argOption.valueOf("var"));
      actors.add(setvariable);

      // Flow.Trigger
      Trigger trigger = new Trigger();
      List<Actor> actors2 = new ArrayList<Actor>();

      // Flow.Trigger.SwitchedSource
      SwitchedSource switchedsource = new SwitchedSource();
      List<BooleanCondition> conditions = new ArrayList<BooleanCondition>();
      Expression expression = new Expression();
      argOption = (AbstractArgumentOption) expression.getOptionManager().findByProperty("expression");
      expression.setExpression((BooleanExpressionText) argOption.valueOf("(@{var} % 3) = 1"));
      conditions.add(expression);
      switchedsource.setConditions(conditions.toArray(new BooleanCondition[0]));

      List<Actor> cases = new ArrayList<Actor>();

      // .MathExpression
      MathExpression mathexpression = new MathExpression();
      argOption = (AbstractArgumentOption) mathexpression.getOptionManager().findByProperty("expression");
      mathexpression.setExpression((MathematicalExpressionText) argOption.valueOf("@{var} * 2"));
      cases.add(mathexpression);

      // .MathExpression-1
      MathExpression mathexpression2 = new MathExpression();
      argOption = (AbstractArgumentOption) mathexpression2.getOptionManager().findByProperty("name");
      mathexpression2.setName((String) argOption.valueOf("MathExpression-1"));
      argOption = (AbstractArgumentOption) mathexpression2.getOptionManager().findByProperty("expression");
      mathexpression2.setExpression((MathematicalExpressionText) argOption.valueOf("@{var} * 100"));
      cases.add(mathexpression2);
      switchedsource.setCases(cases.toArray(new Actor[0]));

      actors2.add(switchedsource);

      // Flow.Trigger.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile.setAppend(true);
      actors2.add(dumpfile);
      trigger.setActors(actors2.toArray(new Actor[0]));

      actors.add(trigger);
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

