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
 * MinMaxTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.outlier;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.instance.Instance;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.env.Environment;

/**
 * Test class for the MinMax outlier detector. Run from the command line with: <p/>
 * java adams.data.outlier.MinMaxTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MinMaxTest
  extends AbstractInstanceOutlierDetectorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public MinMaxTest(String name) {
    super(name);
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  protected String[] getRegressionInputFiles() {
    return new String[]{
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
  protected AbstractOutlierDetector[] getRegressionSetups() {
    MinMax[]	result;

    result = new MinMax[3];

    result[0] = new MinMax();
    result[0].setField(new Field(Instance.REPORT_CLASS, DataType.NUMERIC));

    result[1] = new MinMax();
    result[1].setField(new Field(Instance.REPORT_CLASS, DataType.NUMERIC));
    result[1].setMin(0.0);
    result[1].setMax(100.0);

    result[2] = new MinMax();
    result[2].setField(new Field("blah", DataType.UNKNOWN));

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(MinMaxTest.class);
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
