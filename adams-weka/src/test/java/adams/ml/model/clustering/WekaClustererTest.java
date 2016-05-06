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
 * WekaClustererTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.model.clustering;

import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;
import weka.clusterers.SimpleKMeans;

/**
 * Tests the WekaClusterer class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClustererTest
  extends AbstractClustererTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public WekaClustererTest(String name) {
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
      "iris_no_class.csv",
    };
  }

  /**
   * Returns the readers for the input data files to use
   * in the regression test.
   *
   * @return		the readers
   */
  @Override
  protected SpreadSheetReader[] getRegressionInputReaders() {
    return new SpreadSheetReader[]{
      new CsvSpreadSheetReader(),
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Clusterer[] getRegressionSetups() {
    WekaClusterer[]	result;
    SimpleKMeans	simple;

    result    = new WekaClusterer[1];
    result[0] = new WekaClusterer();
    simple    = new SimpleKMeans();
    try {
      simple.setNumClusters(3);
    }
    catch (Exception e) {
      fail("Failed to set the number of clusters!");
    }
    result[0].setClusterer(simple);

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaClustererTest.class);
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
