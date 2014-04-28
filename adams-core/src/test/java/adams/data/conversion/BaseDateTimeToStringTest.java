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
 * BaseDateTimeToStringTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;


/**
 * Tests the BaseDateTimeToString conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseDateTimeToStringTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public BaseDateTimeToStringTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  protected Object[] getRegressionInput() {
    return new String[]{
	"2010-10-10 11:03:01",
	"2010-10-10 23:03:01",
	"2010-10-10 11:03:01 +1 HOUR",
	"2010-10-10 23:03:01 +3 DAY +1 HOUR -4 MINUTE",
	"2010-10-10 23:03:01 +3 DAY +1 HOUR -4 SECOND",
	"2010-10-10 12:03:01 -5 DAY +1 HOUR -1 MINUTE",
	"2010-10-10 23:03:01 -14 DAY +1 HOUR -4 MINUTE",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected Conversion[] getRegressionSetups() {
    return new Conversion[]{
	new BaseDateTimeToString()
    };
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
