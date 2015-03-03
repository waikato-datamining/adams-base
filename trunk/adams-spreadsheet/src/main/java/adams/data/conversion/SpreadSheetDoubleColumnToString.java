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
 * SpreadSheetDoubleColumnToString.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.Utils;
import adams.data.spreadsheet.Cell;

/**
 <!-- globalinfo-start -->
 * Converts the specified spreadsheet double column to string.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-column &lt;adams.core.Index&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The double column to convert to string; An index is a number starting with 
 * &nbsp;&nbsp;&nbsp;1; the following placeholders can be used as well: first, second, third, 
 * &nbsp;&nbsp;&nbsp;last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 * <pre>-num-decimals &lt;int&gt; (property: numDecimals)
 * &nbsp;&nbsp;&nbsp;The number of decimals for numeric values to use; -1 uses Java's Double.toString
 * &nbsp;&nbsp;&nbsp;() method.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-fixed-decimals (property: fixedDecimals)
 * &nbsp;&nbsp;&nbsp;If enabled and 'num-decimals' is specified, a fixed number of decimals will 
 * &nbsp;&nbsp;&nbsp;get output (incl. trailing zeroes), otherwise up-to 'num-decimals'.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetDoubleColumnToString
  extends AbstractSpreadSheetColumnConverter {

  /** for serialization. */
  private static final long serialVersionUID = 8681800940519018023L;

  /** the number of decimals to in the output. */
  protected int m_NumDecimals;

  /** whether to use a fixed number of decimals. */
  protected boolean m_FixedDecimals;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Converts the specified spreadsheet double column to string.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-decimals", "numDecimals",
	    -1, -1, null);

    m_OptionManager.add(
	    "fixed-decimals", "fixedDecimals",
	    false);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String columnTipText() {
    return "The double column to convert to string; " + m_Column.getExample();
  }

  /**
   * Sets the number of decimals for numbers in tables.
   *
   * @param value	the number of decimals
   */
  public void setNumDecimals(int value) {
    if ((value >= 0) || (value == -1)) {
      m_NumDecimals = value;
      reset();
    }
    else {
      getLogger().severe("Number of decimals cannot be negative!");
    }
  }

  /**
   * Returns the number of decimals for numbers in tables.
   *
   * @return 		the number of decimals
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDecimalsTipText() {
    return "The number of decimals for numeric values to use; -1 uses Java's Double.toString() method.";
  }

  /**
   * Sets whether to always use a fixed number of decimals, incl trailing zeroes.
   *
   * @param value	true to use fixed number
   */
  public void setFixedDecimals(boolean value) {
    m_FixedDecimals = value;
    reset();
  }

  /**
   * Returns whether to always use a fixed number of decimals, incl trailing zeroes.
   *
   * @return 		true if fixed number used
   */
  public boolean getFixedDecimals() {
    return m_FixedDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fixedDecimalsTipText() {
    return 
	"If enabled and 'num-decimals' is specified, a fixed number of "
	+ "decimals will get output (incl. trailing zeroes), otherwise up-to "
	+ "'num-decimals'.";
  }

  /**
   * Converts the cell's content to a new format.
   * 
   * @param cellOld	the current cell
   * @param cellNew	the new cell with the converted content
   * @throws Exception	if conversion fails
   */
  @Override
  protected void convert(Cell cellOld, Cell cellNew) throws Exception {
    if (m_NumDecimals == -1) {
      cellNew.setContentAsString(cellOld.toDouble().toString());
    }
    else {
      if (m_FixedDecimals)
	cellNew.setContentAsString(Utils.doubleToStringFixed(cellOld.toDouble(), m_NumDecimals));
      else
	cellNew.setContentAsString(Utils.doubleToString(cellOld.toDouble(), m_NumDecimals));
    }
  }
}
