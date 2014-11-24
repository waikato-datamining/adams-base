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
 * DownSample.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowfinder;

import gnu.trove.list.array.TIntArrayList;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Returns the indices of every n-th row.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8096 $
 */
public class DownSample
  extends AbstractRowFinder {

  /** for serialization. */
  private static final long serialVersionUID = 2989233908194930918L;

  /** the n-th row to use. */
  protected int m_NthRow;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the indices of every n-th row.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "nth", "nthRow",
	    1);
  }

  /**
   * Sets the nth row setting.
   *
   * @param value 	the nth row to use
   */
  public void setNthRow(int value) {
    m_NthRow = value;
    reset();
  }

  /**
   * Returns the nth row setting.
   *
   * @return 		the nth row
   */
  public int getNthRow() {
    return m_NthRow;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nthRowTipText() {
    return "Only every n-th row will be output.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "nthRow", m_NthRow, "nth: ");
  }

  /**
   * Returns the rows of interest in the spreadsheet.
   * 
   * @param data	the spreadsheet to inspect
   * @return		the rows of interest
   */
  @Override
  protected int[] doFindRows(SpreadSheet data) {
    TIntArrayList	result;
    int			i;
    
    result = new TIntArrayList();
    i      = 0;
    while (i < data.getRowCount()) {
      result.add(i);
      i += m_NthRow;
    }
    
    return result.toArray();
  }
}
