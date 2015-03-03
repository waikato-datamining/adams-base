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
 * DistanceFromConstant.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.rowscore;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Calculates the Euclidean distance to a timeseries that is basically a 'flat-liner' with a constant value.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-constant &lt;double&gt; (property: constant)
 * &nbsp;&nbsp;&nbsp;The constant to use for calculating the distance.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DistanceFromConstant
  extends AbstractRowScore {

  /** for serialization. */
  private static final long serialVersionUID = -13137285273610739L;
  
  /** the 'flat liner' value. */
  protected double m_Constant;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Calculates the Euclidean distance to a timeseries that is basically "
	+ "a 'flat-liner' with a constant value.\n"
	+ "Only non-missing, numeric cells are included in the calculation.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "constant", "constant",
	    0.0);
  }

  /**
   * Sets the constant to use.
   *
   * @param value	the constant
   */
  public void setConstant(double value) {
    m_Constant = value;
    reset();
  }

  /**
   * Returns the constant in use.
   *
   * @return		the constant
   */
  public double getConstant() {
    return m_Constant;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String constantTipText() {
    return "The constant to use for calculating the distance.";
  }

  /**
   * Returns how many score values will get generated.
   * 
   * @return		the number of scores
   */
  @Override
  public int getNumScores() {
    return 1;
  }

  /**
   * Performs the actual calculation of the row score.
   *
   * @param sheet	the spreadsheet to generate the score for
   * @param rowIndex	the row index
   * @return		the generated score, null in case of an error
   */
  @Override
  protected Double[] doCalculateScore(SpreadSheet sheet, int rowIndex) {
    Double[]	result;
    Row		row;
    
    result = new Double[]{0.0};
    row    = sheet.getRow(rowIndex);
    for (Cell cell: row.cells()) {
      if (cell.isNumeric())
	result[0] += Math.pow(cell.toDouble() - m_Constant, 2);
    }
    result[0] = Math.sqrt(result[0]);
    
    return result;
  }
}
