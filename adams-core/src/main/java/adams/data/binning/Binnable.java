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
 * Binnable.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning;

import java.io.Serializable;

/**
 * Wrapper for objects to be binned.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of payload
 */
public class Binnable<T>
  implements Serializable {

  private static final long serialVersionUID = 4963864458212337110L;

  /** the actual data object. */
  protected T m_Payload;

  /** the value to use for binning. */
  protected double m_Value;

  /**
   * Initializes the wrapper.
   *
   * @param payload 	the actual object
   * @param value 	the value to use for binning
   */
  public Binnable(T payload, double value) {
    m_Payload = payload;
    m_Value   = value;
  }

  /**
   * Returns the actual object.
   *
   * @return		the payload
   */
  public T getPayload() {
    return m_Payload;
  }

  /**
   * Returns the value to use for the binning calculation.
   *
   * @return		the value
   */
  public double getValue() {
    return m_Value;
  }

  /**
   * Returns a short description of the wrapper.
   *
   * @return		the description
   */
  public String toString() {
    return m_Value + ": " + m_Payload;
  }

}
