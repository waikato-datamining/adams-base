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
 * ListTextRenderer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.textrenderer;

import adams.core.Utils;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.List;
import java.util.Map;

/**
 * Just uses the object's toString() method. Also handles null objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ListTextRenderer
  extends AbstractLimitedTextRenderer {

  private static final long serialVersionUID = -3112399546457037505L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Renders " + Utils.classToString(Map.class) + " objects.";
  }

  /**
   * Returns the default limit.
   *
   * @return		the default
   */
  @Override
  protected int getDefaultLimit() {
    return 100;
  }

  /**
   * Returns the minimum limit.
   *
   * @return		the minimum
   */
  @Override
  protected Integer getMinLimit() {
    return 0;
  }

  /**
   * Returns the maximum limit.
   *
   * @return		the maximum
   */
  @Override
  protected Integer getMaxLimit() {
    return null;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  @Override
  public String limitTipText() {
    return "The maximum number of list elements to render.";
  }

  /**
   * Checks whether the object is handled.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Object obj) {
    return (obj instanceof List);
  }

  /**
   * Checks whether the class is handled.
   *
   * @param cls		the class to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.matches(List.class, cls);
  }

  /**
   * Renders the object as text.
   *
   * @param obj		the object to render
   * @return		the generated string or null if failed to render
   */
  @Override
  protected String doRender(Object obj) {
    StringBuilder	result;
    List 		list;
    int 		i;

    result = new StringBuilder();
    list = (List) obj;

    for (i = 0; i < list.size(); i++) {
      if (i > getActualLimit())
        break;
      result.append((i+1));
      result.append(": ");
      result.append("" + list.get(i));
      result.append("\n");
    }
    if (list.size() > getActualLimit())
      result.append(DOTS);

    return result.toString();
  }
}
