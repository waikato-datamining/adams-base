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
 * WekaClassifierTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.model.classification;

import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.env.Environment;
import adams.ml.data.Dataset;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the WekaClassifier class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClassifierTest
  extends AbstractClassifierTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public WekaClassifierTest(String name) {
    super(name);
  }

  /**
   * Returns a typical setup.
   *
   * @return		the setup
   */
  protected Classifier getTypicalSetup() {
    return new WekaClassifier();
  }

  /**
   * Returns a typical dataset.
   *
   * @return		the dataset
   */
  protected Dataset getTypicalDataset() {
    return load("iris.csv",  new CsvSpreadSheetReader(), "class");
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
      "iris.csv",
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
   * Returns the class attributes names for the input data files to use
   * in the regression test.
   *
   * @return		the attribute names
   */
  @Override
  protected String[] getRegressionInputClasses() {
    return new String[]{
      "class",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Classifier[] getRegressionSetups() {
    return new Classifier[]{
      new WekaClassifier(),
    };
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaClassifierTest.class);
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
