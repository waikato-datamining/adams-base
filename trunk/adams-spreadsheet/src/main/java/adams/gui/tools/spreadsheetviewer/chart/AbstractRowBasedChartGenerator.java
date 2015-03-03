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
 * AbstractRowBasedChartGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.chart;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.rowfinder.ByIndex;
import adams.data.spreadsheet.rowfinder.RowFinder;
import adams.flow.control.Flow;
import adams.flow.transformer.SpreadSheetRowFilter;

/**
 * Ancestor for row-based plot generators.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRowBasedChartGenerator
  extends AbstractChartGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -3167297287561137402L;
  
  /** the row finder to use. */
  protected RowFinder m_RowFinder;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "row-finder", "rowFinder",
	    new ByIndex());
  }

  /**
   * Sets the row finder to use for restricting the rows used for the chart.
   *
   * @param value	the row finder
   */
  public void setRowFinder(RowFinder value) {
    m_RowFinder = value;
    reset();
  }

  /**
   * Returns the row finder to use for restricting the rows used for the chart.
   *
   * @return		the row finder
   */
  public RowFinder getRowFinder() {
    return m_RowFinder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowFinderTipText() {
    return "The row finder to use for restricting the rows used for the chart.";
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
    SpreadSheetRowFilter	rf;
    
    rf = new SpreadSheetRowFilter();
    rf.setFinder(m_RowFinder);
    flow.add(rf);
  }
}
