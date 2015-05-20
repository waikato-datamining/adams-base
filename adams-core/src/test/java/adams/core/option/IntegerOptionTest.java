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
 * IntegerOptionTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Test class for all integer options. Run from the command line with: <br><br>
 * java adams.core.option.IntegerOptionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class IntegerOptionTest
  extends AbstractNumericOptionTest {

  /**
   * Dummy class for testing integer options.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class IntegerOptionClass
    extends AbstractOptionClass {

    /** for serialization. */
    private static final long serialVersionUID = 8808117463200049431L;

    protected int m_IntegerPrim;

    protected int[] m_IntegerPrimArray;

    protected Integer m_IntegerObj;

    protected Integer[] m_IntegerObjArray;

    /**
     * Adds options to the internal list of options.
     */
    public void defineOptions() {
      super.defineOptions();

      m_OptionManager.add(
	  "int-prim", "intPrim",
	  (int) 1);

      m_OptionManager.add(
	  "int-prim-array", "intPrimArray",
	  new int[]{1, 2, 3});

      m_OptionManager.add(
	  "int-obj", "integerObj",
	  new Integer((int) 2), new Integer(-10), new Integer(+10));

      m_OptionManager.add(
	  "int-obj-array", "integerObjArray",
	  new Integer[]{new Integer((int) 4), new Integer((int) 5), new Integer((int) 6)});
    }

    public void setIntPrim(int value) {
      m_IntegerPrim = value;
    }

    public int getIntPrim() {
      return m_IntegerPrim;
    }

    public String intPrimTipText() {
      return "intPrim";
    }

    public void setIntPrimArray(int[] value) {
      m_IntegerPrimArray = value;
    }

    public int[] getIntPrimArray() {
      return m_IntegerPrimArray;
    }

    public String intPrimArrayTipText() {
      return "intPrimArray";
    }

    public void setIntegerObj(Integer value) {
      m_IntegerObj = value;
    }

    public Integer getIntegerObj() {
      return m_IntegerObj;
    }

    public String integerObjTipText() {
      return "integerObj";
    }

    public void setIntegerObjArray(Integer[] value) {
      m_IntegerObjArray = value;
    }

    public Integer[] getIntegerObjArray() {
      return m_IntegerObjArray;
    }

    public String integerObjArrayTipText() {
      return "intObjArray";
    }
  }

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public IntegerOptionTest(String name) {
    super(name);
  }

  /**
   * Returns the fully setup dummy object to test.
   *
   * @return		the object to use for testing the options
   */
  protected OptionHandler getOptionHandler() {
    return new IntegerOptionClass();
  }

  /**
   * Returns the name of the property to use for testing the lower bound.
   *
   * @return		the property
   */
  protected String getLowerBoundTestProperty() {
    return "integerObj";
  }

  /**
   * Returns the (outside) value to test the lower bound with.
   *
   * @return		the value
   */
  protected Integer getLowerBoundTestValue() {
    return new Integer(-100);
  }

  /**
   * Returns the name of the property to use for testing the upper bound.
   *
   * @return		the property
   */
  protected String getUpperBoundTestProperty() {
    return "integerObj";
  }

  /**
   * Returns the (outside) value to test the upper bound with.
   *
   * @return		the value
   */
  protected Integer getUpperBoundTestValue() {
    return new Integer(100);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(IntegerOptionTest.class);
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
