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
 * CountTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.condition.bool.Counting;
import adams.flow.condition.bool.Expression;
import adams.flow.control.Count;
import adams.flow.control.Sleep;
import adams.flow.core.AbstractActor;
import adams.flow.sink.DumpFile;
import adams.flow.source.ForLoop;
import adams.parser.BooleanExpressionText;
import adams.test.TmpFile;

/**
 * Tests the Count actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CountTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CountTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.deleteFileFromTmp("dumpfile_all.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_min.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_max.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_interval.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_min_max.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_min_max_interval.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_expr.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_expr_inc_only.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile_all.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_min.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_max.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_interval.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_min_max.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_min_max_interval.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_expr.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_expr_inc_only.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    Counting 	cnt;
    Expression	expr;
    
    ForLoop fl = new ForLoop();
    fl.setLoopLower(1);
    fl.setLoopUpper(30);
    fl.setLoopStep(1);

    DumpFile df_all = new DumpFile();
    df_all.setAppend(true);
    df_all.setOutputFile(new TmpFile("dumpfile_all.txt"));
    Count cvp_all = new Count();
    cnt = new Counting();
    cvp_all.setCondition(cnt);
    cvp_all.add(0, df_all);

    DumpFile df_min = new DumpFile();
    df_min.setAppend(true);
    df_min.setOutputFile(new TmpFile("dumpfile_min.txt"));
    Count cvp_min = new Count();
    cnt = new Counting();
    cnt.setMinimum(10);
    cvp_min.setCondition(cnt);
    cvp_min.add(0, df_min);

    DumpFile df_max = new DumpFile();
    df_max.setAppend(true);
    df_max.setOutputFile(new TmpFile("dumpfile_max.txt"));
    Count cvp_max = new Count();
    cnt = new Counting();
    cnt.setMaximum(15);
    cvp_max.setCondition(cnt);
    cvp_max.add(0, df_max);

    DumpFile df_interval = new DumpFile();
    df_interval.setAppend(true);
    df_interval.setOutputFile(new TmpFile("dumpfile_interval.txt"));
    Count cvp_interval = new Count();
    cnt = new Counting();
    cnt.setInterval(3);
    cvp_interval.setCondition(cnt);
    cvp_interval.add(0, df_interval);

    DumpFile df_min_max = new DumpFile();
    df_min_max.setAppend(true);
    df_min_max.setOutputFile(new TmpFile("dumpfile_min_max.txt"));
    Count cvp_min_max = new Count();
    cnt = new Counting();
    cnt.setMinimum(5);
    cnt.setMaximum(23);
    cvp_min_max.setCondition(cnt);
    cvp_min_max.add(0, df_min_max);

    DumpFile df_min_max_interval = new DumpFile();
    df_min_max_interval.setAppend(true);
    df_min_max_interval.setOutputFile(new TmpFile("dumpfile_min_max_interval.txt"));
    Count cvp_min_max_interval = new Count();
    cnt = new Counting();
    cnt.setMinimum(5);
    cnt.setMaximum(23);
    cnt.setInterval(3);
    cvp_min_max_interval.setCondition(cnt);
    cvp_min_max_interval.add(0, df_min_max_interval);

    DumpFile df_expr = new DumpFile();
    df_expr.setAppend(true);
    df_expr.setOutputFile(new TmpFile("dumpfile_expr.txt"));
    Count cvp_expr = new Count();
    expr = new Expression();
    expr.setExpression(new BooleanExpressionText("X % 3 = 0"));
    cvp_expr.setCondition(expr);
    cvp_expr.add(0, df_expr);

    DumpFile df_expr_inc_only = new DumpFile();
    df_expr_inc_only.setAppend(true);
    df_expr_inc_only.setOutputFile(new TmpFile("dumpfile_expr_inc_only.txt"));
    Count cvp_expr_inc_only = new Count();
    expr = new Expression();
    expr.setExpression(new BooleanExpressionText("X % 3 = 0"));
    cvp_expr_inc_only.setCondition(expr);
    cvp_expr_inc_only.setIncrementOnlyIfConditionMet(true);
    cvp_expr_inc_only.add(0, df_expr_inc_only);

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{
	fl, 
	cvp_all, 
	cvp_min, 
	cvp_max, 
	cvp_interval, 
	cvp_min_max, 
	cvp_min_max_interval,
	cvp_expr,
	cvp_expr_inc_only
    });

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new File[]{
	    new TmpFile("dumpfile_all.txt"),
	    new TmpFile("dumpfile_min.txt"),
	    new TmpFile("dumpfile_max.txt"),
	    new TmpFile("dumpfile_interval.txt"),
	    new TmpFile("dumpfile_min_max.txt"),
	    new TmpFile("dumpfile_min_max_interval.txt"),
	    new TmpFile("dumpfile_expr.txt"),
	    new TmpFile("dumpfile_expr_inc_only.txt")
	});
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(CountTest.class);
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
