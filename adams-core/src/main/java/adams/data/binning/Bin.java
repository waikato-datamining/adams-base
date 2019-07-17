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
 * Bin.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning;

import adams.core.CloneHandler;
import adams.core.Mergeable;
import adams.core.Utils;
import adams.core.base.BaseInterval;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single bin.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of payload
 */
public class Bin<T>
  implements Serializable, Comparable<Bin<T>>, CloneHandler<Bin<T>>, Mergeable<Bin<T>> {

  private static final long serialVersionUID = -6040460682753068962L;

  /** the bin index. */
  protected int m_Index;

  /** the start of the bin. */
  protected double m_Start;

  /** the end of the bin. */
  protected double m_End;

  /** the range. */
  protected BaseInterval m_Interval;

  /** the binnable objects that belong to this bin. */
  protected List<Binnable<T>> m_Objects;

  /**
   * Initializes the bin.
   *
   * @param index	the index of the bin
   * @param start	the starting point
   * @param interval	the complete range
   */
  public Bin(int index, double start, double end, BaseInterval interval) {
    m_Index    = index;
    m_Start    = start;
    m_End      = end;
    m_Interval = interval;
    m_Objects  = new ArrayList<>();
  }

  /**
   * Returns the index of the bin.
   *
   * @return		the index
   */
  public int getIndex() {
    return m_Index;
  }

  /**
   * Returns the start of the bin.
   *
   * @return		the start
   */
  public double getStart() {
    return m_Start;
  }

  /**
   * Returns the end of the bin.
   *
   * @return		the end
   */
  public double getEnd() {
    return m_End;
  }

  /**
   * Returns the complete interval of the bin.
   *
   * @return		the interval
   */
  public BaseInterval getInterval() {
    return m_Interval;
  }

  /**
   * Checks whether the object belongs to this bin.
   *
   * @param object	the object to check
   * @return		true if it fits
   */
  public boolean fits(Binnable<T> object) {
    boolean	result;
    double	value;

    value = object.getValue();
    if (m_Interval.isLowerInclusive())
      result = (value >= m_Start);
    else
      result = (value > m_Start);
    if (result) {
      if (m_Interval.isUpperInclusive())
	result = (value <= m_End);
      else
	result = (value < m_End);
    }

    return result;
  }

  /**
   * Adds the object to the list of objects belong to this bin.
   *
   * @param object	the object to add
   */
  public void add(Binnable<T> object) {
    m_Objects.add(object);
  }

  /**
   * Adds the objects to the list of objects belong to this bin.
   *
   * @param objects	the objects to add
   */
  public void addAll(Collection<Binnable<T>> objects) {
    m_Objects.addAll(objects);
  }

  /**
   * Returns the list of objects in the bin.
   *
   * @return		the objects
   */
  public List<Binnable<T>> get() {
    return m_Objects;
  }

  /**
   * Returns the number of stored objects.
   *
   * @return		the number of objects
   * @see		#get()
   */
  public int size() {
    return m_Objects.size();
  }

  /**
   * Uses the index of the bin for comparison.
   *
   * @param o		the other bin
   * @return		less than, equal to, or larger based on the index
   * 			comparison
   */
  @Override
  public int compareTo(Bin<T> o) {
    return Integer.compare(getIndex(), o.getIndex());
  }

  /**
   * Returns true if the other object is a bin with the same index.
   *
   * @param obj		the other bin
   * @return		true if the same index
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Bin) && (compareTo((Bin) obj) == 0);
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public Bin<T> getClone() {
    Bin<T>	result;

    result = new Bin<>(m_Index, m_Start, m_End, new BaseInterval(m_Interval.getValue()));
    result.addAll(get());

    return result;
  }

  /**
   * Merges its own data with the one provided by the specified object.
   *
   * @param other		the object to merge with
   */
  public void mergeWith(Bin<T> other) {
    m_Objects.addAll(other.get());
    m_Interval = new BaseInterval(
      this.getInterval().getLower(), this.getInterval().isLowerInclusive(),
      other.getInterval().getUpper(), other.getInterval().isUpperInclusive());
  }

  /**
   * Returns a short description of the bin.
   *
   * @return		the description
   */
  public String toString() {
    return toString(-1);
  }

  /**
   * Returns a short description of the bin.
   *
   * @param decimals 	the number of decimals to use in the output, -1 for no limit
   * @return		the description
   */
  public String toString(int decimals) {
    if (decimals == -1)
      return m_Index + ": start=" + m_Start + ", end=" + m_End + ", interval=" + m_Interval + ", #objects=" + m_Objects.size();
    else
      return m_Index + ": start=" + Utils.doubleToString(m_Start, decimals) + ", end=" + Utils.doubleToString(m_End, decimals) + ", interval=" + m_Interval + ", #objects=" + m_Objects.size();
  }

  /**
   * Returns the size of the bins.
   *
   * @param bins	the bins to obtain the size from
   * @return		the sizes
   */
  public static <T> int[] binSizes(List<Bin<T>> bins) {
    TIntList	result;

    result = new TIntArrayList();
    for (Bin<T> bin: bins)
      result.add(bin.size());

    return result.toArray();
  }
}
