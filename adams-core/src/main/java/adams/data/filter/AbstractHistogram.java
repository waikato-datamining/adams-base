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
 * AbstractHistogram.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.List;

import adams.data.container.DataContainer;
import adams.data.container.DataPoint;
import adams.data.statistics.AbstractArrayStatistic.StatisticContainer;
import adams.data.statistics.ArrayHistogram;

/**
 * Ancestor for filters that generate a histogram from the incoming data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1286 $
 * @param <T> the type of container to process
 */
public abstract class AbstractHistogram<T extends DataContainer>
  extends AbstractFilter<T> {

  /** for serialization. */
  private static final long serialVersionUID = 1836858988505886282L;

  /** the array histogram setup to use. */
  protected ArrayHistogram m_Histogram;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "histogram", "histogram",
	    getDefaultHistogram());
  }

  /**
   * Returns the default setup for the array histogram.
   * 
   * @return		the default
   */
  protected ArrayHistogram getDefaultHistogram() {
    return new ArrayHistogram();
  }
  
  /**
   * Sets the array histogram setup to use.
   *
   * @param value 	the setup
   */
  public void setHistogram(ArrayHistogram value) {
    m_Histogram = value;
    reset();
  }

  /**
   * Returns the array histogram setup to use.
   *
   * @return 		the setup
   */
  public ArrayHistogram getHistogram() {
    return m_Histogram;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String histogramTipText() {
    return "The array histogram setup to use for generating the histogram data.";
  }

  /**
   * Obtains the Y value from the given data point.
   * 
   * @param point	the data point to extract the Y value from
   * @return		the Y value
   */
  protected abstract double getY(DataPoint point);

  /**
   * Creates a new data point from the X and Y values.
   * 
   * @param index	the index in the histogram
   * @param y		the raw Y value
   * @return		the data point
   */
  protected abstract DataPoint newDataPoint(int index, double y);
  
  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected T processData(T data) {
    T				result;
    List<DataPoint>		points;
    DataPoint			point;
    Double[]			values;
    int				i;
    double[]			histo;
    ArrayHistogram<Number>	calc;
    StatisticContainer		cont;

    result = (T) data.getHeader();
    if (data.size() == 0)
      return result;
    
    points    = data.toList();
    
    values = new Double[points.size()];
    for (i = 0; i < points.size(); i++)
      values[i] = getY(points.get(i));
    
    calc = (ArrayHistogram<Number>) m_Histogram.shallowCopy();
    calc.add(values);
    cont = calc.calculate();
    histo = new double[cont.getColumnCount()];
    for (i = 0; i < cont.getColumnCount(); i++)
      histo[i] = ((Number) cont.getCell(0, i)).doubleValue();
    
    for (i = 0; i < histo.length; i++) {
      point = newDataPoint(i, histo[i]);
      result.add(point);
    }
    
    return result;
  }
}
