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
 * WhileLoopTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.VariableName;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.condition.bool.Expression;
import adams.flow.control.WhileLoop;
import adams.flow.core.AbstractActor;
import adams.flow.sink.DumpFile;
import adams.flow.source.Start;
import adams.flow.source.StringConstants;
import adams.flow.transformer.MathExpression;
import adams.flow.transformer.Convert;
import adams.data.conversion.StringToDouble;
import adams.parser.BooleanExpressionText;
import adams.parser.MathematicalExpressionText;
import adams.test.TmpFile;

/**
 * Tests the WhileLoop actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WhileLoopTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WhileLoopTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
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
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    adams.flow.standalone.SetVariable sv1 = new adams.flow.standalone.SetVariable();
    sv1.setVariableName(new VariableName("i"));
    sv1.setVariableValue("0");

    Start st = new Start();
    
    Expression expr = new Expression();
    expr.setExpression(new BooleanExpressionText("@{i}<10"));
    WhileLoop wl = new WhileLoop();
    wl.setCondition(expr);

    StringConstants sc = new StringConstants();
    sc.setStrings(new BaseString[]{new BaseString("1")});

    StringToDouble s2d = new StringToDouble();
    Convert con = new Convert();
    con.setConversion(s2d);

    MathExpression me = new MathExpression();
    me.setExpression(new MathematicalExpressionText("@{i}+1"));

    adams.flow.transformer.SetVariable sv2 = new adams.flow.transformer.SetVariable();
    sv2.setVariableName(new VariableName("i"));

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    wl.setActors(new AbstractActor[]{
	sc,
	con,
	me,
	sv2,
	df
    });

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{sv1, st, wl});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new File[]{
	    new TmpFile("dumpfile.txt")});
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WhileLoopTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
