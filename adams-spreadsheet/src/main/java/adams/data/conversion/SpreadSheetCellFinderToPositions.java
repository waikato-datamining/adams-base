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
 * SpreadSheetCellFinderToPositions.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.cellfinder.CellFinder;
import adams.data.spreadsheet.cellfinder.CellLocation;
import adams.data.spreadsheet.cellfinder.SingleCell;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Turns the cells that the specified cell finder locates into an array of cell positions (like 'A2').
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-cell-finder &lt;adams.data.spreadsheet.cellfinder.CellFinder&gt; (property: cellFinder)
 * &nbsp;&nbsp;&nbsp;The cell finder to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.cellfinder.SingleCell
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6556 $
 */
public class SpreadSheetCellFinderToPositions
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 4117708470154504868L;
  
  /** the CellFinder to apply. */
  protected CellFinder m_CellFinder;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Turns the cells that the specified cell finder locates into an "
	+ "array of cell positions (like 'A2').";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "cell-finder", "cellFinder",
	    new SingleCell());
  }

  /**
   * Sets the cell finder to use.
   *
   * @param value	the cell finder
   */
  public void setCellFinder(CellFinder value) {
    m_CellFinder = value;
    reset();
  }

  /**
   * Returns the cell finder in use.
   *
   * @return		the cell finder
   */
  public CellFinder getCellFinder() {
    return m_CellFinder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String cellFinderTipText() {
    return "The cell finder to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "cellFinder", m_CellFinder, "cell finder: ");
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
    return String[].class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @thcells Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    List<String>		result;
    SpreadSheet			sheet;
    Iterator<CellLocation>	cells;
    
    sheet  = (SpreadSheet) m_Input;
    cells  = m_CellFinder.findCells(sheet);
    result = new ArrayList<String>();
    while (cells.hasNext())
      result.add(cells.next().toPosition());
    
    return result.toArray(new String[result.size()]);
  }
}
