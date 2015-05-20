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
 * SpreadSheetAddRowID.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

/**
 <!-- globalinfo-start -->
 * Adds an ID column to the spreadsheet, using the row index as value.
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
 * <pre>-header &lt;java.lang.String&gt; (property: header)
 * &nbsp;&nbsp;&nbsp;The name of the new column.
 * &nbsp;&nbsp;&nbsp;default: ID
 * </pre>
 * 
 * <pre>-position &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: position)
 * &nbsp;&nbsp;&nbsp;The position where to insert the ID column.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-after &lt;boolean&gt; (property: after)
 * &nbsp;&nbsp;&nbsp;If enabled, the ID column is inserted after the position instead of at the 
 * &nbsp;&nbsp;&nbsp;position.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-start &lt;int&gt; (property: start)
 * &nbsp;&nbsp;&nbsp;The first ID to use.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetAddRowID
  extends AbstractInPlaceSpreadSheetConversion {

  /** for serialization. */
  private static final long serialVersionUID = -6368389681769797003L;
  
  /** the column header. */
  protected String m_Header;
  
  /** the position where to insert the column. */
  protected SpreadSheetColumnIndex m_Position;  
  
  /** whether to insert after the position instead of at. */
  protected boolean m_After;
  
  /** the index to start with. */
  protected int m_Start;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Adds an ID column to the spreadsheet, using the row index as value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "header", "header",
	    "ID");

    m_OptionManager.add(
	    "position", "position",
	    new SpreadSheetColumnIndex(SpreadSheetColumnIndex.FIRST));

    m_OptionManager.add(
	    "after", "after",
	    false);

    m_OptionManager.add(
	    "start", "start",
	    1);
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Position = new SpreadSheetColumnIndex();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "header", "'" + m_Header + "'", "header: ");

    if (QuickInfoHelper.hasVariable(this, "after"))
      result += ", at/after: ";
    else if (m_After)
      result += ", after: ";
    else
      result += ", at: ";
    result += QuickInfoHelper.toString(this, "position", m_Position);
    result = QuickInfoHelper.toString(this, "start", m_Start, ", start: ");

    return result;
  }

  /**
   * Sets the name of the column.
   *
   * @param value	the name
   */
  public void setHeader(String value) {
    m_Header = value;
    reset();
  }

  /**
   * Returns the name of the column.
   *
   * @return		the name
   */
  public String getHeader() {
    return m_Header;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String headerTipText() {
    return "The name of the new column.";
  }

  /**
   * Sets the position where to insert the column.
   *
   * @param value	the position
   */
  public void setPosition(SpreadSheetColumnIndex value) {
    m_Position = value;
    reset();
  }

  /**
   * Returns the position where to insert the column.
   *
   * @return		the position
   */
  public SpreadSheetColumnIndex getPosition() {
    return m_Position;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String positionTipText() {
    return "The position where to insert the ID column.";
  }

  /**
   * Sets whether to insert at or after the position.
   *
   * @param value	true if to add after
   */
  public void setAfter(boolean value) {
    m_After = value;
    reset();
  }

  /**
   * Returns whether to insert at or after the position.
   *
   * @return		true if to add after
   */
  public boolean getAfter() {
    return m_After;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String afterTipText() {
    return
        "If enabled, the ID column is inserted after the position instead of at "
	+ "the position.";
  }

  /**
   * Sets the first row ID to use.
   *
   * @param value	the ID start
   */
  public void setStart(int value) {
    m_Start = value;
    reset();
  }

  /**
   * Returns the start of the row ID.
   *
   * @return		the ID start
   */
  public int getStart() {
    return m_Start;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startTipText() {
    return "The first ID to use.";
  }

  /**
   * Generates the new spreadsheet from the input.
   * 
   * @param input	the incoming spreadsheet
   * @return		the generated spreadsheet
   * @throws Exception	if conversion fails for some reason
   */
  @Override
  protected SpreadSheet convert(SpreadSheet input) throws Exception {
    SpreadSheet	result;
    Row		row;
    int		i;
    int		pos;
    
    if (m_NoCopy)
      result = input;
    else
      result = input.getClone();
    
    // determine position
    m_Position.setSpreadSheet(result);
    pos = m_Position.getIntIndex();
    if (m_After)
      pos++;
    
    result.insertColumn(pos, m_Header);

    for (i = 0; i < result.getRowCount(); i++) {
      row = result.getRow(i);
      row.getCell(pos).setContent(m_Start + i);
    }
    
    return result;
  }
}
