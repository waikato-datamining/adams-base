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
 * Tick.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.axis;

import java.awt.Rectangle;
import java.io.Serializable;

/**
 * Represents a single tick of an axis.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Tick
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -6608030365327102253L;

  /** the position. */
  protected int m_Position;

  /** the label, if any. */
  protected String m_Label;
  
  /** the computed rectangle for the text. */
  protected Rectangle m_Bounds;

  /**
   * Initializes the tick.
   *
   * @param pos		the position of this tick
   * @param label	the label for this tick, null to suppress
   */
  public Tick(int pos, String label) {
    super();

    m_Position = pos;
    m_Label    = label;
    m_Bounds   = null;
  }

  /**
   * Returns the tick's position.
   *
   * @return		the position
   */
  public int getPosition() {
    return m_Position;
  }

  /**
   * Returns whether this tick has a label or not.
   *
   * @return		true if the tick has a label
   */
  public boolean hasLabel() {
    return (m_Label != null);
  }

  /**
   * Returns the label of this tick, can be null.
   *
   * @return		the label
   */
  public String getLabel() {
    return m_Label;
  }
  
  /**
   * Sets the bounds for the text.
   * 
   * @param x		the x position
   * @param y		the y position
   * @param width	the width
   * @param height	the height
   */
  public void setBounds(int x, int y, int width, int height) {
    m_Bounds = new Rectangle(x, y, width, height);
  }
  
  /**
   * Returns whether bounds are stored with this tick.
   * 
   * @return		true if bounds are available
   */
  public boolean hasBounds() {
    return (m_Bounds != null);
  }
  
  /**
   * Returns the bounds.
   * 
   * @return		the bounds, can be null if not available
   */
  public Rectangle getBounds() {
    return m_Bounds;
  }
  
  /**
   * Returns a string representation of the tick.
   *
   * @return		a string representation
   */
  @Override
  public String toString() {
    return "pos=" + m_Position + ", label=" + m_Label + (hasBounds() ? ", bounds=" + m_Bounds : "");
  }
}