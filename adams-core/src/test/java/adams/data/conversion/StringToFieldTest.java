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
 * StringToFieldTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.data.report.DataType;
import adams.data.report.Field;

/**
 * Tests the StringToField conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringToFieldTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public StringToFieldTest(String name) {
    super(name);
  }

  /**
   * Turns the data object into a useful string representation.
   * <p/>
   * Outputs the parseable format.
   *
   * @param data	the object to convert
   * @return		the string representation
   */
  protected String toString(Object data) {
    return ((Field) data).toParseableString();
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  protected Object[] getRegressionInput() {
    return new String[]{
	"Blah",
	"numeric[N]",
	"boolean[B]",
	"string[S]",
	"unknown[U]"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected Conversion[] getRegressionSetups() {
    StringToField[]	result;

    result = new StringToField[2];
    result[0] = new StringToField();
    result[1] = new StringToField();
    result[1].setDefaultDataType(DataType.NUMERIC);

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
