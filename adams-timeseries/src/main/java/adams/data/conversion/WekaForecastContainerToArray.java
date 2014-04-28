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
 * WekaForecastContainerToArray.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.List;

import weka.classifiers.evaluation.NumericPrediction;
import adams.core.Index;
import adams.core.Range;
import adams.flow.container.WekaForecastContainer;

/**
 <!-- globalinfo-start -->
 * Turns a series of predictions of a adams.flow.container.WekaForecastContainer container into a Double array.
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;The range of predictions to convert into an array.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaForecastContainerToArray
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 3934411483801077460L;
  
  /** the index of the timeseries to extract from the container. */
  protected Index m_Index;
  
  /** the range of predictions to turn into an array. */
  protected Range m_PredictionRange;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Turns a series of predictions of a " 
	+ WekaForecastContainer.class.getName() + " container into a Double array.";
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
   * Sets the range of predictions to turn into an array.
   *
   * @param value	the range
   */
  public void setPredictionRange(Range value) {
    m_PredictionRange = value;
    reset();
  }

  /**
   * Returns the range of predictions to turn into an array.
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
    return "The range of predictions to convert into an array.";
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
    return Double[].class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Double[]				result;
    WekaForecastContainer		forecast;
    List<List<NumericPrediction>>	list;
    int					index;
    int[]				range;
    boolean				first;

    forecast = (WekaForecastContainer) m_Input;
    list     = (List<List<NumericPrediction>>) forecast.getValue(WekaForecastContainer.VALUE_FORECASTS);
    index    = 0;
    m_PredictionRange.setMax(list.size());
    range    = m_PredictionRange.getIntIndices();
    result   = new Double[range.length];
    first    = true;
    for (int i: range) {
      if (first) {
	m_Index.setMax(list.get(i).size());
	index = m_Index.getIntIndex();
      }
      result[i] = list.get(i).get(index).predicted();
      first = false;
    }
    
    return result;
  }
}
