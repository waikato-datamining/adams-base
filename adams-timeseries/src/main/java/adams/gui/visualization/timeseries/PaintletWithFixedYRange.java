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
 * PaintletWithFixedYRange.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.timeseries;

import adams.flow.core.Actor;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.FlowAwarePaintlet;
import adams.gui.visualization.core.PaintablePanel;

import java.awt.Graphics;

/**
 * A wrapper for XY-sequence paintlets, in order to use fixed a Y range.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PaintletWithFixedYRange
  extends AbstractTimeseriesPaintlet
  implements adams.gui.visualization.core.PaintletWithFixedYRange,
             FlowAwarePaintlet {

  /** for serialization. */
  private static final long serialVersionUID = 354723429582771889L;

  /** the minimum of Y. */
  protected double m_MinY;

  /** the maximum of Y. */
  protected double m_MaxY;

  /** the actual paintlet to use. */
  protected AbstractTimeseriesPaintlet m_Paintlet;

  /** the actor the paintlet belongs to. */
  protected Actor m_Actor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Meta-paintlet that uses a fixed Y range (for faster drawing) and a base-paintlet to draw the actual data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "min-y", "minY",
	    0.0, null, null);

    m_OptionManager.add(
	    "max-y", "maxY",
	    1000.0, null, null);

    m_OptionManager.add(
	    "paintlet", "paintlet",
	    getDefaultPaintlet());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    setPaintlet(getDefaultPaintlet());

    super.initialize();
  }

  /**
   * Returns the default paintlet to use.
   *
   * @return		the default paintlet
   */
  protected AbstractTimeseriesPaintlet getDefaultPaintlet() {
    return new TimeseriesPaintlet();
  }

  /**
   * Sets the spectrum panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   */
  @Override
  public void setPanel(PaintablePanel value) {
    if (m_Paintlet != null)
      m_Paintlet.setPanel(value);

    super.setPanel(value);
  }

  /**
   * Sets the minimum of the Y range.
   *
   * @param value	the minimum
   */
  public void setMinY(double value) {
    m_MinY = value;
    memberChanged(true);
  }

  /**
   * Returns the minimum of the Y range.
   *
   * @return		the minimum
   */
  public double getMinY() {
    return m_MinY;
  }

  /**
   * Returns the minimum of the Y range.
   *
   * @return		the minimum
   */
  public double getMinimumY() {
    return m_MinY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minYTipText() {
    return "The minimum value for the Y range.";
  }

  /**
   * Sets the maximum of the Y range.
   *
   * @param value	the maximum
   */
  public void setMaxY(double value) {
    m_MaxY = value;
    memberChanged(true);
  }

  /**
   * Returns the maximum of the Y range.
   *
   * @return		the maximum
   */
  public double getMaxY() {
    return m_MaxY;
  }

  /**
   * Returns the maximum of the Y range.
   *
   * @return		the maximum
   */
  public double getMaximumY() {
    return m_MaxY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxYTipText() {
    return "The maximum value for the Y range.";
  }

  /**
   * Sets the actual paintlet to use.
   *
   * @param value	the paintlet
   */
  public void setPaintlet(AbstractTimeseriesPaintlet value) {
    if (m_Paintlet != null)
      m_Paintlet.setPanel(null);

    m_Paintlet = value;
    m_Paintlet.setPanel(getPanel());

    memberChanged();
  }

  /**
   * Returns the painlet in use.
   *
   * @return		the paintlet
   */
  public AbstractTimeseriesPaintlet getPaintlet() {
    return m_Paintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paintletTipText() {
    return "The actual paintlet to use for drawing the data.";
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    m_Paintlet.performPaint(g, moment);
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    if (m_Paintlet != null)
      return m_Paintlet.getPaintMoment();
    else
      return PaintMoment.PAINT;
  }

  /**
   * Sets the owning actor.
   *
   * @param actor	the actor this paintlet belongs to
   */
  @Override
  public void setActor(Actor actor) {
    m_Actor = actor;
  }

  /**
   * Returns the owning actor.
   *
   * @return		the actor this paintlet belongs to, null if none set
   */
  @Override
  public Actor getActor() {
    return m_Actor;
  }
}
