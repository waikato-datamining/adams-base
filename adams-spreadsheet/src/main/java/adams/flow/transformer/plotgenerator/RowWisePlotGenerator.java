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
 * RowWisePlotGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.plotgenerator;

import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.container.SequencePlotterContainer;

/**
 <!-- globalinfo-start -->
 * Generates a separate plot per row in the spreadsheet, using the specified columns as data points. Additional meta-data columns can be specified as well. The optional ID column can be used to name the row plots.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-default-cell-value &lt;double&gt; (property: defaultCellValue)
 * &nbsp;&nbsp;&nbsp;The default value for missing or non-numeric cells.
 * &nbsp;&nbsp;&nbsp;default: -1.0
 * </pre>
 * 
 * <pre>-id-column &lt;java.lang.String&gt; (property: IDColumn)
 * &nbsp;&nbsp;&nbsp;The (optional) column to use for naming the row plot; An index is a number 
 * &nbsp;&nbsp;&nbsp;starting with 1; column names (case-sensitive) as well as the following 
 * &nbsp;&nbsp;&nbsp;placeholders can be used: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-data-columns &lt;java.lang.String&gt; (property: dataColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to include in the plot; A range is a comma-separated 
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); '
 * &nbsp;&nbsp;&nbsp;inv(...)' inverts the range '...'; column names (case-sensitive) as well 
 * &nbsp;&nbsp;&nbsp;as the following placeholders can be used: first, second, third, last_2, 
 * &nbsp;&nbsp;&nbsp;last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-meta-data-columns &lt;java.lang.String&gt; (property: metaDataColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to add as meta-data in the plot; A range is a comma-separated 
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); '
 * &nbsp;&nbsp;&nbsp;inv(...)' inverts the range '...'; column names (case-sensitive) as well 
 * &nbsp;&nbsp;&nbsp;as the following placeholders can be used: first, second, third, last_2, 
 * &nbsp;&nbsp;&nbsp;last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-add-column-name &lt;boolean&gt; (property: addColumnName)
 * &nbsp;&nbsp;&nbsp;If enabled, the column name gets added to the meta-data.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8739 $
 */
