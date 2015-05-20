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
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-binarize-type &lt;NUMERIC|BOOLEAN|LABELS&gt; (property: binarizeType)
 * &nbsp;&nbsp;&nbsp;The type of binarization to perform.
 * &nbsp;&nbsp;&nbsp;default: NUMERIC
 * </pre>
 * 
 * <pre>-label-positive &lt;java.lang.String&gt; (property: labelPositive)
 * &nbsp;&nbsp;&nbsp;The positive label (ie 1s) in case of LABELS.
 * &nbsp;&nbsp;&nbsp;default: yes
 * </pre>
 * 
 * <pre>-label-negative &lt;java.lang.String&gt; (property: labelNegative)
 * &nbsp;&nbsp;&nbsp;The negative label (ie 0s) in case of LABELS.
 * &nbsp;&nbsp;&nbsp;default: no
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

  /** the separator to use between column name and label. */
  public final static String SEPARATOR = "-";
  
  /**
   * How to binarize the data.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 8614 $
   */
  public enum BinarizeType {
    /** using 1s and 0s. */
    NUMERIC,
    /** using booleans. */
    BOOLEAN,
    /** using string labels. */
    LABELS
  }
  
  /** the columns to merge. */
  protected SpreadSheetColumnRange m_Columns;
  
  /** how to binarize. */
  protected BinarizeType m_BinarizeType;
  
  /** the positive label. */
  protected String m_LabelPositive;
  
  /** the negative label. */
  protected String m_LabelNegative;
  
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

    m_OptionManager.add(
	    "binarize-type", "binarizeType",
	    BinarizeType.NUMERIC);

    m_OptionManager.add(
	    "label-positive", "labelPositive",
	    "yes");

    m_OptionManager.add(
	    "label-negative", "labelNegative",
	    "no");
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
   * Sets how to binarize the data.
   *
   * @param value	the type
   */
  public void setBinarizeType(BinarizeType value) {
    m_BinarizeType = value;
    reset();
  }

  /**
   * Returns how to binarize the data.
   *
   * @return		the type
   */
  public BinarizeType getBinarizeType() {
    return m_BinarizeType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String binarizeTypeTipText() {
    return "The type of binarization to perform.";
  }

  /**
   * Sets the positive label (for 1s) in case of {@link BinarizeType#LABELS}.
   *
   * @param value	the label
   */
  public void setLabelPositive(String value) {
    m_LabelPositive = value;
    reset();
  }

  /**
   * Returns the positive label (for 1s) in case of {@link BinarizeType#LABELS}.
   *
   * @return		the label
   */
  public String getLabelPositive() {
    return m_LabelPositive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelPositiveTipText() {
    return "The positive label (ie 1s) in case of " + BinarizeType.LABELS + ".";
  }

  /**
   * Sets the negative label (for 0s) in case of {@link BinarizeType#LABELS}.
   *
   * @param value	the label
   */
  public void setLabelNegative(String value) {
    m_LabelNegative = value;
    reset();
  }

  /**
   * Returns the negative label (for 0s) in case of {@link BinarizeType#LABELS}.
   *
   * @return		the label
   */
  public String getLabelNegative() {
    return m_LabelNegative;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelNegativeTipText() {
    return "The negative label (ie 0s) in case of " + BinarizeType.LABELS + ".";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "columns", m_Columns, "cols: ");
    result += QuickInfoHelper.toString(this, "binarizeType", m_BinarizeType, ", type: ");
    result += QuickInfoHelper.toString(this, "labelPositive", m_LabelPositive, ", pos: ");
    result += QuickInfoHelper.toString(this, "labelNegative", m_LabelNegative, ", neg: ");
    
    return result;
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
    boolean				match;
    
    m_Columns.setSpreadSheet(input);
    indices = m_Columns.getIntIndices();
    if (indices.length == 0)
      return input;
  
    mapping = new HashMap<Integer,List<String>>();
    for (i = 0; i < indices.length; i++) {
      if (input.isNumeric(indices[i], true))
	continue;
      labels = input.getCellValues(indices[i]);
      if (!mapping.containsKey(indices[i]))
	mapping.put(indices[i], new ArrayList<String>());
      for (n = 0; n < labels.size(); n++)
	mapping.get(indices[i]).add(labels.get(n));
    }

    result = input.newInstance();
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
	  binarized = rowInp.getCell(i).getContent() + SEPARATOR + label;
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
	    match = label.equals(row.getCell(i).getContent());
	    switch (m_BinarizeType) {
	      case NUMERIC:
		rowRes.addCell(n).setContent(match ? 1 : 0);
		break;
	      case BOOLEAN:
		rowRes.addCell(n).setContent(match);
		break;
	      case LABELS:
		rowRes.addCell(n).setContent(match ? m_LabelPositive : m_LabelNegative);
		break;
	      default:
		throw new IllegalStateException("Unhandled binarize type: " + m_BinarizeType);
	    }
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
