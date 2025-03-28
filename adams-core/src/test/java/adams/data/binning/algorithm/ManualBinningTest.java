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
 * ManualBinningTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.algorithm;

import adams.data.binning.Binnable;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Tests ManualBinning.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ManualBinningTest
  extends AbstractBinningAlgorithmTestCase<Integer> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public ManualBinningTest(String name) {
    super(name);
  }

  /**
   * Returns the data to use in the regression test.
   *
   * @return		the data
   */
  @Override
  protected List<Binnable<Integer>> getRegressionInputData() {
    List<Binnable<Integer>>	result;
    Random			rnd;

    result = new ArrayList<>();
    rnd    = new Random(1);
    for (int i = 0; i < 100; i++)
      result.add(new Binnable<>(i, rnd.nextDouble()));

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected BinningAlgorithm[] getRegressionSetups() {
    ManualBinning[]	result;

    result = new ManualBinning[3];
    result[0] = new ManualBinning();
    result[1] = new ManualBinning();
    result[1].setNumBins(20);
    result[2] = new ManualBinning();
    result[2].setUseFixedMinMax(true);
    result[2].setManualMin(0.0);
    result[2].setManualMax(2.0);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(ManualBinningTest.class);
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
