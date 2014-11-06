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
 * EqualWidthBinsTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowscore;

/**
 * Tests the EqualWidthBins row score.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9765 $
 */
public class EqualWidthBinsTest
  extends AbstractRowScoreTestCase {

  /**
   * Initializes the test.
   * 
   * @param name	the name of the test
   */
  public EqualWidthBinsTest(String name) {
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
	"house_16H.csv",
	"house_16H.csv",
	"house_16H.csv",
	"house_16H.csv",
	"house_16H.csv",
	"house_16H.csv",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractRowScore[] getRegressionSetups() {
    EqualWidthBins[]	result;
    
    result    = new EqualWidthBins[6];
    result[0] = new EqualWidthBins();
    result[1] = new EqualWidthBins();
    result[2] = new EqualWidthBins();
    result[2].setNumBins(4);
    result[3] = new EqualWidthBins();
    result[3].setNumBins(4);
    result[4] = new EqualWidthBins();
    result[4].setNumBins(5);
    result[5] = new EqualWidthBins();
    result[5].setNumBins(5);

    return result;
  }

  @Override
  protected int[] getRegressionRows() {
    return new int[]{
	0,
	1,
	0,
	1,
	0,
	1,
    };
  }
}
