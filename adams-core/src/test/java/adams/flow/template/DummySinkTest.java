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
 * DummySinkTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.template;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.VariableName;
import adams.env.Environment;
import adams.test.TmpFile;

/**
 * Tests the DummySink template.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DummySinkTest
  extends AbstractActorTemplateTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DummySinkTest(String name) {
    super(name);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected AbstractActorTemplate[] getRegressionSetups() {
    DummySink[]	result;

    result = new DummySink[1];

    result[0] = new DummySink();

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(DummySinkTest.class);
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
