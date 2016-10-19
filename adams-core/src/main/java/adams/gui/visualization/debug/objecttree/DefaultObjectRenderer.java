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
 * DefaultObjectRenderer.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.objecttree;

import adams.core.TextSupporter;

/**
 * Just uses the Object's toString() method or if the object is an instance
 * of {@link TextSupporter} then the text the the {@link TextSupporter#supplyText()}
 * method returns.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultObjectRenderer
  extends AbstractObjectPlainTextRenderer {

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls		the class to check
   * @return		always true
   */
  @Override
  public boolean handles(Class cls) {
    return true;
  }

  /**
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @return		the rendered string
   */
  @Override
  protected String doRender(Object obj) {
    if (obj instanceof TextSupporter)
      return ((TextSupporter) obj).supplyText();
    else
      return obj.toString();
  }
}
