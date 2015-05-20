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
 * AdditionalOptionsHandlerUtilsTest.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import java.util.Hashtable;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.Variables;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.flow.core.AdditionalOptionsHandlerUtils class. Run from commandline with: <br><br>
 * java adams.flow.core.AdditionalOptionsHandlerUtilsTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AdditionalOptionsHandlerUtilsTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AdditionalOptionsHandlerUtilsTest(String name) {
    super(name);
  }

  /**
   * Performs a break-up test.
   *
   * @param optStr	the option string to break up
   * @param expected	the expected key-value relations
   */
  protected void performBreakUpTest(String optStr, Hashtable<String,String> expected) {
    Hashtable<String,String>	generated;

    try {
      generated = AdditionalOptionsHandlerUtils.breakUpOptions(optStr, new Variables());
      assertEquals("Number of stored pairs differ", expected.size(), generated.size());
      assertEquals("Different key-value pairs generated", expected, generated);
    }
    catch (Exception e) {
      fail("Exception generated: " + e);
    }
  }

  /**
   * Tests the break-up of an empty options string.
   */
  public void testSingleEmptyBreakUp() {
    String 			optStr;
    Hashtable<String,String>	expected;

    optStr   = "";
    expected = new Hashtable<String,String>();

    performBreakUpTest(optStr, expected);
  }

  /**
   * Tests the break-up of a single key-value pair.
   */
  public void testSinglePairBreakUp() {
    String 			optStr;
    Hashtable<String,String>	expected;

    optStr   = "blah=10";
    expected = new Hashtable<String,String>();
    expected.put("blah", "10");

    performBreakUpTest(optStr, expected);
  }

  /**
   * Tests the break-up of multiple key-value pairs.
   */
  public void testMultiPairBreakUp() {
    String 			optStr;
    Hashtable<String,String>	expected;

    optStr   = "blah=10 hello=world what=the f**k=42";
    expected = new Hashtable<String,String>();
    expected.put("blah",  "10");
    expected.put("hello", "world");
    expected.put("what",  "the");
    expected.put("f**k",  "42");

    performBreakUpTest(optStr, expected);
  }

  /**
   * Tests the break-up of an option string with an invalid key-value pairs.
   */
  public void testInvalidOption() {
    String 			optStr;
    Hashtable<String,String>	expected;

    optStr   = "blah=10 hello what=the what:ever";
    expected = new Hashtable<String,String>();
    expected.put("blah",  "10");
    expected.put("what",  "the");

    performBreakUpTest(optStr, expected);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(AdditionalOptionsHandlerUtilsTest.class);
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
