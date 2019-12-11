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
 * TextSupporterTextRenderer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.textrenderer;

import adams.core.TextSupporter;
import adams.core.Utils;
import nz.ac.waikato.cms.locator.ClassLocator;

/**
 * Outputs the text representation returned from TextSupporter classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TextSupporterTextRenderer
  extends AbstractTextRenderer {

  private static final long serialVersionUID = -4566406956008755488L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the text representation returned from " + Utils.classToString(TextSupporter.class) + " objects.";
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
    return ClassLocator.matches(TextSupporter.class, cls);
  }

  /**
   * Renders the object as text.
   *
   * @param obj		the object to render
   * @return		the generated string or null if failed to render
   */
  @Override
  protected String doRender(Object obj) {
    return ((TextSupporter) obj).supplyText();
  }
}
