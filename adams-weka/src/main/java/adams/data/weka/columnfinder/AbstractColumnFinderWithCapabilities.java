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

/**
 * AbstractColumnFinderWithCapabilities.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.columnfinder;

import weka.core.Capabilities;
import weka.core.CapabilitiesHandler;
import weka.core.Instances;

/**
 * Ancestor for classes that find columns of interest in datasets.
 * <br><br>
 * Requires the data to meet the required capabilities.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractColumnFinderWithCapabilities
  extends AbstractColumnFinder 
  implements CapabilitiesHandler {

  /** for serialization. */
  private static final long serialVersionUID = -1036987527953097874L;

  /**
   * Returns the capabilities of this object.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  public abstract Capabilities getCapabilities();

  /**
   * Checks the data.
   * <br><br>
   * Checks the data against the capabilities.
   * 
   * @param data	the data to check
   */
  @Override
  protected void check(Instances data) {
    super.check(data);
    
    try {
      getCapabilities().testWithFail(data);
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Data didn't meet capabilities!", e);
    }
  }
}
