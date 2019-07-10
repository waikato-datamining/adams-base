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
 * CrossValidationTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.preparefilebaseddataset;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests CrossValidation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CrossValidationTest
  extends AbstractFileBasedDatasetPreparationTestCase<String[]> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public CrossValidationTest(String name) {
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
      "00.txt",
      "01.txt",
      "02.txt",
      "03.txt",
      "04.txt",
      "05.txt",
      "06.txt",
      "07.txt",
      "08.txt",
      "09.txt",
      "10.txt",
      "11.txt",
      "12.txt",
      "13.txt",
      "14.txt",
      "15.txt",
      "16.txt",
      "17.txt",
      "18.txt",
      "19.txt",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractFileBasedDatasetPreparation<String[]>[] getRegressionSetups() {
    CrossValidation[]	result;

    result = new CrossValidation[3];
    result[0] = new CrossValidation();
    result[1] = new CrossValidation();
    result[1].setRandomize(false);
    result[2] = new CrossValidation();
    result[2].setNumFolds(3);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(CrossValidationTest.class);
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
