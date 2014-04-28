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
 * IfThenElseTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.condition.bool.Expression;
import adams.flow.control.Sleep;
import adams.flow.core.AbstractActor;
import adams.flow.sink.DumpFile;
import adams.flow.source.ForLoop;
import adams.flow.transformer.PassThrough;
import adams.parser.BooleanExpressionText;
import adams.test.TmpFile;

/**
 * Tests the IfThenElse actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class IfThenElseTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public IfThenElseTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.deleteFileFromTmp("dumpfile_default_then.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_default_else.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_custom_then.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_custom_else.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile_default_then.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_default_else.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_custom_then.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_custom_else.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    ForLoop fl = new ForLoop();

    DumpFile df_default_then = new DumpFile();
    df_default_then.setAppend(true);
    df_default_then.setOutputFile(new TmpFile("dumpfile_default_then.txt"));

    DumpFile df_default_else = new DumpFile();
    df_default_else.setAppend(true);
    df_default_else.setOutputFile(new TmpFile("dumpfile_default_else.txt"));

    IfThenElse ite_default = new IfThenElse();
    ite_default.setThenActor(df_default_then);
    ite_default.setElseActor(df_default_else);

    Tee tee_default = new Tee();
    tee_default.add(0, ite_default);

    DumpFile df_custom_then = new DumpFile();
    df_custom_then.setAppend(true);
    df_custom_then.setOutputFile(new TmpFile("dumpfile_custom_then.txt"));

    DumpFile df_custom_else = new DumpFile();
    df_custom_else.setAppend(true);
    df_custom_else.setOutputFile(new TmpFile("dumpfile_custom_else.txt"));

    IfThenElse ite_custom = new IfThenElse();
    Expression cond = new Expression();
    cond.setExpression(new BooleanExpressionText("(X > 5)"));
    ite_custom.setCondition(cond);
    ite_custom.setThenActor(df_custom_then);
    ite_custom.setElseActor(df_custom_else);

    Tee tee_custom = new Tee();
    tee_custom.add(0, ite_custom);

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{fl, tee_default, tee_custom});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new File[]{
	    new TmpFile("dumpfile_default_then.txt"),
	    new TmpFile("dumpfile_default_else.txt"),
	    new TmpFile("dumpfile_custom_then.txt"),
	    new TmpFile("dumpfile_custom_else.txt")});
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(IfThenElseTest.class);
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
