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
 * PaintEvent.java
 * Copyright (C) 2008-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import java.awt.Graphics;
import java.util.EventObject;

/**
 * Event that gets sent in case of a paint update of a GUI component.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PaintEvent
  extends EventObject {

  /**
   * Enumeration indicating what paintlets should be executed.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum PaintMoment {
    /** execute the paintlet just after the background got cleared, before the grid is drawn. */
    BACKGROUND,
    /** execute the paintlet just after the grid was drawn, before any other painting happens. */
    GRID,
    /** execute the paintlet <i>before</i> the panel's main painting. */
    PRE_PAINT,
    /** execute the paintlet <i>as</i> the panel's main painting. */
    PAINT,
    /** execute the paintlet <i>after</i> the panel's main painting. */
    POST_PAINT,
    /** if multiple paintmoments occur. */
    MULTIPLE
  }

  /** for serialization. */
  private static final long serialVersionUID = 7509294691540485379L;

  /** the graphics context. */
  protected Graphics m_Graphics;

  /** the paint momment. */
  protected PaintMoment m_PaintMoment;

  /**
   * Initializes the event.
   *
   * @param source	the source of the event
   * @param g		the graphics context
   * @param moment	the paint momentm only paintlets of this moment should
   * 			draw themselves
   */
  public PaintEvent(Object source, Graphics g, PaintMoment moment) {
    super(source);

    m_Graphics    = g;
    m_PaintMoment = moment;
  }

  /**
   * Returns the corresponding graphics context.
   *
   * @return		the graphics context
   */
  public Graphics getGraphics() {
    return m_Graphics;
  }

  /**
   * Returns the paint moment of which a paintlet has to be in order to be
   * executed.
   *
   * @return		the paint moment
   */
  public PaintMoment getPaintMoment() {
    return m_PaintMoment;
  }
}
