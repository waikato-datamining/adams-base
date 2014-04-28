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
 * AbstractRowFinder.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowfinder;

import java.util.HashSet;

import adams.core.ClassLister;
import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for classes that find rows of interest in datasets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRowFinder
  extends AbstractOptionHandler
  implements RowFinder, ShallowCopySupporter<RowFinder> {

  /** for serialization. */
  private static final long serialVersionUID = 3871603719188736704L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Checks the data.
   * <p/>
   * Default implementation only checks whether we have any data at all.
   * 
   * @param data	the data to check
   */
  protected void check(SpreadSheet data) {
    if (data == null)
      throw new IllegalArgumentException("No data provided!");
  }
  
  /**
   * Returns the rows of interest in the spreadsheet.
   * 
   * @param data	the spreadsheet to inspect
   * @return		the rows of interest
   */
  protected abstract int[] doFindRows(SpreadSheet data);
  
  /**
   * Returns the rows of interest in the spreadsheet.
   * 
   * @param data	the spreadsheet to inspect
   * @return		the rows of interest
   */
  @Override
  public int[] findRows(SpreadSheet data) {
    check(data);
    return doFindRows(data);
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <p/>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine((RowFinder) o));
  }

  /**
   * Returns whether the two objects are the same.
   * <p/>
   * Only compares the commandlines of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public RowFinder shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public RowFinder shallowCopy(boolean expand) {
    return (RowFinder) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of row finders.
   *
   * @return		the row finder classnames
   */
  public static String[] getRowFinders() {
    return ClassLister.getSingleton().getClassnames(RowFinder.class);
  }

  /**
   * Instantiates the row finder with the given options.
   *
   * @param classname	the classname of the row finder to instantiate
   * @param options	the options for the row finder
   * @return		the instantiated row finder or null if an error occurred
   */
  public static RowFinder forName(String classname, String[] options) {
    RowFinder	result;

    try {
      result = (RowFinder) OptionUtils.forName(RowFinder.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the row finder from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			row finder to instantiate
   * @return		the instantiated row finder
   * 			or null if an error occurred
   */
  public static RowFinder forCommandLine(String cmdline) {
    return (RowFinder) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }

  /**
   * Turns the array into a hashset.
   * 
   * @param indices	the indices to turn into hashset
   * @return		the generated hashset
   */
  public static HashSet<Integer> arrayToHashSet(int[] indices) {
    HashSet<Integer>	result;

    result = new HashSet<Integer>();
    for (int index: indices)
      result.add(index);

    return result;
  }
  
  /**
   * Filters the rows of the spreadsheet with the specified row finder.
   * 
   * @param input	the spreadsheet to filter
   * @param finder	the row finder to use
   * @return		the generated output
   */
  public static SpreadSheet filter(SpreadSheet input, RowFinder finder) {
    SpreadSheet		result;
    int[]		indices;
    Row			rowIn;
    Row			rowOut;
    int			n;

    indices = finder.findRows(input);
    result  = input.getHeader();
    
    // data
    for (n = 0; n < indices.length; n++) {
      rowIn  = input.getRow(indices[n]);
      rowOut = result.addRow();
      rowOut.assign(rowIn);
    }
    
    return result;
  }
}
