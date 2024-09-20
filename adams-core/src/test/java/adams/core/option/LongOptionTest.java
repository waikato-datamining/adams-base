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
 * LongOptionTest.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test class for all long options. Run from the command line with: <br><br>
 * java adams.core.option.LongOptionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class LongOptionTest
  extends AbstractNumericOptionTest {

  /**
   * Dummy class for testing long options.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class LongOptionClass
    extends AbstractOptionClass {

    /** for serialization. */
    private static final long serialVersionUID = 8808117463200049431L;

    protected int m_LongPrim;

    protected int[] m_LongPrimArray;

    protected Long m_LongObj;

    protected Long[] m_LongObjArray;

    /**
     * Adds options to the internal list of options.
     */
    public void defineOptions() {
      super.defineOptions();

      m_OptionManager.add(
	  "int-prim", "intPrim",
	  1L);

      m_OptionManager.add(
	  "int-prim-array", "intPrimArray",
	  new long[]{1L, 2L, 3L});

      m_OptionManager.add(
	  "int-obj", "longObj",
	  2L, -10L, +10L);

      m_OptionManager.add(
	  "int-obj-array", "longObjArray",
	  new Long[]{4L, 5L, 6L});
    }

    public void setIntPrim(int value) {
      m_LongPrim = value;
    }

    public int getIntPrim() {
      return m_LongPrim;
    }

    public String intPrimTipText() {
      return "intPrim";
    }

    public void setIntPrimArray(int[] value) {
      m_LongPrimArray = value;
    }

    public int[] getIntPrimArray() {
      return m_LongPrimArray;
    }

    public String intPrimArrayTipText() {
      return "intPrimArray";
    }

    public void setLongObj(Long value) {
      m_LongObj = value;
    }

    public Long getLongObj() {
      return m_LongObj;
    }

    public String longObjTipText() {
      return "longObj";
    }

    public void setLongObjArray(Long[] value) {
      m_LongObjArray = value;
    }

    public Long[] getLongObjArray() {
      return m_LongObjArray;
    }

    public String longObjArrayTipText() {
      return "intObjArray";
    }
  }

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public LongOptionTest(String name) {
    super(name);
  }

  /**
   * Returns the fully setup dummy object to test.
   *
   * @return		the object to use for testing the options
   */
  protected OptionHandler getOptionHandler() {
    return new LongOptionClass();
  }

  /**
   * Returns the name of the property to use for testing the lower bound.
   *
   * @return		the property
   */
  protected String getLowerBoundTestProperty() {
    return "longObj";
  }

  /**
   * Returns the (outside) value to test the lower bound with.
   *
   * @return		the value
   */
  protected Long getLowerBoundTestValue() {
    return -100L;
  }

  /**
   * Returns the name of the property to use for testing the upper bound.
   *
   * @return		the property
   */
  protected String getUpperBoundTestProperty() {
    return "longObj";
  }

  /**
   * Returns the (outside) value to test the upper bound with.
   *
   * @return		the value
   */
  protected Long getUpperBoundTestValue() {
    return 100L;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(LongOptionTest.class);
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
