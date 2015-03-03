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
 * FileExistsTest.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.condition.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.io.PlaceholderFile;
import adams.env.Environment;
import adams.test.TmpFile;

/**
 * Tests the FileExists condition.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileExistsTest
  extends AbstractTestConditionTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public FileExistsTest(String name) {
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
    FileExists[]	result;

    result = new FileExists[6];

    result[0] = new FileExists();
    result[0].setFile(new TmpFile("bolts.csv"));

    result[1] = new FileExists();
    result[1].setFile(new TmpFile("bolts2.csv"));

    result[2] = new FileExists();
    result[2].setFile(new PlaceholderFile("${TMP}"));

    result[3] = new FileExists();
    result[3].setFile(new TmpFile("bolts.csv"));
    result[3].setInvert(true);

    result[4] = new FileExists();
    result[4].setFile(new TmpFile("bolts2.csv"));
    result[4].setInvert(true);

    result[5] = new FileExists();
    result[5].setFile(new PlaceholderFile("${TMP}/blah"));
    result[5].setInvert(true);

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(FileExistsTest.class);
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
