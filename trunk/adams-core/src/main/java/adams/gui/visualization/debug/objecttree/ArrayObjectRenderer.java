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
 * ArrayObjectRenderer.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.objecttree;

import java.lang.reflect.Array;

/**
 * Renders arrays, one per element, with the index prefixed.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayObjectRenderer
  extends AbstractObjectPlainTextRenderer {

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the renderer can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return cls.isArray();
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
    int					i;
    AbstractObjectPlainTextRenderer	renderer;
    Object				element;
    
    result   = new StringBuilder();
    renderer = null;
    for (i = 0; i < Array.getLength(obj); i++) {
      element = Array.get(obj, i);
      if (renderer == null)
	renderer = AbstractObjectPlainTextRenderer.getRenderer(element).get(0);
      result.append((i+1) + ": ");
      result.append(renderer.render(element));
      result.append("\n");
    }
    
    return result.toString();
  }
}
