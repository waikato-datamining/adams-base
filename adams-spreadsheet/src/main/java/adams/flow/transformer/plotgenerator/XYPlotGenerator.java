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
 * XYPlotGenerator.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.plotgenerator;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.container.SequencePlotterContainer;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses one column for the X value and one or more other columns as Y to plot against.
 * <br><br>
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
 * <pre>-x-column &lt;java.lang.String&gt; (property: XColumn)
 * &nbsp;&nbsp;&nbsp;The (optional) index of the column which values to use as X values in the 
 * &nbsp;&nbsp;&nbsp;plot; An index is a number starting with 1; column names (case-sensitive
 * &nbsp;&nbsp;&nbsp;) as well as the following placeholders can be used: first, second, third,
 * &nbsp;&nbsp;&nbsp; last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-prefix-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: prefixColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to prefix the plot name with; A range is a comma-separated 
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); '
 * &nbsp;&nbsp;&nbsp;inv(...)' inverts the range '...'; column names (case-sensitive) as well 
 * &nbsp;&nbsp;&nbsp;as the following placeholders can be used: first, second, third, last_2, 
 * &nbsp;&nbsp;&nbsp;last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-separator &lt;java.lang.String&gt; (property: separator)
 * &nbsp;&nbsp;&nbsp;The separator to use between columns.
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
public class XYPlotGenerator
  extends AbstractPlotGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 2881757430571628883L;

  /** the columns to plot. */
  protected SpreadSheetColumnRange m_PlotColumns;

  /** the (optional) column to use as X value. */
  protected SpreadSheetColumnIndex m_XColumn;
  
  /** the columns to prefix the plot name with. */
  protected SpreadSheetColumnRange m_PrefixColumns;
  
  /** the separator string. */
  protected String m_Separator;
  
  /** the meta-data columns. */
  protected SpreadSheetColumnRange m_MetaDataColumns;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses one column for the X value and one or more other columns as Y to plot against.";
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
	    "x-column", "XColumn",
	    "");

    m_OptionManager.add(
	    "prefix-columns", "prefixColumns",
	    new SpreadSheetColumnRange());

    m_OptionManager.add(
	    "separator", "separator",
	    " ");

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
    m_XColumn         = new SpreadSheetColumnIndex();
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
    result += QuickInfoHelper.toString(this, "XColumn", (getXColumn().isEmpty() ? "-none-" : getXColumn()), ", x: ");
    result += QuickInfoHelper.toString(this, "plotColumns", (getPlotColumns().isEmpty() ? "-none-" : getPlotColumns()), ", y cols: ");
    result += QuickInfoHelper.toString(this, "prefixColumns", (getPrefixColumns().isEmpty() ? "-none-" : getPrefixColumns()), ", prefix cols: ");
    result += QuickInfoHelper.toString(this, "separator", getSeparator(), ", separator: ");
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
    return "The (optional) index of the column which values to use as X values in the plot; " + m_XColumn.getExample();
  }

  /**
   * Sets the column range to generate the prefix of the plot with.
   *
   * @param value	the column range
   */
  public void setPrefixColumns(SpreadSheetColumnRange value) {
    m_PrefixColumns = value;
    reset();
  }

  /**
   * Returns the current column range to generate the prefix of the plot with.
   *
   * @return		the column range
   */
  public SpreadSheetColumnRange getPrefixColumns() {
    return m_PrefixColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixColumnsTipText() {
    return "The range of columns to prefix the plot name with; " + m_PlotColumns.getExample();
  }

  /**
   * Sets the separator to use between columns.
   *
   * @param value	the separator
   */
  public void setSeparator(String value) {
    m_Separator = value;
    reset();
  }

  /**
   * Returns the separator in use between columns.
   *
   * @return		the separator
   */
  public String getSeparator() {
    return m_Separator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String separatorTipText() {
    return "The separator to use between columns.";
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
    
    m_XColumn.setSpreadSheet(sheet);
    if (m_XColumn.getIntIndex() == -1)
      throw new IllegalStateException("No X column defined/available?");

    m_PrefixColumns.setSpreadSheet(sheet);
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
    int					xIndex;
    int[]				prefixCols;
    String				prefix;
    int[]				metaCols;

    result = new ArrayList<SequencePlotterContainer>();

    // gather data
    xIndex = m_XColumn.getIntIndex();

    // create plot containers
    plotCols   = m_PlotColumns.getIntIndices();
    plotNames  = new String[plotCols.length];
    prefixCols = m_PrefixColumns.getIntIndices();
    metaCols   = m_MetaDataColumns.getIntIndices();
    for (i = 0; i < plotNames.length; i++)
      plotNames[i] = sheet.getHeaderRow().getCell(plotCols[i]).getContent();
    for (i = 0; i < sheet.getRowCount(); i++) {
      row    = sheet.getRow(i);
      prefix = "";
      if (prefixCols.length > 0) {
	for (n = 0; n < prefixCols.length; n++) {
	  if (n > 0)
	    prefix += m_Separator;
	  if (row.hasCell(prefixCols[n]))
	    prefix += row.getCell(prefixCols[n]).getContent();
	  else
	    prefix += SpreadSheet.MISSING_VALUE;
	}
	prefix += " ";
      }
      for (n = 0; n < plotCols.length; n++) {
	if (xIndex == -1) {
	  cont = new SequencePlotterContainer(getActualPlotName(row, prefix + plotNames[n]), new Double(i), getCellValue(row, plotCols[n]));
	}
	else {
	  if (xIndex == plotCols[n])
	    continue;
	  cont = new SequencePlotterContainer(getActualPlotName(row, prefix + plotNames[n]), getCellValue(row, xIndex), getCellValue(row, plotCols[n]));
	}
	// meta-data
	for (m = 0; m < metaCols.length; m++)
	  cont.addMetaData(sheet.getColumnName(metaCols[m]), getCellObject(row, metaCols[m], null));
	// add container
	result.add(cont);
      }
    }

    return result;
  }
}
