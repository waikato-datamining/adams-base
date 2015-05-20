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
 * BasePasswordTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BasePassword;
import adams.env.Environment;

/**
 * Tests the adams.core.base.BasePassword class. Run from commandline with: <br><br>
 * java adams.core.base.BasePasswordTest
 * <br><br>
 * Example strings taken from <a href="http://en.wikipedia.org/wiki/Base64">here</a>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BasePasswordTest
  extends AbstractBaseObjectTestCase<BasePassword> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BasePasswordTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected BasePassword getDefault() {
    return new BasePassword();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected BasePassword getCustom(String s) {
    return new BasePassword(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return "bGVhc3VyZS4=";
  }

  /**
   * Performs decoding. Fails if decoded string differs from clear text string.
   *
   * @param encoded	the encoded string
   * @param clear	the clear text string to compare against
   */
  protected void performDecoding(String encoded, String clear) {
    BasePassword	base;

    base = new BasePassword();
    base.setValue(encoded);
    assertEquals("Decoded string differs", clear, base.getValue());
  }

  /**
   * Performs some decoding tests.
   */
  public void testDecoding() {
    performDecoding(BasePassword.BASE64_START + "bGVhc3VyZS4=" + BasePassword.BASE64_END, "leasure.");
    performDecoding(BasePassword.BASE64_START + "ZWFzdXJlLg==" + BasePassword.BASE64_END, "easure.");
    performDecoding(BasePassword.BASE64_START + "YXN1cmUu"     + BasePassword.BASE64_END, "asure.");
    performDecoding(BasePassword.BASE64_START + "c3VyZS4="     + BasePassword.BASE64_END, "sure.");
  }

  /**
   * Performs encoding. Fails if encoded string differs from the provided
   * encoded string.
   *
   * @param clear	the clear text string
   * @param encoded	the encoded string to compare against
   */
  protected void performEncoding(String clear, String encoded) {
    BasePassword	base;

    base = new BasePassword();
    base.setValue(clear);
    assertEquals("Encoded string differs", encoded, base.stringValue());
  }

  /**
   * Performs some encoding tests.
   */
  public void testEncoding() {
    performEncoding("leasure.", BasePassword.BASE64_START + "bGVhc3VyZS4=" + BasePassword.BASE64_END);
    performEncoding("easure.",  BasePassword.BASE64_START + "ZWFzdXJlLg==" + BasePassword.BASE64_END);
    performEncoding("asure.",   BasePassword.BASE64_START + "YXN1cmUu"     + BasePassword.BASE64_END);
    performEncoding("sure.",    BasePassword.BASE64_START + "c3VyZS4="     + BasePassword.BASE64_END);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BasePasswordTest.class);
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
