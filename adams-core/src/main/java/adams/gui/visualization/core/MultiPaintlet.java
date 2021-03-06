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
 * MultiPaintlet.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import adams.flow.core.Actor;
import adams.gui.event.PaintEvent.PaintMoment;

import java.awt.Graphics;

/**
 * Paintlet that combines multiple paintlets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiPaintlet
  extends AbstractPaintlet
  implements FlowAwarePaintlet {

  /** for serialization. */
  private static final long serialVersionUID = 159999248427405834L;
  
  /** the paintlets to use. */
  protected Paintlet[] m_SubPaintlets;

  /** the actor that this paintlet belongs to. */
  protected Actor m_Actor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines multiple paintlets.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "paintlet", "subPaintlets",
	    getDefaultSubPaintlets());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Actor = null;
  }

  /**
   * Sets the owning actor.
   *
   * @param actor	the actor this paintlet belongs to
   */
  public void setActor(Actor actor) {
    m_Actor = actor;
    memberChanged();
  }

  /**
   * Returns the owning actor.
   *
   * @return		the actor this paintlet belongs to, null if none set
   */
  public Actor getActor() {
    return m_Actor;
  }

  /**
   * Returns the default paintlets.
   * 
   * @return		the paintlets
   */
  protected Paintlet[] getDefaultSubPaintlets() {
    return new Paintlet[0];
  }
  
  /**
   * Sets the paintlets to use.
   *
   * @param value	the paintlets
   */
  public void setSubPaintlets(Paintlet[] value) {
    m_SubPaintlets = value;
    setPanel(getPanel());  // update sub paintlets
    for (Paintlet paintlet: m_SubPaintlets) {
      if (paintlet instanceof FlowAwarePaintlet)
        ((FlowAwarePaintlet) paintlet).setActor(m_Actor);
    }
    memberChanged();
  }

  /**
   * Returns the paintlets to use.
   *
   * @return		the paintlets
   */
  public Paintlet[] getSubPaintlets() {
    return m_SubPaintlets;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String subPaintletsTipText() {
    return "The paintlets to combine.";
  }

  /**
   * Sets the panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   */
  @Override
  public void setPanel(PaintablePanel value) {
    super.setPanel(value);
    for (Paintlet paintlet: m_SubPaintlets)
      paintlet.setPanel(value);
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.MULTIPLE;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    for (Paintlet paintlet: m_SubPaintlets) {
      if (paintlet.canPaint(moment))
	paintlet.performPaint(g, moment);
    }
  }
}
