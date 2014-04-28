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
 * AbstractEquiDistanceWithOffset.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.container.DataContainer;

/**
 * Abstract ancestor for filters that equi-distance the data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to filter
 */
public abstract class AbstractEquiDistanceWithOffset<T extends DataContainer>
  extends AbstractEquiDistance<T> {

  /** for serialization. */
  private static final long serialVersionUID = -2590871295104049256L;

  /** offset: add to timestamp/scan-num. */
  protected int m_Offset;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "offset", "offset",
	    -1, -1, null);
  }

  /**
   * Sets the offset for wave numbers (use -1 to ignore).
   *
   * @param off 	the offset
   */
  public void setOffset(int off) {
    m_Offset = off;
    reset();
  }

  /**
   * Returns the offset for wave numbers (-1 if ignored).
   *
   * @return		the offset
   */
  public int getOffset() {
    return m_Offset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public abstract String offsetTipText();
}
