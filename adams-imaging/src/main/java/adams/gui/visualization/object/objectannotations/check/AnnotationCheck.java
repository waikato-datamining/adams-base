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
 * AnnotationCheck.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.check;

import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Interface for annotation checks.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface AnnotationCheck
  extends OptionHandler, QuickInfoSupporter {

  /**
   * Checks the annotations.
   *
   * @param objects	the annotations to check
   * @return		null if checks passed, otherwise error message
   */
  public String checkAnnotations(LocatedObjects objects);
}
