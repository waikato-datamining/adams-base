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
 * WekaForecastContainerToTimeseries.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.Date;
import java.util.List;

import weka.classifiers.evaluation.NumericPrediction;
import adams.core.Index;
import adams.core.Range;
import adams.core.base.BaseDateTime;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.flow.container.WekaForecastContainer;

/**
 <!-- globalinfo-start -->
 * Turns a series of predictions of a adams.flow.container.WekaForecastContainer container into a adams.data.timeseries.Timeseries.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-index &lt;adams.core.Index&gt; (property: index)
 * &nbsp;&nbsp;&nbsp;The index of the timeseries to extract from the container.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-prediction-range &lt;adams.core.Range&gt; (property: predictionRange)
 * &nbsp;&nbsp;&nbsp;The range of predictions to convert into a timeseries.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-start &lt;adams.core.base.BaseDateTime&gt; (property: start)
 * &nbsp;&nbsp;&nbsp;The start date&#47;time for the timeseries.
 * &nbsp;&nbsp;&nbsp;default: 2000-01-01 00:00:00
 * </pre>
 * 
 * <pre>-interval &lt;adams.core.base.BaseDateTime&gt; (property: interval)
 * &nbsp;&nbsp;&nbsp;The interval between timeseries points; START refers to the previous data 
 * &nbsp;&nbsp;&nbsp;point.
 * &nbsp;&nbsp;&nbsp;default: START +1 DAY
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaForecastContainerToTimeseries
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 3934411483801077460L;
  
  /** the index of the timeseries to extract from the container. */
  protected Index m_Index;
  
  /** the range of predictions to turn into a timeseries. */
  protected Range m_PredictionRange;
  
  /** the start date for timeseries. */
  protected BaseDateTime m_Start;
  
  /** the interval for predictions. */
  protected BaseDateTime m_Interval;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Turns a series of predictions of a " 
	+ WekaForecastContainer.class.getName() + " container into a " 
	+ Timeseries.class.getName() + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "index", "index",
	    new Index(Index.FIRST));

    m_OptionManager.add(
	    "prediction-range", "predictionRange",
	    new Range(Range.ALL));

    m_OptionManager.add(
	    "start", "start",
	    getDefaultStart());

    m_OptionManager.add(
	    "interval", "interval",
	    getDefaultInterval());
  }

  /**
   * Sets the index of the timeseries to extract from the container.
   *
   * @param value	the index
   */
  public void setIndex(Index value) {
    m_Index = value;
    reset();
  }

  /**
   * Returns the index of the timeseries to extract from the container.
   *
   * @return		the index
   */
  public Index getIndex() {
    return m_Index;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indexTipText() {
    return "The index of the timeseries to extract from the container.";
  }

  /**
   * Sets the range of predictions to turn into a timeseries.
   *
   * @param value	the range
   */
  public void setPredictionRange(Range value) {
    m_PredictionRange = value;
    reset();
  }

  /**
   * Returns the range of predictions to turn into a timeseries.
   *
   * @return		the range
   */
  public Range getPredictionRange() {
    return m_PredictionRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictionRangeTipText() {
    return "The range of predictions to convert into a timeseries.";
  }

  /**
   * Returns the default start date/time.
   * 
   * @return		the default
   */
  protected BaseDateTime getDefaultStart() {
    return new BaseDateTime("2000-01-01 00:00:00");
  }
  
  /**
   * Sets the start date/time for the timeseries.
   *
   * @param value	the start
   */
  public void setStart(BaseDateTime value) {
    m_Start = value;
    reset();
  }

  /**
   * Returns the start date/time for the timeseries.
   *
   * @return		the start
   */
  public BaseDateTime getStart() {
    return m_Start;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startTipText() {
    return "The start date/time for the timeseries.";
  }

  /**
   * Returns the default interval date/time.
   * 
   * @return		the default
   */
  protected BaseDateTime getDefaultInterval() {
    return new BaseDateTime(BaseDateTime.START + " +1 DAY");
  }

  /**
   * Sets the interval betwen timeseries points, with {@link BaseDateTime#START}
   * referring to the previous data point.
   *
   * @param value	the interval
   */
  public void setInterval(BaseDateTime value) {
    m_Interval = value;
    reset();
  }

  /**
   * Returns the interval betwen timeseries points, with {@link BaseDateTime#START}
   * referring to the previous data point.
   *
   * @return		the interval
   */
  public BaseDateTime getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String intervalTipText() {
    return "The interval between timeseries points; " + BaseDateTime.START + " refers to the previous data point.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return WekaForecastContainer.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Timeseries.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Timeseries				result;
    WekaForecastContainer		forecast;
    List<List<NumericPrediction>>	list;
    int					index;
    int[]				range;
    double				pred;
    Date				current;
    Date				prev;
    boolean				first;
    
    forecast = (WekaForecastContainer) m_Input;
    list     = (List<List<NumericPrediction>>) forecast.getValue(WekaForecastContainer.VALUE_FORECASTS);
    result   = new Timeseries();
    index    = 0;
    prev     = null;
    current  = null;
    m_PredictionRange.setMax(list.size());
    range    = m_PredictionRange.getIntIndices();
    first    = true;
    for (int i: range) {
      if (first) {
	m_Index.setMax(list.get(i).size());
	index = m_Index.getIntIndex();
      }
      pred = list.get(i).get(index).predicted();
      prev = current;
      if (first) {
	current = m_Start.dateValue();
      }
      else {
	m_Interval.setStart(prev);
	current = m_Interval.dateValue();
      }
      result.add(new TimeseriesPoint(current, pred));
      first = false;
    }
    
    return result;
  }
}
