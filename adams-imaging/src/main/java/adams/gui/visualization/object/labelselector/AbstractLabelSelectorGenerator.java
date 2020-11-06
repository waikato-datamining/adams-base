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
 * AbstractLabelSelectorGenerator.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.labelselector;

import adams.core.option.AbstractOptionHandler;
import adams.gui.visualization.object.ObjectAnnotationPanel;

/**
 * Ancestor for classes that generate label selector panels.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractLabelSelectorGenerator
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -942057544621842546L;

  /**
   * Generates the panel.
   *
   * @param owner 	the owning panel
   * @return		the panel, null if none generated
   */
  public abstract AbstractLabelSelectorPanel generate(ObjectAnnotationPanel owner);
}
