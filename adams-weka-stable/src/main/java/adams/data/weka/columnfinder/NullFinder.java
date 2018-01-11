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
 * NullFinder.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.columnfinder;

import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Dummy finder, does not find any columns.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NullFinder
  extends AbstractColumnFinder {

  /** for serialization. */
  private static final long serialVersionUID = -7945692540826937829L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy finder, does not find any columns.";
  }
  
  /**
   * Returns the columns of interest in the dataset.
   * 
   * @param data	the dataset to inspect
   * @return		the columns of interest
   */
  @Override
  protected int[] doFindColumns(Instances data) {
    return new int[0];
  }
}
