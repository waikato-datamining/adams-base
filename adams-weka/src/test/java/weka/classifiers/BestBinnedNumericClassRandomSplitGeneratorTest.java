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
 * BestBinnedNumericClassRandomSplitGeneratorTest.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package weka.classifiers;

import adams.core.ObjectCopyHelper;
import adams.data.binning.algorithm.BinningAlgorithm;
import adams.data.binning.algorithm.DensityBinning;
import adams.data.binning.algorithm.FrequencyBinning;
import adams.data.binning.algorithm.NoBinning;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;
import weka.core.Instances;

/**
 * Tests weka.classifiers.BestBinnedNumericClassRandomSplitGenerator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BestBinnedNumericClassRandomSplitGeneratorTest
  extends AbstractSplitGeneratorTestCase {

  /**
   * Initializes the test.
   *
   * @param name 	the name of the test
   */
  public BestBinnedNumericClassRandomSplitGeneratorTest(String name) {
    super(name);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractSplitGenerator[] getRegressionSetups() {
    BestBinnedNumericClassRandomSplitGenerator[]	result;
    Instances						bodyfat;
    BinningAlgorithm[]					algorithms;

    bodyfat = load("bodyfat.arff", 50);
    algorithms = new BinningAlgorithm[]{
      new NoBinning(),
      new FrequencyBinning(),
      new DensityBinning(),
    };

    result    = new BestBinnedNumericClassRandomSplitGenerator[3];
    result[0] = new BestBinnedNumericClassRandomSplitGenerator(bodyfat, ObjectCopyHelper.copyObjects(algorithms), 42, 0.66);
    result[1] = new BestBinnedNumericClassRandomSplitGenerator(bodyfat, ObjectCopyHelper.copyObjects(algorithms), 0.33);
    result[2] = new BestBinnedNumericClassRandomSplitGenerator(bodyfat, ObjectCopyHelper.copyObjects(algorithms), 42, 0.33, true);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(BestBinnedNumericClassRandomSplitGeneratorTest.class);
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
