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
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.condition.bool.Counting;
import adams.flow.control.ConditionalTee;
import adams.flow.core.AbstractActor;
import adams.flow.sink.DumpFile;
import adams.flow.source.StringConstants;
import adams.test.TmpFile;

/**
 * Tests "ConditionalTee".
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConditionalTeeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ConditionalTeeTest(String name) {
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
    m_TestHelper.deleteFileFromTmp("dumpfile-count1.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile-count2.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile-count3.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile-count1.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile-count2.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile-count3.txt");

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
    df1.setOutputFile(new TmpFile("dumpfile-count1.txt"));

    ConditionalTee cnt1 = new ConditionalTee();
    Counting count = new Counting();
    count.setInterval(3);
    cnt1.setCondition(count);
    cnt1.add(0, df1);

    DumpFile df2 = new DumpFile();
    df2.setAppend(true);
    df2.setOutputFile(new TmpFile("dumpfile-count2.txt"));

    ConditionalTee cnt2 = new ConditionalTee();
    count = new Counting();
    count.setInterval(3);
    count.setMinimum(10);
    cnt2.setCondition(count);
    cnt2.add(0, df2);

    DumpFile df3 = new DumpFile();
    df3.setAppend(true);
    df3.setOutputFile(new TmpFile("dumpfile-count3.txt"));

    ConditionalTee cnt3 = new ConditionalTee();
    count = new Counting();
    count.setInterval(3);
    count.setMinimum(10);
    count.setMaximum(20);
    cnt3.setCondition(count);
    cnt3.add(0, df3);

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{ids, cnt1, cnt2, cnt3, df});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new File[]{
	    new TmpFile("dumpfile.txt"),
	    new TmpFile("dumpfile-count1.txt"),
	    new TmpFile("dumpfile-count2.txt"),
	    new TmpFile("dumpfile-count3.txt")});
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ConditionalTeeTest.class);
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
