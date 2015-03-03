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
 * FilteredColumnFinder.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.columnfinder;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Filters the data first before applying the actual finder to locate the 
 * columns.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FilteredColumnFinder
  extends AbstractColumnFinder {

  /** for serialization. */
  private static final long serialVersionUID = 2477340443897443265L;

  /** the ColumnFinder to filter the data with first. */
  protected ColumnFinder m_Filter;

  /** the ColumnFinder to use on the filtered data. */
  protected ColumnFinder m_Finder;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Applies the 'filter' column-finder first to the data before "
	+ "applying the actual 'finder'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "filter",
	    new ByIndex());

    m_OptionManager.add(
	    "finder", "Finder",
	    new ByName());
  }

  /**
   * Sets the column finder to filter the data with first.
   *
   * @param value	the column finder
   */
  public void setFilter(ColumnFinder value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the column finder to filter the data with first.
   *
   * @return		the column finder
   */
  public ColumnFinder getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String filterTipText() {
    return "The column finder to filter the data with first.";
  }

  /**
   * Sets the column finder to use on the filtered data.
   *
   * @param value	the column finder
   */
  public void setFinder(ColumnFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the column finder to use on the filtered data.
   *
   * @return		the column finder
   */
  public ColumnFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String finderTipText() {
    return "The column finder to use on the filtered data.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "filder", m_Filter, "filter: ");
    result += QuickInfoHelper.toString(this, "finder", m_Finder, ", finder: ");
    
    return result;
  }

  /**
   * Returns the columns of interest in the spreadsheet.
   * 
   * @param data	the spreadsheet to inspect
   * @return		the columns of interest
   */
  @Override
  protected int[] doFindColumns(SpreadSheet data) {
    int[]	result;
    SpreadSheet	filteredData;
    int[]	filteredIndices;
    int[]	found;
    int		i;
    
    // filter data
    filteredIndices = m_Filter.findColumns(data);
    filteredData    = AbstractColumnFinder.filter(data, filteredIndices);
    
    // select columns
    found = m_Finder.findColumns(filteredData);
    
    // translate columns into original space
    result = new int[found.length];
    for (i = 0; i < found.length; i++)
      result[i] = filteredIndices[found[i]];
    
    return result;
  }
}
