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
 * ArrayProcessTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseRegExp;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.AbstractActor;
import adams.flow.sink.DumpFile;
import adams.flow.transformer.ArrayToSequence;
import adams.flow.transformer.Convert;
import adams.data.conversion.DoubleToString;
import adams.data.conversion.IntToDouble;
import adams.flow.transformer.MathExpression;
import adams.flow.transformer.SequenceToArray;
import adams.flow.transformer.StringReplace;
import adams.parser.MathematicalExpressionText;
import adams.test.TmpFile;

/**
 * Tests the ArrayProcess actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayProcessTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayProcessTest(String name) {
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
    adams.flow.source.ForLoop fl = new adams.flow.source.ForLoop();
    fl.setLoopLower(1);
    fl.setLoopUpper(30);
    fl.setLoopStep(1);

    IntToDouble i2d = new IntToDouble();
    Convert con1 = new Convert();
    con1.setConversion(i2d);

    SequenceToArray s2a = new SequenceToArray();
    s2a.setArrayLength(30);

    ArrayProcess sub = new ArrayProcess();
    MathExpression me = new MathExpression();
    me.setExpression(new MathematicalExpressionText("X^2"));
    DoubleToString d2s = new DoubleToString();
    Convert con2 = new Convert();
    con2.setConversion(d2s);
    StringReplace sr = new StringReplace();
    sr.setFind(new BaseRegExp("$"));
    sr.setReplace("-blah");
    sub.setActors(new AbstractActor[]{me, con2, sr});

    ArrayToSequence a2s = new ArrayToSequence();

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{fl, con1, s2a, sub, a2s, df});

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
    return new TestSuite(ArrayProcessTest.class);
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
