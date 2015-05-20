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
 * AbstractColumnFinder.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.columnfinder;

import java.util.HashSet;

import weka.core.Instances;
import adams.core.ClassLister;
import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;

/**
 * Ancestor for classes that find columns of interest in datasets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractColumnFinder
  extends AbstractOptionHandler
  implements ColumnFinder, ShallowCopySupporter<ColumnFinder> {

  /** for serialization. */
  private static final long serialVersionUID = 3871603719188736704L;

  /**
   * Checks the data.
   * <br><br>
   * Default implementation only checks whether we have any data at all.
   * 
   * @param data	the data to check
   */
  protected void check(Instances data) {
    if (data == null)
      throw new IllegalArgumentException("No data provided!");
  }
  
  /**
   * Returns the columns of interest in the dataset.
   * 
   * @param data	the dataset to inspect
   * @return		the columns of interest
   */
  protected abstract int[] doFindColumns(Instances data);
  
  /**
   * Returns the columns of interest in the dataset.
   * 
   * @param data	the dataset to inspect
   * @return		the columns of interest
   */
  @Override
  public int[] findColumns(Instances data) {
    check(data);
    return doFindColumns(data);
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @thcolumns ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine((ColumnFinder) o));
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
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
  public ColumnFinder shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public ColumnFinder shallowCopy(boolean expand) {
    return (ColumnFinder) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of column finders.
   *
   * @return		the column finder classnames
   */
  public static String[] getColumnFinders() {
    return ClassLister.getSingleton().getClassnames(AbstractColumnFinder.class);
  }

  /**
   * Instantiates the column finder with the given options.
   *
   * @param classname	the classname of the column finder to instantiate
   * @param options	the options for the column finder
   * @return		the instantiated column finder or null if an error occurred
   */
  public static ColumnFinder forName(String classname, String[] options) {
    ColumnFinder	result;

    try {
      result = (ColumnFinder) OptionUtils.forName(ColumnFinder.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the column finder from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			column finder to instantiate
   * @return		the instantiated column finder
   * 			or null if an error occurred
   */
  public static ColumnFinder forCommandLine(String cmdline) {
    return (ColumnFinder) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
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
}
