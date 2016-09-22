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
 * CrossHitDetector.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.sequence.XYSequenceUtils;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

/**
 * Detects selections of crosses.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8896 $
 */
public class CrossHitDetector
  extends AbstractXYSequencePointHitDetector {

  /** for serialization. */
  private static final long serialVersionUID = -3363546923840405674L;

  /** the default diameter to use. */
  protected int m_Diameter;

  /**
   * Initializes the hit detector (constructor only for GOE) with no owner.
   */
  public CrossHitDetector() {
    this(null);
  }

  /**
   * Initializes the hit detector.
   *
   * @param owner	the paintlet that uses this detector
   */
  public CrossHitDetector(XYSequencePaintlet owner) {
    super(owner);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Detects selections of crosses.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "diameter", "diameter",
      7, 1, null);
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
   * Sets the cross diameter.
   *
   * @param value	the diameter
   */
  public void setDiameter(int value) {
    m_Diameter = value;
    reset();
  }

  /**
   * Returns the diameter of the cross.
   *
   * @return		the diameter
   */
  public int getDiameter() {
    return m_Diameter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String diameterTipText() {
    return "The diameter of the cross in pixels.";
  }

  /**
   * Checks for a hit.
   *
   * @param e		the MouseEvent (for coordinates)
   * @return		the associated object with the hit, otherwise null
   */
  @Override
  protected Object isHit(MouseEvent e) {
    double			y;
    double			x;
    double			diffY;
    double			diffX;
    double			diffPixel;
    int				i;
    XYSequence			s;
    XYSequencePoint		sp;
    Vector<XYSequencePoint>	result;
    AxisPanel			axisBottom;
    AxisPanel			axisLeft;
    int				index;
    List<XYSequencePoint>	points;
    int				diameter;
    boolean			logging;
    double			diameterActual;
    int				fromIndex;
    int				toIndex;

    if (m_Owner == null)
      return null;

    result     = new Vector<>();
    axisBottom = m_Owner.getPlot().getAxis(Axis.BOTTOM);
    axisLeft   = m_Owner.getPlot().getAxis(Axis.LEFT);
    y          = axisLeft.posToValue(e.getY());
    x          = axisBottom.posToValue(e.getX());
    logging    = isLoggingEnabled();
    diameter   = 1;
    if (m_Owner instanceof DiameterBasedPaintlet) {
      diameter = ((DiameterBasedPaintlet) m_Owner).getDiameter();
    }
    else if (m_Owner instanceof MetaXYSequencePaintlet) {
      if (((MetaXYSequencePaintlet) m_Owner).getPaintlet() instanceof DiameterBasedPaintlet)
        diameter = ((DiameterBasedPaintlet) ((MetaXYSequencePaintlet) m_Owner).getPaintlet()).getDiameter();
    }
    diameterActual = Math.abs(axisBottom.posToValue(diameter));

    for (i = 0; i < m_Owner.getSequencePanel().getContainerManager().count(); i++) {
      if (!m_Owner.getSequencePanel().getContainerManager().get(i).isVisible())
	continue;

      // check for hit
      s      = m_Owner.getSequencePanel().getContainerManager().get(i).getData();
      points = s.toList();

      if (logging)
	getLogger().info("\n" + s.getID() + ":");

      fromIndex = XYSequenceUtils.findClosestX(points, x - diameterActual);
      toIndex   = XYSequenceUtils.findClosestX(points, x + diameterActual);
      if ((fromIndex == -1) || (toIndex == -1))
	continue;
      
      for (index = fromIndex; index <= toIndex; index++) {
	sp = points.get(index);

	diffX     = sp.getX() - x;
	diffPixel = Math.abs(axisBottom.valueToPos(diffX) - axisBottom.valueToPos(0));
	if (logging)
	  getLogger().info("diff x=" + diffPixel);
	if (diffPixel > m_MinimumPixelDifference + (diameter / 2))
	  continue;
	diffY     = sp.getY() - y;
	diffPixel = Math.abs(axisLeft.valueToPos(diffY) - axisLeft.valueToPos(0));
	if (logging)
	  getLogger().info("diff y=" + diffPixel);
	if (diffPixel > m_MinimumPixelDifference + (diameter / 2))
	  continue;

	// add hit
	if (logging)
	  getLogger().info("hit!");
	result.add(sp);
      }
    }

    if (result.size() > 0)
      return result;
    else
      return null;
  }
}
