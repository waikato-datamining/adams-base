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
 * MouseMovementTracker.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.awt.event.MouseEvent;
import java.io.Serializable;

import adams.core.option.OptionHandler;

/**
 * Interface for classes that track the mouse position on the content panel.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MouseMovementTracker 
  extends Serializable {

  /**
   * Sets the spectrum panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   */
  public void setPanel(PaintablePanel value);

  /**
   * Returns the spectrum panel currently in use.
   *
   * @return		the panel in use
   */
  public PaintablePanel getPanel();

  /**
   * Gets triggered when the mouse moved.
   * 
   * @param e		the mouse event that triggered the event
   */
  public void mouseMovementTracked(MouseEvent e);
  
  /**
   * Returns a shallow copy of the tracker. Doesn't expand variables in case
   * of {@link OptionHandler} objects.
   * 
   * @return		a shallow copy of the tracker
   */
  public MouseMovementTracker shallowCopyTracker();
  
  /**
   * Returns a shallow copy of the tracker.
   * 
   * @param expand	whether to expand variables to their actual value
   * @return		a shallow copy of the tracker
   */
  public MouseMovementTracker shallowCopyTracker(boolean expand);
}
