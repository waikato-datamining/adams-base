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
 * ShortOptionTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Test class for all short options. Run from the command line with: <p/>
 * java adams.core.option.ShortOptionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ShortOptionTest
  extends AbstractNumericOptionTest {

  /**
   * Dummy class for testing short options.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ShortOptionClass
    extends AbstractOptionClass {

    /** for serialization. */
    private static final long serialVersionUID = 8286280518774728029L;

    protected short m_ShortPrim;

    protected short[] m_ShortPrimArray;

    protected Short m_ShortObj;

    protected Short[] m_ShortObjArray;

    /**
     * Adds options to the shorternal list of options.
     */
    public void defineOptions() {
      super.defineOptions();

      m_OptionManager.add(
	  "short-prim", "shortPrim",
	  (short) 1);

      m_OptionManager.add(
	  "short-prim-array", "shortPrimArray",
	  new short[]{1, 2, 3});

      m_OptionManager.add(
	  "short-obj", "shortObj",
	  new Short((short) 2), new Short((short) -10), new Short((short) +10));

      m_OptionManager.add(
	  "short-obj-array", "shortObjArray",
	  new Short[]{new Short((short) 4), new Short((short) 5), new Short((short) 6)});
    }

    public void setShortPrim(short value) {
      m_ShortPrim = value;
    }

    public short getShortPrim() {
      return m_ShortPrim;
    }

    public String shortPrimTipText() {
      return "shortPrim";
    }

    public void setShortPrimArray(short[] value) {
      m_ShortPrimArray = value;
    }

    public short[] getShortPrimArray() {
      return m_ShortPrimArray;
    }

    public String shortPrimArrayTipText() {
      return "shortPrimArray";
    }

    public void setShortObj(Short value) {
      m_ShortObj = value;
    }

    public Short getShortObj() {
      return m_ShortObj;
    }

    public String shortObjTipText() {
      return "shortObj";
    }

    public void setShortObjArray(Short[] value) {
      m_ShortObjArray = value;
    }

    public Short[] getShortObjArray() {
      return m_ShortObjArray;
    }

    public String shortObjArrayTipText() {
      return "shortObjArray";
    }
  }

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ShortOptionTest(String name) {
    super(name);
  }

  /**
   * Returns the fully setup dummy object to test.
   *
   * @return		the object to use for testing the options
   */
  protected OptionHandler getOptionHandler() {
    return new ShortOptionClass();
  }

  /**
   * Returns the name of the property to use for testing the lower bound.
   *
   * @return		the property
   */
  protected String getLowerBoundTestProperty() {
    return "shortObj";
  }

  /**
   * Returns the (outside) value to test the lower bound with.
   *
   * @return		the value
   */
  protected Short getLowerBoundTestValue() {
    return new Short((short) -100);
  }

  /**
   * Returns the name of the property to use for testing the upper bound.
   *
   * @return		the property
   */
  protected String getUpperBoundTestProperty() {
    return "shortObj";
  }

  /**
   * Returns the (outside) value to test the upper bound with.
   *
   * @return		the value
   */
  protected Short getUpperBoundTestValue() {
    return new Short((short) 100);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ShortOptionTest.class);
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
