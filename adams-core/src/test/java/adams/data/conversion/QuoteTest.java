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
 * QuoteTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.conversion.Quote.QuoteType;
import adams.env.Environment;

/**
 * Tests the Quote conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class QuoteTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public QuoteTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    return new String[]{
	"1",
	"1'2",
	"1 3",
	"12\t34",
	"123\n45",
	"12 34 56",
	"123 \"45\" 67"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    Quote[]	result;

    result    = new Quote[2];
    result[0] = new Quote();
    result[1] = new Quote();
    result[1].setQuoteType(QuoteType.SINGLE);

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
    return new TestSuite(QuoteTest.class);
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
