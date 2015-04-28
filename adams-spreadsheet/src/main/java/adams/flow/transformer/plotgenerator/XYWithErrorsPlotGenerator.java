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
 * XYWithErrorsPlotGenerator.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.plotgenerator;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.container.SequencePlotterContainer.ContentType;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Generator for X&#47;Y plots (with X optional) that attaches error information for Y and optionally X as well. If only one error column is defined, this is interpreted as 'error-delta'; providing two columns is interpreted as 'low' and 'high'.
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
 * <pre>-y-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: YColumn)
 * &nbsp;&nbsp;&nbsp;The column to use for Y; An index is a number starting with 1; column names 
 * &nbsp;&nbsp;&nbsp;(case-sensitive) as well as the following placeholders can be used: first,
 * &nbsp;&nbsp;&nbsp; second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-x-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: XColumn)
 * &nbsp;&nbsp;&nbsp;The (optional) index of the column which values to use as X values in the 
 * &nbsp;&nbsp;&nbsp;plot; An index is a number starting with 1; column names (case-sensitive
 * &nbsp;&nbsp;&nbsp;) as well as the following placeholders can be used: first, second, third,
 * &nbsp;&nbsp;&nbsp; last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-y-error-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: YErrorColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to use for Y error information (1=delta, 2=low&#47;high
 * &nbsp;&nbsp;&nbsp;); A range is a comma-separated list of single 1-based indices or sub-ranges 
 * &nbsp;&nbsp;&nbsp;of indices ('start-end'); 'inv(...)' inverts the range '...'; column names 
 * &nbsp;&nbsp;&nbsp;(case-sensitive) as well as the following placeholders can be used: first,
 * &nbsp;&nbsp;&nbsp; second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-x-error-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: XErrorColumns)
 * &nbsp;&nbsp;&nbsp;The (optional) range of columns to use for X error information (1=delta, 
 * &nbsp;&nbsp;&nbsp;2=low&#47;high); A range is a comma-separated list of single 1-based indices 
 * &nbsp;&nbsp;&nbsp;or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...
 * &nbsp;&nbsp;&nbsp;'; column names (case-sensitive) as well as the following placeholders can 
 * &nbsp;&nbsp;&nbsp;be used: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
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
public class XYWithErrorsPlotGenerator
  extends AbstractPlotGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 2881757430571628883L;

  /** the column to use as Y value. */
  protected SpreadSheetColumnIndex m_YColumn;

  /** the column to use as X value (optional). */
  protected SpreadSheetColumnIndex m_XColumn;

  /** the columns to use as Y error, 1=delta, 2=low/high. */
  protected SpreadSheetColumnRange m_YErrorColumns;

  /** the columns to use as X error, 1=delta, 2=low/high (optional). */
  protected SpreadSheetColumnRange m_XErrorColumns;
  
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
	"Generator for X/Y plots (with X optional) that attaches error "
	+ "information for Y and optionally X as well. If only one error "
	+ "column is defined, this is interpreted as 'error-delta'; providing "
	+ "two columns is interpreted as 'low' and 'high'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "y-column", "YColumn",
	    new SpreadSheetColumnIndex());

    m_OptionManager.add(
	    "x-column", "XColumn",
	    new SpreadSheetColumnIndex());

    m_OptionManager.add(
	    "y-error-columns", "YErrorColumns",
	    new SpreadSheetColumnRange());

    m_OptionManager.add(
	    "x-error-columns", "XErrorColumns",
	    new SpreadSheetColumnRange());

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
    
    m_YColumn         = new SpreadSheetColumnIndex();
    m_XColumn         = new SpreadSheetColumnIndex();
    m_YErrorColumns   = new SpreadSheetColumnRange();
    m_XErrorColumns   = new SpreadSheetColumnRange();
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
    result += QuickInfoHelper.toString(this, "YColumn", getYColumn(), ", y: ");
    result += QuickInfoHelper.toString(this, "XColumn", getXColumn(), ", x: ");
    result += QuickInfoHelper.toString(this, "YErrorColumns", (getYErrorColumns().isEmpty() ? "-none-" : getYErrorColumns()), ", y error: ");
    result += QuickInfoHelper.toString(this, "XErrorColumns", (getXErrorColumns().isEmpty() ? "-none-" : getXErrorColumns()), ", x error: ");
    result += QuickInfoHelper.toString(this, "metaDataColumns", (getMetaDataColumns().isEmpty() ? "-none-" : getMetaDataColumns()), ", meta-data: ");
    
    return result;
  }

  /**
   * Sets the y column to use in the plot.
   *
   * @param value	the column
   */
  public void setYColumn(SpreadSheetColumnIndex value) {
    m_YColumn = value;
    reset();
  }

  /**
   * Returns the y column to use in the plot.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getYColumn() {
    return m_YColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YColumnTipText() {
    return "The column to use for Y; " + m_YColumn.getExample();
  }

  /**
   * Sets the index of the column which values to use as X values.
   *
   * @param value	the column index
   */
  public void setXColumn(SpreadSheetColumnIndex value) {
    m_XColumn = value;
    reset();
  }

  /**
   * Returns the index of the column which values to use as X values.
   *
   * @return		the column index
   */
  public SpreadSheetColumnIndex getXColumn() {
    return m_XColumn;
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
   * Sets the column range to use for Y error information (1=delta, 2=low/high).
   *
   * @param value	the column range
   */
  public void setYErrorColumns(SpreadSheetColumnRange value) {
    m_YErrorColumns = value;
    reset();
  }

  /**
   * Returns the column range to use for Y error information (1=delta, 2=low/high).
   *
   * @return		the column range
   */
  public SpreadSheetColumnRange getYErrorColumns() {
    return m_YErrorColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YErrorColumnsTipText() {
    return "The range of columns to use for Y error information (1=delta, 2=low/high); " + m_YErrorColumns.getExample();
  }

  /**
   * Sets the column range to use for X error information (1=delta, 2=low/high).
   *
   * @param value	the column range
   */
  public void setXErrorColumns(SpreadSheetColumnRange value) {
    m_XErrorColumns = value;
    reset();
  }

  /**
   * Returns the column range to use for X error information (1=delta, 2=low/high).
   *
   * @return		the column range
   */
  public SpreadSheetColumnRange getXErrorColumns() {
    return m_XErrorColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XErrorColumnsTipText() {
    return "The (optional) range of columns to use for X error information (1=delta, 2=low/high); " + m_XErrorColumns.getExample();
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
    int[]	cols;
    
    super.check(sheet);

    m_YColumn.setSpreadSheet(sheet);
    if (m_YColumn.getIntIndex() == -1)
      throw new IllegalStateException("No Y column defined/available?");
    
    m_XColumn.setSpreadSheet(sheet);

    m_YErrorColumns.setSpreadSheet(sheet);
    cols = m_YErrorColumns.getIntIndices();
    if (cols.length > 2)
      throw new IllegalStateException("Only max. 2 columns can be used for the Y error!");

    m_XErrorColumns.setSpreadSheet(sheet);
    cols = m_XErrorColumns.getIntIndices();
    if (cols.length > 2)
      throw new IllegalStateException("Only max. 2 columns can be used for the X error!");

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
    int					xCol;
    int					yCol;
    int[]				yErrCols;
    int[]				xErrCols;
    String				plotName;
    Comparable				x;
    Comparable				y;
    Double[]				errX;
    Double[]				errY;
    int[]				metaCols;

    result = new ArrayList<SequencePlotterContainer>();

    yErrCols   = m_YErrorColumns.getIntIndices();
    xErrCols   = m_XErrorColumns.getIntIndices();
    yCol       = m_YColumn.getIntIndex(); 
    xCol       = m_XColumn.getIntIndex();
    metaCols   = m_MetaDataColumns.getIntIndices();

    // create plot containers
    plotName = sheet.getHeaderRow().getCell(yCol).getContent();
    for (i = 0; i < sheet.getRowCount(); i++) {
      row  = sheet.getRow(i);
      errX = null;
      errY = null;
      
      // coordinates
      if (xCol == -1)
	x = i + 1;
      else
	x = getCellValue(row, xCol);
      y = getCellValue(row, yCol);
      
      // errors?
      if (xErrCols.length > 0) {
	errX = new Double[xErrCols.length];
	for (n = 0; n < xErrCols.length; n++)
	  errX[n] = (Double) getCellValue(row, xErrCols[n]);
      }
      if (yErrCols.length > 0) {
	errY = new Double[yErrCols.length];
	for (n = 0; n < yErrCols.length; n++)
	  errY[n] = (Double) getCellValue(row, yErrCols[n]);
      }
      
      // container
      cont = new SequencePlotterContainer(getActualPlotName(row, plotName), x, y, errX, errY, ContentType.PLOT);
      // meta-data
      for (m = 0; m < metaCols.length; m++)
	cont.addMetaData(sheet.getColumnName(metaCols[m]), getCellObject(row, metaCols[m], null));
      // add container
      result.add(cont);
    }

    return result;
  }
}
