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
 * FixedColumnTextualFeatureConverter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.featureconverter;

import java.util.List;

import adams.core.QuickInfoHelper;
import adams.data.report.DataType;

/**
 <!-- globalinfo-start -->
 * Simple feature converter that generates textual output with fixed column width.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-column-width &lt;int&gt; (property: columnWidth)
 * &nbsp;&nbsp;&nbsp;The width of a column in characters.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-separator-cells &lt;java.lang.String&gt; (property: separatorCells)
 * &nbsp;&nbsp;&nbsp;The separator to use between cells.
 * &nbsp;&nbsp;&nbsp;default:  | 
 * </pre>
 * 
 * <pre>-separator-header &lt;java.lang.String&gt; (property: separatorHeader)
 * &nbsp;&nbsp;&nbsp;The separator to use between header and data.
 * &nbsp;&nbsp;&nbsp;default: -
 * </pre>
 * 
 * <pre>-missing-value &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The value to use for missing values.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedColumnTextualFeatureConverter
  extends AbstractFeatureConverter<String,String> {

  /** for serialization. */
  private static final long serialVersionUID = 2245576408802564218L;

  /** the width of the columns. */
  protected int m_ColumnWidth;
  
  /** the separator to use for cells. */
  protected String m_SeparatorCells;

  /** the separator between header and data (gets automatically repeated). */
  protected String m_SeparatorHeader;

  /** the string to use for missing values. */
  protected String m_MissingValue;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simple feature converter that generates textual output with fixed column width.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "column-width", "columnWidth",
	    10, 1, null);

    m_OptionManager.add(
	    "separator-cells", "separatorCells",
	    " | ");

    m_OptionManager.add(
	    "separator-header", "separatorHeader",
	    "-");

    m_OptionManager.add(
	    "missing-value", "missingValue",
	    "");
  }
  
  /**
   * Sets the column width.
   *
   * @param value	the width
   */
  public void setColumnWidth(int value) {
    m_ColumnWidth = value;
    reset();
  }

  /**
   * Returns the column width.
   *
   * @return		the width
   */
  public int getColumnWidth() {
    return m_ColumnWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnWidthTipText() {
    return "The width of a column in characters.";
  }

  /**
   * Sets the separator to use between cells.
   *
   * @param value	the separator
   */
  public void setSeparatorCells(String value) {
    m_SeparatorCells = value;
    reset();
  }

  /**
   * Returns the separator in use between cells.
   *
   * @return		the separator
   */
  public String getSeparatorCells() {
    return m_SeparatorCells;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String separatorCellsTipText() {
    return "The separator to use between cells.";
  }
  
  /**
   * Sets the separator to use between header and data.
   * Gets automatically repeated to fill up row.
   *
   * @param value	the separator
   */
  public void setSeparatorHeader(String value) {
    m_SeparatorHeader = value;
    reset();
  }

  /**
   * Returns the separator in use between header and data. 
   * Gets automatically repeated to fill up row.
   *
   * @return		the separator
   */
  public String getSeparatorHeader() {
    return m_SeparatorHeader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String separatorHeaderTipText() {
    return "The separator to use between header and data.";
  }
  
  /**
   * Sets the string to use for missing values.
   *
   * @param value	the missing value string
   */
  public void setMissingValue(String value) {
    m_MissingValue = value;
    reset();
  }

  /**
   * Returns the string to use for missing values.
   *
   * @return		the missing value string
   */
  public String getMissingValue() {
    return m_MissingValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingValueTipText() {
    return "The value to use for missing values.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result = QuickInfoHelper.toString(this, "columnWidth", m_ColumnWidth, "width: ");
    result += QuickInfoHelper.toString(this, "separatorCells", m_SeparatorCells, ", cells: ");
    result += QuickInfoHelper.toString(this, "separatorHeader", m_SeparatorHeader, ", header: ");
    
    return result;
  }

  /**
   * Returns the class of the dataset that the converter generates.
   * 
   * @return		the format
   */
  @Override
  public Class getDatasetFormat() {
    return String.class;
  }

  /**
   * Returns the class of the row that the converter generates.
   * 
   * @return		the format
   */
  @Override
  public Class getRowFormat() {
    return String.class;
  }

  /**
   * Fixes the string to have the desired maximum length. Uses blanks to fill
   * up.
   * 
   * @param s		the string to fix
   * @param max		the maximum number of characters.
   * @param rightFill	whether to fill on the right
   * @return		the fixed string
   */
  protected String fixLength(String s, int max, boolean rightFill) {
    StringBuilder	result;
    
    if (s.length() > max)
      return s.substring(0, max);
    
    result = new StringBuilder(s);
    while (result.length() < max) {
      if (rightFill)
	result.append(" ");
      else
	result.insert(0, " ");
    }
    return result.toString();
  }
  
  /**
   * Performs the actual generation of the header data structure using the 
   * supplied header definition.
   * 
   * @param header	the header definition
   * @return		the dataset structure
   */
  @Override
  protected String doGenerateHeader(HeaderDefinition header) {
    StringBuilder	result;
    StringBuilder	sep;
    int			max;
    
    result = new StringBuilder();
    
    // columns
    for (String name: header.getNames()) {
      if (result.length() > 0)
	result.append(m_SeparatorCells);
      result.append(fixLength(name, m_ColumnWidth, true));
    }
    
    result.append("\n");

    // separator
    max = header.size() * m_ColumnWidth + m_SeparatorCells.length() * (m_ColumnWidth - 1);
    sep = new StringBuilder();
    while (sep.length() < max)
      sep.append(m_SeparatorHeader);
    if (sep.length() > max)
      sep.delete(max, sep.length());
    
    result.append(sep.toString());

    return result.toString();
  }

  /**
   * Performs the actual generation of a row from the raw data.
   * 
   * @param data	the data of the row, elements can be null (= missing)
   * @return		the dataset structure
   */
  @Override
  protected String doGenerateRow(List data) {
    StringBuilder	result;
    int			i;
    Object		value;
    
    result = new StringBuilder();
    for (i = 0; i < data.size(); i++) {
      value = data.get(i);
      if (result.length() > 0)
	result.append(m_SeparatorCells);
      if (value == null)
	result.append(fixLength(m_MissingValue, m_ColumnWidth, m_HeaderDefinition.getType(i) != DataType.NUMERIC));
      else
	result.append(fixLength(value.toString(), m_ColumnWidth, m_HeaderDefinition.getType(i) != DataType.NUMERIC));
    }
    
    return result.toString();
  }
}
