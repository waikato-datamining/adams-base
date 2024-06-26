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
 * SpreadSheetColumnFinderToRange.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.columnfinder.ColumnFinder;
import adams.data.spreadsheet.columnfinder.NullFinder;

/**
 <!-- globalinfo-start -->
 * Turns the columns that the specified column finder locates into a 1-based range string. Instead of a compressed range string (eg 1-4) it is also possible to output the individual indices (eg 1,2,3,4).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-column-finder &lt;adams.data.spreadsheet.columnfinder.ColumnFinder&gt; (property: columnFinder)
 * &nbsp;&nbsp;&nbsp;The column finder to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.columnfinder.NullFinder
 * </pre>
 * 
 * <pre>-individual-indices &lt;boolean&gt; (property: individualIndices)
 * &nbsp;&nbsp;&nbsp;If enabled then individual indices (1,2,3,4) are output instead of compressed 
 * &nbsp;&nbsp;&nbsp;ranges (1-4).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-index-separator &lt;java.lang.String&gt; (property: indexSeparator)
 * &nbsp;&nbsp;&nbsp;The separator to use when outputting individual indices, eg comma or blank.
 * &nbsp;&nbsp;&nbsp;default: ,
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6556 $
 */
public class SpreadSheetColumnFinderToRange
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 4117708470154504868L;
  
  /** the ColumnFinder to apply. */
  protected ColumnFinder m_ColumnFinder;

  /** whether to output individual indices (1,2,3,4) rather than compressed ranges (1-4). */
  protected boolean m_IndividualIndices;
  
  /** the separator for the individual indices. */
  protected String m_IndexSeparator;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Turns the columns that the specified column finder locates into a "
	+ "1-based range string. Instead of a compressed range string (eg 1-4) "
	+ "it is also possible to output the individual indices (eg 1,2,3,4).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "column-finder", "columnFinder",
	    new NullFinder());

    m_OptionManager.add(
	    "individual-indices", "individualIndices",
	    false);

    m_OptionManager.add(
	    "index-separator", "indexSeparator",
	    ",");
  }

  /**
   * Sets the column finder to use.
   *
   * @param value	the column finder
   */
  public void setColumnFinder(ColumnFinder value) {
    m_ColumnFinder = value;
    reset();
  }

  /**
   * Returns the column finder in use.
   *
   * @return		the column finder
   */
  public ColumnFinder getColumnFinder() {
    return m_ColumnFinder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String columnFinderTipText() {
    return "The column finder to use.";
  }

  /**
   * Sets whether to output individual indices (1,2,3,4) instead of
   * compressed ranges (1-4).
   *
   * @param value	true if to output individual indices
   */
  public void setIndividualIndices(boolean value) {
    m_IndividualIndices = value;
    reset();
  }

  /**
   * Returns whether to output individual indices (1,2,3,4) instead of
   * compressed ranges (1-4).
   *
   * @return		true if to output individual indices
   */
  public boolean getIndividualIndices() {
    return m_IndividualIndices;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String individualIndicesTipText() {
    return "If enabled then individual indices (1,2,3,4) are output instead of compressed ranges (1-4).";
  }

  /**
   * Sets the separator for the individual indices.
   *
   * @param value	the index separator
   */
  public void setIndexSeparator(String value) {
    if (value.length() > 0) {
      m_IndexSeparator = value;
      reset();
    }
    else {
      getLogger().warning("Index separator cannot be empty!");
    }
  }

  /**
   * Returns the separator for the individual indices.
   *
   * @return		the index separator
   */
  public String getIndexSeparator() {
    return m_IndexSeparator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String indexSeparatorTipText() {
    return "The separator to use when outputting individual indices, eg comma or blank.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "columnFinder", m_ColumnFinder, "column finder: ");
    result += QuickInfoHelper.toString(this, "individualIndices", (m_IndividualIndices ? "indices" : "range"), ", type: ");
    result += QuickInfoHelper.toString(this, "indexSeparator", "'" + m_IndexSeparator + "'", ", separator: ");
    
    return result;
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
   * @thcolumns Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    String		result;
    SpreadSheet		sheet;
    int[]		columns;
    Range		range;
    StringBuilder	indices;
    
    sheet   = (SpreadSheet) m_Input;
    columns = m_ColumnFinder.findColumns(sheet);
    
    if (m_IndividualIndices) {
      indices = new StringBuilder();
      for (int index: columns) {
	if (indices.length() > 0)
	  indices.append(m_IndexSeparator);
	indices.append("" + (index + 1));
      }
      result = indices.toString();
    }
    else {
      range  = new Range();
      range.setIndices(columns);
      result = range.getRange();
    }
    
    return result;
  }
}
