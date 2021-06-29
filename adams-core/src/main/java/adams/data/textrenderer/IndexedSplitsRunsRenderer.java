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
 * IndexedSplitsRunsRenderer.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.textrenderer;

import adams.core.MessageCollection;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.io.output.PropertiesIndexedSplitsRunsWriter;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.io.StringWriter;

/**
 * Renders indexed splits runs as text.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IndexedSplitsRunsRenderer
  extends AbstractTextRenderer {

  private static final long serialVersionUID = 1088987716683320898L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Renders indexed splits runs as text.";
  }

  /**
   * Checks whether the object is handled.
   *
   * @param obj the object to check
   * @return true if handled
   */
  @Override
  public boolean handles(Object obj) {
    return (obj != null) && handles(obj.getClass());
  }

  /**
   * Checks whether the class is handled.
   *
   * @param cls the class to check
   * @return true if handled
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.isSubclass(IndexedSplitsRuns.class, cls);
  }

  /**
   * Renders the object as text.
   *
   * @param obj the object to render
   * @return the generated string or null if failed to render
   */
  @Override
  protected String doRender(Object obj) {
    StringWriter 			swriter;
    PropertiesIndexedSplitsRunsWriter 	pwriter;
    MessageCollection 			errors;

    errors  = new MessageCollection();
    swriter = new StringWriter();
    pwriter = new PropertiesIndexedSplitsRunsWriter();
    pwriter.write(swriter, (IndexedSplitsRuns) obj, errors);
    if (errors.isEmpty()) {
      return swriter.toString();
    }
    else {
      getLogger().warning(errors.toString());
      return null;
    }
  }
}
