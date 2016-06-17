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
 * CorrespondingColumnTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.cellfinder;

import adams.core.PositionType;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.rowfinder.ByNumericValue;
import adams.data.spreadsheet.rowfinder.RowFinder;

/**
 * Tests the CorrespondingColumn cell locator.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CorrespondingColumnTest
  extends AbstractCellFinderTestCase {

  /**
   * Initializes the test.
   * 
   * @param name	the name of the test
   */
  public CorrespondingColumnTest(String name) {
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
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected CellFinder[] getRegressionSetups() {
    CorrespondingColumn[]	result;
    RowFinder			finder;
    CellFinder			cfinder;
    
    result    = new CorrespondingColumn[2];
    
    result[0] = new CorrespondingColumn();
    
    result[1] = new CorrespondingColumn();
    finder = new ByNumericValue();
    ((ByNumericValue) finder).setAttributeIndex(new SpreadSheetColumnIndex("first"));
    ((ByNumericValue) finder).setMinimum(10.0);
    ((ByNumericValue) finder).setMaximum(20.0);
    cfinder = new RowFinderRange();
    ((RowFinderRange) cfinder).setColumns(new SpreadSheetColumnRange("first"));
    ((RowFinderRange) cfinder).setRowFinder(finder);
    result[1].setFinder(cfinder);
    result[1].setCorrespondingColumn(8);
    result[1].setCorrespondingPosition(PositionType.ABSOLUTE);
    
    return result;
  }

}
