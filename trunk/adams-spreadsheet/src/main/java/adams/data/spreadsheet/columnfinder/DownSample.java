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
package adams.data.spreadsheet.columnfinder;

import gnu.trove.list.array.TIntArrayList;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Returns the indices of every n-th column.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8096 $
 */
public class DownSample
  extends AbstractColumnFinder {

  /** for serialization. */
  private static final long serialVersionUID = 2989233908194930918L;

  /** the n-th column to use. */
  protected int m_NthColumn;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the indices of every n-th column.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "nth", "nthColumn",
	    1);
  }

  /**
   * Sets the nth column setting.
   *
   * @param value 	the nth column to use
   */
  public void setNthColumn(int value) {
    m_NthColumn = value;
    reset();
  }

  /**
   * Returns the nth column setting.
   *
   * @return 		the nth column
   */
  public int getNthColumn() {
    return m_NthColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nthColumnTipText() {
    return "Only every n-th column will be output.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "nthColumn", m_NthColumn, "nth: ");
  }

  /**
   * Returns the columns of interest in the spreadsheet.
   * 
   * @param data	the spreadsheet to inspect
   * @return		the columns of interest
   */
  @Override
  protected int[] doFindColumns(SpreadSheet data) {
    TIntArrayList	result;
    int			i;
    
    result = new TIntArrayList();
    i      = 0;
    while (i < data.getColumnCount()) {
      result.add(i);
      i += m_NthColumn;
    }
    
    return result.toArray();
  }
}
