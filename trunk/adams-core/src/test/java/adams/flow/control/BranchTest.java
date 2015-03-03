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
 * BranchTest.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.AbstractActor;
import adams.flow.sink.DumpFile;
import adams.flow.source.StringConstants;
import adams.test.TmpFile;

/**
 * Tests the branching.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BranchTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BranchTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.deleteFileFromTmp("dumpfile1.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile2.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile1.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile2.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    StringConstants ids = new StringConstants();
    ids.setStrings(new BaseString[]{
	new BaseString("1"),
	new BaseString("2"),
	new BaseString("3"),
	new BaseString("4"),
	new BaseString("5"),
	new BaseString("6"),
	new BaseString("7"),
	new BaseString("8"),
	new BaseString("9"),
	new BaseString("10"),
	new BaseString("11"),
	new BaseString("12"),
	new BaseString("13"),
	new BaseString("14"),
	new BaseString("15"),
	new BaseString("16"),
	new BaseString("17"),
	new BaseString("18"),
	new BaseString("19"),
	new BaseString("20"),
	new BaseString("21"),
	new BaseString("22"),
	new BaseString("23"),
	new BaseString("24"),
	new BaseString("25"),
	new BaseString("26"),
	new BaseString("27"),
	new BaseString("28"),
	new BaseString("29"),
	new BaseString("30")
    });

    DumpFile df1 = new DumpFile();
    df1.setAppend(true);
    df1.setOutputFile(new TmpFile("dumpfile1.txt"));

    DumpFile df2 = new DumpFile();
    df2.setAppend(true);
    df2.setOutputFile(new TmpFile("dumpfile2.txt"));

    Sequence seq1 = new Sequence();
    seq1.setActors(new AbstractActor[]{df1});

    Sequence seq2 = new Sequence();
    seq2.setActors(new AbstractActor[]{df2});

    Branch br = new Branch();
    br.setBranches(new AbstractControlActor[]{seq1, seq2});

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{ids, br});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new File[]{
	    new TmpFile("dumpfile1.txt"),
	    new TmpFile("dumpfile2.txt")});
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BranchTest.class);
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
