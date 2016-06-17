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
 * SingleCellTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.cellfinder;

import adams.core.Index;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

/**
 * Tests the SingleCell cell locator.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SingleCellTest
  extends AbstractCellFinderTestCase {

  /**
   * Initializes the test.
   * 
   * @param name	the name of the test
   */
  public SingleCellTest(String name) {
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
	"bolts.csv",
	"bolts.csv",
	"bolts.csv",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected CellFinder[] getRegressionSetups() {
    SingleCell[]	result;
    
    result    = new SingleCell[3];
    result[0] = new SingleCell();
    result[1] = new SingleCell();
    result[1].setColumn(new SpreadSheetColumnIndex(Index.LAST));
    result[1].setRow(new Index(Index.LAST));
    result[2] = new SingleCell();
    result[2].setColumn(new SpreadSheetColumnIndex("2"));
    result[2].setRow(new Index("10"));
    
    return result;
  }

}
