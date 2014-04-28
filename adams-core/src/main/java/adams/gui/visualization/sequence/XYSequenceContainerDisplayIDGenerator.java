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
 * XYSequenceContainerDisplayIDGenerator.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

import adams.gui.visualization.container.AbstractContainerDisplayStringGenerator;

/**
 * Class for generating display IDs for XY sequences.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XYSequenceContainerDisplayIDGenerator
  extends AbstractContainerDisplayStringGenerator<XYSequenceContainer> {

  /** for serialization. */
  private static final long serialVersionUID = -5479641588217477884L;

  /**
   * Returns the display ID for the sequence.
   *
   * @param c		the sequence to get the display ID for
   * @return		the ID
   */
  public String getDisplay(XYSequenceContainer c) {
    return c.getData().getID();
  }
}