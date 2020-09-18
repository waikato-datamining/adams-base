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
 * JsonTextRenderer.java
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, NZ
 */

package adams.data.textrenderer;

import adams.data.json.JsonHelper;
import com.google.gson.JsonElement;
import net.minidev.json.JSONAware;
import nz.ac.waikato.cms.locator.ClassLocator;

/**
 * Renders JSON objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class JsonTextRenderer
  extends AbstractTextRenderer {

  private static final long serialVersionUID = 4610900500263644291L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Renders JSON objects.";
  }

  /**
   * Checks whether the object is handled.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Object obj) {
    return (obj != null) && handles(obj.getClass());
  }

  /**
   * Checks whether the class is handled.
   *
   * @param cls		the class to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.matches(JSONAware.class, cls) || ClassLocator.matches(JsonElement.class, cls);
  }

  /**
   * Renders the object as text.
   *
   * @param obj		the object to render
   * @return		the generated string or null if failed to render
   */
  @Override
  protected String doRender(Object obj) {
    return JsonHelper.prettyPrint(obj.toString());
  }
}
