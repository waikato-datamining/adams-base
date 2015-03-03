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
 * AbstractCellFinder.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.cellfinder;

import java.util.Iterator;

import adams.core.ClassLister;
import adams.core.QuickInfoSupporter;
import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for schemes that locate cells of interest in a spreadsheet.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCellFinder
  extends AbstractOptionHandler 
  implements ShallowCopySupporter<AbstractCellFinder>, QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 3647241823201101006L;

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
   * Checks whether the spreadsheet can be processed.
   * <p/>
   * Default implementation only checks whether a spreadsheet was provided.
   * 
   * @param sheet	the spreadsheet to check
   */
  protected void check(SpreadSheet sheet) {
    if (sheet == null)
      throw new IllegalArgumentException("No spreadsheet provided!");
  }
  
  /**
   * Performs the actual locating.
   * 
   * @param sheet	the sheet to locate the cells in
   * @return		the iterator over the locations
   */
  protected abstract Iterator<CellLocation> doFindCells(SpreadSheet sheet);
  
  /**
   * Locates the cells in the spreadsheet.
   * 
   * @param sheet	the sheet to locate the cells in
   * @return		the iterator over the locations
   */
  public Iterator<CellLocation> findCells(SpreadSheet sheet) {
    check(sheet);
    return doFindCells(sheet);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractCellFinder shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractCellFinder shallowCopy(boolean expand) {
    return (AbstractCellFinder) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of cell finders.
   *
   * @return		the cell finder classnames
   */
  public static String[] getCellFinders() {
    return ClassLister.getSingleton().getClassnames(AbstractCellFinder.class);
  }

  /**
   * Instantiates the cell finder with the given options.
   *
   * @param classname	the classname of the cell finder to instantiate
   * @param options	the options for the cell finder
   * @return		the instantiated cell finder or null if an error occurred
   */
  public static AbstractCellFinder forName(String classname, String[] options) {
    AbstractCellFinder	result;

    try {
      result = (AbstractCellFinder) OptionUtils.forName(AbstractCellFinder.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the cell finder from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			cell finder to instantiate
   * @return		the instantiated cell finder
   * 			or null if an error occurred
   */
  public static AbstractCellFinder forCommandLine(String cmdline) {
    return (AbstractCellFinder) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
