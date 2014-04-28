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
 * ImagePanelSelectionEvent.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import adams.gui.visualization.image.ImagePanel;

/**
 * Event that gets sent in case of a box selection event in the {@link ImagePanel}.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImagePanelSelectionEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 722590085059939598L;

  /** the top-left of the selection. */
  protected Point m_TopLeft;

  /** the bottom-right of the selection. */
  protected Point m_BottomRight;
  
  /** the associated modifiers. */
  protected int m_ModifiersEx;
  
  /**
   * Initializes the event.
   *
   * @param source		the source of the event
   * @param topLeft		the top-left of the selection
   * @param bottomRight		the bottom-right of the selection
   * @param modifiersEx	the extended modifiers associated with the event when releasing the mouse
   */
  public ImagePanelSelectionEvent(ImagePanel source, Point topLeft, Point bottomRight, int modifiersEx) {
    super(source);
    
    m_TopLeft     = topLeft;
    m_BottomRight = bottomRight;
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
   * Returns the top-left position.
   * 
   * @return		the position
   */
  public Point getTopLeft() {
    return m_TopLeft;
  }
  
  /**
   * Returns the bottom-right position.
   * 
   * @return		the position
   */
  public Point getBottomRight() {
    return m_BottomRight;
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
