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
 * GPDGamma.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

import adams.core.discovery.PropertyPath.PropertyContainer;
import weka.classifiers.functions.GPD;

/**
 * GPD gamma handler.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GPDGamma
  extends AbstractGeneticDoubleDiscoveryHandlerResolution {

  private static final long serialVersionUID = 9168998412950337023L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Handles the gamma parameter of the GPD.";
  }

  /**
   * Returns the default splits.
   *
   * @return		the default
   */
  @Override
  protected int getDefaultSplits() {
    return 4;
  }

  /**
   * Returns the default minimum.
   *
   * @return		the default
   */
  @Override
  protected double getDefaultMinimum() {
    return 0.01;
  }

  /**
   * Returns the default maximum.
   *
   * @return		the default
   */
  @Override
  protected double getDefaultMaximum() {
    return 0.04;
  }

  /**
   * Returns the packed bits for the genetic algorithm.
   *
   * @param cont	the container to obtain the value from to turn into a string
   * @return		the bits
   */
  @Override
  public String pack(PropertyContainer cont) {
    GPD gpd=(GPD)cont.getObject();
    double val=gpd.getGamma();
    return(doubleToBits(val));
  }

  /**
   * Unpacks and applies the bits from the genetic algorithm.
   *
   * @param cont	the container to set the value for created from the string
   * @param bits	the bits to use
   */
  @Override
  public void unpack(PropertyContainer cont, String bits) {
    double val=bitsToDouble(bits);
    GPD gpd=(GPD)cont.getObject();
    gpd.setGamma(val);
  }

  /**
   * Checks whether this object is handled by this discovery handler.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  @Override
  protected boolean handles(Object obj) {
    return (obj instanceof GPD);
  }
}
