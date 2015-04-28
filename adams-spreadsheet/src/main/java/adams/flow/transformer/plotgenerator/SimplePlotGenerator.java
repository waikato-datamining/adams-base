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
 * SimplePlotGenerator.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.plotgenerator;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.container.SequencePlotterContainer;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Plots the numeric data from one or more columns. The plot name is the column name and the X value is the row index.
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
 * <pre>-columns &lt;java.lang.String&gt; (property: plotColumns)
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimplePlotGenerator
  extends AbstractPlotGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -1679247321811941656L;
  
  /** the columns to plot. */
  protected SpreadSheetColumnRange m_PlotColumns;
  
  /** the meta-data columns. */
  protected SpreadSheetColumnRange m_MetaDataColumns;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Plots the numeric data from one or more columns. "
	+ "The plot name is the column name and the X value is the row index.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "columns", "plotColumns",
	    "");

    m_OptionManager.add(
	    "meta-data-columns", "metaDataColumns",
	    "");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_PlotColumns     = new SpreadSheetColumnRange();
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

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "plotColumns", (getPlotColumns().isEmpty() ? "-none-" : getPlotColumns()), ", cols: ");
    result += QuickInfoHelper.toString(this, "metaDataColumns", (getMetaDataColumns().isEmpty() ? "-none-" : getMetaDataColumns()), ", meta-data: ");
    
    return result;
  }

  /**
   * Sets the column range to use in the plot.
   *
   * @param value	the column range
   */
  public void setPlotColumns(String value) {
    m_PlotColumns.setRange(value);
    reset();
  }

  /**
   * Returns the current column range to use in the plot.
   *
   * @return		the column range
   */
  public String getPlotColumns() {
    return m_PlotColumns.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plotColumnsTipText() {
    return "The range of columns to include in the plot; " + m_PlotColumns.getExample();
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
   * Checks the spreadsheet.
   * 
   * @param sheet	the sheet to check
   */
  @Override
  protected void check(SpreadSheet sheet) {
    super.check(sheet);

    m_PlotColumns.setSpreadSheet(sheet);
    if (m_PlotColumns.getIntIndices().length == 0)
      throw new IllegalStateException("No plot columns defined/available?");
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
    int[]				plotCols;
    String[]				plotNames;
    int[]				metaCols;

    result = new ArrayList<SequencePlotterContainer>();

    // create plot containers
    plotCols  = m_PlotColumns.getIntIndices();
    plotNames = new String[plotCols.length];
    metaCols  = m_MetaDataColumns.getIntIndices();
    for (i = 0; i < plotNames.length; i++)
      plotNames[i] = sheet.getHeaderRow().getCell(plotCols[i]).getContent();
    for (i = 0; i < sheet.getRowCount(); i++) {
      row = sheet.getRow(i);
      for (n = 0; n < plotCols.length; n++) {
	cont = new SequencePlotterContainer(getActualPlotName(row, plotNames[n]), new Double(i), getCellValue(row, plotCols[n]));
	// meta-data
	for (m = 0; m < metaCols.length; m++)
	  cont.addMetaData(sheet.getColumnName(metaCols[m]), getCellObject(row, metaCols[m], null));
	// container
	result.add(cont);
      }
    }

    return result;
  }
}
