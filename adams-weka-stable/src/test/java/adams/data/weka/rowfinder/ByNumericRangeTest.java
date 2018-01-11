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
 * ByNumericRangeTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.rowfinder;

import adams.core.base.BaseInterval;
import adams.data.weka.WekaAttributeIndex;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test class for the ByNumericRange finder.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByNumericRangeTest
  extends AbstractRowFinderTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ByNumericRangeTest(String name) {
    super(name);
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"bolts.arff",
	"bolts.arff",
	"bolts.arff",
	"bolts.arff"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected RowFinder[] getRegressionSetups() {
    ByNumericRange[]	result;

    result = new ByNumericRange[4];

    result[0] = new ByNumericRange();
    result[1] = new ByNumericRange();
    result[1].setRanges(new BaseInterval[]{new BaseInterval("(20.0;+Infinity)")});
    result[2] = new ByNumericRange();
    result[2].setAttributeIndex(new WekaAttributeIndex("2"));
    result[2].setRanges(new BaseInterval[]{new BaseInterval("(1.0;2.0)")});
    result[3] = new ByNumericRange();
    result[3].setAttributeIndex(new WekaAttributeIndex("2"));
    result[3].setRanges(new BaseInterval[]{new BaseInterval("[1.0;2.0]")});

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(ByNumericRangeTest.class);
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
