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
 * AbstractFilteredColumnFinder.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.columnfinder;

import weka.core.Instances;

/**
 * Ancestor for column finders that pre-filter the columns.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFilteredColumnFinder
  extends AbstractTrainableColumnFinder {

  /** for serialization. */
  private static final long serialVersionUID = 8526492076204492557L;
  
  /** the ColumnFinder to apply. */
  protected ColumnFinder m_ColumnFinder;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "column-finder", "columnFinder",
	    new NullFinder());
  }

  /**
   * Sets the column finder to use.
   *
   * @param value	the column finder
   */
  public void setColumnFinder(ColumnFinder value) {
    m_ColumnFinder = value;
    reset();
  }

  /**
   * Returns the column finder in use.
   *
   * @return		the column finder
   */
  public ColumnFinder getColumnFinder() {
    return m_ColumnFinder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String columnFinderTipText() {
    return "The column finder to use.";
  }

  /**
   * Performs the actual training of the column finder with the specified dataset.
   * 
   * @param data	the training data
   * @return		true if successfully trained
   */
  @Override
  protected boolean doTrainColumnFinder(Instances data) {
    boolean	result;
    
    result = true;
    
    if (m_ColumnFinder instanceof TrainableColumnFinder)
      result = ((TrainableColumnFinder) m_ColumnFinder).trainColumnFinder(data);
    
    return result;
  }
}
