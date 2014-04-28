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
 * AbstractDataContainerStatistics.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

import java.util.Hashtable;
import java.util.List;

import adams.core.option.AbstractOptionHandler;
import adams.data.container.DataContainer;
import adams.data.statistics.AbstractArrayStatistic.StatisticContainer;

/**
 * Ancestor for all schemes that calculate statistics on data containers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data container to process
 */
public abstract class AbstractDataContainerStatistics<T extends DataContainer>
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 3772060799823458030L;

  /** the prefix to use. */
  protected String m_Prefix;
  
  /** the statistics to calculate. */
  protected AbstractArrayStatistic[] m_Statistics;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "prefix", "prefix",
	    "");

    m_OptionManager.add(
	    "statistic", "statistics",
	    new AbstractArrayStatistic[0]);
  }

  /**
   * Sets the prefix to use.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to use.
   *
   * @return		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix to use for the generated statistics.";
  }

  /**
   * Sets the statistics to use.
   *
   * @param value	the statistics
   */
  public void setStatistics(AbstractArrayStatistic[] value) {
    m_Statistics = value;
    reset();
  }

  /**
   * Returns the statistics to use.
   *
   * @return		the statistics
   */
  public AbstractArrayStatistic[] getStatistics() {
    return m_Statistics;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticsTipText() {
    return "The statistics to generate.";
  }
  
  /**
   * Performs some checks before executing the calculations.
   * <p/>
   * Default implementation ensures that prefix is not empty and at least
   * one array statistic has been supplied.
   * 
   * @param cont	the current container
   */
  protected void check(T cont) {
    if (m_Prefix.length() == 0)
      throw new IllegalStateException("No prefix defined!");
    if (m_Statistics.length == 0)
      throw new IllegalStateException("No array statistics supplied!");
  }
  
  /**
   * Generates the arrays to work with.
   * 
   * @param cont	the container to work on
   * @return		the generated arrays
   */
  protected abstract List<Double[]> createArrays(T cont);
  
  /**
   * Performs the actual calculations of the statistics.
   * 
   * @param index	the 
   * @param array	the array to use as basis for the calculations
   * @param values	the container to store the statistics in
   */
  protected void calculate(int index, Double[] array, Hashtable<String,Double> values) {
    int			i;
    StatisticContainer	cont;
    
    for (AbstractArrayStatistic stat: m_Statistics) {
      stat.clear();
      stat.add(array);
      cont = stat.calculate();
      stat.clear();
      
      if (cont.getRowCount() > 0) {
	for (i = 0; i < cont.getColumnCount(); i++)
	  values.put(
	      m_Prefix + "-" + index + "-" + cont.getHeader(i), 
	      ((Number) cont.getCell(0, i)).doubleValue());
      }
    }
  }
  
  /**
   * Calculates the statistics.
   * 
   * @param cont	the container to generate the statistics for
   * @return		the generated statistics (name - value relation)
   */
  public Hashtable<String,Double> calculate(T cont) {
    Hashtable<String,Double>	result;
    List<Double[]>		arrays;
    int				i;
    
    check(cont);
    
    arrays = createArrays(cont);
    if (isLoggingEnabled())
      getLogger().info("# arrays: " + arrays.size());
    
    result = new Hashtable<String,Double>();
    for (i = 0; i < arrays.size(); i++)
      calculate(i, arrays.get(i), result);
    if (isLoggingEnabled())
      getLogger().info("Statistics: " + result);
    
    return result;
  }
}
