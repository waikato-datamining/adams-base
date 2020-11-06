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
 * NoPanelGenerator.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.labelselector;

import adams.gui.visualization.object.ObjectAnnotationPanel;

/**
 * Generates no panel.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NoPanelGenerator
  extends AbstractLabelSelectorGenerator {

  private static final long serialVersionUID = -2277474971365709236L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates no panel.";
  }

  /**
   * Generates the panel.
   *
   * @return		the panel, null if none generated
   */
  @Override
  public AbstractLabelSelectorPanel generate(ObjectAnnotationPanel owner) {
    return null;
  }
}
