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
 * SpreadSheetStringColumnToBoolean.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.spreadsheet.Cell;

/**
 <!-- globalinfo-start -->
 * Converts the specified spreadsheet column from string to boolean.
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
 * <pre>-no-copy (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * </pre>
 * 
 * <pre>-column &lt;adams.core.Index&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column to convert to boolean; An index is a number starting with 1; 
 * &nbsp;&nbsp;&nbsp;the following placeholders can be used as well: first, second, third, last
 * &nbsp;&nbsp;&nbsp;_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 * <pre>-value-true &lt;java.lang.String&gt; (property: valueTrue)
 * &nbsp;&nbsp;&nbsp;The value representing 'true'.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-case-insensitive (property: caseInsensitive)
 * &nbsp;&nbsp;&nbsp;If enabled, strings are matched case-sensitive.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetStringColumnToBoolean
  extends AbstractSpreadSheetColumnConverter {

  /** for serialization. */
  private static final long serialVersionUID = 2390017930652080091L;

  /** the string to represent "true". */
  protected String m_ValueTrue;

  /** whether to ignored case. */
  protected boolean m_CaseInsensitive;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Converts the specified spreadsheet column from string to boolean.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "value-true", "valueTrue",
	    "true");

    m_OptionManager.add(
	    "case-insensitive", "caseInsensitive",
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
    return "The column to convert to boolean; " + m_Column.getExample();
  }

  /**
   * Sets the string representing 'true'.
   *
   * @param value	the 'true' string
   */
  public void setValueTrue(String value) {
    m_ValueTrue = value;
    reset();
  }

  /**
   * Returns the string representing 'true'.
   *
   * @return		the 'true' string
   */
  public String getValueTrue() {
    return m_ValueTrue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTrueTipText() {
    return "The value representing 'true'.";
  }

  /**
   * Sets whether to match strings case-sensitive.
   *
   * @param value	true to match strings case-sensitive
   */
  public void setCaseInsensitive(boolean value) {
    m_CaseInsensitive = value;
    reset();
  }

  /**
   * Returns whether to match strings case-sensitive.
   *
   * @return		true if strings are matched case-sensitive
   */
  public boolean getCaseInsensitive() {
    return m_CaseInsensitive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String caseInsensitiveTipText() {
    return "If enabled, strings are matched case-sensitive.";
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
    String	content;
    
    content = cellOld.getContent().trim();
    
    if (m_CaseInsensitive)
      cellNew.setContent(content.equals(m_ValueTrue));
    else
      cellNew.setContent(content.equalsIgnoreCase(m_ValueTrue));
  }
}
