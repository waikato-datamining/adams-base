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
 * BaseHostnameTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.core.base.BaseHostname class. Run from commandline with: <br><br>
 * java adams.core.base.BaseHostnameTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseHostnameTest
  extends AbstractBaseObjectTestCase<BaseHostname> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BaseHostnameTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected BaseHostname getDefault() {
    return new BaseHostname();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected BaseHostname getCustom(String s) {
    return new BaseHostname(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return Object.class.getName();
  }

  /**
   * Tests an invalid host.
   */
  public void testInvalidHostname() {
    BaseHostname host = new BaseHostname();
    host.setValue("blah.bla&");
    assertNotSame("blah.blah", host.getValue());
  }

  /**
   * Tests port handling.
   */
  public void testPort() {
    BaseHostname host = new BaseHostname();
    host.setValue("blah:22");
    assertEquals(22, host.portValue());

    host = new BaseHostname();
    host.setValue("blah:220000");
    assertEquals(-1, host.portValue());

    host = new BaseHostname();
    host.setValue("blah:-1");
    assertEquals(-1, host.portValue());

    host = new BaseHostname();
    host.setValue("blah:65536");
    assertEquals(-1, host.portValue());

    host = new BaseHostname();
    host.setValue("blah");
    assertEquals(-1, host.portValue());
  }

  /**
   * Tests port handling.
   */
  public void testHostname() {
    BaseHostname host = new BaseHostname();
    host.setValue("blah:22");
    assertEquals("blah", host.hostnameValue());

    host = new BaseHostname();
    host.setValue("adams.cms.waikato.ac.nz");
    assertEquals("adams.cms.waikato.ac.nz", host.hostnameValue());

    host = new BaseHostname();
    host.setValue("adams.cms.waikato.ac.nz:443");
    assertEquals("adams.cms.waikato.ac.nz", host.hostnameValue());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BaseHostnameTest.class);
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
