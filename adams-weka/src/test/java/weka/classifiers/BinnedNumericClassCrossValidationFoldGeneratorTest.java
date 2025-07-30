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

/*
 * BinnedNumericClassCrossValidationFoldGeneratorTest.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package weka.classifiers;

import adams.data.binning.algorithm.FrequencyBinning;
import adams.data.binning.algorithm.ManualBinning;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;
import weka.core.Instances;

/**
 * Tests weka.classifiers.BinnedNumericClassCrossValidationFoldGenerator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BinnedNumericClassCrossValidationFoldGeneratorTest
  extends AbstractSplitGeneratorTestCase {

  /**
   * Initializes the test.
   *
   * @param name 	the name of the test
   */
  public BinnedNumericClassCrossValidationFoldGeneratorTest(String name) {
    super(name);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractSplitGenerator[] getRegressionSetups() {
    BinnedNumericClassCrossValidationFoldGenerator[]	result;
    Instances						bodyfat;

    bodyfat = load("bodyfat.arff", 50);

    result    = new BinnedNumericClassCrossValidationFoldGenerator[4];
    result[0] = new BinnedNumericClassCrossValidationFoldGenerator(bodyfat, new ManualBinning(), 3, 42, true);
    result[1] = new BinnedNumericClassCrossValidationFoldGenerator(bodyfat, new FrequencyBinning(), 3, 42, true);
    result[2] = new BinnedNumericClassCrossValidationFoldGenerator(bodyfat, new FrequencyBinning(), 3, 42, false);
    result[3] = new BinnedNumericClassCrossValidationFoldGenerator(bodyfat, new FrequencyBinning(), 3, 42, false, false, null);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(BinnedNumericClassCrossValidationFoldGeneratorTest.class);
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
