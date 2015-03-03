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
 * AbstractMouseClickAction.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import java.awt.event.MouseEvent;

import adams.core.option.AbstractOptionHandler;

import com.googlecode.jfilechooserbookmarks.gui.MouseUtils;

/**
 * Ancestor for classes that react to mouse clicks on the canvas.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMouseClickAction
  extends AbstractOptionHandler
  implements MouseClickAction {

  /** for serialization. */
  private static final long serialVersionUID = 5402943914014171320L;

  /**
   * Gets called in case of a left-click.
   * 
   * @param panel	the associated panel
   * @param e		the mouse event
   */
  protected abstract void processLeftClick(SequencePlotterPanel panel, MouseEvent e);

  /**
   * Gets called in case of a right-click.
   * 
   * @param panel	the associated panel
   * @param e		the mouse event
   */
  protected abstract void processRightClick(SequencePlotterPanel panel, MouseEvent e);

  /**
   * Gets triggered if the user clicks on the canvas.
   * 
   * @param panel	the associated panel
   * @param e		the mouse event
   */
  public void mouseClickOccurred(SequencePlotterPanel panel, MouseEvent e) {
    if (MouseUtils.isLeftClick(e))
      processLeftClick(panel, e);
    else
      processRightClick(panel, e);
  }
}
