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
 * AbstractHitDetector.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.plot;

import java.awt.event.MouseEvent;
import java.util.logging.Level;

import adams.core.CleanUpHandler;
import adams.core.logging.LoggingObject;

/**
 * Abstract ancestor for classes that detect hits in in the plot panel.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractHitDetector
  extends LoggingObject
  implements CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = 7654372428971254128L;

  /** whether debug mode is on. */
  protected boolean m_Debug;

  /** whether the detector is enabled. */
  protected boolean m_Enabled;

  /**
   * Initializes the detector.
   */
  public AbstractHitDetector() {
    super();

    m_Debug   = false;
    m_Enabled = true;
  }

  /**
   * Turns the debug mode on or off.
   *
   * @param value	if true then the debug mode is turned on
   */
  public void setDebug(boolean value) {
    m_Debug = value;
    getLogger().setLevel(value ? Level.INFO : Level.OFF);
  }

  /**
   * Returns whether debug mode is on or not.
   *
   * @return		true if debug mode is on
   */
  public boolean getDebug() {
    return m_Debug;
  }

  /**
   * Sets whether the detector is enabled or not.
   *
   * @param value	if true then the detector is enabled
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
  }

  /**
   * Returns whether the detector is currently enabled.
   *
   * @return		true if the detector is enabled.
   */
  public boolean isEnabled() {
    return m_Enabled;
  }

  /**
   * Checks for a hit.
   *
   * @param e		the MouseEvent (for coordinates)
   * @return		the associated object with the hit, otherwise null
   */
  protected abstract Object isHit(MouseEvent e);

  /**
   * Performs the action when a hit is detected.
   *
   * @param e		the MouseEvent (for coordinates)
   * @param hit	the object that got determined by the hit
   * @return		optional result of processing the event
   */
  protected abstract Object processHit(MouseEvent e, Object hit);

  /**
   * Detects hits and processes them.
   *
   * @param e		the mouse event to analyze for a hit
   * @return		optional result of processing the event
   */
  public Object detect(MouseEvent e) {
    Object	result;
    Object	hit;

    result = null;
    hit    = isHit(e);

    getLogger().info(
        "Hit (x=" + e.getX() + ",y=" + e.getY() + "): " + (hit != null)
        + ((hit == null) ? "" : ("/" + hit)));

    if (hit != null)
      result = processHit(e, hit);

    return result;
  }

  /**
   * Detects hits and returns them without processing them.
   *
   * @param e		the mouse event to analyze for a hit
   * @return		the hits if any, null otherwise
   */
  public Object locate(MouseEvent e) {
    return isHit(e);
  }

  /**
   * Cleans up data structures, frees up memory.
   * <br><br>
   * Default implementation does nothing.
   */
  public void cleanUp() {
  }
}