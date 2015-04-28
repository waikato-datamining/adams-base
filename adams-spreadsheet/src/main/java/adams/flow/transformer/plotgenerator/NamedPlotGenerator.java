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
 * NamedPlotGenerator.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.plotgenerator;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.container.SequencePlotterContainer;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Plots the numeric data of two columns, X and Y with the plot name from a separate column.
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
 * <pre>-plot-name-range &lt;java.lang.String&gt; (property: plotNameRange)
 * &nbsp;&nbsp;&nbsp;The range of columns to use for generating the plot name (overrides any 
 * &nbsp;&nbsp;&nbsp;plot generator specific names); A range is a comma-separated list of single 
 * &nbsp;&nbsp;&nbsp;1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts 
 * &nbsp;&nbsp;&nbsp;the range '...'; column names (case-sensitive) as well as the following 
 * &nbsp;&nbsp;&nbsp;placeholders can be used: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-plot-name-separator &lt;java.lang.String&gt; (property: plotNameSeparator)
 * &nbsp;&nbsp;&nbsp;The separator to use when constructing the plot name from cell values.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-x-column &lt;java.lang.String&gt; (property: XColumn)
 * &nbsp;&nbsp;&nbsp;The index of the column which values to use as X values in the plot; An 
 * &nbsp;&nbsp;&nbsp;index is a number starting with 1; column names (case-sensitive) as well 
 * &nbsp;&nbsp;&nbsp;as the following placeholders can be used: first, second, third, last_2, 
 * &nbsp;&nbsp;&nbsp;last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-y-column &lt;java.lang.String&gt; (property: YColumn)
 * &nbsp;&nbsp;&nbsp;The index of the column which values to use as Y values in the plot; An 
 * &nbsp;&nbsp;&nbsp;index is a number starting with 1; column names (case-sensitive) as well 
 * &nbsp;&nbsp;&nbsp;as the following placeholders can be used: first, second, third, last_2, 
 * &nbsp;&nbsp;&nbsp;last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-name-column &lt;java.lang.String&gt; (property: nameColumn)
 * &nbsp;&nbsp;&nbsp;The index of the column which values to use as for naming the plots; An 
 * &nbsp;&nbsp;&nbsp;index is a number starting with 1; column names (case-sensitive) as well 
 * &nbsp;&nbsp;&nbsp;as the following placeholders can be used: first, second, third, last_2, 
 * &nbsp;&nbsp;&nbsp;last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NamedPlotGenerator
  extends AbstractPlotGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 114825117464139953L;

  /** the X column. */
  protected SpreadSheetColumnIndex m_XColumn;
  
  /** the Y column. */
  protected SpreadSheetColumnIndex m_YColumn;

  /** the column for the plot name. */
  protected SpreadSheetColumnIndex m_NameColumn;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Plots the numeric data of two columns, X and Y with the plot name from a separate column.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "x-column", "XColumn",
	    "");

    m_OptionManager.add(
	    "y-column", "YColumn",
	    "");

    m_OptionManager.add(
	    "name-column", "nameColumn",
	    "");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_XColumn    = new SpreadSheetColumnIndex();
    m_YColumn    = new SpreadSheetColumnIndex();
    m_NameColumn = new SpreadSheetColumnIndex();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	variable;

    result = super.getQuickInfo();

    result = ", x: ";
    variable = getOptionManager().getVariableForProperty("XColumn");
    if (variable != null)
      result += variable;
    else
      result += m_XColumn.getIndex();
    
    result   += ", y: ";
    variable = getOptionManager().getVariableForProperty("YColumn");
    if (variable != null)
      result += variable;
    else
      result += m_YColumn.getIndex();
    
    result   += ", name: ";
    variable = getOptionManager().getVariableForProperty("nameColumn");
    if (variable != null)
      result += variable;
    else
      result += m_NameColumn.getIndex();
    
    return result;
  }

  /**
   * Sets the index of the column which values to use as X values.
   *
   * @param value	the column index
   */
  public void setXColumn(String value) {
    m_XColumn.setIndex(value);
    reset();
  }

  /**
   * Returns the index of the column which values to use as X values.
   *
   * @return		the column index
   */
  public String getXColumn() {
    return m_XColumn.getIndex();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XColumnTipText() {
    return "The index of the column which values to use as X values in the plot; " + m_XColumn.getExample();
  }

  /**
   * Sets the index of the column which values to use as Y values.
   *
   * @param value	the column index
   */
  public void setYColumn(String value) {
    m_YColumn.setIndex(value);
    reset();
  }

  /**
   * Returns the index of the column which values to use as Y values.
   *
   * @return		the column index
   */
  public String getYColumn() {
    return m_YColumn.getIndex();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YColumnTipText() {
    return "The index of the column which values to use as Y values in the plot; " + m_YColumn.getExample();
  }

  /**
   * Sets the index of the column which values to use to name the plots.
   *
   * @param value	the column index
   */
  public void setNameColumn(String value) {
    m_NameColumn.setIndex(value);
    reset();
  }

  /**
   * Returns the index of the column which values to use to name the plots.
   *
   * @return		the column index
   */
  public String getNameColumn() {
    return m_NameColumn.getIndex();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nameColumnTipText() {
    return "The index of the column which values to use as for naming the plots; " + m_NameColumn.getExample();
  }

  /**
   * Checks the spreadsheet.
   * 
   * @param sheet	the sheet to check
   */
  @Override
  protected void check(SpreadSheet sheet) {
    super.check(sheet);

    m_XColumn.setSpreadSheet(sheet);
    if (m_XColumn.getIntIndex() == -1)
      throw new IllegalStateException("No X column defined/available?");
    if (!sheet.isNumeric(m_XColumn.getIntIndex()))
      throw new IllegalStateException("X column is not numeric!");

    m_YColumn.setSpreadSheet(sheet);
    if (m_YColumn.getIntIndex() == -1)
      throw new IllegalStateException("No Y column defined/available?");
    if (!sheet.isNumeric(m_YColumn.getIntIndex()))
      throw new IllegalStateException("Y column is not numeric!");

    m_NameColumn.setSpreadSheet(sheet);
    if (m_NameColumn.getIntIndex() == -1)
      throw new IllegalStateException("No name column defined/available?");
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
    Row					row;
    int					xCol;
    int					yCol;
    int					nameCol;

    result = new ArrayList<SequencePlotterContainer>();

    // create plot containers
    xCol    = m_XColumn.getIntIndex();
    yCol    = m_YColumn.getIntIndex();
    nameCol = m_NameColumn.getIntIndex();
    for (i = 0; i < sheet.getRowCount(); i++) {
      row  = sheet.getRow(i);
      cont = new SequencePlotterContainer(
	  getActualPlotName(row, getCellString(row, nameCol)),
	  getCellValue(row, xCol), 
	  getCellValue(row, yCol));
      result.add(cont);
    }

    return result;
  }
}
