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
 * StringReplaceTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.template;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.test.TmpFile;

/**
 * Tests the StringReplace template.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringReplaceTest
  extends AbstractActorTemplateTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StringReplaceTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("subprocess.flow");
    m_TestHelper.copyResourceToTmp("subprocessWithPlaceholders.flow");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("subprocess.flow");
    m_TestHelper.deleteFileFromTmp("subprocessWithPlaceholders.flow");

    super.tearDown();
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected AbstractActorTemplate[] getRegressionSetups() {
    StringReplace[]	result;

    result = new StringReplace[2];

    result[0] = new StringReplace();
    result[0].setTemplateFile(new TmpFile("subprocess.flow"));

    result[1] = new StringReplace();
    result[1].setTemplateFile(new TmpFile("subprocessWithPlaceholders.flow"));
    result[1].setFind(new BaseRegExp[]{
	new BaseRegExp("#base#"),
	new BaseRegExp("#mult#")
    });
    result[1].setReplace(new BaseString[]{
	new BaseString("1000"),
	new BaseString("3")
    });

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(StringReplaceTest.class);
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
