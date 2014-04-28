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
 * WekaPredictionContainerToSpreadSheetTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.data.conversion.WekaPredictionContainerToSpreadSheet.Sorting;
import adams.flow.container.WekaPredictionContainer;

/**
 * Tests the WekaPredictionContainerToSpreadSheet conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaPredictionContainerToSpreadSheetTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public WekaPredictionContainerToSpreadSheetTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  protected Object[] getRegressionInput() {
    WekaPredictionContainer[]	result;

    result    = new WekaPredictionContainer[3];
    result[0] = new WekaPredictionContainer(null, 7.8, new double[0]);
    result[1] = new WekaPredictionContainer(null, 1.0, new double[]{0.1, 0.7, 0.1, 0.1});
    result[2] = new WekaPredictionContainer(null, 1.0, new double[]{0.15, 0.7, 0.05, 0.1});

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected Conversion[] getRegressionSetups() {
    WekaPredictionContainerToSpreadSheet[]	result;

    result = new WekaPredictionContainerToSpreadSheet[6];
    result[0] = new WekaPredictionContainerToSpreadSheet();
    result[1] = new WekaPredictionContainerToSpreadSheet();
    result[1].setAddClassification(true);
    result[2] = new WekaPredictionContainerToSpreadSheet();
    result[2].setAddDistribution(true);
    result[3] = new WekaPredictionContainerToSpreadSheet();
    result[3].setAddDistribution(true);
    result[3].setDistributionFormat("Distribution-" + WekaPredictionContainerToSpreadSheet.PLACEHOLDER_LABEL);
    result[4] = new WekaPredictionContainerToSpreadSheet();
    result[4].setAddDistribution(true);
    result[4].setDistributionFormat("Distribution-" + WekaPredictionContainerToSpreadSheet.PLACEHOLDER_LABEL);
    result[4].setDistributionSorting(Sorting.ASCENDING);
    result[5] = new WekaPredictionContainerToSpreadSheet();
    result[5].setAddDistribution(true);
    result[5].setDistributionFormat("Distribution-" + WekaPredictionContainerToSpreadSheet.PLACEHOLDER_LABEL);
    result[5].setDistributionSorting(Sorting.DESCENDING);

    return result;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }
}
