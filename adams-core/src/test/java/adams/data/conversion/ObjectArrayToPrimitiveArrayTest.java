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
 * ObjectArrayToPrimitiveArrayTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the ObjectArrayToPrimitiveArray conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ObjectArrayToPrimitiveArrayTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ObjectArrayToPrimitiveArrayTest(String name) {
    super(name);
  }
  
  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    return new Object[]{
	new Byte[]{1,2,3,4,5},
	new Short[]{1,2,3,4,5,300},
	new Integer[]{1,2,3,4,5,300,70000},
	new Long[]{1L,2L,3L,4L,5L,300L,70000L,5000000000L},
	new Float[]{1.1f,2.2f,3.3f},
	new Double[]{1.1,2.2,3.3},
	new Character[]{'a','b','c'},
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    ObjectArrayToPrimitiveArray[]	result;

    result    = new ObjectArrayToPrimitiveArray[1];
    result[0] = new ObjectArrayToPrimitiveArray();

    return result;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(ObjectArrayToPrimitiveArrayTest.class);
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
