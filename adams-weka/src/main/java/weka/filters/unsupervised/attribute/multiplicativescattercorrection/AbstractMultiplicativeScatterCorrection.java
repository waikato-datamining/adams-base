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
 * AbstractMultiplicativeScatterCorrection.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package weka.filters.unsupervised.attribute.multiplicativescattercorrection;

import weka.core.AbstractSimpleOptionHandler;

/**
 * Ancestor for correction schemes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMultiplicativeScatterCorrection
  extends AbstractSimpleOptionHandler {

  private static final long serialVersionUID = 1910580022776009212L;

  /**
   * Corrects the spectrum.
   *
   * @param waveno 	the wave numbers
   * @param average 	the average spectrum
   * @param data 	the spectrum to process
   * @return		the processed spectrum
   */
  public abstract double[] correct(double[] waveno, double[] average, double[] data);
}
