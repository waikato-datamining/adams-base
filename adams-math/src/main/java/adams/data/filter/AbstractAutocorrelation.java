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
 * AbstractAutocorrelation.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.autocorrelation.AbstractAutoCorrelation;
import adams.data.autocorrelation.BruteForce;
import adams.data.container.DataContainer;
import adams.data.container.DataPoint;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Abstract ancestor for autocorrelation filters.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to process
 */
public abstract class AbstractAutocorrelation<T extends DataContainer>
  extends AbstractFilter<T> {

  /** for serialization. */
  private static final long serialVersionUID = 7714239052976065971L;

  /** the algorithm to use. */
  protected AbstractAutoCorrelation m_Algorithm;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "algorithm", "algorithm",
      new BruteForce());
  }

  /**
   * Sets the algorithm to use.
   *
   * @param value the algorithm
   */
  public void setAlgorithm(AbstractAutoCorrelation value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the algorithm to use.
   *
   * @return the algorithm
   */
  public AbstractAutoCorrelation getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String algorithmTipText() {
    return "The autocorrelation algorithm to use.";
  }

  /**
   * Returns the X value of the DataPoint.
   *
   * @param point the point to get the X value from
   * @return the X value
   */
  protected abstract double getX(DataPoint point);

  /**
   * Returns the Y value of the DataPoint.
   *
   * @param point the point to get the Y value from
   * @return the Y value
   */
  protected abstract double getY(DataPoint point);

  /**
   * Creates a new DataPoint from the X/Y data.
   *
   * @param x the X of the data point
   * @param y the Y of the data point
   * @return the new DataPoint
   */
  protected abstract DataPoint newDataPoint(double x, double y);

  @Override
  protected void checkData(T data) {
    super.checkData(data);
  }

  /**
   * Performs the actual filtering.
   *
   * @param data the data to filter
   * @return the filtered data
   */
  @Override
  protected T processData(T data) {
    T 			result;
    int 		i;
    TDoubleArrayList 	list;
    boolean 		padded;
    double 		diff;
    double[] 		ac;

    padded = false;
    list = new TDoubleArrayList();
    for (i = 0; i < data.size(); i++)
      list.add(getY((DataPoint) data.toList().get(i)));

    if (list.size() % 2 != 0) {
      padded = true;
      list.add(0.0);
    }

    ac = m_Algorithm.correlate(list.toArray());

    result = (T) data.getHeader();
    for (i = 0; i < data.size(); i++)
      result.add(newDataPoint(getX((DataPoint) data.toList().get(i)), ac[i]));

    if (padded) {
      diff = getX((DataPoint) data.toList().get(data.size() - 1))
	- getX((DataPoint) data.toList().get(data.size() - 2));
      result.add(
	newDataPoint(getX((DataPoint) data.toList().get(data.size() - 1)) + diff,
	  ac[ac.length - 1]));
    }

    return result;
  }
}