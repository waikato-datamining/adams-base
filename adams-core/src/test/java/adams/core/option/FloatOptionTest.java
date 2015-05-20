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
 * FloatOptionTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Test class for all float options. Run from the command line with: <br><br>
 * java adams.core.option.FloatOptionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FloatOptionTest
  extends AbstractNumericOptionTest {

  /**
   * Dummy class for testing float options.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FloatOptionClass
    extends AbstractOptionClass {

    /** for serialization. */
    private static final long serialVersionUID = 4373423899774166393L;

    protected float m_FloatPrim;

    protected float[] m_FloatPrimArray;

    protected Float m_FloatObj;

    protected Float[] m_FloatObjArray;

    /**
     * Adds options to the floaternal list of options.
     */
    public void defineOptions() {
      super.defineOptions();

      m_OptionManager.add(
	  "float-prim", "floatPrim",
	  (float) 1);

      m_OptionManager.add(
	  "float-prim-array", "floatPrimArray",
	  new float[]{1, 2, 3});

      m_OptionManager.add(
	  "float-obj", "floatObj",
	  new Float((float) 2), new Float((float) -10), new Float((float) +10));

      m_OptionManager.add(
	  "float-obj-array", "floatObjArray",
	  new Float[]{new Float((float) 4), new Float((float) 5), new Float((float) 6)});
    }

    public void setFloatPrim(float value) {
      m_FloatPrim = value;
    }

    public float getFloatPrim() {
      return m_FloatPrim;
    }

    public String floatPrimTipText() {
      return "floatPrim";
    }

    public void setFloatPrimArray(float[] value) {
      m_FloatPrimArray = value;
    }

    public float[] getFloatPrimArray() {
      return m_FloatPrimArray;
    }

    public String floatPrimArrayTipText() {
      return "floatPrimArray";
    }

    public void setFloatObj(Float value) {
      m_FloatObj = value;
    }

    public Float getFloatObj() {
      return m_FloatObj;
    }

    public String floatObjTipText() {
      return "floatObj";
    }

    public void setFloatObjArray(Float[] value) {
      m_FloatObjArray = value;
    }

    public Float[] getFloatObjArray() {
      return m_FloatObjArray;
    }

    public String floatObjArrayTipText() {
      return "floatObjArray";
    }
  }

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public FloatOptionTest(String name) {
    super(name);
  }

  /**
   * Returns the fully setup dummy object to test.
   *
   * @return		the object to use for testing the options
   */
  protected OptionHandler getOptionHandler() {
    return new FloatOptionClass();
  }

  /**
   * Returns the name of the property to use for testing the lower bound.
   *
   * @return		the property
   */
  protected String getLowerBoundTestProperty() {
    return "floatObj";
  }

  /**
   * Returns the (outside) value to test the lower bound with.
   *
   * @return		the value
   */
  protected Float getLowerBoundTestValue() {
    return new Float((float) -100);
  }

  /**
   * Returns the name of the property to use for testing the upper bound.
   *
   * @return		the property
   */
  protected String getUpperBoundTestProperty() {
    return "floatObj";
  }

  /**
   * Returns the (outside) value to test the upper bound with.
   *
   * @return		the value
   */
  protected Float getUpperBoundTestValue() {
    return new Float((float) 100);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(FloatOptionTest.class);
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
