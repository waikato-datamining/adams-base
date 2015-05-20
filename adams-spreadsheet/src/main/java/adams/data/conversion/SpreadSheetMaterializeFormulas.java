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
 * SpreadSheetMaterializeFormulas.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.Iterator;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.cellfinder.AbstractCellFinder;
import adams.data.spreadsheet.cellfinder.CellLocation;
import adams.data.spreadsheet.cellfinder.CellRange;

/**
 <!-- globalinfo-start -->
 * Finds cells with formulas in a spreadsheet and replaces the cell content with the value calculated by the formula.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-finder &lt;adams.data.spreadsheet.cellfinder.AbstractCellFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The cell finder to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.cellfinder.CellRange
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetMaterializeFormulas
  extends AbstractInPlaceSpreadSheetConversion {

  /** for serialization. */
  private static final long serialVersionUID = -4911635975083212510L;
  
  /** for locating the cells. */
  protected AbstractCellFinder m_Finder;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Finds cells with formulas in a spreadsheet and replaces the cell content with the value calculated by the formula.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "finder", "finder",
	    new CellRange());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "finder", m_Finder, "finder: ");
    result += QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no copy", ", ");

    return result;
  }

  /**
   * Sets the cell finder to use.
   *
   * @param value	the finder
   */
  public void setFinder(AbstractCellFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the cell finder to use.
   *
   * @return		the finder
   */
  public AbstractCellFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finderTipText() {
    return "The cell finder to use.";
  }
  
  /**
   * Generates the new spreadsheet from the input.
   * 
   * @param input	the incoming spreadsheet
   * @return		the generated spreadsheet
   * @throws Exception	if conversion fails for some reason
   */
  @Override
  protected SpreadSheet convert(SpreadSheet input) throws Exception {
    SpreadSheet			result;
    Iterator<CellLocation>	cells;
    CellLocation		loc;
    Cell			cell;
    
    if (m_NoCopy)
      result = input;
    else
      result = input.getClone();
    
    cells = m_Finder.findCells(result);
    while (cells.hasNext() && !m_Stopped) {
      loc = cells.next();
      if (!result.hasCell(loc.getRow(), loc.getColumn()))
        continue;
      cell = result.getCell(loc.getRow(), loc.getColumn());
      if (!cell.isFormula())
        continue;
      cell.setContent(cell.getContent());
    }
    
    return result;
  }
}
