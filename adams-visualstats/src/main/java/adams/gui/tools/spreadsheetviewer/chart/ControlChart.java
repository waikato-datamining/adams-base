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
 * ControlChart.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetviewer.chart;

import adams.core.QuickInfoHelper;
import adams.data.conversion.SpreadSheetToDoubleMatrix;
import adams.data.conversion.StringToDouble;
import adams.data.spc.CChart;
import adams.data.spc.IndividualsControlChart;
import adams.data.spc.MatrixControlChart;
import adams.data.spc.NullViolations;
import adams.data.spc.SamplesControlChart;
import adams.data.spc.ViolationFinder;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.control.ArrayProcess;
import adams.flow.control.Flow;
import adams.flow.sink.ControlChartPlot;
import adams.flow.transformer.Convert;
import adams.flow.transformer.SpreadSheetInfo;
import adams.flow.transformer.SpreadSheetInfo.InfoType;

/**
 <!-- globalinfo-start -->
 * Generates the specified control chart from the data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the chart dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the chart dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-columns &lt;java.lang.String&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The columns to generate the control chart(s) for; A range is a comma-separated 
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); '
 * &nbsp;&nbsp;&nbsp;inv(...)' inverts the range '...'; column names (case-sensitive) as well 
 * &nbsp;&nbsp;&nbsp;as the following placeholders can be used: first, second, third, last_2, 
 * &nbsp;&nbsp;&nbsp;last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-chart &lt;adams.data.spc.ControlChart&gt; (property: chart)
 * &nbsp;&nbsp;&nbsp;The control chart to generate.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spc.CChart
 * </pre>
 * 
 * <pre>-violation-finder &lt;adams.data.spc.ViolationFinder&gt; (property: violationFinder)
 * &nbsp;&nbsp;&nbsp;The algorithm for locating violations.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spc.NullViolations
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ControlChart
  extends AbstractChartGenerator {

  private static final long serialVersionUID = -2495314289911915476L;

  /** the control chart to generate. */
  protected adams.data.spc.ControlChart m_Chart;

  /** for locating violations. */
  protected ViolationFinder m_ViolationFinder;

  /** the column(s) to generate the chart(s) for. */
  protected SpreadSheetColumnRange m_Columns;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates the specified control chart from the data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "columns", "columns",
	    "");

    m_OptionManager.add(
	    "chart", "chart",
	    new CChart());

    m_OptionManager.add(
	    "violation-finder", "violationFinder",
	    new NullViolations());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Columns = new SpreadSheetColumnRange();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "chart", m_Chart, "chart: ");
    result += QuickInfoHelper.toString(this, "violationFinder", m_ViolationFinder, ", violations: ");
    result += QuickInfoHelper.toString(this, "columns", (m_Columns.isEmpty() ? "-none-" : m_Columns), ", col: ");

    return result;
  }

  /**
   * Sets the control chart to generate.
   *
   * @param value	the chart
   */
  public void setChart(adams.data.spc.ControlChart value) {
    m_Chart = value;
    reset();
  }

  /**
   * Returns the control chart to generate.
   *
   * @return		the chart
   */
  public adams.data.spc.ControlChart getChart() {
    return m_Chart;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String chartTipText() {
    return "The control chart to generate.";
  }

  /**
   * Sets the algorithm for locating violations.
   *
   * @param value	the algorithm
   */
  public void setViolationFinder(ViolationFinder value) {
    m_ViolationFinder = value;
    reset();
  }

  /**
   * Returns the algorithm for locating violations.
   *
   * @return		the algorithm
   */
  public ViolationFinder getViolationFinder() {
    return m_ViolationFinder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String violationFinderTipText() {
    return "The algorithm for locating violations.";
  }

  /**
   * Sets the column range to use for the chart(s).
   *
   * @param value	the column range
   */
  public void setColumns(String value) {
    m_Columns.setRange(value);
    reset();
  }

  /**
   * Returns the column range to use for the chart(s).
   *
   * @return		the column range
   */
  public String getColumns() {
    return m_Columns.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsTipText() {
    return "The columns to generate the control chart(s) for; " + m_Columns.getExample();
  }

  /**
   * Checks whether the spreadsheet can be processed.
   *
   * @param name	the name of the tab/sheet
   * @param sheet	the spreadsheet to check
   */
  protected void check(String name, SpreadSheet sheet) {
    int[]	indices;

    super.check(name, sheet);

    m_Columns.setData(sheet);
    indices = m_Columns.getIntIndices();

    if (getColumns().trim().isEmpty() || (indices.length == 0)) {
      if (m_Chart instanceof IndividualsControlChart)
	throw new IllegalStateException("No column selected!");
      else
	throw new IllegalStateException("No columns selected!");
    }

    if ((m_Chart instanceof IndividualsControlChart) && (indices.length != 1))
      throw new IllegalStateException("Expected 1 column, but " + indices.length + " selected!");
    if ((m_Chart instanceof SamplesControlChart) && (indices.length != 2))
      throw new IllegalStateException("Expected 2 columns, but " + indices.length + " selected!");
    if ((m_Chart instanceof MatrixControlChart) && (indices.length < 2))
      throw new IllegalStateException("Expected at least 2 columns, but " + indices.length + " selected!");
  }

  /**
   * Adds the chart generation to the flow. The flow already contains
   * forwarding of spreadsheet and selecting subset of rows.
   *
   * @param flow	the flow to extend
   * @param name	the name of the tab/sheet
   * @param sheet	the spreadsheet to generate the flow for
   */
  @Override
  protected void addChartGeneration(Flow flow, String name, SpreadSheet sheet) {
    adams.flow.transformer.ControlChart		chart;
    SpreadSheetInfo				info;
    ArrayProcess				proc;
    Convert 					conv;
    SpreadSheetToDoubleMatrix			matrix;
    StringToDouble				todouble;
    ControlChartPlot				plot;

    if (m_Chart instanceof IndividualsControlChart) {
      info = new SpreadSheetInfo();
      info.setColumnIndex(new SpreadSheetColumnIndex(getColumns()));
      info.setType(InfoType.CELL_VALUES);
      info.setOutputArray(true);
      flow.add(info);

      proc = new ArrayProcess();
      flow.add(proc);

      todouble = new StringToDouble();
      conv = new Convert();
      conv.setConversion(todouble);
      proc.add(conv);
    }
    else {
      matrix = new SpreadSheetToDoubleMatrix();
      matrix.setColumns(new SpreadSheetColumnRange(getColumns()));
      conv = new Convert();
      conv.setConversion(matrix);
      flow.add(conv);
    }

    chart = new adams.flow.transformer.ControlChart();
    chart.setChart(m_Chart);
    chart.setViolationFinder(m_ViolationFinder);
    flow.add(chart);

    plot = new ControlChartPlot();
    if (name != null)
      plot.setTitle(name + " - " + m_Chart.getName());
    else
      plot.setTitle("Control chart - " + m_Chart.getName());
    plot.setName(plot.getTitle());
    plot.setShortTitle(true);
    flow.add(plot);
  }
}
