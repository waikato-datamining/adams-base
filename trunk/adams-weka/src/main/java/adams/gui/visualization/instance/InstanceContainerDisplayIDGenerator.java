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
 * InstanceContainerDisplayIDGenerator.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.instance;

import adams.gui.visualization.container.AbstractContainerDisplayStringGenerator;

/**
 * Class for generating display IDs for Instance objects (based on
 * weka.core.Instance objects).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstanceContainerDisplayIDGenerator
  extends AbstractContainerDisplayStringGenerator<InstanceContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 5365866966393976397L;

  /**
   * Returns the display ID for the sequence.
   *
   * @param c		the sequence to get the display ID for
   * @return		the ID
   */
  public String getDisplay(InstanceContainer c) {
    return c.getData().getID().replaceAll("-weka\\.filters.*", "");
  }
}