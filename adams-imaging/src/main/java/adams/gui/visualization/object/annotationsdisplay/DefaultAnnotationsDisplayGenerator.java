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
 * DefaultAnnotationsDisplayGenerator.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotationsdisplay;

/**
 * Ancestor for classes that create AbstractAnnotationsPanel implementations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DefaultAnnotationsDisplayGenerator
  extends AbstractAnnotationsDisplayGenerator {

  private static final long serialVersionUID = 4349554101379518737L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a panel that displays the annotations as a report table.";
  }

  /**
   * Generates the panel.
   *
   * @return		the panel
   */
  @Override
  public AbstractAnnotationsDisplayPanel generate() {
    DefaultAnnotationsDisplayPanel result;

    result = new DefaultAnnotationsDisplayPanel();
    result.setPrefix(m_Prefix);

    return result;
  }
}
