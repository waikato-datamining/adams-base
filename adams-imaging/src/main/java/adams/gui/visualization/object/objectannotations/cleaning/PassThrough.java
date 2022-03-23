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
 * PassThr.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.cleaning;

import adams.core.MessageCollection;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Performs no cleaning, just passes through the annotations.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PassThrough
  extends AbstractAnnotationCleaner {

  private static final long serialVersionUID = 6357986486229533511L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs no cleaning, just passes through the annotations.";
  }

  /**
   * Cleans the annotations.
   *
   * @param objects the annotations to clean
   * @param errors  for recording errors
   * @return the (potentially) cleaned annotations
   */
  @Override
  protected LocatedObjects doCleanAnnotations(LocatedObjects objects, MessageCollection errors) {
    return objects;
  }
}
