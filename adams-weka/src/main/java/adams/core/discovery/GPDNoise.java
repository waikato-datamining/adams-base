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
 * GPDNoise.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

import weka.classifiers.functions.GPD;

/**
 * GPD noise handler.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GPDNoise
  extends AbstractGeneticDoubleDiscoveryHandler {

  private static final long serialVersionUID = 9168998412950337023L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Handles the noise parameter of the GPD.";
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
    return 0.1;
  }

  /**
   * Returns the packed bits for the genetic algorithm.
   *
   * @return		the bits
   */
  @Override
  public String pack() {
    return "TODO";
  }

  /**
   * Unpacks and applies the bits from the genetic algorithm.
   *
   * @param bits	the bits to use
   */
  @Override
  public void unpack(String bits) {
    // TODO
  }

  /**
   * Checks whether this object is handled by this discovery handler.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  @Override
  protected boolean handles(Object obj) {
    return (obj instanceof GPD) || (obj instanceof GPD[]);
  }
}
