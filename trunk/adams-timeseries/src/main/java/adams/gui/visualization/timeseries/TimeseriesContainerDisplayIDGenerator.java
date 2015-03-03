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
 * TimeseriesContainerDisplayIDGenerator.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.timeseries;

import adams.gui.visualization.container.AbstractContainerDisplayStringGenerator;

/**
 * Class for generating display IDs.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesContainerDisplayIDGenerator<C extends TimeseriesContainer>
  extends AbstractContainerDisplayStringGenerator<C> {

  /** for serialization. */
  private static final long serialVersionUID = -4131184984196361251L;

  /**
   * Returns the display ID for the timeseries.
   *
   * @param c		the timeseries to get the display ID for
   * @return		the ID
   */
  @Override
  public String getDisplay(C c) {
    return c.getID();
  }
}