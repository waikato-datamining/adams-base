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
 * Copyright (C) 2008-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.plot;

import java.awt.event.MouseEvent;

/**
 * Interface for classes that detect hits in a plot.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <H> type of hit results
 * @param <P> type of processed hits
 */
public interface HitDetector<H, P> {

  /**
   * Turns the debug mode on or off.
   *
   * @param value	if true then the debug mode is turned on
   */
  public void setDebug(boolean value);

  /**
   * Returns whether debug mode is on or not.
   *
   * @return		true if debug mode is on
   */
  public boolean getDebug();

  /**
   * Sets whether the detector is enabled or not.
   *
   * @param value	if true then the detector is enabled
   */
  public void setEnabled(boolean value);

  /**
   * Returns whether the detector is currently enabled.
   *
   * @return		true if the detector is enabled.
   */
  public boolean isEnabled();

  /**
   * Detects hits and processes them.
   *
   * @param e		the mouse event to analyze for a hit
   * @return		optional result of processing the event
   */
  public P detect(MouseEvent e);

  /**
   * Detects hits and returns them without processing them.
   *
   * @param e		the mouse event to analyze for a hit
   * @return		the hits if any, null otherwise
   */
  public H locate(MouseEvent e);
}