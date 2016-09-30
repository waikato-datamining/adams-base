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

/*
 * AbstractScatterPlotHitDetector.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.gui.visualization.core.plot.AbstractDistanceBasedHitDetector;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.awt.event.MouseEvent;

/**
 * Ancestor for scatter plot hit detectors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractScatterPlotHitDetector
  extends AbstractDistanceBasedHitDetector {

  /** for serialization. */
  private static final long serialVersionUID = 8048373104725687691L;

  /** the owner of this detector. */
  protected AbstractScatterPlotPaintlet m_Owner;

  /**
   * Initializes the hit detector (constructor only for GOE) with no owner.
   */
  public AbstractScatterPlotHitDetector() {
    this(null);
  }

  /**
   * Initializes the hit detector.
   *
   * @param owner	the paintlet that uses this detector
   */
  public AbstractScatterPlotHitDetector(AbstractScatterPlotPaintlet owner) {
    super();
    setOwner(owner);
  }

  /**
   * Sets the owner.
   *
   * @param value	the owning panel
   */
  public void setOwner(AbstractScatterPlotPaintlet value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owning paintlet
   */
  public AbstractScatterPlotPaintlet getOwner() {
    return m_Owner;
  }

  /**
   * Determines the indices of the X values that are closest to the
   * provided x.
   *
   * @param x		the X value to look for
   * @param wobble	the "fuzziness"
   * @return		the indices
   */
  protected int[] findClosestXs(double x, double wobble) {
    TIntList 	result;
    int		i;
    Row		row;
    Cell 	cell;
    double	value;

    result = new TIntArrayList();

    for (i = 0; i < m_Owner.getData().getRowCount(); i++) {
      row = m_Owner.getData().getRow(i);
      if (row.hasCell(m_Owner.getXIndex()) && row.hasCell(m_Owner.getYIndex())) {
	cell = row.getCell(m_Owner.getXIndex());
	if (!cell.isMissing() && cell.isNumeric()) {
	  value = cell.toDouble();
	  if ((value >= x - wobble) && (value <= x + wobble))
	    result.add(i);
	}
      }
    }

    return result.toArray();
  }

  /**
   * Checks for a hit.
   *
   * @param e		the MouseEvent (for coordinates)
   * @return		the associated object with the hit, otherwise null
   */
  @Override
  protected abstract Object isHit(MouseEvent e);

  /**
   * Performs the action when a hit is detected.
   *
   * @param e		the MouseEvent (for coordinates)
   * @param hit		the object that got determined by the hit
   * @return		the generated appendix for the tiptext
   */
  @Override
  protected Object processHit(MouseEvent e, Object hit) {
    String		result;
    int[] 		rows;
    int			i;

    rows = (int[]) hit;

    result = "";
    for (i = 0; i < rows.length; i++) {
      if (i > 0)
	result += ", ";
      result += "" + (rows[i]+1);
    }

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_Owner = null;

    super.cleanUp();
  }
}
