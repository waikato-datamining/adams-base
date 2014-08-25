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
 * SpreadSheetBinarize.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;

/**
 <!-- globalinfo-start -->
 * Binarizes the non-numeric columns in the selected column range by creating a new column for each of the labels.
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
 * &nbsp;&nbsp;&nbsp;The range of columns to binarize.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8614 $
 */
public class SpreadSheetBinarize
  extends AbstractSpreadSheetConversion {

  /** for serialization. */
  private static final long serialVersionUID = -5364554292793461868L;

  /** the columns to merge. */
  protected SpreadSheetColumnRange m_Columns;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Binarizes the non-numeric columns in the selected column range by "
	+ "creating a new column for each of the labels.";
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
    return "The range of columns to binarize.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "columns", m_Columns);
  }
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  protected SpreadSheet convert(SpreadSheet input) throws Exception {
    SpreadSheet				result;
    Row					rowInp;
    Row					rowRes;
    int[]				indices;
    int					i;
    int					n;
    List<String>			labels;
    HashMap<Integer,List<String>>	mapping;
    String				binarized;
    
    m_Columns.setSpreadSheet(input);
    indices = m_Columns.getIntIndices();
    if (indices.length == 0)
      return input;
  
    mapping = new HashMap<Integer,List<String>>();
    for (i = 0; i < indices.length; i++) {
      if (input.isNumeric(indices[i]))
	continue;
      labels = input.getCellValues(indices[i]);
      Collections.sort(labels);
      if (!mapping.containsKey(indices[i]))
	mapping.put(indices[i], new ArrayList<String>());
      for (n = 0; n < labels.size(); n++)
	mapping.get(indices[i]).add(labels.get(n));
    }

    result = new SpreadSheet();
    result.setDataRowClass(input.getDataRowClass());
    result.addComment(input.getComments());
    result.setName(input.getName());
    
    // header
    rowRes  = result.getHeaderRow();
    rowInp  = input.getHeaderRow();
    for (i = 0; i < input.getColumnCount(); i++) {
      if (m_Columns.isInRange(i) && mapping.containsKey(i)) {
	labels = new ArrayList<String>(mapping.get(i));
	for (String label: labels) {
	  binarized = rowInp.getCell(i).getContent() + "-" + label;
	  rowRes.addCell("" + rowRes.getCellCount()).setContent(binarized);
	}
      }
      else {
	rowRes.addCell("" + rowRes.getCellCount()).assign(rowInp.getCell(i));
      }
    }
    
    // data
    for (Row row: input.rows()) {
      rowRes = result.addRow();
      n      = 0;
      for (i = 0; i < input.getColumnCount(); i++) {
	if (!row.hasCell(i) || row.getCell(i).isMissing()) {
	  rowRes.addCell(n).setMissing();
	  n++;
	}
	else if (m_Columns.isInRange(i) && mapping.containsKey(i)) {
	  labels = new ArrayList<String>(mapping.get(i));
	  for (String label: labels) {
	    binarized = rowInp.getCell(i).getContent() + "-" + label;
	    if (label.equals(row.getCell(i).getContent()))
	      rowRes.addCell(n).setContent(1);
	    else
	      rowRes.addCell(n).setContent(0);
	    n++;
	  }
	}
	else {
	  rowRes.addCell(n).assign(row.getCell(i));
	  n++;
	}
      }
    }
    
    return result;
  }
}
