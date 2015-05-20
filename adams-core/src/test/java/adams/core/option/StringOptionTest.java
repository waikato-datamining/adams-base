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

/**
 * StringOptionTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Test class for all string options. Run from the command line with: <br><br>
 * java adams.core.option.StringOptionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringOptionTest
  extends AbstractArgumentOptionTest {

  /**
   * Dummy class for testing byte options.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class StringOptionClass
    extends AbstractOptionClass {

    /** for serialization. */
    private static final long serialVersionUID = 5861574574955490382L;

    protected String m_String;

    protected String[] m_StringArray;

    /**
     * Adds options to the internal list of options.
     */
    public void defineOptions() {
      super.defineOptions();

      m_OptionManager.add(
	  "string", "string",
      "1");

      m_OptionManager.add(
	  "string-array", "stringArray",
	  new String[]{"1", "2", "3"});
    }

    public void setString(String value) {
      m_String = value;
    }

    public String getString() {
      return m_String;
    }

    public String stringTipText() {
      return "string";
    }

    public void setStringArray(String[] value) {
      m_StringArray = value;
    }

    public String[] getStringArray() {
      return m_StringArray;
    }

    public String stringArrayTipText() {
      return "stringArray";
    }
  }

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StringOptionTest(String name) {
    super(name);
  }

  /**
   * Returns the fully setup dummy object to test.
   *
   * @return		the object to use for testing the options
   */
  protected OptionHandler getOptionHandler() {
    return new StringOptionClass();
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(StringOptionTest.class);
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
