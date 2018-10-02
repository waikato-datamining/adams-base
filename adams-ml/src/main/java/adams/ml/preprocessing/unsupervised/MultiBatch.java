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
 * MultiBatch.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.ml.preprocessing.unsupervised;

import adams.ml.capabilities.Capabilities;
import adams.ml.data.Dataset;
import adams.ml.preprocessing.AbstractBatchFilter;
import adams.ml.preprocessing.StreamFilter;

/**
 <!-- globalinfo-start -->
 * Applies the specified filters sequentially.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-filter &lt;adams.ml.preprocessing.StreamFilter&gt; [-filter ...] (property: filters)
 * &nbsp;&nbsp;&nbsp;The filters to apply sequentially.
 * &nbsp;&nbsp;&nbsp;default: adams.ml.preprocessing.unsupervised.PassThrough
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MultiBatch
  extends AbstractBatchFilter {

  private static final long serialVersionUID = 8129384772744387384L;

  /** the filters to apply. */
  protected StreamFilter[] m_Filters;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified filters sequentially.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "filter", "filters",
      new StreamFilter[]{new PassThrough()});
  }

  /**
   * Sets the filters to apply.
   *
   * @param value 	the filters
   */
  public void setFilters(StreamFilter[] value) {
    m_Filters = value;
    reset();
  }

  /**
   * Returns the filters to apply.
   *
   * @return 		the filters
   */
  public StreamFilter[] getFilters() {
    return m_Filters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filtersTipText() {
    return "The filters to apply sequentially.";
  }

  /**
   * Returns the capabilities.
   *
   * @return		the capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    if (m_Filters.length == 0) {
      result = new Capabilities(this);
      result.enableAllClass();
      result.enableAll();
    }
    else {
      result = m_Filters[0].getCapabilities();
      result.setOwner(this);
    }

    return result;
  }

  /**
   * Filter-specific initialization.
   *
   * @param data 	the data to initialize with
   * @throws Exception	if initialization fails
   */
  @Override
  protected void doInitFilter(Dataset data) throws Exception {
    if (m_Filters.length == 0)
      throw new Exception("No filters specified!");
  }

  /**
   * Initializes the output format.
   *
   * @param data	the output format
   * @throws Exception	if initialization fails
   */
  @Override
  protected Dataset initOutputFormat(Dataset data) throws Exception {
    return m_Filters[0].getOutputFormat();
  }

  /**
   * Filters the dataset coming through.
   *
   * @param data	the data to filter
   * @return		the filtered data
   * @throws Exception	if filtering fails
   */
  @Override
  protected Dataset doFilter(Dataset data) throws Exception {
    Dataset	result;
    int		i;

    result = data;

    for (i = 0; i < m_Filters.length; i++) {
      if (isLoggingEnabled())
        getLogger().info("Applying filter #" + (i+1));
      result = m_Filters[i].filter(result);
    }

    return result;
  }
}
