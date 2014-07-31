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
 * HistogramTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.data.jai.features;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.adams.features.AbstractBufferedImageFeatureGenerator;
import adams.data.jai.features.Histogram.HistogramType;
import adams.env.Environment;

/**
 * Test class for the Histogram flattener. Run from the command line with: <p/>
 * java adams.data.jai.flattener.HistogramTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HistogramTest
  extends AbstractJAIFeatureGeneratorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public HistogramTest(String name) {
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
	"adams_logo.png",
	"adams_logo.png"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBufferedImageFeatureGenerator[] getRegressionSetups() {
    Histogram[]	result;

    result = new Histogram[2];

    result[0] = new Histogram();
    result[0].setHistogramType(HistogramType.RGB);

    result[1] = new Histogram();
    result[1].setHistogramType(HistogramType.EIGHT_BIT);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(HistogramTest.class);
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
