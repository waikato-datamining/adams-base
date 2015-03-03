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
 * TriggerTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Sleep;
import adams.flow.core.AbstractActor;
import adams.flow.sink.DumpFile;
import adams.flow.source.StringConstants;
import adams.flow.transformer.Convert;
import adams.data.conversion.IntToDouble;
import adams.flow.transformer.MathExpression;
import adams.parser.MathematicalExpressionText;
import adams.test.TmpFile;

/**
 * Tests the Trigger actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TriggerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TriggerTest(String name) {
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
    StringConstants sc = new StringConstants();
    sc.setStrings(new BaseString[]{
	new BaseString("1")
    });

    // trigger 1
    adams.flow.source.ForLoop fl1 = new adams.flow.source.ForLoop();
    fl1.setLoopLower(1);
    fl1.setLoopUpper(10);
    fl1.setLoopStep(1);

    IntToDouble i2d1 = new IntToDouble();
    Convert con1 = new Convert();
    con1.setConversion(i2d1);

    MathExpression me1 = new MathExpression();
    me1.setExpression(new MathematicalExpressionText("X"));

    DumpFile df1 = new DumpFile();
    df1.setAppend(true);
    df1.setOutputFile(new TmpFile("dumpfile.txt"));

    Trigger trg1 = new Trigger();
    trg1.setActors(new AbstractActor[]{fl1, con1, me1, df1});

    // trigger 2
    adams.flow.source.ForLoop fl2 = new adams.flow.source.ForLoop();
    fl2.setLoopLower(1);
    fl2.setLoopUpper(10);
    fl2.setLoopStep(1);

    IntToDouble i2d2 = new IntToDouble();
    Convert con2 = new Convert();
    con2.setConversion(i2d2);

    MathExpression me2 = new MathExpression();
    me2.setExpression(new MathematicalExpressionText("X^2"));

    DumpFile df2 = new DumpFile();
    df2.setAppend(true);
    df2.setOutputFile(new TmpFile("dumpfile.txt"));

    Trigger trg2 = new Trigger();
    trg2.setActors(new AbstractActor[]{fl2, con2, me2, df2});

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{sc, trg1, trg2});

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
    return new TestSuite(TriggerTest.class);
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
