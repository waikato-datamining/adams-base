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
 * PassThrough.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.check;

import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Dummy, performs no checks.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PassThrough
  extends AbstractAnnotationCheck {

  private static final long serialVersionUID = 2081359805181761621L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, performs no checks.";
  }

  /**
   * Checks the annotations.
   *
   * @param objects 	the annotations to check
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String doCheckAnnotations(LocatedObjects objects) {
    return null;
  }
}
