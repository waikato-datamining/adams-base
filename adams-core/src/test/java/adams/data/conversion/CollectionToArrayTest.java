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
 * CollectionToArrayTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Tests the CollectionToArray conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CollectionToArrayTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public CollectionToArrayTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  protected Object[] getRegressionInput() {
    return new Collection[]{
	new ArrayList(
	    Arrays.asList(
		new Double[]{
		    1.0,
		    -1.0,
		    3.1415926535,
		    -3.1415926535,
		    1E6,
		    -1E2,
		    1.34E2,
		    -4.67E3
		})
	    )
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected Conversion[] getRegressionSetups() {
    CollectionToArray[]		result;
    
    result = new CollectionToArray[1];
    result[0] = new CollectionToArray();
    result[0].setArrayClass(Double.class.getName());
    
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
