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

import adams.core.Utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

  /** the meta-data. */
  protected Map<String,Object> m_MetaData;

  /**
   * Initializes the wrapper.
   *
   * @param payload 	the actual object
   * @param value 	the value to use for binning
   */
  public Binnable(T payload, double value) {
    this(payload, value, null);
  }

  /**
   * Initializes the wrapper.
   *
   * @param payload 	the actual object
   * @param value 	the value to use for binning
   * @param metaData 	the meta-data to use, ignored if null
   */
  public Binnable(T payload, double value, Map<String,Object> metaData) {
    m_Payload  = payload;
    m_Value    = value;
    m_MetaData = null;
    if (metaData != null)
      m_MetaData = new HashMap<>(metaData);
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
   * Returns if meta-data is present.
   *
   * @return		true if meta-data there
   */
  public boolean hasMetaData() {
    return (m_MetaData != null);
  }

  /**
   * Adds meta-data.
   *
   * @param key		the key of the value
   * @param value	the value to store
   */
  public synchronized void addMetaData(String key, Object value) {
    if (m_MetaData == null)
      m_MetaData = new HashMap<>();
    m_MetaData.put(key, value);
  }

  /**
   * Removes meta-data.
   *
   * @param key		the key of the value to remove
   * @return		the removed value, null if none removed
   */
  public synchronized Object removeMetaData(String key) {
    if (m_MetaData != null)
      return m_MetaData.remove(key);
    else
      return null;
  }

  /**
   * Rertrieves meta-data.
   *
   * @param key		the key of the value to retrieve
   * @return		the associated value, null if none available
   */
  public synchronized Object getMetaData(String key) {
    if (m_MetaData != null)
      return m_MetaData.get(key);
    else
      return null;
  }

  /**
   * Returns the meta-data.
   *
   * @return		the data, null if none stored
   */
  public Map<String,Object> getMetaData() {
    return m_MetaData;
  }

  /**
   * Returns a short description of the wrapper.
   *
   * @return		the description
   */
  public String toString() {
    return toString(-1);
  }

  /**
   * Returns a short description of the wrapper.
   *
   * @param decimals 	the number of decimals in the output, -1 for no limit
   * @return		the description
   */
  public String toString(int decimals) {
    if (decimals == -1)
      return m_Value + ": " + m_Payload;
    else
      return Utils.doubleToString(m_Value, decimals) + ": " + m_Payload;
  }
}
