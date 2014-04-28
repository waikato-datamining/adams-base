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
 * AbstractFilenameGenerator.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for filename generators.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFilenameGenerator
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -9019484464686478277L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Performs checks on the object to generate the filename for.
   * <p/>
   * Default implementation only checks whether object is null.
   *
   * @param obj		the object to check
   */
  protected void check(Object obj) {
    if (obj == null)
      throw new IllegalArgumentException("Object to generate filename for is null!");
  }

  /**
   * Performs the actual generation of the filename.
   *
   * @param obj		the object to generate the filename for
   * @return		the generated filename
   */
  protected abstract String doGenerate(Object obj);

  /**
   * Generates a filename for the object.
   *
   * @param obj		the object to generate the filename for
   * @return		the generated filename
   */
  public String generate(Object obj) {
    check(obj);
    return doGenerate(obj);
  }
}
