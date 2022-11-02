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
 * ObjectRenderer.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.objectrenderer;

import javax.swing.JPanel;

/**
 * Interface for classes that render objects visually.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ObjectRenderer {

  /**
   * Returns whether a limit is supported by the renderer.
   *
   * @param obj		the object to render
   * @return		true if supplying a limit has an effect
   */
  public boolean supportsLimit(Object obj);

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the renderer can handle this type of object
   */
  public boolean handles(Class cls);

  /**
   * Renders the object with a new renderer setup.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @param limit       the limit to use for the rendering (if applicable), ignored if null
   * @return		null if successful, otherwise error message
   */
  public String render(Object obj, JPanel panel, Integer limit);

  /**
   * Checks whether the renderer can use a cached setup to render an object.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		true if possible
   */
  public boolean canRenderCached(Object obj, JPanel panel);

  /**
   * Renders the object using a cached setup (if available).
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @param limit       the limit to use for the rendering (if applicable), ignored if null
   * @return		null if successful, otherwise error message
   */
  public String renderCached(Object obj, JPanel panel, Integer limit);
}
