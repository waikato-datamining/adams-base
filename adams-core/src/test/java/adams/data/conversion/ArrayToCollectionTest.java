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
 * ArrayToCollectionTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import java.util.Stack;
import java.util.Vector;


/**
 * Tests the ArrayToCollection conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayToCollectionTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ArrayToCollectionTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  protected Object[] getRegressionInput() {
    return new Double[][]{
	{
	  1.0,
	  -1.0,
	  3.1415926535,
	  -3.1415926535,
	  1E6,
	  -1E2,
	  1.34E2,
	  -4.67E3
	}
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected Conversion[] getRegressionSetups() {
    ArrayToCollection[]		result;
    
    result = new ArrayToCollection[3];
    result[0] = new ArrayToCollection();
    
    result[1] = new ArrayToCollection();
    result[1].setCollectionClass(Vector.class.getName());

    result[2] = new ArrayToCollection();
    result[2].setCollectionClass(Stack.class.getName());
    
    return result;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }
}
