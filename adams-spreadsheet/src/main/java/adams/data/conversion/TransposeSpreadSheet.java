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
 * TransposeSpreadSheet.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Transposes a spreadsheet, i.e., swaps columns with rows.
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
 * <pre>-use-header-as-first-column (property: useHeaderAsFirstColumn)
 * &nbsp;&nbsp;&nbsp;Whether to use the current header as first column.
 * </pre>
 * 
 * <pre>-use-first-column-as-header (property: useFirstColumnAsHeader)
 * &nbsp;&nbsp;&nbsp;Whether to use the first column as new header.
 * </pre>
 * 
 * <pre>-column-prefix &lt;java.lang.String&gt; (property: columnPrefix)
 * &nbsp;&nbsp;&nbsp;The column prefix for the new columns if the first column is not used for 
 * &nbsp;&nbsp;&nbsp;the header; 1-based column index is appended.
 * &nbsp;&nbsp;&nbsp;default: col-
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TransposeSpreadSheet
  extends AbstractSpreadSheetConversion {

  /** for serialization. */
  private static final long serialVersionUID = 4733940137387662202L;

  /** whether to add the header row as first column. */
  protected boolean m_UseHeaderAsFirstColumn;

  /** whether to use the first column as header. */
  protected boolean m_UseFirstColumnAsHeader;
  
  /** the column prefix if the first column is not used as header. */
  protected String m_ColumnPrefix;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Transposes a spreadsheet, i.e., swaps columns with rows.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "use-header-as-first-column", "useHeaderAsFirstColumn",
	    false);

    m_OptionManager.add(
	    "use-first-column-as-header", "useFirstColumnAsHeader",
	    false);

    m_OptionManager.add(
	    "column-prefix", "columnPrefix",
	    "col-");
  }

  /**
   * Sets whether to use the current header as first column.
   *
   * @param value	true if to use header
   */
  public void setUseHeaderAsFirstColumn(boolean value) {
    m_UseHeaderAsFirstColumn = value;
    reset();
  }

  /**
   * Returns whether to use the current header as first column.
   *
   * @return		true if to use header
   */
  public boolean getUseHeaderAsFirstColumn() {
    return m_UseHeaderAsFirstColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useHeaderAsFirstColumnTipText() {
    return "Whether to use the current header as first column.";
  }

  /**
   * Sets whether to use the first column as new header.
   *
   * @param value	true if to use column
   */
  public void setUseFirstColumnAsHeader(boolean value) {
    m_UseFirstColumnAsHeader = value;
    reset();
  }

  /**
   * Returns whether to use the first column as new header.
   *
   * @return		true if to use column
   */
  public boolean getUseFirstColumnAsHeader() {
    return m_UseFirstColumnAsHeader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useFirstColumnAsHeaderTipText() {
    return "Whether to use the first column as new header.";
  }

  /**
   * Sets the column prefix if the first column is not used as header.
   *
   * @param value	the prefix
   */
  public void setColumnPrefix(String value) {
    m_ColumnPrefix = value;
    reset();
  }

  /**
   * Returns whether to use the first column as new header.
   *
   * @return		true if to use column
   */
  public String getColumnPrefix() {
    return m_ColumnPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnPrefixTipText() {
    return 
	"The column prefix for the new columns if the first column is not "
	+ "used for the header; 1-based column index is appended.";
  }

  /**
   * Transposes the matrix.
   * 
   * @param matrix	the matrix to transose
   * @return		the tranposed matrix
   */
  protected Object[][] transpose(Object[][] matrix) {
    Object[][]	result;
    int		x;
    int		y;
    
    result = new Object[matrix[0].length][matrix.length];
    
    for (y = 0; y < matrix.length; y++) {
      for (x = 0; x < matrix[0].length; x++) {
	result[x][y] = matrix[y][x];
      }
    }
    
    return result;
  }
  
  /**
   * Performs the actual conversion.
   * <br><br>
   * 
   * Input spreadsheet:
   * <pre>
   * +----+----+----+
   * | c1 | c2 | c3 |
   * +====+====+====+
   * | A  | B  | C  |
   * +----+----+----+
   * | D  | E  | F  |
   * +----+----+----+
   * </pre>
   * 
   * <pre>
   * m_UseFirstColumnAsHeader = FAH
   * m_HeaderAsFirstColumn    = HAF
   * </pre>
   * 
   * !FAH & !HAF:
   * <pre>
   * +----+----+
   * | P1 | P2 |
   * +====+====+
   * | A  | D  |
   * +----+----+
   * | B  | E  |
   * +----+----+
   * | C  | F  |
   * +----+----+
   * </pre>
   * 
   * FAH & !HAF:
   * <pre>
   * +----+----+
   * | A  | D  |
   * +====+====+
   * | B  | E  |
   * +----+----+
   * | C  | F  |
   * +----+----+
   * </pre>
   * 
   * !FAH & HAF:
   * <pre>
   * +----+----+----+
   * | P1 | P2 | P3 |
   * +====+====+====+
   * | c1 | A  | D  |
   * +----+----+----+
   * | c2 | B  | E  |
   * +----+----+----+
   * | c3 | C  | F  |
   * +----+----+----+
   * </pre>
   * 
   * FAH & HAF:
   * <pre>
   * +----+----+----+
   * | c1 | A  | D  |
   * +====+====+====+
   * | c2 | B  | E  |
   * +----+----+----+
   * | c3 | C  | F  |
   * +----+----+----+
   * </pre>
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected SpreadSheet convert(SpreadSheet input) throws Exception {
    SpreadSheet		result;
    int			x;
    int			y;
    Row			rowOut;
    Object[][]		matrix;

    result = input.newInstance();
    result.setDataRowClass(input.getDataRowClass());

    // name and comments
    result.setName(input.getName() + "-transposed");
    for (String comment: input.getComments())
      result.addComment(comment);

    matrix = transpose(input.toMatrix());
    
    // !FAH & !HAF
    if (!m_UseFirstColumnAsHeader && !m_UseHeaderAsFirstColumn) {
      // header
      rowOut = result.getHeaderRow();
      for (x = 1; x < matrix[0].length; x++) {
	rowOut.addCell("" +  (x - 1)).setContent(m_ColumnPrefix + x);
      }
      // data
      for (y = 0; y < matrix.length; y++) {
	rowOut = result.addRow();
	for (x = 1; x < matrix[0].length; x++) {
	  if (matrix[y][x] == null)
	    rowOut.addCell(x - 1).setContent(SpreadSheet.MISSING_VALUE);
	  else
	    rowOut.addCell(x - 1).setContent(matrix[y][x].toString());
	}
      }
    }
    // FAH & !HAF
    else if (m_UseFirstColumnAsHeader && !m_UseHeaderAsFirstColumn) {
      // header
      rowOut = result.getHeaderRow();
      for (x = 1; x < matrix[0].length; x++) {
	rowOut.addCell("" + (x - 1)).setContent(matrix[0][x].toString());
      }
      // data
      for (y = 1; y < matrix.length; y++) {
	rowOut = result.addRow();
	for (x = 1; x < matrix[0].length; x++) {
	  if (matrix[y][x] == null)
	    rowOut.addCell(x - 1).setContent(SpreadSheet.MISSING_VALUE);
	  else
	    rowOut.addCell(x - 1).setContent(matrix[y][x].toString());
	}
      }
    }
    // !FAH & HAF
    else if (!m_UseFirstColumnAsHeader && m_UseHeaderAsFirstColumn) {
      // header
      rowOut = result.getHeaderRow();
      for (x = 0; x < matrix[0].length; x++) {
	rowOut.addCell("" + x).setContent(m_ColumnPrefix + (x + 1));
      }
      // data
      for (y = 0; y < matrix.length; y++) {
	rowOut = result.addRow();
	for (x = 0; x < matrix[0].length; x++) {
	  if (matrix[y][x] == null)
	    rowOut.addCell(x).setContent(SpreadSheet.MISSING_VALUE);
	  else
	    rowOut.addCell(x).setContent(matrix[y][x].toString());
	}
      }
    }
    // FAH & HAF
    else if (m_UseFirstColumnAsHeader && m_UseHeaderAsFirstColumn) {
      // header
      rowOut = result.getHeaderRow();
      y      = 0;
      for (x = 0; x < matrix[0].length; x++) {
	if (matrix[y][x] == null)
	  rowOut.addCell("" + x).setContent(m_ColumnPrefix + (x + 1));
	else
	  rowOut.addCell("" + x).setContent(matrix[y][x].toString());
      }
      // data
      for (y = 1; y < matrix.length; y++) {
	rowOut = result.addRow();
	for (x = 0; x < matrix[0].length; x++) {
	  if (matrix[y][x] == null)
	    rowOut.addCell(x).setContent(SpreadSheet.MISSING_VALUE);
	  else
	    rowOut.addCell(x).setContent(matrix[y][x].toString());
	}
      }
    }
    
    return result;
  }
}
