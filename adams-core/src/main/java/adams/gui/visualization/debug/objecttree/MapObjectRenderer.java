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
 * MapObjectRenderer.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.objecttree;

import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.Map;

/**
 * Renders the map items, one per line, with the key prefixed.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MapObjectRenderer
  extends AbstractObjectPlainTextRenderer {

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the renderer can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(Map.class, cls);
  }

  /**
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @return		the rendered string
   */
  @Override
  protected String doRender(Object obj) {
    StringBuilder			result;
    Map					map;
    AbstractObjectPlainTextRenderer	renderer;

    result = new StringBuilder();
    map    = (Map) obj;
    for (Object key: map.keySet()) {
      renderer = AbstractObjectPlainTextRenderer.getRenderer(map.get(key)).get(0);
      result.append(key + ": ");
      result.append(renderer.render(map.get(key)));
      result.append("\n");
    }
    
    return result.toString();
  }
}
