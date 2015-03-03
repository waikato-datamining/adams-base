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
 * RegionRecorder.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data;

import java.util.List;

import adams.data.container.DataContainer;

/**
 * A scheme that also records regions in the data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data that is recorded
 */
public interface RegionRecorder<T extends DataContainer> {

  /**
   * Sets whether the regions are recorded as well.
   *
   * @param value 	if true the regions will be recorded
   * @see		#getRegions()
   */
  public void setRecordRegions(boolean value);

  /**
   * Returns whether regions are recorded.
   *
   * @return 		true if the regions are recorded
   * @see		#getRegions()
   */
  public boolean getRecordRegions();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String recordRegionsTipText();

  /**
   * Returns the regions that were recorded.
   *
   * @return		the regions
   */
  public List<T> getRegions();
}
