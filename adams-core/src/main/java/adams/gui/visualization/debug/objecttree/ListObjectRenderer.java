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
 * ListObjectRenderer.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.objecttree;

import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.List;

/**
 * Renders the list items, one per line, with the index prefixed.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ListObjectRenderer
  extends AbstractObjectPlainTextRenderer {

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the renderer can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(List.class, cls);
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
    List				list;
    int					i;
    AbstractObjectPlainTextRenderer	renderer;
    
    result = new StringBuilder();
    list   = (List) obj;
    for (i = 0; i < list.size(); i++) {
      renderer = AbstractObjectPlainTextRenderer.getRenderer(list.get(i)).get(0);
      result.append((i+1) + ": ");
      result.append(renderer.render(list.get(i)));
      result.append("\n");
    }
    
    return result.toString();
  }
}
