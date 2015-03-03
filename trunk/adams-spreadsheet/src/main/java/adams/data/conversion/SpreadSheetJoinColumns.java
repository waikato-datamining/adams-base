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
 * SpreadSheetJoinColumns.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.HashSet;

import adams.core.Range;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;

/**
 <!-- globalinfo-start -->
 * Merges two or more columns in a spreadsheet into a single column.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The range of columns to merge into a single one; A range is a comma-separated 
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); '
 * &nbsp;&nbsp;&nbsp;inv(...)' inverts the range '...'; apart from column names (case-sensitive
 * &nbsp;&nbsp;&nbsp;), the following placeholders can be used as well: first, second, third, 
 * &nbsp;&nbsp;&nbsp;last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-glue &lt;java.lang.String&gt; (property: glue)
 * &nbsp;&nbsp;&nbsp;The 'glue' string to use between two values that get merged.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-column-name &lt;java.lang.String&gt; (property: columnName)
 * &nbsp;&nbsp;&nbsp;The new column name; if left empty, a name is generated from the processed 
 * &nbsp;&nbsp;&nbsp;columns.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetJoinColumns
  extends AbstractSpreadSheetConversion {

  /** for serialization. */
  private static final long serialVersionUID = -5364554292793461868L;

  /** the columns to merge. */
  protected SpreadSheetColumnRange m_Columns;
  
  /** the concatenation string to use. */
  protected String m_Glue;
  
  /** the new column name. */
  protected String m_ColumnName;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Merges two or more columns in a spreadsheet into a single column.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "columns", "columns",
	    new SpreadSheetColumnRange(Range.ALL));

    m_OptionManager.add(
	    "glue", "glue",
	    "");

    m_OptionManager.add(
	    "column-name", "columnName",
	    "");
  }

  /**
   * Sets the range of columns to merge.
   *
   * @param value	the range
   */
  public void setColumns(SpreadSheetColumnRange value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the range of columns to merge.
   *
   * @return		true range
   */
  public SpreadSheetColumnRange getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsTipText() {
    return "The range of columns to merge into a single one; " + m_Columns.getExample();
  }

  /**
   * Sets the "glue" to use between two columns.
   *
   * @param value	the glue
   */
  public void setGlue(String value) {
    m_Glue = value;
    reset();
  }

  /**
   * Returns the "glue" to use between two columns.
   *
   * @return		the glue
   */
  public String getGlue() {
    return m_Glue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String glueTipText() {
    return "The 'glue' string to use between two values that get merged.";
  }

  /**
   * Sets the new column name.
   *
   * @param value	the column name
   */
  public void setColumnName(String value) {
    m_ColumnName = value;
    reset();
  }

  /**
   * Returns the new column name.
   *
   * @return		the column name
   */
  public String getColumnName() {
    return m_ColumnName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnNameTipText() {
    return "The new column name; if left empty, a name is generated from the processed columns.";
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  protected SpreadSheet convert(SpreadSheet input) throws Exception {
    SpreadSheet		result;
    int[]		indicesMerge;
    HashSet<Integer>	hashMerge;
    String		headerMerged;
    int			i;
    Row			headerInput;
    Row			headerOutput;
    Row			rowOutput;
    int			n;
    String		content;
    int			indexMerged;
    boolean		first;
    
    m_Columns.setSpreadSheet(input);
    indicesMerge = m_Columns.getIntIndices();
    if (indicesMerge.length < 2) {
      getLogger().severe("Need at least two column indices to merge!");
      return input;
    }

    hashMerge = new HashSet<Integer>();
    for (int index: indicesMerge)
      hashMerge.add(index);
    
    result = input.getHeader();
    result.getHeaderRow().clear();
    
    // header
    if (m_ColumnName.isEmpty()) {
      headerMerged = "";
      for (i = 0; i < indicesMerge.length; i++) {
	if (i > 0)
	  headerMerged += m_Glue;
	headerMerged += input.getHeaderRow().getCell(indicesMerge[i]);
      }
    }
    else {
      headerMerged = m_ColumnName;
    }
    headerInput  = input.getHeaderRow();
    headerOutput = result.getHeaderRow();
    indexMerged  = -1;
    n            = 0;
    first        = true;
    for (i = 0; i < input.getColumnCount(); i++) {
      if (hashMerge.contains(i)) {
	if (headerMerged != null) {
	  indexMerged  = n;
	  headerOutput.addCell("" + n).setContentAsString(headerMerged);
	  headerMerged = null;
	}
	if (first) {
	  first = false;
	  n++;
	}
      }
      else {
	headerOutput.addCell("" + n).setContent(headerInput.getCell(i).getContent());
	n++;
      }
    }
    
    // data
    for (DataRow rowInput: input.rows()) {
      rowOutput = result.addRow();
      first     = true;
      n         = 0;
      for (i = 0; i < input.getColumnCount(); i++) {
	if (hashMerge.contains(i)) {
	  content = SpreadSheet.MISSING_VALUE;
	  if (rowInput.hasCell(i) && !rowInput.getCell(i).isMissing())
	    content = rowInput.getCell(i).getContent();
	  if (!rowOutput.hasCell(indexMerged) || rowOutput.getCell(indexMerged).isMissing())
	    rowOutput.addCell(indexMerged).setContentAsString(content);
	  else
	    rowOutput.getCell(indexMerged).setContentAsString(rowOutput.getCell(indexMerged).getContent() + m_Glue + content);
	  if (first) {
	    first = false;
	    n++;
	  }
	}
	else {
	  if (rowInput.hasCell(i) && !rowInput.getCell(i).isMissing())
	    rowOutput.addCell("" + n).setContent(rowInput.getCell(i).getContent());
	  n++;
	}
      }
    }
  
    return result;
  }
}
