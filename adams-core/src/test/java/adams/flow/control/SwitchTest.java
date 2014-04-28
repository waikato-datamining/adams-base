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
 * SwitchTest.java
 * Copyright (C) 2010-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.random.JavaRandomInt;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.Expression;
import adams.flow.core.AbstractActor;
import adams.flow.sink.DumpFile;
import adams.flow.source.RandomNumberGenerator;
import adams.parser.BooleanExpressionText;
import adams.test.TmpFile;

/**
 * Tests the Switch actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SwitchTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SwitchTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.deleteFileFromTmp("dumpfile-300.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile-600.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile-default.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile-300.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile-600.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile-default.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    RandomNumberGenerator rng = new RandomNumberGenerator();
    rng.setGenerator(new JavaRandomInt());

    DumpFile df300 = new DumpFile();
    df300.setAppend(true);
    df300.setOutputFile(new TmpFile("dumpfile-300.txt"));

    DumpFile df600 = new DumpFile();
    df600.setAppend(true);
    df600.setOutputFile(new TmpFile("dumpfile-600.txt"));

    DumpFile dfDef = new DumpFile();
    dfDef.setAppend(true);
    dfDef.setOutputFile(new TmpFile("dumpfile-default.txt"));

    Expression exp300 = new Expression();
    exp300.setExpression(new BooleanExpressionText("X < 300"));

    Expression exp600 = new Expression();
    exp600.setExpression(new BooleanExpressionText("X < 600"));
    
    Switch sw = new Switch();
    sw.setConditions(
	new BooleanCondition[]{
	    exp300,
	    exp600
	});
    sw.setCases(
	new AbstractActor[]{
	    df300,
	    df600,
	    dfDef
	});

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{rng, sw});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new File[]{
	    new TmpFile("dumpfile-300.txt"),
	    new TmpFile("dumpfile-600.txt"),
	    new TmpFile("dumpfile-default.txt")});
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SwitchTest.class);
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
