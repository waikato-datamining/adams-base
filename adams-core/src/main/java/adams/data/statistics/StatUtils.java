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
 * StatUtils.java
 * Copyright (C) 2008-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;

import adams.core.Utils;
import adams.data.random.JavaRandomInt;
import adams.data.random.RandomIntegerRangeGenerator;
import adams.env.Environment;
import gnu.trove.list.array.TIntArrayList;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Arrays;
import java.util.Random;

/**
 * A statistical helper class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StatUtils {

  /** for computation. */
  protected static NormalDistribution m_NormalDist = new NormalDistribution();
  
  /**
   * Turns the byte array into a Byte array.
   *
   * @param array	the array to convert
   * @return		the converted array
   */
  public static Number[] toNumberArray(byte[] array) {
    Byte[]	result;
    int		i;

    result = new Byte[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = new Byte(array[i]);

    return result;
  }

  /**
   * Turns the short array into a Short array.
   *
   * @param array	the array to convert
   * @return		the converted array
   */
  public static Number[] toNumberArray(short[] array) {
    Short[]	result;
    int		i;

    result = new Short[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = new Short(array[i]);

    return result;
  }

  /**
   * Turns the int array into a Integer array.
   *
   * @param array	the array to convert
   * @return		the converted array
   */
  public static Number[] toNumberArray(int[] array) {
    Integer[]	result;
    int		i;

    result = new Integer[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = new Integer(array[i]);

    return result;
  }

  /**
   * Turns the long array into a Long array.
   *
   * @param array	the array to convert
   * @return		the converted array
   */
  public static Number[] toNumberArray(long[] array) {
    Long[]	result;
    int		i;

    result = new Long[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = new Long(array[i]);

    return result;
  }

  /**
   * Turns the float array into a Float array.
   *
   * @param array	the array to convert
   * @return		the converted array
   */
  public static Number[] toNumberArray(float[] array) {
    Float[]	result;
    int		i;

    result = new Float[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = new Float(array[i]);

    return result;
  }

  /**
   * Turns the double array into a Double array.
   *
   * @param array	the array to convert
   * @return		the converted array
   */
  public static Number[] toNumberArray(double[] array) {
    Double[]	result;
    int		i;

    result = new Double[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = new Double(array[i]);

    return result;
  }

  /**
   * Turns the Number array into one consisting of primitive bytes.
   *
   * @param array	the array to convert
   * @return		the converted array
   */
  public static byte[] toByteArray(Number[] array) {
    byte[]	result;
    int		i;

    result = new byte[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i].byteValue();

    return result;
  }

  /**
   * Turns the Number array into one consisting of primitive shorts.
   *
   * @param array	the array to convert
   * @return		the converted array
   */
  public static short[] toShortArray(Number[] array) {
    short[]	result;
    int		i;

    result = new short[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i].shortValue();

    return result;
  }

  /**
   * Turns the Number array into one consisting of primitive ints.
   *
   * @param array	the array to convert
   * @return		the converted array
   */
  public static int[] toIntArray(Number[] array) {
    int[]	result;
    int		i;

    result = new int[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i].intValue();

    return result;
  }

  /**
   * Turns the Number array into one consisting of primitive longs.
   *
   * @param array	the array to convert
   * @return		the converted array
   */
  public static long[] toLongArray(Number[] array) {
    long[]	result;
    int		i;

    result = new long[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i].longValue();

    return result;
  }

  /**
   * Turns the Number array into one consisting of primitive floats.
   *
   * @param array	the array to convert
   * @return		the converted array
   */
  public static float[] toFloatArray(Number[] array) {
    float[]	result;
    int		i;

    result = new float[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i].floatValue();

    return result;
  }

  /**
   * Turns the Number array into one consisting of primitive doubles.
   *
   * @param array	the array to convert
   * @return		the converted array
   */
  public static double[] toDoubleArray(Number[] array) {
    double[]	result;
    int		i;

    result = new double[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i].doubleValue();

    return result;
  }

  /**
   * Returns the (first occurrence of the) index of the cell with the smallest
   * number. -1 in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the index
   */
  public static int minIndex(Number[] array) {
    int		result;
    int		i;
    double	minValue;

    result = -1;

    minValue = Double.MAX_VALUE;
    for (i = 0; i < array.length; i++) {
      if (array[i].doubleValue() < minValue) {
	minValue = array[i].doubleValue();
	result   = i;
      }
    }

    return result;
  }

  /**
   * Returns the (first occurrence of the) index of the cell with the smallest
   * int. -1 in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the index
   */
  public static int minIndex(int[] array) {
    return minIndex(toNumberArray(array));
  }

  /**
   * Returns the (first occurrence of the) index of the cell with the smallest
   * double. -1 in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the index
   */
  public static int minIndex(double[] array) {
    return minIndex(toNumberArray(array));
  }

  /**
   * Returns the (first occurrence of the) smallest value in the given array.
   * Null in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the smallest value
   */
  public static Number min(Number[] array) {
    int		index;

    index = minIndex(array);

    if (index == -1)
      return null;
    else
      return array[index];
  }

  /**
   * Returns the (first occurrence of the) smallest value in the given array.
   * Integer.MIN_VALUE in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the smallest value
   */
  public static int min(int[] array) {
    Integer	result;

    result = (Integer) min(toNumberArray(array));
    if (result == null)
      return Integer.MIN_VALUE;
    else
      return result;
  }

  /**
   * Returns the (first occurrence of the) smallest value in the given array.
   * -Double.MAX_VALUE in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the smallest value
   */
  public static double min(double[] array) {
    Double	result;

    result = (Double) min(toNumberArray(array));
    if (result == null)
      return -Double.MAX_VALUE;
    else
      return result;
  }

  /**
   * Returns the (first occurrence of the) index of the cell with the biggest
   * number. -1 in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the index
   */
  public static int maxIndex(Number[] array) {
    int		result;
    int		i;
    double	maxValue;

    result = -1;

    maxValue = -Double.MAX_VALUE;
    for (i = 0; i < array.length; i++) {
      if (array[i].doubleValue() > maxValue) {
	maxValue = array[i].doubleValue();
	result   = i;
      }
    }

    return result;
  }

  /**
   * Returns the (first occurrence of the) index of the cell with the biggest
   * int. -1 in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the index
   */
  public static int maxIndex(int[] array) {
    return maxIndex(toNumberArray(array));
  }

  /**
   * Returns the (first occurrence of the) index of the cell with the biggest
   * double. -1 in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the index
   */
  public static int maxIndex(double[] array) {
    return maxIndex(toNumberArray(array));
  }

  /**
   * Returns the (first occurrence of the) biggest value in the given array.
   * Null in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the biggest value
   */
  public static Number max(Number[] array) {
    int		index;

    index = maxIndex(array);

    if (index == -1)
      return null;
    else
      return array[index];
  }

  /**
   * Returns the (first occurrence of the) biggest value in the given array.
   * Integer.MAX_VALUE in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the biggest value
   */
  public static int max(int[] array) {
    Integer	result;

    result = (Integer) max(toNumberArray(array));
    if (result == null)
      return Integer.MAX_VALUE;
    else
      return result;
  }

  /**
   * Returns the (first occurrence of the) biggest value in the given array.
   * Double.MAX_VALUE in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the biggest value
   */
  public static double max(double[] array) {
    Double	result;

    result = (Double) max(toNumberArray(array));
    if (result == null)
      return Double.MAX_VALUE;
    else
      return result;
  }

  /**
   * Returns the mean of the given array.
   * NaN is returned in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the mean
   */
  public static double mean(Number[] array) {
    double	result;
    int		i;

    if (array.length == 0)
      return Double.NaN;

    result = 0;

    for (i = 0; i < array.length; i++)
      result += array[i].doubleValue();

    result /= array.length;

    return result;
  }

  /**
   * Returns the mean of the given array.
   * NaN is returned in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the mean
   */
  public static double mean(int[] array) {
    return mean(toNumberArray(array));
  }

  /**
   * Returns the mean of the given array.
   * NaN is returned in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the mean
   */
  public static double mean(double[] array) {
    return mean(toNumberArray(array));
  }

  /**
   * Returns the iqr of the given array.
   * NaN is returned in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the iqr
   */
  public static double iqr(double[] array) {
    return iqr(toNumberArray(array));
  }

  /**
   * Returns the iqr of the given array.
   * NaN is returned in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the iqr
   */
  public static double iqr(int[] array) {
    return iqr(toNumberArray(array));
  }

  /**
   * Returns the interquartile of the given array.
   * NaN is returned in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the iqr
   */
  public static double iqr(Number[] array) {
    Number[]	sorted;
    double 	iqr1;
    double	iqr3;

    if (array.length == 0)
      return Double.NaN;

    sorted = array.clone();
    Arrays.sort(sorted);

    iqr1 = sorted[(int) (sorted.length * 0.25)].doubleValue();
    iqr3 = sorted[(int) (sorted.length * 0.75)].doubleValue();

    return iqr3 - iqr1;
  }

  /**
   * Returns the quartile of the given array.
   * NaN is returned in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @param quartile	the quartile to return (0-1)
   * @return		the quartile
   */
  public static double quartile(double[] array, double quartile) {
    return quartile(toNumberArray(array), quartile);
  }

  /**
   * Returns the quartile of the given array.
   * NaN is returned in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @param quartile	the quartile to return (0-1)
   * @return		the quartile
   */
  public static double quartile(int[] array, double quartile) {
    return quartile(toNumberArray(array), quartile);
  }

  /**
   * Returns the quartile of the given array.
   * NaN is returned in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @param quartile	the quartile to return (0-1)
   * @return		the quartile
   */
  public static double quartile(Number[] array, double quartile) {
    double  result;
    Number[]	sorted;

    if (array.length == 0)
      return Double.NaN;

    sorted = array.clone();
    Arrays.sort(sorted);

    result = sorted[(int) (sorted.length * quartile)].doubleValue();

    return result;
  }

  /**
   * Returns the median of the given array.
   * NaN is returned in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the median
   */
  public static double median(Number[] array) {
    double	result;
    Number[]	sorted;

    if (array.length == 0)
      return Double.NaN;

    sorted = array.clone();
    Arrays.sort(sorted);

    if (sorted.length % 2 == 0)
      result = (sorted[sorted.length / 2 - 1].doubleValue() + sorted[sorted.length / 2].doubleValue()) / 2;
    else
      result = sorted[sorted.length / 2].doubleValue();

    return result;
  }

  /**
   * Returns the median of the given array.
   * NaN is returned in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the median
   */
  public static double median(int[] array) {
    return median(toNumberArray(array));
  }

  /**
   * Returns the median of the given array.
   * NaN is returned in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @return		the median
   */
  public static double median(double[] array) {
    return median(toNumberArray(array));
  }

  /**
   * Returns the std deviation of the given array.
   * NaN is returned in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @param isSample	if true, then the sample standard deviation instead
   * 			of the population standard deviation is calculated
   * 			(using n-1 instead of n).
   * @return		the std deviation
   */
  public static double stddev(Number[] array, boolean isSample) {
    double	result;
    double	mean;
    int		i;

    if (array.length == 0)
      return Double.NaN;

    result = 0;
    mean   = mean(array);
    for (i = 0; i < array.length; i++)
      result += Math.pow(array[i].doubleValue() - mean, 2);
    if (isSample)
      result /= (array.length - 1);
    else
      result /= array.length;
    result = Math.sqrt(result);

    return result;
  }

  /**
   * Returns the std deviation of the given array.
   * NaN is returned in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @param isSample	if true, then the sample standard deviation instead
   * 			of the population standard deviation is calculated
   * 			(using n-1 instead of n).
   * @return		the std deviation
   */
  public static double stddev(int[] array, boolean isSample) {
    return stddev(toNumberArray(array), isSample);
  }

  /**
   * Returns the std deviation of the given array.
   * NaN is returned in case of zero-length arrays.
   *
   * @param array	the array to work on
   * @param isSample	if true, then the sample standard deviation instead
   * 			of the population standard deviation is calculated
   * 			(using n-1 instead of n).
   * @return		the std deviation
   */
  public static double stddev(double[] array, boolean isSample) {
    return stddev(toNumberArray(array), isSample);
  }

  /**
   * Normalizes the given array (returns a copy), i.e., the array will sum up
   * to 1. In case of a sum of 0, it returns null.
   *
   * @param array	the array to work on
   * @return		the normalized array
   */
  public static Double[] normalize(Number[] array) {
    Double[]	result;
    double	sum;
    int		i;

    result = new Double[array.length];
    sum    = 0;
    for (i = 0; i < array.length; i++)
      sum += array[i].doubleValue();
    if (sum > 0) {
      for (i = 0; i < array.length; i++)
	result[i] = array[i].doubleValue() / sum;
    }
    else {
      result = null;
    }

    return result;
  }

  /**
   * Normalizes the given array (returns a copy).
   *
   * @param array	the array to work on
   * @return		the std deviation
   */
  public static double[] normalize(int[] array) {
    return toDoubleArray(normalize(toNumberArray(array)));
  }

  /**
   * Normalizes the given array (returns a copy).
   *
   * @param array	the array to work on
   * @return		the std deviation
   */
  public static double[] normalize(double[] array) {
    return toDoubleArray(normalize(toNumberArray(array)));
  }

  /**
   * Standardizes the given array (returns a copy). Returns null if the
   * standard deviation is zero and data cannot be standardized.
   *
   * @param array	the array to work on
   * @param isSample	if true, then the sample standard deviation instead
   * 			of the population standard deviation is calculated
   * 			(using n-1 instead of n).
   * @return		the std deviation
   */
  public static Double[] standardize(Number[] array, boolean isSample) {
    Double[]	result;
    double	mean;
    double	stddev;
    int		i;

    result = new Double[array.length];
    mean   = mean(array);
    stddev = stddev(array, isSample);
    if (stddev == 0.0)
      return null;

    for (i = 0; i < array.length; i++)
      result[i] = (array[i].doubleValue() - mean) / stddev;

    return result;
  }

  /**
   * Standardizes the given array (returns a copy). Returns null if the
   * standard deviation is zero and data cannot be standardized.
   *
   * @param array	the array to work on
   * @param isSample	if true, then the sample standard deviation instead
   * 			of the population standard deviation is calculated
   * 			(using n-1 instead of n).
   * @return		the std deviation
   */
  public static double[] standardize(double[] array, boolean isSample) {
    return toDoubleArray(standardize(toNumberArray(array), isSample));
  }

  /**
   * Standardizes the given array (returns a copy). Returns null if the
   * standard deviation is zero and data cannot be standardized.
   *
   * @param array	the array to work on
   * @param isSample	if true, then the sample standard deviation instead
   * 			of the population standard deviation is calculated
   * 			(using n-1 instead of n).
   * @return		the std deviation
   */
  public static double[] standardize(int[] array, boolean isSample) {
    return toDoubleArray(standardize(toNumberArray(array), isSample));
  }

  /**
   * Returns sum of all the elements in the array.
   *
   * @param array	the array to work on
   * @return		the sum
   */
  public static double sum(Number[] array) {
    double	result;
    int		i;

    result = 0.0;
    for (i = 0; i < array.length; i++)
      result += array[i].doubleValue();

    return result;
  }

  /**
   * Returns sum of all the elements in the array.
   *
   * @param array	the array to work on
   * @return		the sum
   */
  public static double sum(int[] array) {
    return sum(toNumberArray(array));
  }

  /**
   * Returns sum of all the elements in the array.
   *
   * @param array	the array to work on
   * @return		the sum
   */
  public static double sum(double[] array) {
    return sum(toNumberArray(array));
  }

  /**
   * Returns sum of all the squared elements in the array.
   *
   * @param array	the array to work on
   * @return		the sum
   */
  public static double sumOfSquares(Number[] array) {
    double	result;
    int		i;

    result = 0.0;
    for (i = 0; i < array.length; i++)
      result += array[i].doubleValue() * array[i].doubleValue();

    return result;
  }

  /**
   * Returns sum of all the squared elements in the array.
   *
   * @param array	the array to work on
   * @return		the sum
   */
  public static double sumOfSquares(int[] array) {
    return sumOfSquares(toNumberArray(array));
  }

  /**
   * Returns sum of all the squared elements in the array.
   *
   * @param array	the array to work on
   * @return		the sum
   */
  public static double sumOfSquares(double[] array) {
    return sumOfSquares(toNumberArray(array));
  }

  /**
   * Returns a sorted copy of the array (ascending).
   *
   * @param array	the array to sort
   * @return		the sorted array
   */
  public static Number[] sort(Number[] array) {
    return sort(array, true);
  }

  /**
   * Returns a sorted copy of the array (ascending).
   *
   * @param array	the array to sort
   * @return		the sorted array
   */
  public static int[] sort(int[] array) {
    return sort(array, true);
  }

  /**
   * Returns a sorted copy of the array (ascending).
   *
   * @param array	the array to sort
   * @return		the sorted array
   */
  public static double[] sort(double[] array) {
    return sort(array, true);
  }

  /**
   * Returns a sorted copy of the array (ascending or descending).
   *
   * @param array	the array to sort
   * @param asc		if true then the data gets sorted in ascending manner,
   * 			otherwise in descending manner
   * @return		the sorted array
   */
  public static Number[] sort(Number[] array, boolean asc) {
    Number[]	sorted;
    Number	value;
    int		i;
    int		n;

    sorted = array.clone();
    Arrays.sort(sorted);

    if (!asc) {
      for (i = 0; i < array.length / 2; i++) {
	n         = sorted.length - i - 1;
	value     = sorted[i];
	sorted[i] = sorted[n];
	sorted[n] = value;
      }
    }

    return sorted;
  }

  /**
   * Returns a sorted copy of the array (ascending or descending).
   *
   * @param array	the array to sort
   * @param asc		if true then the data gets sorted in ascending manner,
   * 			otherwise in descending manner
   * @return		the sorted array
   */
  public static int[] sort(int[] array, boolean asc) {
    int[]	result;
    Integer[]	sorted;
    int		i;

    sorted = (Integer[]) sort(toNumberArray(array), asc);
    result = new int[sorted.length];
    for (i = 0; i < sorted.length; i++)
      result[i] = sorted[i];

    return result;
  }

  /**
   * Returns a sorted copy of the array (ascending or descending).
   *
   * @param array	the array to sort
   * @param asc		if true then the data gets sorted in ascending manner,
   * 			otherwise in descending manner
   * @return		the sorted array
   */
  public static double[] sort(double[] array, boolean asc) {
    double[]	result;
    Double[]	sorted;
    int		i;

    sorted = (Double[]) sort(toNumberArray(array), asc);
    result = new double[sorted.length];
    for (i = 0; i < sorted.length; i++)
      result[i] = sorted[i];

    return result;
  }

  /**
   * Returns the (first) index of the number one is looking for in the given
   * array. -1 is returned if not found.
   *
   * @param array	the array to search
   * @param toFind	the number to find
   * @return		the index
   */
  public static int findFirst(Number[] array, Number toFind) {
    int		result;
    int		i;

    result = -1;

    for (i = 0; i < array.length; i++) {
      if (toFind.equals(array[i])) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Returns the (first) index of the integer one is looking for in the given
   * array. -1 is returned if not found.
   *
   * @param array	the array to search
   * @param toFind	the integer to find
   * @return		the index
   */
  public static int findFirst(int[] array, int toFind) {
    return findFirst(toNumberArray(array), new Integer(toFind));
  }

  /**
   * Returns the (first) index of the double one is looking for in the given
   * array. -1 is returned if not found.
   *
   * @param array	the array to search
   * @param toFind	the double to find
   * @return		the index
   */
  public static int findFirst(double[] array, double toFind) {
    return findFirst(toNumberArray(array), new Double(toFind));
  }

  /**
   * Returns the index of the number closest to the one one is looking for in
   * the given array.
   *
   * @param array	the array to search
   * @param toFind	the number to find
   * @return		the index
   */
  public static int findClosest(Number[] array, Number toFind) {
    int		result;
    double	diff;
    double	newDiff;
    int		i;

    result = -1;
    diff   = Double.MAX_VALUE;

    for (i = 0; i < array.length; i++) {
      newDiff = Math.abs(array[i].doubleValue() - toFind.doubleValue());
      if (newDiff < diff) {
	diff   = newDiff;
	result = i;
      }
    }

    return result;
  }

  /**
   * Returns the index of the integer closest to the one one is looking for in
   * the given array.
   *
   * @param array	the array to search
   * @param toFind	the integer to find
   * @return		the index
   */
  public static int findClosest(int[] array, int toFind) {
    return findClosest(toNumberArray(array), new Integer(toFind));
  }

  /**
   * Returns the index of the double closest to the one one is looking for in
   * the given array.
   *
   * @param array	the array to search
   * @param toFind	the double to find
   * @return		the index
   */
  public static int findClosest(double[] array, double toFind) {
    return findClosest(toNumberArray(array), new Double(toFind));
  }

  /**
   * Computes the correlation coefficient between the two data vectors and returns it.
   *
   * @param y1		the first data array
   * @param y2		the second data array
   * @return		the computed correlation
   */
  public static double correlationCoefficient(double[] y1, double[] y2) {
    return correlationCoefficient(toNumberArray(y1), toNumberArray(y2));
  }

  /**
   * Computes the correlation coefficient between the two data vectors and returns it.
   *
   * @param y1		the first data array
   * @param y2		the second data array
   * @return		the computed correlation
   */
  public static double correlationCoefficient(Number[] y1, Number[] y2) {
    double		c;
    int 		i;
    int			n;
    double 		av1;
    double		av2;
    double		y11;
    double		y22;
    double		y12;

    if (y1.length != y2.length)
      throw new IllegalArgumentException(
	  "Arrays differ in length: " + y1.length + " != " + y2.length);

    n      = y1.length;
    av1    = 0.0;
    av2    = 0.0;
    y11    = 0.0;
    y22    = 0.0;
    y12    = 0.0;

    if (y1.length <= 1) {
      c = 1.0;
    }
    else {
      for (i = 0; i < n; i++) {
	av1 += y1[i].doubleValue();
	av2 += y2[i].doubleValue();
      }

      av1 /= (double) n;
      av2 /= (double) n;

      for (i = 0; i < n; i++) {
	y11 += (y1[i].doubleValue() - av1) * (y1[i].doubleValue() - av1);
	y22 += (y2[i].doubleValue() - av2) * (y2[i].doubleValue() - av2);
	y12 += (y1[i].doubleValue() - av1) * (y2[i].doubleValue() - av2);
      }

      if (y11 == 0.0 && y22 == 0.0) {
	c = 1.0;
      } else if (y11 == 0.0 || y22 == 0.0) {
	c = 0.0;
      } else
	c = y12 / Math.sqrt(Math.abs(y11 * y22));
    }

    return c;
  }

  /**
   * Computes the root mean squared error between the two data vectors and returns it.
   *
   * @param actual	the second data array
   * @param predicted	the first data array
   * @return		the rmse
   */
  public static double rmse(double[] actual, double[] predicted) {
    return rmse(toNumberArray(actual), toNumberArray(predicted));
  }

  /**
   * Computes the root mean squared error between the two data vectors and returns it.
   *
   * @param actual	the second data array
   * @param predicted	the first data array
   * @return		the rmse
   */
  public static double rmse(Number[] actual, Number[] predicted) {
    double	result;
    int		i;

    if (actual.length != predicted.length)
      throw new IllegalArgumentException(
	  "Arrays differ in length: " + actual.length + " != " + predicted.length);

    result = 0;

    for (i = 0; i < predicted.length; i++)
      result += Math.pow(predicted[i].doubleValue() - actual[i].doubleValue(), 2.0);
    result /= predicted.length;
    result = Math.sqrt(result);

    return result;
  }

  /**
   * Computes the mean absolute error between the two data vectors and returns it.
   *
   * @param actual	the second data array
   * @param predicted	the first data array
   * @return		the mae
   */
  public static double mae(double[] actual, double[] predicted) {
    return mae(toNumberArray(actual), toNumberArray(predicted));
  }

  /**
   * Computes the mean absolute error between the two data vectors and returns it.
   *
   * @param actual	the second data array
   * @param predicted	the first data array
   * @return		the mae
   */
  public static double mae(Number[] actual, Number[] predicted) {
    double	result;
    int		i;

    if (actual.length != predicted.length)
      throw new IllegalArgumentException(
	  "Arrays differ in length: " + actual.length + " != " + predicted.length);

    result = 0;

    for (i = 0; i < predicted.length; i++)
      result += Math.abs(predicted[i].doubleValue() - actual[i].doubleValue());
    result /= predicted.length;

    return result;
  }

  /**
   * Computes the relative absolute error between the two data vectors and returns it.
   *
   * @param actual	the second data array
   * @param predicted	the first data array
   * @return		the rae
   */
  public static double rae(double[] actual, double[] predicted) {
    return rae(toNumberArray(actual), toNumberArray(predicted));
  }

  /**
   * Computes the relative absolute error between the two data vectors and returns it.
   *
   * @param actual	the second data array
   * @param predicted	the first data array
   * @return		the rae
   */
  public static double rae(Number[] actual, Number[] predicted) {
    double	result;
    double	pred;
    double	act;
    double	actMean;
    int		i;

    if (actual.length != predicted.length)
      throw new IllegalArgumentException(
	  "Arrays differ in length: " + actual.length + " != " + predicted.length);

    pred    = 0;
    act     = 0;
    actMean = mean(actual);
    for (i = 0; i < predicted.length; i++) {
      pred += Math.abs(predicted[i].doubleValue() - actual[i].doubleValue());
      act  += Math.abs(actual[i].doubleValue() - actMean);
    }

    if (act == 0)
      result = Double.NaN;
    else
      result = pred / act;

    return result;
  }

  /**
   * Computes the root relative squared error between the two data vectors and returns it.
   *
   * @param actual	the second data array
   * @param predicted	the first data array
   * @return		the rrse
   */
  public static double rrse(double[] actual, double[] predicted) {
    return rrse(toNumberArray(actual), toNumberArray(predicted));
  }

  /**
   * Computes the root relative squared error between the two data vectors and returns it.
   *
   * @param actual	the second data array
   * @param predicted	the first data array
   * @return		the rrse
   */
  public static double rrse(Number[] actual, Number[] predicted) {
    double	result;
    double	pred;
    double	act;
    double	actMean;
    int		i;

    if (actual.length != predicted.length)
      throw new IllegalArgumentException(
	  "Arrays differ in length: " + actual.length + " != " + predicted.length);

    pred    = 0;
    act     = 0;
    actMean = mean(actual);
    for (i = 0; i < predicted.length; i++) {
      pred += Math.pow(predicted[i].doubleValue() - actual[i].doubleValue(), 2.0);
      act  += Math.pow(actual[i].doubleValue() - actMean, 2.0);
    }

    if (act == 0)
      result = Double.NaN;
    else
      result = Math.sqrt(pred / act);

    return result;
  }

  /**
   * Computes the standard scores for the array.
   *
   * @param x		the data array
   * @param isSample	if true, then the sample standard deviation instead
   * 			of the population standard deviation is calculated
   * 			(using n-1 instead of n).
   * @return		the standard scores
   */
  public static double[] standardScores(double[] x, boolean isSample) {
    Number[]	n;

    n = toNumberArray(x);

    return standardScores(n, n, isSample);
  }

  /**
   * Computes the standard scores for the array.
   *
   * @param x		the data array
   * @param isSample	if true, then the sample standard deviation instead
   * 			of the population standard deviation is calculated
   * 			(using n-1 instead of n).
   * @return		the standard scores
   */
  public static double[] standardScores(Number[] x, boolean isSample) {
    return standardScores(x, x, isSample);
  }

  /**
   * Computes the standard scores. The mean/stdev are determined from the first
   * array (actual) and the z-scores are produced for the second one (predicted).
   *
   * @param actual	the second data array
   * @param predicted	the first data array
   * @param isSample	if true, then the sample standard deviation instead
   * 			of the population standard deviation is calculated
   * 			(using n-1 instead of n).
   * @return		the standard scores
   */
  public static double[] standardScores(double[] actual, double[] predicted, boolean isSample) {
    return standardScores(toNumberArray(actual), toNumberArray(predicted), isSample);
  }

  /**
   * Computes the standard scores. The mean/stdev are determined from the first
   * array (actual) and the z-scores are produced for the second one (predicted).
   *
   * @param actual	the second data array
   * @param predicted	the first data array
   * @param isSample	if true, then the sample standard deviation instead
   * 			of the population standard deviation is calculated
   * 			(using n-1 instead of n).
   * @return		the standard scores
   */
  public static double[] standardScores(Number[] actual, Number[] predicted, boolean isSample) {
    double[]	result;
    double	actMean;
    double	actStdev;
    int		i;

    if (actual.length != predicted.length)
      throw new IllegalArgumentException(
	  "Arrays differ in length: " + actual.length + " != " + predicted.length);

    result   = new double[actual.length];
    actMean  = mean(actual);
    actStdev = stddev(actual, isSample);
    for (i = 0; i < predicted.length; i++) {
      if (actStdev == 0)
	result[i] = Double.NaN;
      else
	result[i] = (predicted[i].doubleValue() - actMean) / actStdev;
    }

    return result;
  }
  
  /**
   * Calculates the signal/noise ratio.
   * <br><br>
   * For more details, see <a href="http://en.wikipedia.org/wiki/Signal-to-noise_ratio" target="_blank">Signal-to-noise ratio</a>.
   * 
   * @param x		the input data to calculate the ratio for
   * @return		the ratio
   */
  public static double signalToNoiseRatio(Number[] x) {
    return mean(x) / stddev(x, true);
  }
  
  /**
   * Calculates the signal/noise ratio.
   * <br><br>
   * For more details, see <a href="http://en.wikipedia.org/wiki/Signal-to-noise_ratio" target="_blank">Signal-to-noise ratio</a>.
   * 
   * @param x		the input data to calculate the ratio for
   * @return		the ratio
   */
  public static double signalToNoiseRatio(double[] x) {
    return mean(x) / stddev(x, true);
  }
  
  /**
   * Applies row-wise normalization to the data.
   * 
   * @param x		the input data
   * @return		the normalize data
   */
  public static double[] rowNorm(Number[] x) {
    return rowNorm(toDoubleArray(x));
  }
  
  /**
   * Applies row-wise normalization to the data.
   * 
   * @param x		the input data
   * @return		the normalize data
   */
  public static double[] rowNorm(double[] x) {
    double[]	result;
    StatCalc 	sc;
    double 	mn;
    double 	sd;
    int		i;
    
    result = new double[x.length];
    sc     = new StatCalc();
    for (i = 0; i < x.length; i++)
      sc.enter(x[i]);

    mn = sc.getMean();
    sd = sc.getStandardDeviation();

    for (i = 0; i < x.length; i++)
      result[i] = (x[i] - mn) / sd;
    
    return result;
  }

  /**
   * Returns the area under the Normal (Gaussian) probability density function,
   * integrated from minus infinity to <tt>x</tt> (assumes mean is zero,
   * variance is one).
   * 
   * <pre>
   *                            x
   *                             -
   *                   1        | |          2
   *  normal(x)  = ---------    |    exp( - t /2 ) dt
   *               sqrt(2pi)  | |
   *                           -
   *                          -inf.
   * 
   *             =  ( 1 + erf(z) ) / 2
   *             =  erfc(z) / 2
   * </pre>
   * 
   * where <tt>z = x/sqrt(2)</tt>. Computation is via the functions
   * <tt>errorFunction</tt> and <tt>errorFunctionComplement</tt>.
   * 
   * @param a the z-value
   * @return the probability of the z value according to the normal pdf
   */
  public static double normalProbability(double a) {
    return m_NormalDist.cumulativeProbability(a);
  }

  /**
   * Returns the value, <tt>x</tt>, for which the area under the Normal
   * (Gaussian) probability density function (integrated from minus infinity to
   * <tt>x</tt>) is equal to the argument <tt>y</tt> (assumes mean is zero,
   * variance is one).
   * <p>
   * For small arguments <tt>0 < y < exp(-2)</tt>, the program computes
   * <tt>z = sqrt( -2.0 * log(y) )</tt>; then the approximation is
   * <tt>x = z - log(z)/z  - (1/z) P(1/z) / Q(1/z)</tt>. There are two rational
   * functions P/Q, one for <tt>0 < y < exp(-32)</tt> and the other for
   * <tt>y</tt> up to <tt>exp(-2)</tt>. For larger arguments,
   * <tt>w = y - 0.5</tt>, and <tt>x/sqrt(2pi) = w + w**3 R(w**2)/S(w**2))</tt>.
   * 
   * @param y0 the area under the normal pdf
   * @return the z-value
   */
  public static double normalInverse(double y0) {
    return m_NormalDist.inverseCumulativeProbability(y0);
  }

  /**
   * Calculates the slope and intercept between the two arrays.
   *
   * @param x		the first array, representing the X values
   * @param y		the second array, representing the Y values
   * @return		intercept/slope
   */
  public static double[] linearRegression(Number[] x, Number[] y) {
    return linearRegression(toDoubleArray(x), toDoubleArray(y));
  }

  /**
   * Calculates the slope and intercept between the two arrays.
   *
   * @param x		the first array, representing the X values
   * @param y		the second array, representing the Y values
   * @return		intercept/slope
   */
  public static double[] linearRegression(double[] x, double[] y) {
    double[]	xTimesY;
    int		i;
    int		n;
    double	a;
    double	b;

    n      = x.length;
    xTimesY = new double[n];
    for (i = 0; i < n; i++)
      xTimesY[i] = x[i] * y[i];

    a = (StatUtils.sum(y) * StatUtils.sumOfSquares(x) - StatUtils.sum(x) * StatUtils.sum(xTimesY))
        / (n * (StatUtils.sumOfSquares(x)) - Math.pow(StatUtils.sum(x), 2));
    b = (n * StatUtils.sum(xTimesY) - StatUtils.sum(x) * StatUtils.sum(y))
        / (n * StatUtils.sumOfSquares(x) - Math.pow(StatUtils.sum(x), 2));

    return new double[]{a, b};
  }

  /**
   * Computes the Kendall-Theil robust regression of the given data points.
   * Also called Theil-Sen estimator (see
   * <a href="https://en.wikipedia.org/wiki/Theil%E2%80%93Sen_estimator">here</a>).
   *
   * @param x		the x coordinates
   * @param y		the y coordinates
   * @return		intercept/slope
   */
  public static double[] kendallTheil(double[] x, double[] y) {
    return kendallTheil(toNumberArray(x), toNumberArray(y));
  }

  /**
   * Computes the Kendall-Theil robust regression of the given data points.
   * Also called Theil-Sen estimator (see
   * <a href="https://en.wikipedia.org/wiki/Theil%E2%80%93Sen_estimator">here</a>).
   *
   * @param x		the x coordinates
   * @param y		the y coordinates
   * @return		intercept/slope
   */
  public static double[] kendallTheil(Number[] x, Number[] y) {
    double[]	result;
    double[]	slopes;
    int		i;
    int		j;
    int		n;

    if (x.length != y.length)
      throw new IllegalArgumentException(
	  "Arrays differ in length: " + x.length + " != " + y.length);

    if (x.length <= 1)
      return new double[]{1.0, 0.0};

    slopes = new double[x.length * (x.length - 1) / 2];
    n      = 0;
    for (i = 0; i < x.length - 1; i++) {
      for (j = i + 1; j < x.length; j++) {
	slopes[n] = (y[j].doubleValue() - y[i].doubleValue()) / (x[j].doubleValue() - x[i].doubleValue());
	n++;
      }
    }

    result    = new double[2];
    result[1] = median(slopes);
    result[0] = median(y) - result[1] * median(x);

    return result;
  }

  /**
   * Converts the matrix into a flat array, row after row.
   *
   * @param matrix	the matrix to convert
   * @return		the matrix as arrawy (row after row)
   */
  public static byte[] flatten(byte[][] matrix) {
    byte[]	result;
    int		i;
    int		n;
    int		len;

    len = 0;
    for (i = 0; i < matrix.length; i++)
      len += matrix[i].length;

    result = new byte[len];
    len    = 0;
    for (i = 0; i < matrix.length; i++) {
      for (n = 0; n < matrix[i].length; n++)
	result[len + n] = matrix[i][n];
      len += matrix[i].length;
    }

    return result;
  }

  /**
   * Converts the matrix into a flat array, row after row.
   *
   * @param matrix	the matrix to convert
   * @return		the matrix as arrawy (row after row)
   */
  public static short[] flatten(short[][] matrix) {
    short[]	result;
    int		i;
    int		n;
    int		len;

    len = 0;
    for (i = 0; i < matrix.length; i++)
      len += matrix[i].length;

    result = new short[len];
    len    = 0;
    for (i = 0; i < matrix.length; i++) {
      for (n = 0; n < matrix[i].length; n++)
	result[len + n] = matrix[i][n];
      len += matrix[i].length;
    }

    return result;
  }

  /**
   * Converts the matrix into a flat array, row after row.
   *
   * @param matrix	the matrix to convert
   * @return		the matrix as arrawy (row after row)
   */
  public static int[] flatten(int[][] matrix) {
    int[]	result;
    int		i;
    int		n;
    int		len;

    len = 0;
    for (i = 0; i < matrix.length; i++)
      len += matrix[i].length;

    result = new int[len];
    len    = 0;
    for (i = 0; i < matrix.length; i++) {
      for (n = 0; n < matrix[i].length; n++)
	result[len + n] = matrix[i][n];
      len += matrix[i].length;
    }

    return result;
  }

  /**
   * Converts the matrix into a flat array, row after row.
   *
   * @param matrix	the matrix to convert
   * @return		the matrix as arrawy (row after row)
   */
  public static long[] flatten(long[][] matrix) {
    long[]	result;
    int		i;
    int		n;
    int		len;

    len = 0;
    for (i = 0; i < matrix.length; i++)
      len += matrix[i].length;

    result = new long[len];
    len    = 0;
    for (i = 0; i < matrix.length; i++) {
      for (n = 0; n < matrix[i].length; n++)
	result[len + n] = matrix[i][n];
      len += matrix[i].length;
    }

    return result;
  }

  /**
   * Converts the matrix into a flat array, row after row.
   *
   * @param matrix	the matrix to convert
   * @return		the matrix as arrawy (row after row)
   */
  public static float[] flatten(float[][] matrix) {
    float[]	result;
    int		i;
    int		n;
    int		len;

    len = 0;
    for (i = 0; i < matrix.length; i++)
      len += matrix[i].length;

    result = new float[len];
    len    = 0;
    for (i = 0; i < matrix.length; i++) {
      for (n = 0; n < matrix[i].length; n++)
	result[len + n] = matrix[i][n];
      len += matrix[i].length;
    }

    return result;
  }

  /**
   * Converts the matrix into a flat array, row after row.
   *
   * @param matrix	the matrix to convert
   * @return		the matrix as arrawy (row after row)
   */
  public static double[] flatten(double[][] matrix) {
    double[]	result;
    int		i;
    int		n;
    int		len;

    len = 0;
    for (i = 0; i < matrix.length; i++)
      len += matrix[i].length;

    result = new double[len];
    len    = 0;
    for (i = 0; i < matrix.length; i++) {
      for (n = 0; n < matrix[i].length; n++)
	result[len + n] = matrix[i][n];
      len += matrix[i].length;
    }

    return result;
  }

  /**
   * Converts the matrix into a flat array, row after row.
   *
   * @param matrix	the matrix to convert
   * @return		the matrix as arrawy (row after row)
   */
  public static Number[] flatten(Number[][] matrix) {
    Number[]	result;
    int		i;
    int		n;
    int		len;

    len = 0;
    for (i = 0; i < matrix.length; i++)
      len += matrix[i].length;

    result = new Number[len];
    len    = 0;
    for (i = 0; i < matrix.length; i++) {
      for (n = 0; n < matrix[i].length; n++)
	result[len + n] = matrix[i][n];
      len += matrix[i].length;
    }

    return result;
  }

  /**
   * Turns the Number matrix into one consisting of primitive bytes.
   *
   * @param matrix	the matrix to convert
   * @return		the converted matrix
   */
  public static byte[][] toByteMatrix(Number[][] matrix) {
    byte[][]	result;
    int		i;
    int		n;

    result = new byte[matrix.length][];
    for (i = 0; i < matrix.length; i++) {
      result[i] = new byte[matrix[i].length];
      for (n = 0; n < matrix[i].length; n++)
	result[i][n] = matrix[i][n].byteValue();
    }

    return result;
  }

  /**
   * Turns the Number matrix into one consisting of primitive shorts.
   *
   * @param matrix	the matrix to convert
   * @return		the converted matrix
   */
  public static short[][] toShortMatrix(Number[][] matrix) {
    short[][]	result;
    int		i;
    int		n;

    result = new short[matrix.length][];
    for (i = 0; i < matrix.length; i++) {
      result[i] = new short[matrix[i].length];
      for (n = 0; n < matrix[i].length; n++)
	result[i][n] = matrix[i][n].shortValue();
    }

    return result;
  }

  /**
   * Turns the Number matrix into one consisting of primitive ints.
   *
   * @param matrix	the matrix to convert
   * @return		the converted matrix
   */
  public static int[][] toIntMatrix(Number[][] matrix) {
    int[][]	result;
    int		i;
    int		n;

    result = new int[matrix.length][];
    for (i = 0; i < matrix.length; i++) {
      result[i] = new int[matrix[i].length];
      for (n = 0; n < matrix[i].length; n++)
	result[i][n] = matrix[i][n].intValue();
    }

    return result;
  }

  /**
   * Turns the Number matrix into one consisting of primitive longs.
   *
   * @param matrix	the matrix to convert
   * @return		the converted matrix
   */
  public static long[][] toLongMatrix(Number[][] matrix) {
    long[][]	result;
    int		i;
    int		n;

    result = new long[matrix.length][];
    for (i = 0; i < matrix.length; i++) {
      result[i] = new long[matrix[i].length];
      for (n = 0; n < matrix[i].length; n++)
	result[i][n] = matrix[i][n].longValue();
    }

    return result;
  }

  /**
   * Turns the Number matrix into one consisting of primitive floats.
   *
   * @param matrix	the matrix to convert
   * @return		the converted matrix
   */
  public static float[][] toFloatMatrix(Number[][] matrix) {
    float[][]	result;
    int		i;
    int		n;

    result = new float[matrix.length][];
    for (i = 0; i < matrix.length; i++) {
      result[i] = new float[matrix[i].length];
      for (n = 0; n < matrix[i].length; n++)
	result[i][n] = matrix[i][n].floatValue();
    }

    return result;
  }

  /**
   * Turns the Number matrix into one consisting of primitive doubles.
   *
   * @param matrix	the matrix to convert
   * @return		the converted matrix
   */
  public static double[][] toDoubleMatrix(Number[][] matrix) {
    double[][]	result;
    int		i;
    int		n;

    result = new double[matrix.length][];
    for (i = 0; i < matrix.length; i++) {
      result[i] = new double[matrix[i].length];
      for (n = 0; n < matrix[i].length; n++)
	result[i][n] = matrix[i][n].doubleValue();
    }

    return result;
  }

  /**
   * Turns the Number matrix into one consisting of primitive bytes.
   *
   * @param matrix	the matrix to convert
   * @return		the converted matrix
   */
  public static Number[][] toNumberMatrix(byte[][] matrix) {
    Byte[][]	result;
    int		i;
    int		n;

    result = new Byte[matrix.length][];
    for (i = 0; i < matrix.length; i++) {
      result[i] = new Byte[matrix[i].length];
      for (n = 0; n < matrix[i].length; n++)
	result[i][n] = new Byte(matrix[i][n]);
    }

    return result;
  }

  /**
   * Turns the Number matrix into one consisting of primitive shorts.
   *
   * @param matrix	the matrix to convert
   * @return		the converted matrix
   */
  public static Number[][] toNumberMatrix(short[][] matrix) {
    Short[][]	result;
    int		i;
    int		n;

    result = new Short[matrix.length][];
    for (i = 0; i < matrix.length; i++) {
      result[i] = new Short[matrix[i].length];
      for (n = 0; n < matrix[i].length; n++)
	result[i][n] = new Short(matrix[i][n]);
    }

    return result;
  }

  /**
   * Turns the primitve int matrix into one consisting of Integers.
   *
   * @param matrix	the matrix to convert
   * @return		the converted matrix
   */
  public static Number[][] toNumberMatrix(int[][] matrix) {
    Integer[][]	result;
    int		i;
    int		n;

    result = new Integer[matrix.length][];
    for (i = 0; i < matrix.length; i++) {
      result[i] = new Integer[matrix[i].length];
      for (n = 0; n < matrix[i].length; n++)
	result[i][n] = new Integer(matrix[i][n]);
    }

    return result;
  }

  /**
   * Turns the primitive long matrix into one consisting of Longs.
   *
   * @param matrix	the matrix to convert
   * @return		the converted matrix
   */
  public static Number[][] toNumberMatrix(long[][] matrix) {
    Long[][]	result;
    int		i;
    int		n;

    result = new Long[matrix.length][];
    for (i = 0; i < matrix.length; i++) {
      result[i] = new Long[matrix[i].length];
      for (n = 0; n < matrix[i].length; n++)
	result[i][n] = new Long(matrix[i][n]);
    }

    return result;
  }

  /**
   * Turns the primitive float matrix into one consisting of Floats.
   *
   * @param matrix	the matrix to convert
   * @return		the converted matrix
   */
  public static Number[][] toNumberMatrix(float[][] matrix) {
    Float[][]	result;
    int		i;
    int		n;

    result = new Float[matrix.length][];
    for (i = 0; i < matrix.length; i++) {
      result[i] = new Float[matrix[i].length];
      for (n = 0; n < matrix[i].length; n++)
	result[i][n] = new Float(matrix[i][n]);
    }

    return result;
  }

  /**
   * Turns the primitive double matrix into one consisting of Doubles.
   *
   * @param matrix	the matrix to convert
   * @return		the converted matrix
   */
  public static Number[][] toNumberMatrix(double[][] matrix) {
    Double[][]	result;
    int		i;
    int		n;

    result = new Double[matrix.length][];
    for (i = 0; i < matrix.length; i++) {
      result[i] = new Double[matrix[i].length];
      for (n = 0; n < matrix[i].length; n++)
	result[i][n] = new Double(matrix[i][n]);
    }

    return result;
  }

  /**
   * Creates a random sub-sample of indices of a certain percentage using
   * the specified number of entrie. Uses {@link JavaRandomInt}.
   *
   * @param num		the maximum number of indices
   * @param perc	the size of the subsample (0-1)
   * @param seed	the seed value for JavaRandomInt
   * @return		the subsample of indices (chosen from 0 to num-1)
   */
  public static TIntArrayList subsample(int num, double perc, long seed) {
    JavaRandomInt	generator;

    generator = new JavaRandomInt();
    generator.setSeed(seed);

    return subsample(num, perc, generator);
  }

  /**
   * Creates a random sub-sample of indices of a certain percentage using
   * the specified number of entrie.
   *
   * @param num		the maximum number of indices
   * @param perc	the size of the subsample (0-1)
   * @param generator	the random int generator to use
   * @return		the subsample of indices (chosen from 0 to num-1)
   */
  public static TIntArrayList subsample(int num, double perc, RandomIntegerRangeGenerator generator) {
    TIntArrayList 	result;
    TIntArrayList	available;
    int			i;
    int			size;
    Random		rand;

    available = new TIntArrayList();
    for (i = 0; i < num; i++)
      available.add(i);
    result = new TIntArrayList();
    size    = (int) Math.round(num * perc);
    while (size > 0) {
      if (available.size() == 1) {
	i = 0;
      }
      else {
	generator.setMaxValue(available.size() - 1);
	i = generator.next().intValue();
      }
      result.add(available.get(i));
      available.removeAt(i);
      size--;
    }

    return result;
  }

  /**
   * Just for testing.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    Double[] array = new Double[]{12.0, 3.2, 2.0, 6.0, -1.0, 99.0};
    System.out.println("array: " + Utils.arrayToString(array));
    System.out.println("sorted(asc): " + Utils.arrayToString(sort(array, true)));
    System.out.println("sorted(desc): " + Utils.arrayToString(sort(array, false)));
    System.out.println("min: " + min(array));
    System.out.println("max: " + max(array));
    System.out.println("mean: " + mean(array));
    System.out.println("median: " + median(array));
    System.out.println("stddev (pop.): " + stddev(array, false));
    System.out.println("normalized array: " + Utils.arrayToString(normalize(array)));
    System.out.println("standardized array (pop.): " + Utils.arrayToString(standardize(array, false)));
  }
}
