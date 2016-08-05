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
 * OutlierDetector.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.outlier;

import adams.core.CleanUpHandler;
import adams.core.ShallowCopySupporter;
import adams.core.option.OptionHandler;
import adams.data.container.DataContainer;

import java.util.List;

/**
 * Interface for outlier detectors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to process
 */
public interface OutlierDetector<T extends DataContainer>
  extends OptionHandler, Comparable, CleanUpHandler, ShallowCopySupporter<OutlierDetector> {

  /**
   * Returns the detections on the specified data.
   *
   * @param data	the data to process
   * @return		the detections
   */
  public List<String> detect(T data);

  /**
   * Returns a shallow copy of itself.
   *
   * @return		the shallow copy
   */
  public OutlierDetector shallowCopy();

  /**
   * Returns a shallow copy of itself.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public OutlierDetector shallowCopy(boolean expand);
}
