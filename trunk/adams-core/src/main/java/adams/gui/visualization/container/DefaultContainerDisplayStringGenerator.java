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
 * DefaultContainerDisplayStringGenerator.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

/**
 * Default class for generating display IDs.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultContainerDisplayStringGenerator
  extends AbstractContainerDisplayStringGenerator<AbstractContainer> {

  /** for serialization. */
  private static final long serialVersionUID = -4131184984196361251L;

  /**
   * Returns the display string for the container.
   *
   * @param c		the container to get the display string for
   * @return		the display string
   */
  public String getDisplay(AbstractContainer c) {
    return c.toString();
  }
}