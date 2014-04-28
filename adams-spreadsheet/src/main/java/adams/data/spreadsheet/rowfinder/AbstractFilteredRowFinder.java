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
 * AbstractFilteredRowFinder.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowfinder;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for row finders that pre-filter the rows.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFilteredRowFinder
  extends AbstractTrainableRowFinder {

  /** for serialization. */
  private static final long serialVersionUID = 7779829526710745924L;
  
  /** the RowFinder to apply. */
  protected RowFinder m_RowFinder;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "row-finder", "rowFinder",
	    new NullFinder());
  }

  /**
   * Sets the row finder to use.
   *
   * @param value	the row finder
   */
  public void setRowFinder(RowFinder value) {
    m_RowFinder = value;
    reset();
  }

  /**
   * Returns the row finder in use.
   *
   * @return		the row finder
   */
  public RowFinder getRowFinder() {
    return m_RowFinder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String rowFinderTipText() {
    return "The row finder to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "rowFinder", m_RowFinder, "row finder: ");
  }

  /**
   * Performs the actual training of the row finder with the specified spreadsheet.
   * 
   * @param data	the training data
   * @return		true if successfully trained
   */
  @Override
  protected boolean doTrainRowFinder(SpreadSheet data) {
    boolean	result;
    
    result = true;
    
    if (m_RowFinder instanceof TrainableRowFinder)
      result = ((TrainableRowFinder) m_RowFinder).trainRowFinder(data);
    
    return result;
  }
}
