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
 * AbstractMapObjectHitListener.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;

import adams.core.option.AbstractOptionHandler;
import adams.data.mapobject.MetaDataSupporter;
import adams.gui.event.MapObjectHitEvent;
import adams.gui.event.MapObjectHitListener;

/**
 * Ancestor for classes that listen to hits on {@link MapObject}s in a 
 * {@link JMapViewer}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMapObjectHitListener
  extends AbstractOptionHandler
  implements MapObjectHitListener {

  /** for serialization. */
  private static final long serialVersionUID = 4468210013390130296L;

  /**
   * Returns the sorted list of metadata keys.
   * 
   * @param hits	the hits to analyze
   * @return		the sorted list of keys
   */
  protected List<String> getMetaDataKeys(List<MapObject> hits) {
    List<String> 	result;
    HashSet<String>	keys;
    MetaDataSupporter	meta;
    
    result = new ArrayList<String>();
    keys       = new HashSet<String>();
    for (MapObject hit: hits) {
      if (hit instanceof MetaDataSupporter) {
	meta = (MetaDataSupporter) hit;
	for (String key: meta.metaDataKeys()) {
	  if (!keys.contains(key))
	    keys.add(key);
	}
      }
    }
    
    result.addAll(keys);
    Collections.sort(result);
    
    return result;
  }

  /**
   * Performs the processing of the hits.
   * 
   * @param viewer	the associated viewer
   * @param hits	the objects that were "hit"
   */
  protected abstract void processHits(JMapViewer viewer, List<MapObject> hits);
  
  /**
   * Gets called when one or more {@link MapObject}s got left-clicked on in the
   * {@link JMapViewer}.
   * 
   * @param e		the event
   */
  @Override
  public void mapObjectsHit(MapObjectHitEvent e) {
    processHits(e.getViewer(), e.getHits());
  }
}
