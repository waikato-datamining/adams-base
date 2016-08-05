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
 * ImagePanelLeftClickEvent.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import adams.gui.visualization.image.ImagePanel;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * Event that gets sent in case of a left-click event in the {@link ImagePanel}.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImagePanelLeftClickEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 722590085059939598L;

  /** the position of the click. */
  protected Point m_Position;

  /** the associated modifiers. */
  protected int m_ModifiersEx;

  /**
   * Initializes the event.
   *
   * @param source		the source of the event
   * @param position		the position of the click
   * @param modifiersEx	the extended modifiers associated with the event when releasing the mouse
   */
  public ImagePanelLeftClickEvent(ImagePanel source, Point position, int modifiersEx) {
    super(source);
    
    m_Position = position;
    m_ModifiersEx = modifiersEx;
  }
  
  /**
   * Returns the image panel that triggered the event.
   * 
   * @return		the image panel
   */
  public ImagePanel getImagePanel() {
    return (ImagePanel) getSource();
  }
  
  /**
   * Returns the position.
   * 
   * @return		the position
   */
  public Point getPosition() {
    return m_Position;
  }

  /**
   * Returns the associated modifiers.
   * 
   * @return		the modifiers
   * @see		MouseEvent#getModifiersEx()
   */
  public int getModifiersEx() {
    return m_ModifiersEx;
  }
}