public class RowWisePlotGenerator
  extends AbstractPlotGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -1679247321811941656L;
  
  /** the (optional) column that identifies a row, uses as plot name. */
  protected SpreadSheetColumnIndex m_IDColumn;
  
  /** the columns to plot. */
  protected SpreadSheetColumnRange m_DataColumns;
  
  /** the meta-data columns. */
  protected SpreadSheetColumnRange m_MetaDataColumns;

  /** whether to add the column name as meta-data. */
  protected boolean m_AddColumnName;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a separate plot per row in the spreadsheet, using the "
	+ "specified columns as data points. Additional meta-data columns "
	+ "can be specified as well. The optional ID column can be used "
	+ "to name the row plots.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "id-column", "IDColumn",
	    "");

    m_OptionManager.add(
	    "data-columns", "dataColumns",
	    "");

    m_OptionManager.add(
	    "meta-data-columns", "metaDataColumns",
	    "");

    m_OptionManager.add(
	    "add-column-name", "addColumnName",
	    false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_IDColumn        = new SpreadSheetColumnIndex();
    m_DataColumns     = new SpreadSheetColumnRange();
    m_MetaDataColumns = new SpreadSheetColumnRange();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;
    
    result  = QuickInfoHelper.toString(this, "IDColumn", (getIDColumn().isEmpty() ? "-none-" : getIDColumn()), "ID: ");
    result += QuickInfoHelper.toString(this, "dataColumns", (getDataColumns().isEmpty() ? "-none-" : getDataColumns()), ", data: ");
    result += QuickInfoHelper.toString(this, "metaDataColumns", (getMetaDataColumns().isEmpty() ? "-none-" : getMetaDataColumns()), ", meta-data: ");
    value   = QuickInfoHelper.toString(this, "addColumnName", m_AddColumnName, "col name", ", ");
    if (value != null)
      result += value;
    
    return result;
  }

  /**
   * Sets the (optional) column to use for naming the row plots.
   *
   * @param value	the column
   */
  public void setIDColumn(String value) {
    m_IDColumn.setIndex(value);
    reset();
  }

  /**
   * Returns the (optional) column to use for naming the row plots.
   *
   * @return		the column
   */
  public String getIDColumn() {
    return m_IDColumn.getIndex();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String IDColumnTipText() {
    return "The (optional) column to use for naming the row plot; " + m_IDColumn.getExample();
  }

  /**
   * Sets the column range to use in the plot.
   *
   * @param value	the column range
   */
  public void setDataColumns(String value) {
    m_DataColumns.setRange(value);
    reset();
  }

  /**
   * Returns the current column range to use in the plot.
   *
   * @return		the column range
   */
  public String getDataColumns() {
    return m_DataColumns.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataColumnsTipText() {
    return "The range of columns to include in the plot; " + m_DataColumns.getExample();
  }

  /**
   * Sets the column range to add as meta-data in the plot.
   *
   * @param value	the column range
   */
  public void setMetaDataColumns(String value) {
    m_MetaDataColumns.setRange(value);
    reset();
  }

  /**
   * Returns the current column range to add as meta-data in the plot.
   *
   * @return		the column range
   */
  public String getMetaDataColumns() {
    return m_MetaDataColumns.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataColumnsTipText() {
    return "The range of columns to add as meta-data in the plot; " + m_MetaDataColumns.getExample();
  }

  /**
   * Sets whether to add the column name to the meta-data.
   *
   * @param value	true if to add column name
   */
  public void setAddColumnName(boolean value) {
    m_AddColumnName = value;
    reset();
  }

  /**
   * Returns whether to add the column name to the meta-data.
   *
   * @return		true if to add column name
   */
  public boolean getAddColumnName() {
    return m_AddColumnName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addColumnNameTipText() {
    return "If enabled, the column name gets added to the meta-data.";
  }

  /**
   * Checks the spreadsheet.
   * 
   * @param sheet	the sheet to check
   */
  @Override
  protected void check(SpreadSheet sheet) {
    super.check(sheet);

    m_IDColumn.setSpreadSheet(sheet);
    m_DataColumns.setSpreadSheet(sheet);
    if (m_DataColumns.getIntIndices().length == 0)
      throw new IllegalStateException("No data columns defined/available?");
    m_MetaDataColumns.setSpreadSheet(sheet);
  }
  
  /**
   * Performs the actual generation of containers.
   * 
   * @param sheet	the basis for the containers
   * @return		the generated containers
   */
  @Override
  protected List<SequencePlotterContainer> doGenerate(SpreadSheet sheet) {
    ArrayList<SequencePlotterContainer>	result;
    SequencePlotterContainer		cont;
    int					i;
    int					n;
    int					m;
    Row					row;
    int					idCol;
    int[]				dataCols;
    int[]				metaCols;
    String				name;
    Comparable				comp;

    result = new ArrayList<SequencePlotterContainer>();

    // create plot containers
    idCol     = m_IDColumn.getIntIndex();
    dataCols  = m_DataColumns.getIntIndices();
    metaCols  = m_MetaDataColumns.getIntIndices();
    for (i = 0; i < sheet.getRowCount(); i++) {
      row = sheet.getRow(i);
      if (idCol == -1)
	name = "" + (i+1);
      else
	name = getCellString(row, idCol);
      for (n = 0; n < dataCols.length; n++) {
	comp = getCellValue(row, dataCols[n], null);
	if (comp == null)
	  continue;
	cont = new SequencePlotterContainer(name, new Double(n), comp);
	// meta-data
	for (m = 0; m < metaCols.length; m++)
	  cont.addMetaData(sheet.getColumnName(metaCols[m]), getCellObject(row, metaCols[m], null));
	if (m_AddColumnName)
	  cont.addMetaData("column", sheet.getColumnName(dataCols[n]));
	// container
	result.add(cont);
      }
    }

    return result;
  }
}
