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
 * CellTypeRangeTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.cellfinder;

import adams.core.Range;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.SpreadSheetColumnRange;

/**
 * Tests the CellTypeRange cell locator.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CellTypeRangeTest
  extends AbstractCellFinderTestCase {

  /**
   * Initializes the test.
   * 
   * @param name	the name of the test
   */
  public CellTypeRangeTest(String name) {
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
    CellTypeRange[]	result;
    
    result    = new CellTypeRange[4];
    result[0] = new CellTypeRange();
    result[1] = new CellTypeRange();
    result[1].setColumns(new SpreadSheetColumnRange(Range.ALL));
    result[1].setRows(new Range("2"));
    result[1].setType(ContentType.LONG);
    result[2] = new CellTypeRange();
    result[2].setColumns(new SpreadSheetColumnRange("4"));
    result[2].setRows(new Range(Range.ALL));
    result[2].setType(ContentType.LONG);
    result[3] = new CellTypeRange();
    result[3].setColumns(new SpreadSheetColumnRange("last"));
    result[3].setRows(new Range("20-30"));
    result[3].setType(ContentType.LONG);
    
    return result;
  }

}
