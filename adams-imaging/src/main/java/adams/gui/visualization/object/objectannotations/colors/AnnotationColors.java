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
 * AnnotationColors.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.colors;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.Color;

/**
 * Interface for classes that generate colors for annotations.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface AnnotationColors
  extends OptionHandler, QuickInfoSupporter {

  /**
   * Initializes the colors with the annotations.
   *
   * @param objects	the annotations to use for initialization
   * @param errors	for collecting errors
   */
  public void initColors(LocatedObjects objects, MessageCollection errors);

  /**
   * Returns the color for the object.
   *
   * @param object	the annotation to get the color for
   */
  public Color getColor(LocatedObject object);
}
