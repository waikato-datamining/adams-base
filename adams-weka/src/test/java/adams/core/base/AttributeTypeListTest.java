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
 * AttributeTypeListTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.core.base.AttributeTypeList class. Run from commandline with: <p/>
 * java adams.core.base.AttributeTypeListTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AttributeTypeListTest
  extends AbstractBaseObjectTestCase<AttributeTypeList> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AttributeTypeListTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected AttributeTypeList getDefault() {
    return new AttributeTypeList();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected AttributeTypeList getCustom(String s) {
    return new AttributeTypeList(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return "NUM,NOM,STR,DAT";
  }

  /**
   * Tests the generated list.
   */
  public void testListValue() {
    try {
      AttributeTypeList b = getCustom(getTypicalValue());
      String[] typical = getTypicalValue().split(",");
      String[] parsed = b.listValue();
      assertEquals("array lengths differ", typical.length, parsed.length);
      for (int i = 0; i < typical.length; i++)
	assertEquals("arrays differ at #", typical[i], parsed[i]);
    }
    catch (Exception e) {
      fail("Parsing failed: " + e);
    }
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(AttributeTypeListTest.class);
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
