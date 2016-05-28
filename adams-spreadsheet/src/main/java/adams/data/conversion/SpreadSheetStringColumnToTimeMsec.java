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
 * SpreadSheetStringColumnToTimeMsec.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.DateFormat;
import adams.core.TimeMsec;
import adams.data.DateFormatString;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Converts the specified spreadsheet column from string to time&#47;msec, according to the provided format.<br>
 * For more information on the format, see Javadoc of 'java.text.SimpleTimeFormat' class:<br>
 * http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column to convert to date; An index is a number starting with 1; column 
 * &nbsp;&nbsp;&nbsp;names (case-sensitive) as well as the following placeholders can be used:
 * &nbsp;&nbsp;&nbsp; first, second, third, last_2, last_1, last; numeric indices can be enforced 
 * &nbsp;&nbsp;&nbsp;by preceding them with '#' (eg '#12'); column names can be surrounded by 
 * &nbsp;&nbsp;&nbsp;double quotes.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-keep-failed &lt;boolean&gt; (property: keepFailed)
 * &nbsp;&nbsp;&nbsp;Whether to keep the original value of cells which conversion has failed 
 * &nbsp;&nbsp;&nbsp;rather than setting them to missing.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-format &lt;adams.data.DateFormatString&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The format string used for parsing the strings.
 * &nbsp;&nbsp;&nbsp;default: HH:mm:ss.SSS
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetStringColumnToTimeMsec
  extends AbstractSpreadSheetColumnConverter {

  /** for serialization. */
  private static final long serialVersionUID = 8681800940519018023L;
  
  /** the format to use. */
  protected DateFormatString m_Format;
  
  /** the formatter in use. */
  protected transient DateFormat m_Formatter;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Converts the specified spreadsheet column from string to time/msec, "
	+ "according to the provided format.\n"
	+ "For more information on the format, see Javadoc of 'java.text.SimpleTimeFormat' class:\n"
	+ "http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "format", "format",
	    new DateFormatString("HH:mm:ss.SSS"));
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String columnTipText() {
    return "The column to convert to date; " + m_Column.getExample();
  }

  /**
   * Sets the format of the string used for parsing.
   *
   * @param value	the format
   */
  public void setFormat(DateFormatString value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the format of the string used for parsing.
   *
   * @return		the format
   */
  public DateFormatString getFormat() {
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
    m_Formatter = m_Format.toDateFormat();
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
    
    if ((content.trim().length() > 0) && (m_Formatter.check(content.trim())))
      cellNew.setContent(new TimeMsec(m_Formatter.parse(content).getTime()));
    else
      cellNew.setMissing();
  }
}
