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
 * DoubleOptionTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Test class for all double options. Run from the command line with: <br><br>
 * java adams.core.option.DoubleOptionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DoubleOptionTest
  extends AbstractNumericOptionTest {

  /**
   * Dummy class for testing double options.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class DoubleOptionClass
    extends AbstractOptionClass {

    /** for serialization. */
    private static final long serialVersionUID = 1181533920136806930L;

    protected double m_DoublePrim;

    protected double[] m_DoublePrimArray;

    protected Double m_DoubleObj;

    protected Double[] m_DoubleObjArray;

    /**
     * Adds options to the doubleernal list of options.
     */
    public void defineOptions() {
      super.defineOptions();

      m_OptionManager.add(
	  "double-prim", "doublePrim",
	  (double) 1);

      m_OptionManager.add(
	  "double-prim-array", "doublePrimArray",
	  new double[]{1, 2, 3});

      m_OptionManager.add(
	  "double-obj", "doubleObj",
	  new Double((double) 2), new Double((double) -10), new Double((double) +10));

      m_OptionManager.add(
	  "double-obj-array", "doubleObjArray",
	  new Double[]{new Double((double) 4), new Double((double) 5), new Double((double) 6)});
    }

    public void setDoublePrim(double value) {
      m_DoublePrim = value;
    }

    public double getDoublePrim() {
      return m_DoublePrim;
    }

    public String doublePrimTipText() {
      return "doublePrim";
    }

    public void setDoublePrimArray(double[] value) {
      m_DoublePrimArray = value;
    }

    public double[] getDoublePrimArray() {
      return m_DoublePrimArray;
    }

    public String doublePrimArrayTipText() {
      return "doublePrimArray";
    }

    public void setDoubleObj(Double value) {
      m_DoubleObj = value;
    }

    public Double getDoubleObj() {
      return m_DoubleObj;
    }

    public String doubleObjTipText() {
      return "doubleObj";
    }

    public void setDoubleObjArray(Double[] value) {
      m_DoubleObjArray = value;
    }

    public Double[] getDoubleObjArray() {
      return m_DoubleObjArray;
    }

    public String doubleObjArrayTipText() {
      return "doubleObjArray";
    }
  }

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DoubleOptionTest(String name) {
    super(name);
  }

  /**
   * Returns the fully setup dummy object to test.
   *
   * @return		the object to use for testing the options
   */
  protected OptionHandler getOptionHandler() {
    return new DoubleOptionClass();
  }

  /**
   * Returns the name of the property to use for testing the lower bound.
   *
   * @return		the property
   */
  protected String getLowerBoundTestProperty() {
    return "doubleObj";
  }

  /**
   * Returns the (outside) value to test the lower bound with.
   *
   * @return		the value
   */
  protected Double getLowerBoundTestValue() {
    return new Double((double) -100);
  }

  /**
   * Returns the name of the property to use for testing the upper bound.
   *
   * @return		the property
   */
  protected String getUpperBoundTestProperty() {
    return "doubleObj";
  }

  /**
   * Returns the (outside) value to test the upper bound with.
   *
   * @return		the value
   */
  protected Double getUpperBoundTestValue() {
    return new Double((double) 100);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(DoubleOptionTest.class);
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
