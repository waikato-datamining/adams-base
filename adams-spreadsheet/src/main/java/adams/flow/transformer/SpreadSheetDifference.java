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

/*
 * SpreadSheetDifference.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.HashSet;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.RowIdentifier;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Computes the difference of the numeric cells between two spreadsheets.<br>
 * The values of the second spreadsheet are subtracted from the first one.<br>
 * If no 'key' columns are defined, the current order of rows is used for comparison.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 <!-- flow-summary-end -->
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
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetDifference
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-key-columns &lt;adams.core.Range&gt; (property: keyColumns)
 * &nbsp;&nbsp;&nbsp;The columns to use as keys for identifying rows in the spreadsheets, if 
 * &nbsp;&nbsp;&nbsp;empty the row index is used instead; A range is a comma-separated list of 
 * &nbsp;&nbsp;&nbsp;single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)'
 * &nbsp;&nbsp;&nbsp; inverts the range '...'; the following placeholders can be used as well:
 * &nbsp;&nbsp;&nbsp; first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetDifference
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5056170789277731638L;

  /** the range of column indices to use as key for identifying a row. */
  protected SpreadSheetColumnRange m_KeyColumns;
  
  /** for locating the rows. */
  protected RowIdentifier[] m_Rows;
  
  /** the column indices to use. */
  protected int[] m_ColIndices;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Computes the difference of the numeric cells between two spreadsheets.\n"
	+ "The values of the second spreadsheet are subtracted from the first one.\n"
	+ "If no 'key' columns are defined, the current order of rows is used "
	+ "for comparison.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "key-columns", "keyColumns",
	    new SpreadSheetColumnRange(""));
  }
  
  /**
   * 
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_KeyColumns = new SpreadSheetColumnRange();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "keyColumns", m_KeyColumns, "key columns: ");
  }

  /**
   * Sets the colums that identify a row, use empty string to simply use row index.
   *
   * @param value	the range
   */
  public void setKeyColumns(SpreadSheetColumnRange value) {
    m_KeyColumns = value;
    reset();
  }

  /**
   * Returns the colums that identify a row, use empty string to simply use row index
   *
   * @return		the range
   */
  public SpreadSheetColumnRange getKeyColumns() {
    return m_KeyColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyColumnsTipText() {
    return
        "The columns to use as keys for identifying rows in the spreadsheets, if empty the row index is used instead; " + m_KeyColumns.getExample();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		adams.core.io.SpreadSheet.class
   */
  public Class[] accepts() {
    return new Class[]{SpreadSheet[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		adams.core.io.SpreadSheet.class
   */
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }
  
  /**
   * Initializes the row lookup, if necessary.
   * 
   * @param sheets	the sheets to generated lookup for
   */
  protected void initRowLookup(SpreadSheet[] sheets) {
    if (m_Rows != null)
      return;

    m_Rows       = new RowIdentifier[2];
    m_ColIndices = new int[0];

    if (m_KeyColumns.getRange().length() != 0) {
      m_KeyColumns.setSpreadSheet(sheets[0]);
      m_ColIndices = m_KeyColumns.getIntIndices();
      m_Rows[0]    = new RowIdentifier(m_KeyColumns);
      m_Rows[1]    = new RowIdentifier(m_KeyColumns);
      m_Rows[0].identify(sheets[0]);
      m_Rows[1].identify(sheets[1]);
    }
  }
    
  /**
   * Computes the difference between the two rows: actual difference is
   * computed for numeric cells. If cells are strings, then the results is
   * a missing value in case of differing strings, otherwise the same.
   * 
   * @param output	the spreadsheet the new row will get added to
   * @param row1	the row from the first sheet
   * @param row2	the row from the second sheet
   * @return		the generated difference
   */
  protected Row difference(SpreadSheet output, Row row1, Row row2) {
    Row			result;
    Cell		cell1;
    Cell		cell2;
    int			index;
    HashSet<Integer>	indices;
    
    result = row1.getClone(output);
    result.clear();
    
    indices = new HashSet<Integer>();
    for (int i: m_ColIndices)
      indices.add(m_ColIndices[i]);
    
    for (String key: row1.cellKeys()) {
      index = row1.getOwner().getHeaderRow().indexOf(key);
      cell1 = row1.getCell(key);
      cell2 = row2.getCell(key);
      if (indices.contains(index)) {
	result.addCell(key).setContent(cell1.getContent());
      }
      else if ((cell1 == null) || (cell2 == null)) {
	result.addCell(key).setContent(SpreadSheet.MISSING_VALUE);
      }
      else if (cell1.isMissing() || cell2.isMissing()) {
	result.addCell(key).setContent(SpreadSheet.MISSING_VALUE);
      }
      else if (cell1.isNumeric() && cell2.isNumeric()){
	result.addCell(key).setContent(cell1.toDouble() - cell2.toDouble());
      }
      else {
	if (cell1.getContent().equals(cell2.getContent()))
	  result.addCell(key).setContent(cell1.getContent());
	else
	  result.addCell(key).setContent(SpreadSheet.MISSING_VALUE);
      }
    }
    
    return result;
  }

  /**
   * Generates a row and appends it to the output.
   * 
   * @param output	the spreadsheet to receive the output
   * @Param rowDiff	the difference row
   */
  protected void generateOutputRow(SpreadSheet output, Row rowDiff) {
    Row		rowNew;
    Row		header;
    int		n;
    String	key;

    header = output.getHeaderRow();
    rowNew = output.addRow();
    if (rowDiff != null) {
      for (n = 0; n < header.getCellCount(); n++) {
	key = header.getCellKey(n);
	if (rowDiff.hasCell(key) && !rowDiff.getCell(key).isMissing())
	  rowNew.addCell(key).setContent(rowDiff.getCell(key).getContent());
	else
	  rowNew.addCell(key).setContent(SpreadSheet.MISSING_VALUE);
      }
    }
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    SpreadSheet[]	sheets;
    SpreadSheet		output;
    Row			row1;
    Row			row2;
    int			n;

    result = null;
    sheets = (SpreadSheet[]) m_InputToken.getPayload();
    m_Rows = null;
    
    if (sheets.length != 2) {
      result = "Expected two spreadsheets, received: " + sheets.length;
    }
    else {
      result = sheets[0].equalsHeader(sheets[0]);
      if (result != null)
	result = "Spreadsheets not compatible: " + result;
    }

    if (result == null) {
      output = sheets[0].getHeader();
      initRowLookup(sheets);
      if (m_ColIndices.length > 0) {
	for (String key: m_Rows[0].getKeys()) {
	  row1 = sheets[0].getRow(m_Rows[0].getRows(key).get(0));
	  row2 = null;
	  if (m_Rows[1].getRows(key) != null)
	    row2 = sheets[1].getRow(m_Rows[1].getRows(key).get(0));
	  if (row2 != null)
	    generateOutputRow(output, difference(output, row1, row2));
	}
      }
      else {
	for (n = 0; n < sheets[0].getRowCount() && n < sheets[1].getRowCount(); n++) {
	  row1 = sheets[0].getRow(n);
	  row2 = sheets[0].getRow(n);
	  generateOutputRow(output, difference(output, row1, row2));
	}
      }

      m_OutputToken = new Token(output);
    }
    
    // clean up
    m_Rows       = null;
    m_ColIndices = null;
    
    return result;
  }
}
