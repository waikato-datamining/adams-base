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
 * AbstractAnnotationCleaner.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.cleaning;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Interface for annotation cleaners.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface AnnotationCleaner
  extends OptionHandler, QuickInfoSupporter {

  /**
   * Cleans the annotations.
   *
   * @param objects	the annotations to clean
   * @param errors	for recording errors
   * @return		the (potentially) cleaned annotations
   */
  public LocatedObjects cleanAnnotations(LocatedObjects objects, MessageCollection errors);
}
