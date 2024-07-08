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
 * DataPointWithMetaData.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.data.container;

import java.util.HashMap;

/**
 * Interface for DataPoint-implementing classes that also support meta-data.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface DataPointWithMetaData
  extends DataPoint {

  /**
   * Sets the meta-data to use.
   *
   * @param value	the meta-data
   */
  public void setMetaData(HashMap<String,Object> value);

  /**
   * Returns the stored meta-data.
   *
   * @return		the meta-data, null if none available
   */
  public HashMap<String,Object> getMetaData();

  /**
   * Checks if any meta-data is available.
   *
   * @return		true if meta-data available
   */
  public boolean hasMetaData();
}
