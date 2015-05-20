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
 * SpreadSheetStringColumnToDouble.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.text.DecimalFormat;

import adams.data.DecimalFormatString;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Converts the specified spreadsheet column from string to double, according to the provided format.<br>
 * For more information on the format, see Javadoc of 'java.text.DecimalFormat' class:<br>
 * http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;DecimalFormat.html
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
 * <pre>-column &lt;adams.core.Index&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column to convert to double; An index is a number starting with 1; the 
 * &nbsp;&nbsp;&nbsp;following placeholders can be used as well: first, second, third, last_2,
 * &nbsp;&nbsp;&nbsp; last_1, last
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 * <pre>-format &lt;adams.data.DecimalFormatString&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The format string used for parsing the strings.
 * &nbsp;&nbsp;&nbsp;default: 0.#
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetStringColumnToDouble
  extends AbstractSpreadSheetColumnConverter {

  /** for serialization. */
  private static final long serialVersionUID = 8681800940519018023L;
  
  /** the format to use. */
  protected DecimalFormatString m_Format;

  /** the formatter in use. */
  protected transient DecimalFormat m_Formatter;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Converts the specified spreadsheet column from string to double, "
	+ "according to the provided format.\n"
	+ "For more information on the format, see Javadoc of 'java.text.DecimalFormat' class:\n"
	+ "http://docs.oracle.com/javase/6/docs/api/java/text/DecimalFormat.html";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "format", "format",
	    new DecimalFormatString("0.#"));
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String columnTipText() {
    return "The column to convert to double; " + m_Column.getExample();
  }

  /**
   * Sets the format of the string used for parsing.
   *
   * @param value	the format
   */
  public void setFormat(DecimalFormatString value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the format of the string used for parsing.
   *
   * @return		the format
   */
  public DecimalFormatString getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The format string used for parsing the strings.";
  }

  /**
   * Prepares the conversion.
   * 
   * @param input	the spreadsheet to convert
   */
  @Override
  protected void preConvert(SpreadSheet input) {
    super.preConvert(input);
    m_Formatter = m_Format.toDecimalFormat();
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
    
    content = cellOld.getContent();
    
    if (content.trim().length() > 0)
      cellNew.setContent(m_Formatter.parse(content).doubleValue());
    else
      cellNew.setMissing();
  }
}
