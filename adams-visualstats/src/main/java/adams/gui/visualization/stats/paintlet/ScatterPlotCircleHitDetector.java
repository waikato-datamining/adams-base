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
 * ScatterPlotCircleHitDetector.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import adams.data.spreadsheet.Row;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.DiameterBasedPaintlet;
import adams.gui.visualization.sequence.MetaXYSequencePaintlet;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.awt.event.MouseEvent;

/**
 * Detects selections of sequence points in the sequence panel.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScatterPlotCircleHitDetector
  extends AbstractScatterPlotHitDetector {

  /** for serialization. */
  private static final long serialVersionUID = -3363546923840405674L;

  /**
   * Initializes the hit detector (constructor only for GOE) with no owner.
   */
  public ScatterPlotCircleHitDetector() {
    this(null);
  }

  /**
   * Initializes the hit detector.
   *
   * @param owner	the paintlet that uses this detector
   */
  public ScatterPlotCircleHitDetector(AbstractScatterPlotPaintlet owner) {
    super(owner);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Detects circular data points.";
  }

  /**
   * Returns the default minimum pixel difference.
   *
   * @return		the minimum
   */
  protected int getDefaultMinimumPixelDifference() {
    return 1;
  }

  /**
   * Checks for a hit.
   *
   * @param e		the MouseEvent (for coordinates)
   * @return		the associated object with the hit, otherwise null
   */
  @Override
  protected Object isHit(MouseEvent e) {
    TIntList		result;
    double		y;
    double		x;
    double		diffY;
    double		diffX;
    double		diffPixel;
    AxisPanel		axisBottom;
    AxisPanel		axisLeft;
    int[]		indices;
    int 		size;
    double		wobble;
    boolean		logging;
    Row			row;
    double		currX;
    double		currY;

    if (m_Owner == null)
      return null;

    result     = new TIntArrayList();
    axisBottom = m_Owner.getPlot().getAxis(Axis.BOTTOM);
    axisLeft   = m_Owner.getPlot().getAxis(Axis.LEFT);
    y          = axisLeft.posToValue(e.getY());
    x          = axisBottom.posToValue(e.getX());
    logging    = isLoggingEnabled();
    size       = 1;
    if (m_Owner instanceof SizeBasedPaintlet) {
      size = ((SizeBasedPaintlet) m_Owner).getSize();
    }
    else if (m_Owner instanceof MetaXYSequencePaintlet) {
      if (((MetaXYSequencePaintlet) m_Owner).getPaintlet() instanceof DiameterBasedPaintlet)
        size = ((DiameterBasedPaintlet) ((MetaXYSequencePaintlet) m_Owner).getPaintlet()).getDiameter();
    }
    wobble = Math.abs(axisBottom.posToValue(0) - axisBottom.posToValue(size));

    indices = findClosestXs(x, wobble);
    for (int index: indices) {
      row   = m_Owner.getData().getRow(index);
      currX = row.getCell(m_Owner.getXIndex()).toDouble();
      currY = row.getCell(m_Owner.getYIndex()).toDouble();

      diffX = currX - x;
      diffPixel = Math.abs(axisBottom.valueToPos(diffX) - axisBottom.valueToPos(0));
      if (logging)
	getLogger().info("diff x=" + diffPixel);
      if (diffPixel > m_MinimumPixelDifference + (size / 2))
	continue;
      diffY = currY - y;
      diffPixel = Math.abs(axisLeft.valueToPos(diffY) - axisLeft.valueToPos(0));
      if (logging)
	getLogger().info("diff y=" + diffPixel);
      if (diffPixel > m_MinimumPixelDifference + (size / 2))
	continue;

      // add hit
      if (logging)
	getLogger().info("hit!");
      result.add(index);
    }

    if (result.size() > 0)
      return result.toArray();
    else
      return null;
  }
}
