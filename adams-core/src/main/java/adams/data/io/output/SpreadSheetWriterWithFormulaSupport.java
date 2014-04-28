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
 * SpreadSheetWriterWithFormulaSupport.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

/**
 * Interface for spreadsheet writers that support output of formulas.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface SpreadSheetWriterWithFormulaSupport {
  
  /**
   * Sets whether to output the cell content as displayed, ie, no formulas
   * but the result of formulas.
   * 
   * @param value	true if to output as displayed
   */
  public void setOutputAsDisplayed(boolean value);
  
  /**
   * Returns whether to output the cell content as displayed, ie, no formulas
   * but the result of formulas.
   * 
   * @return		true if to output as displayed
   */
  public boolean getOutputAsDisplayed();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String outputAsDisplayedTipText();
}
