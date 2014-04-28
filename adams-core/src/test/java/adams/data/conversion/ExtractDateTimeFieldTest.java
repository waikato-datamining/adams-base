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
 * ExtractDateTimeFieldTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import java.util.Date;

import adams.data.DateFormatString;
import adams.data.conversion.ExtractDateTimeField.DateTimeField;

/**
 * Tests the ExtractDateTimeField conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExtractDateTimeFieldTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ExtractDateTimeFieldTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    return new Date[]{
	new Date(0L),
	new Date(1000L),
	new Date(1000000L),
	new Date(1000000000L),
	new Date(1000000000000L),
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    ExtractDateTimeField[]	result;
    
    result    = new ExtractDateTimeField[14];
    result[0] = new ExtractDateTimeField();
    result[1] = new ExtractDateTimeField();
    result[1].setField(DateTimeField.MONTH);
    result[2] = new ExtractDateTimeField();
    result[2].setField(DateTimeField.DAY);
    result[3] = new ExtractDateTimeField();
    result[3].setField(DateTimeField.DAY_OF_YEAR);
    result[4] = new ExtractDateTimeField();
    result[4].setField(DateTimeField.DAY_OF_MONTH);
    result[5] = new ExtractDateTimeField();
    result[5].setField(DateTimeField.DAY_OF_WEEK);
    result[6] = new ExtractDateTimeField();
    result[6].setField(DateTimeField.DAY_OF_WEEK_STR_EN);
    result[7] = new ExtractDateTimeField();
    result[7].setField(DateTimeField.HOUR);
    result[8] = new ExtractDateTimeField();
    result[8].setField(DateTimeField.MINUTE);
    result[9] = new ExtractDateTimeField();
    result[9].setField(DateTimeField.SECOND);
    result[10] = new ExtractDateTimeField();
    result[10].setField(DateTimeField.MSEC);
    result[11] = new ExtractDateTimeField();
    result[11].setField(DateTimeField.WEEK_OF_YEAR);
    result[12] = new ExtractDateTimeField();
    result[12].setField(DateTimeField.WEEK_OF_MONTH);
    result[13] = new ExtractDateTimeField();
    result[13].setField(DateTimeField.CUSTOM);
    result[13].setFormatCustom(new DateFormatString("yy W"));
    
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
}
