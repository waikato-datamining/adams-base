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
 * Wrap.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.data.binning.Binnable;
import adams.data.binning.BinnableGroup;
import com.github.fracpete.javautils.enumerate.Enumerated;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * For wrapping data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Wrapping {

  /** the temporary index stored in the binnable meta-data. */
  public static final String TMP_INDEX = "$$$tmpindex$$$";

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

  /**
   * Simply keeps track of the number of objects encountered and returns that
   * count as bin value.
   *
   * @param <T> the payload type
   */
  public static class IndexedBinValueExtractor<T>
    implements BinValueExtractor<T>, Serializable {

    private static final long serialVersionUID = -2287393293543008133L;

    /** for keeping track of the index. */
    protected int m_Index;

    /**
     * Initializes the index.
     */
    public IndexedBinValueExtractor() {
      m_Index = 0;
    }

    /**
     * Extracts the numeric value to use for binning from the object.
     *
     * @param object	the object to process
     * @return		the extracted value
     */
    @Override
    public double extractBinValue(T object) {
      return m_Index++;
    }
  }

  /**
   * Wraps the double array in binnable objects, using the index as payload.
   *
   * @param values	the values to wrap
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
   * @param values	the values to wrap
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
   * @param values	the values to wrap
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
   * @param values	the values to wrap
   * @return		the wrapped indices/values
   * @throws Exception	if NaN value is encountered
   */
  public static List<Binnable<Integer>> wrap(Collection<Number> values) throws Exception {
    List<Binnable<Integer>>   	result;
    int				i;

    result = new ArrayList<>();
    i      = 0;
    for (Number value: values) {
      if (Double.isNaN(value.doubleValue()))
        throw new Exception("Non-numeric or missing value in row #" + (i+1) + ", cannot generate binnable object wrapper!");
      result.add(new Binnable<>(i, value.doubleValue()));
      i++;
    }

    return result;
  }

  /**
   * Wraps the array in binnable objects, using the extractor to determine
   * the value for the binnable object.
   *
   * @param values	the values to wrap
   * @param extractor 	for extracting the bin value from the objects
   * @return		the wrapped values
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
   * Wraps the collection in binnable objects, using the extractor to determine
   * the value for the binnable object.
   *
   * @param values	the values to wrap
   * @param extractor 	for extracting the value from the objects
   * @return		the wrapped values
   */
  public static <T> List<Binnable<T>> wrap(Collection<T> values, BinValueExtractor<T> extractor) throws Exception {
    List<Binnable<T>> 	result;

    result = new ArrayList<>();
    for (T value: values)
      result.add(new Binnable<>(value, extractor.extractBinValue(value)));

    return result;
  }

  /**
   * Wraps the collection of binnable group objects in binnable objects, using the extractor to determine
   * the value for the binnable object (uses first item of group).
   *
   * @param values	the values to wrap
   * @param extractor 	for extracting the value from the objects
   * @return		the wrapped values
   */
  public static <T> List<Binnable<BinnableGroup<T>>> wrapGroups(Collection<BinnableGroup<T>> values, BinValueExtractor<T> extractor) throws Exception {
    List<Binnable<BinnableGroup<T>>> 	result;

    result = new ArrayList<>();
    for (BinnableGroup<T> value: values)
      result.add(new Binnable<>(value, extractor.extractBinValue(value.get().get(0).getPayload())));

    return result;
  }

  /**
   * Unwraps the payloads from the binnable list.
   *
   * @param data	the data to unwrap
   * @param <T>		the type of payload
   * @return		the payload list
   */
  public static <T> List<T> unwrap(List<Binnable<T>> data) {
    List<T>	result;

    result = new ArrayList<>();
    for (Binnable<T> b: data)
      result.add(b.getPayload());

    return result;
  }

  /**
   * Adds the list index under a temporary key {@link #TMP_INDEX}.
   *
   * @param data	the data to enumerate
   * @param <T>		the payload type
   * @return		the updated (input)
   */
  public static <T> List<Binnable<T>> addTmpIndex(List<Binnable<T>> data) {
    for (Enumerated<Binnable<T>> d: enumerate(data))
      d.value.addMetaData(TMP_INDEX, d.index);

    return data;
  }

  /**
   * Collects indices stored under {@link #TMP_INDEX}.
   *
   * @param data	the data to retrieve the indices from
   * @return 		the indices
   */
  public static <T> TIntList getTmpIndices(List<Binnable<T>> data) {
    TIntList  	result;
    int 	i;

    result = new TIntArrayList();
    for (i = 0; i < data.size(); i++)
      result.add((Integer) data.get(i).getMetaData(Wrapping.TMP_INDEX));

    return result;
  }

  /**
   * Removes the list index under a temporary key {@link #TMP_INDEX}.
   *
   * @param data	the data to enumerate
   * @param <T>		the payload type
   * @return		the updated (input)
   */
  public static <T> List<Binnable<T>> removeTmpIndex(List<Binnable<T>> data) {
    for (Binnable<T> item: data)
      item.removeMetaData(TMP_INDEX);

    return data;
  }
}
