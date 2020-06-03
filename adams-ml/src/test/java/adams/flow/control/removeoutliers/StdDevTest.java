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
 * StdDevTest.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.removeoutliers;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the StdDev outlier detector.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class StdDevTest
  extends AbstractOutlierDetectorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public StdDevTest(String name) {
    super(name);
  }

  /**
   * Returns the spreadsheets to use in the regression test.
   *
   * @return		the sheets
   */
  @Override
  protected SpreadSheet[] getRegressionSpreadSheets() {
    return new SpreadSheet[]{
      load("bolts_predictions.csv"),
      load("bolts_predictions.csv"),
      load("bolts_predictions.csv"),
    };
  }

  /**
   * Returns the "actual" columns to use in the regression test.
   *
   * @return		the columns
   */
  @Override
  protected SpreadSheetColumnIndex[] getRegressionActualCols() {
    return new SpreadSheetColumnIndex[]{
      new SpreadSheetColumnIndex("Actual"),
      new SpreadSheetColumnIndex("Actual"),
      new SpreadSheetColumnIndex("Actual"),
    };
  }

  /**
   * Returns the "predicted" columns to use in the regression test.
   *
   * @return		the columns
   */
  @Override
  protected SpreadSheetColumnIndex[] getRegressionPredictedCols() {
    return new SpreadSheetColumnIndex[]{
      new SpreadSheetColumnIndex("Predicted"),
      new SpreadSheetColumnIndex("Predicted"),
      new SpreadSheetColumnIndex("Predicted"),
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractOutlierDetector[] getRegressionSetups() {
    StdDev[]	result;

    result = new StdDev[3];
    result[0] = new StdDev();
    result[1] = new StdDev();
    result[1].setUseRelative(true);
    result[2] = new StdDev();
    result[2].setFactor(1.5);

    return result;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(StdDevTest.class);
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
