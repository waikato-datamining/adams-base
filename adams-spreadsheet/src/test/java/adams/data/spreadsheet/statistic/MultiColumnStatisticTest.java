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
 * MultiColumnStatisticTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.statistic;


/**
 * Tests the MultiColumnStatistic statistic generator.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiColumnStatisticTest
  extends AbstractColumnStatisticTestCase {

  /**
   * Initializes the test.
   * 
   * @param name	the name of the test
   */
  public MultiColumnStatisticTest(String name) {
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
	"labor.csv",
	"labor.csv",
	"labor.csv",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractColumnStatistic[] getRegressionSetups() {
    MultiColumnStatistic[]	result;
    
    result    = new MultiColumnStatistic[3];
    result[0] = new MultiColumnStatistic();
    result[1] = new MultiColumnStatistic();
    result[1].setStatistics(new AbstractColumnStatistic[]{
	new Mean(),
	new StandardDeviation(),
	new Min(),
	new Max(),
	new Median(),
	new IQR(),
	new Sum(),
	new SignalToNoiseRatio(),
	new Unique(),
	new Distinct(),
    });
    result[2] = new MultiColumnStatistic();
    LabelCounts lc = new LabelCounts();
    lc.setPrefix("Label: ");
    result[2].setStatistics(new AbstractColumnStatistic[]{
	new Missing(),
	lc,
	new Unique(),
	new Distinct(),
    });
    
    return result;
  }

  @Override
  protected int[] getRegressionColumns() {
    return new int[]{
	0,
	0,
	11,
    };
  }
}
