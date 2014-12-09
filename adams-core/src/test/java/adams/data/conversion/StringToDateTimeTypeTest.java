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
 * StringToDateTimeTypeTest.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.Constants;
import adams.core.DateFormat;
import adams.env.Environment;

/**
 * Tests the StringToDateTimeType conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringToDateTimeTypeTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public StringToDateTimeTypeTest(String name) {
    super(name);
  }

  /**
   * Turns the data object into a useful string representation.
   *
   * @param data	the object to convert
   * @return		the string representation
   */
  @Override
  protected String toString(Object data) {
    DateFormat df = new DateFormat(Constants.TIMESTAMP_FORMAT);
    return df.format((Date) data);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    return new String[]{
	"1970-01-01",
	"1999-02-03",
	"2013-02-15",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    return new Conversion[]{
	new StringToDateTimeType()
    };
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
    return new TestSuite(StringToDateTimeTypeTest.class);
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
