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
 * RowFinderRangeTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.cellfinder;

import adams.core.Range;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.rowfinder.ByIndex;
import adams.data.spreadsheet.rowfinder.ByNumericValue;
import adams.data.spreadsheet.rowfinder.RowFinder;

/**
 * Tests the RowFinderRange cell locator.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RowFinderRangeTest
  extends AbstractCellFinderTestCase {

  /**
   * Initializes the test.
   * 
   * @param name	the name of the test
   */
  public RowFinderRangeTest(String name) {
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
	"bolts.csv",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractCellFinder[] getRegressionSetups() {
    RowFinderRange[]	result;
    RowFinder		finder;
    
    result    = new RowFinderRange[4];
    
    result[0] = new RowFinderRange();
    
    result[1] = new RowFinderRange();
    finder = new ByNumericValue();
    ((ByNumericValue) finder).setMinimum(10.0);
    ((ByNumericValue) finder).setMaximum(20.0);
    result[1].setRowFinder(finder);
    result[1].setColumns(new SpreadSheetColumnRange("last"));

    result[2] = new RowFinderRange();
    finder = new ByIndex();
    ((ByIndex) finder).setRows(new Range("3-4"));
    result[2].setRowFinder(finder);
    result[2].setColumns(new SpreadSheetColumnRange(Range.ALL));
    
    result[3] = new RowFinderRange();
    finder = new ByIndex();
    ((ByIndex) finder).setRows(new Range("3-4"));
    result[3].setRowFinder(finder);
    result[3].setColumns(new SpreadSheetColumnRange("4-6"));
    
    return result;
  }

}
