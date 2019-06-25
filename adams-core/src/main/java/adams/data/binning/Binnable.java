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
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for objects to be binned.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of payload
 */
public class Binnable<T>
  implements Serializable {

  private static final long serialVersionUID = 4963864458212337110L;

  /**
   * Interface for extracting values for bins from arbitrary objects.
   *
   * @param <T> the type of object to process
   */
  public interface BinValueExtractor<T> {

    /**
     * Extracts the numeric value to use for binning from the object.
     *
     * @param object	the object to process
     * @return		the extracted value
     */
    public double extractBinValue(T object);
  }

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

  /**
   * Wraps the double array in binnable objects, using the index as payload.
   *
   * @param values	the values to bin
   * @return		the wrapped indices/values
   * @throws Exception	if NaN value is encountered
   */
  public static List<Binnable<Integer>> wrap(double[] values) throws Exception {
    List<Binnable<Integer>> result;
    int					i;

    result = new ArrayList<>();
    for (i = 0; i < values.length; i++) {
      if (Double.isNaN(values[i]))
        throw new Exception("Non-numeric or missing value in row #" + (i+1) + ", cannot generate binnable object wrapper!");
      result.add(new Binnable<>(i, values[i]));
    }

    return result;
  }

  /**
   * Wraps the float array in binnable objects, using the index as payload.
   *
   * @param values	the values to bin
   * @return		the wrapped indices/values
   * @throws Exception	if NaN value is encountered
   */
  public static List<Binnable<Integer>> wrap(float[] values) throws Exception {
    List<Binnable<Integer>> result;
    int					i;

    result = new ArrayList<>();
    for (i = 0; i < values.length; i++) {
      if (Float.isNaN(values[i]))
        throw new Exception("Non-numeric or missing value in row #" + (i+1) + ", cannot generate binnable object wrapper!");
      result.add(new Binnable<>(i, values[i]));
    }

    return result;
  }

  /**
   * Wraps the double array in binnable objects, using the index as payload.
   *
   * @param values	the values to bin
   * @return		the wrapped indices/values
   * @throws Exception	if NaN value is encountered
   */
  public static List<Binnable<Integer>> wrap(Number[] values) throws Exception {
    List<Binnable<Integer>> result;
    int					i;

    result = new ArrayList<>();
    for (i = 0; i < values.length; i++) {
      if (Double.isNaN(values[i].doubleValue()))
        throw new Exception("Non-numeric or missing value in row #" + (i+1) + ", cannot generate binnable object wrapper!");
      result.add(new Binnable<>(i, values[i].doubleValue()));
    }

    return result;
  }

  /**
   * Wraps the double array in binnable objects, using the index as payload.
   *
   * @param values	the values to bin
   * @param extractor 	for extracting the bin value from the objects
   * @return		the wrapped indices/values
   * @throws Exception	if NaN value is encountered
   */
  public static <T> List<Binnable<T>> wrap(T[] values, BinValueExtractor<T> extractor) throws Exception {
    List<Binnable<T>> 	result;
    int			i;

    result = new ArrayList<>();
    for (i = 0; i < values.length; i++) {
      result.add(new Binnable<>(values[i], extractor.extractBinValue(values[i])));
    }

    return result;
  }

  /**
   * Turns the values of the binnable objects into a double array.
   *
   * @param objects	the binnable objects to get the values from
   * @return		the values as array
   */
  public static <T> double[] valuesToDoubleArray(List<Binnable<T>> objects) {
    double[] 	result;
    int		i;

    result = new double[objects.size()];
    for (i = 0; i < objects.size(); i++)
      result[i] = objects.get(i).getValue();

    return result;
  }

  /**
   * Turns the values of the binnable objects into a number array.
   *
   * @param objects	the binnable objects to get the values from
   * @return		the values as array
   */
  public static <T> Number[] valuesToNumberArray(List<Binnable<T>> objects) {
    Number[] 	result;
    int		i;

    result = new Number[objects.size()];
    for (i = 0; i < objects.size(); i++)
      result[i] = objects.get(i).getValue();

    return result;
  }
}
