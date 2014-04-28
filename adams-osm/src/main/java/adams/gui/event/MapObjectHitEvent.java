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
 * MapObjectHitEvent.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.event;

import java.util.EventObject;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;

/**
 * Encapsulates all the {@link MapObject}s that got hit when a user left-clicked
 * in a {@link JMapViewer}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MapObjectHitEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = -5924845620629927650L;
  
  /** the list of objects. */
  protected List<MapObject> m_Hits;
  
  /**
   * Initializes the event.
   * 
   * @param source	the viewer that triggered the event
   * @param hits	the list of objects that got hit
   */
  public MapObjectHitEvent(JMapViewer source, List<MapObject> hits) {
    super(source);
    m_Hits = hits;
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
   * Returns the list of hit objects.
   * 
   * @return		the objects
   */
  public List<MapObject> getHits() {
    return m_Hits;
  }
}