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
 * MultiConditionTest.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.condition.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.TmpFile;

/**
 * Tests the MultiCondition condition.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiConditionTest
  extends AbstractTestConditionTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MultiConditionTest(String name) {
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

    m_TestHelper.copyResourceToTmp("bolts.csv");
    m_TestHelper.deleteFileFromTmp("bolts2.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.csv");
    m_TestHelper.deleteFileFromTmp("bolts2.csv");

    super.tearDown();
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractTestCondition[] getRegressionSetups() {
    MultiCondition[]	result;
    FileExists		exists;
    True		tr;

    result = new MultiCondition[2];

    exists = new FileExists();
    exists.setFile(new TmpFile("bolts.csv"));
    tr = new True();
    result[0] = new MultiCondition();
    result[0].setSubConditions(new AbstractTestCondition[]{exists, tr});

    exists = new FileExists();
    exists.setFile(new TmpFile("bolts2.csv"));
    tr = new True();
    result[1] = new MultiCondition();
    result[1].setSubConditions(new AbstractTestCondition[]{exists, tr});

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MultiConditionTest.class);
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
