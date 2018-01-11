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
 * PLSFilterNumComponents.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery.cso;

import adams.core.discovery.PropertyPath.PropertyContainer;
import weka.filters.supervised.attribute.PLSFilter;

/**
 * SavitzkyGolay numPoints handler.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PLSFilterNumComponents
  extends AbstractCatSwarmOptimizationIntegerDiscoveryHandler {


  private static final long serialVersionUID = 6077250097532777489L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Handles the numPoints parameter of the PLSFilter filter.";
  }

  /**
   * Returns the default minimum.
   *
   * @return		the default
   */
  @Override
  protected int getDefaultMinimum() {
    return 5;
  }

  /**
   * Returns the default maximum.
   *
   * @return		the default
   */
  @Override
  protected int getDefaultMaximum() {
    return 30;
  }

  /**
   * Returns the default list.
   *
   * @return		the default
   */
  protected String getDefaultList() {
    return "5 10 15";
  }

  /**
   * Returns the number of dimensions (= double values) this scheme requires.
   *
   * @return		the dimensions
   */
  public int getDimensions() {
    return 1;
  }

  /**
   * Returns the integer value from the property container.
   *
   * @param cont	the container
   * @return		the values
   */
  protected int[] getValue(PropertyContainer cont) {
    return new int[]{((PLSFilter) cont.getObject()).getNumComponents()};
  }

  /**
   * Sets the integer value in the property container.
   *
   * @param cont	the container
   * @param value	the values to set
   */
  protected void setValue(PropertyContainer cont, int[] value) {
    ((PLSFilter) cont.getObject()).setNumComponents(value[0]);
  }

  /**
   * Checks whether this object is handled by this discovery handler.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  @Override
  protected boolean handles(Object obj) {
    return (obj instanceof PLSFilter);
  }
}
