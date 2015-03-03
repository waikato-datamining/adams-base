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
 * ConvertDateTimeTypeTest.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.DateTimeType;
import adams.env.Environment;

/**
 * Tests the ConvertDateTimeType conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConvertDateTimeTypeTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ConvertDateTimeTypeTest(String name) {
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
    ConvertDateTimeType[]	result;
    
    result = new ConvertDateTimeType[5];
    result[0] = new ConvertDateTimeType();
    result[1] = new ConvertDateTimeType();
    result[1].setOutputDateTimeType(DateTimeType.SECONDS);
    result[2] = new ConvertDateTimeType();
    result[2].setOutputDateTimeType(DateTimeType.DATETIME);
    result[3] = new ConvertDateTimeType();
    result[3].setOutputDateTimeType(DateTimeType.TIME);
    result[4] = new ConvertDateTimeType();
    result[4].setOutputDateTimeType(DateTimeType.DATE);
    
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
    return new TestSuite(ConvertDateTimeTypeTest.class);
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
