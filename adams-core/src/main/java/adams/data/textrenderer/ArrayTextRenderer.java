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
 * ArrayTextRenderer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.textrenderer;

import java.lang.reflect.Array;

/**
 * Renders arrays to the specified limit of elements.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ArrayTextRenderer
  extends AbstractLineNumberedLimitedTextRenderer {

  private static final long serialVersionUID = 4240264364517086325L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Renders arrays to the specified limit of elements.";
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
    return 1;
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
    return "The maximum number of array limits to render.";
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
    return cls.isArray();
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
    int			i;
    int			len;
    Object		subObj;

    result = new StringBuilder();
    len    = Array.getLength(obj);
    for (i = 0; i < len; i++) {
      if (i >= getActualLimit())
        break;
      subObj = Array.get(obj, i);
      if (m_OutputLineNumbers) {
        result.append((i + 1));
        result.append(": ");
      }
      result.append(AbstractTextRenderer.renderObject(subObj));
      result.append("\n");
    }
    if (len > getActualLimit())
      result.append(DOTS);

    return result.toString();
  }
}
