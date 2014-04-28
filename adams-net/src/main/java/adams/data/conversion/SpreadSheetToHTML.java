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
 * SpreadSheetToHTML.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.base.HtmlCode;
import adams.core.net.HtmlUtils;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Turns a spreadsheet into an HTML table.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-title &lt;java.lang.String&gt; (property: title)
 * &nbsp;&nbsp;&nbsp;The title to use for the HTML title tag.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-html-before-table &lt;adams.core.base.HtmlCode&gt; (property: htmlBeforeTable)
 * &nbsp;&nbsp;&nbsp;The HTML code to inject before the table.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-html-after-table &lt;adams.core.base.HtmlCode&gt; (property: htmlAfterTable)
 * &nbsp;&nbsp;&nbsp;The HTML code to inect after the table.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-only-table-code &lt;boolean&gt; (property: onlyTableCode)
 * &nbsp;&nbsp;&nbsp;If enabled, only the table code gets generated without surrounding body&#47;
 * &nbsp;&nbsp;&nbsp;html tags.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetToHTML
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -6497336681092989013L;
  
  /** the title to use. */
  protected String m_Title;
  
  /** the HTML code to inject before the table. */
  protected HtmlCode m_HtmlBeforeTable;
  
  /** the HTML code to inject after the table. */
  protected HtmlCode m_HtmlAfterTable;
  
  /** whether to generate only table code. */
  protected boolean m_OnlyTableCode;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a spreadsheet into an HTML table.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "title", "title",
	    "");

    m_OptionManager.add(
	    "html-before-table", "htmlBeforeTable",
	    new HtmlCode());

    m_OptionManager.add(
	    "html-after-table", "htmlAfterTable",
	    new HtmlCode());

    m_OptionManager.add(
	    "only-table-code", "onlyTableCode",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "onlyTableModel", m_OnlyTableCode, "only table");
  }

  /**
   * Sets the HTML title.
   *
   * @param value	the title for the HTML page
   */
  public void setTitle(String value) {
    m_Title = value;
    reset();
  }

  /**
   * Returns the HTML title.
   *
   * @return		the title of the HTML page
   */
  public String getTitle() {
    return m_Title;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String titleTipText() {
    return "The title to use for the HTML title tag.";
  }

  /**
   * Sets the HTML code to inject before the table.
   *
   * @param value	the HTML code
   */
  public void setHtmlBeforeTable(HtmlCode value) {
    m_HtmlBeforeTable = value;
    reset();
  }

  /**
   * Returns the HTML code to inject before the table.
   *
   * @return		the HTML code
   */
  public HtmlCode getHtmlBeforeTable() {
    return m_HtmlBeforeTable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String htmlBeforeTableTipText() {
    return "The HTML code to inject before the table.";
  }

  /**
   * Sets the HTML code to inject after the table.
   *
   * @param value	the HTML code
   */
  public void setHtmlAfterTable(HtmlCode value) {
    m_HtmlAfterTable = value;
    reset();
  }

  /**
   * Returns the HTML code to inject after the table.
   *
   * @return		the HTML code
   */
  public HtmlCode getHtmlAfterTable() {
    return m_HtmlAfterTable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String htmlAfterTableTipText() {
    return "The HTML code to inect after the table.";
  }

  /**
   * Sets whether to omit surrounding body/html tags.
   *
   * @param value	true if to omit html/body tags
   */
  public void setOnlyTableCode(boolean value) {
    m_OnlyTableCode = value;
    reset();
  }

  /**
   * Returns whether to omit surrounding body/html tags.
   *
   * @return		true if to omit html/body tags
   */
  public boolean getOnlyTableCode() {
    return m_OnlyTableCode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onlyTableCodeTipText() {
    return "If enabled, only the table code gets generated without surrounding body/html tags.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    StringBuilder	result;
    SpreadSheet		input;
    int			i;
    
    result = new StringBuilder();
    input  = (SpreadSheet) m_Input;
    
    if (!m_OnlyTableCode) {
      result.append("<html>\n");
      result.append("  <title>" + m_Title + "</title>\n");
      result.append("  <body>\n");
      result.append(m_HtmlBeforeTable + "\n");
    }

    result.append("  <table>\n");
    
    // header
    result.append("    <tr>\n");
    for (i = 0; i < input.getHeaderRow().getCellCount(); i++)
      result.append("      <th>" + HtmlUtils.toHTML(input.getHeaderRow().getCell(i).getContent()) + "</th>\n");
    result.append("    </tr>\n");
    
    // data
    for (Row row: input.rows()) {
      result.append("    <tr>\n");
      for (i = 0; i < row.getCellCount(); i++)
        result.append("      <td>" + HtmlUtils.toHTML(row.getCell(i).getContent()) + "</td>\n");
      result.append("    </tr>\n");
    }
    
    result.append("  </table>\n");
    
    if (!m_OnlyTableCode) {
      result.append(m_HtmlAfterTable + "\n");
      result.append("  </body>\n");
      result.append("</html>\n");
    }
    
    return result.toString();
  }
}
