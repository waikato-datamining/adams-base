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
 * MapClickEvent.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.event;

import java.awt.event.MouseEvent;
import java.util.EventObject;

import org.openstreetmap.gui.jmapviewer.JMapViewer;

/**
 * Gets triggered when the user clicks on the a {@link JMapViewer}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MapClickEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = -5924845620629927650L;
  
  /** the mouse event. */
  protected MouseEvent m_MouseEvent;
  
  /**
   * Initializes the event.
   * 
   * @param source	the viewer that triggered the event
   * @param evt		the associated mouse event
   */
  public MapClickEvent(JMapViewer source, MouseEvent evt) {
    super(source);
    m_MouseEvent = evt;
  }

  /**
   * Returns the viewer that triggered the event.
   * 
   * @return		the viewer
   */
  public JMapViewer getViewer() {
    return (JMapViewer) getSource();
  }
  
  /**
   * Returns the mosue event.
   * 
   * @return		the event
   */
  public MouseEvent getMouseEvent() {
    return m_MouseEvent;
  }
}