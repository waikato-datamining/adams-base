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
 * TrainValidateTestSplitTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.preparefilebaseddataset;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests TrainValidateTestSplit.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TrainValidateTestSplitTest
  extends AbstractFileBasedDatasetPreparationTestCase<String[]> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public TrainValidateTestSplitTest(String name) {
    super(name);
  }

  /**
   * Returns the data to use in the regression test.
   *
   * @return		the data
   */
  @Override
  protected String[] getRegressionInputData() {
    return new String[]{
      "1.txt",
      "2.txt",
      "3.txt",
      "4.txt",
      "5.txt",
      "6.txt",
      "7.txt",
      "8.txt",
      "9.txt",
      "10.txt",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractFileBasedDatasetPreparation<String[]>[] getRegressionSetups() {
    TrainValidateTestSplit[]	result;

    result = new TrainValidateTestSplit[3];
    result[0] = new TrainValidateTestSplit();
    result[1] = new TrainValidateTestSplit();
    result[1].setPreserveOrder(true);
    result[2] = new TrainValidateTestSplit();
    result[2].setTrainPercentage(0.3);
    result[2].setValidatePercentage(0.3);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(TrainValidateTestSplitTest.class);
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
