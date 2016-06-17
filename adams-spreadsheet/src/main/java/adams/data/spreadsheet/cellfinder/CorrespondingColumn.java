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
 * CorrespondingColumn.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.cellfinder;

import adams.core.PositionType;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;

import java.util.Iterator;

/**
 <!-- globalinfo-start -->
 * Rather than return a cell that was located using the given cell finder, you can return a corresponding cell (within the same row).<br>
 * This allows you to locate cells with certain value but then update a different column in the same row. You can specify whether the position of the corresponding column is relative (+3 or -4) or absolute (5th).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-finder &lt;adams.data.spreadsheet.cellfinder.CellFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The base cell finder to locate the cells.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.cellfinder.ColumnFinderRange -column-finder adams.data.spreadsheet.columnfinder.ByIndex
 * </pre>
 * 
 * <pre>-corresponding-position &lt;ABSOLUTE|RELATIVE&gt; (property: correspondingPosition)
 * &nbsp;&nbsp;&nbsp;How to interpret the column position.
 * &nbsp;&nbsp;&nbsp;default: RELATIVE
 * </pre>
 * 
 * <pre>-corresponding-column &lt;int&gt; (property: correspondingColumn)
 * &nbsp;&nbsp;&nbsp;The location of the column: 1-based if absolute mode; 0 in relative mode 
 * &nbsp;&nbsp;&nbsp;is the column itself.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CorrespondingColumn
  extends AbstractCellFinder {

  /** for serialization. */
  private static final long serialVersionUID = -3837662374567098995L;

  /**
   * Cell iterator that returns the locations for the corresponding cells.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class CorrespondingColumnIterator
    extends AbstractMetaIterator {

    /** the owning cell finder. */
    protected CorrespondingColumn m_Owner;
    
    /**
     * Initializes the iterator.
     * 
     * @param owner	the cell finder this iterator belongs to
     * @param base	the base iterator
     */
    public CorrespondingColumnIterator(CorrespondingColumn owner, Iterator<CellLocation> base) {
      super(base);
      m_Owner = owner;
    }
    
    /**
     * Processes the cell location.
     * 
     * @param location	the location to process
     * @return		the processed location
     */
    @Override
    protected CellLocation process(CellLocation location) {
      switch (m_Owner.getCorrespondingPosition()) {
	case ABSOLUTE:
	  return new CellLocation(location.getRow(), m_Owner.getCorrespondingColumn() - 1);
	case RELATIVE:
	  return new CellLocation(location.getRow(), location.getColumn() + m_Owner.getCorrespondingColumn());
	default:
	  throw new IllegalStateException("Unhandled position: " + m_Owner.getCorrespondingPosition());
      }
    }
  }
  
  /** the base cell finder to use. */
  protected CellFinder m_Finder;
  
  /** whether the corresponding column is relative or absolute. */
  protected PositionType m_CorrespondingPosition;
  
  /** the position of the corresponding column. */
  protected int m_CorrespondingColumn;
  
  /**
   * @return
   */
  @Override
  public String globalInfo() {
    return 
	"Rather than return a cell that was located using the given cell finder, "
	+ "you can return a corresponding cell (within the same row).\n"
	+ "This allows you to locate cells with certain value but then update a "
	+ "different column in the same row. You can specify whether the "
	+ "position of the corresponding column is relative (+3 or -4) or "
	+ "absolute (5th).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "finder", "finder",
	    new ColumnFinderRange());

    m_OptionManager.add(
	    "corresponding-position", "correspondingPosition",
	    PositionType.RELATIVE);

    m_OptionManager.add(
	    "corresponding-column", "correspondingColumn",
	    0);
  }

  /**
   * Sets the base cell finder.
   *
   * @param value	the finder
   */
  public void setFinder(CellFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the base cell finder.
   *
   * @return		the finder
   */
  public CellFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String finderTipText() {
    return "The base cell finder to locate the cells.";
  }

  /**
   * Sets how to interpret the column position.
   *
   * @param value	the position type
   */
  public void setCorrespondingPosition(PositionType value) {
    m_CorrespondingPosition = value;
    reset();
  }

  /**
   * Returns how to interpret the column position.
   *
   * @return		the position type
   */
  public PositionType getCorrespondingPosition() {
    return m_CorrespondingPosition;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String correspondingPositionTipText() {
    return "How to interpret the column position.";
  }

  /**
   * Sets the column location (abs/rel).
   *
   * @param value	the location
   */
  public void setCorrespondingColumn(int value) {
    m_CorrespondingColumn = value;
    reset();
  }

  /**
   * Returns the column location (abs/rel).
   *
   * @return		the location
   */
  public int getCorrespondingColumn() {
    return m_CorrespondingColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String correspondingColumnTipText() {
    return "The location of the column: 1-based if absolute mode; 0 in relative mode is the column itself.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "finder", m_Finder, "finder: ");
    result += QuickInfoHelper.toString(this, "correspondingPosition", m_CorrespondingPosition, ", type: ");
    result += QuickInfoHelper.toString(this, "correspondingColumn", m_CorrespondingColumn, ", col: ");
    
    return result;
  }

  /**
   * Performs the actual locating.
   * 
   * @param sheet	the sheet to locate the cells in
   * @return		the iterator over the locations
   */
  @Override
  protected Iterator<CellLocation> doFindCells(SpreadSheet sheet) {
    return new CorrespondingColumnIterator(this, m_Finder.findCells(sheet));
  }
}
