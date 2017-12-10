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
 * AbstractFilter.java
 * Copyright (C) 2009-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.data.NotesHandler;
import adams.data.container.DataContainer;
import adams.data.id.DatabaseIDHandler;
import adams.data.id.IDHandler;
import adams.multiprocess.AbstractJob;

/**
 * Abstract base class for filters.
 *
 * Derived classes only have to override the <code>processData()</code>
 * method. The <code>reset()</code> method can be used to reset an
 * algorithms internal state, e.g., after setting options, which invalidate
 * the previously generated data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data type to pass through the filter
 */
public abstract class AbstractFilter<T extends DataContainer>
  extends AbstractOptionHandler
  implements Filter<T> {

  /** for serialization. */
  private static final long serialVersionUID = 3610605513320220903L;

  /**
   * A job class specific to Filters.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FilterJob<T extends DataContainer>
    extends AbstractJob {

    /** for serialization. */
    private static final long serialVersionUID = 5544327082749651329L;

    /** the filter to use. */
    protected Filter m_Filter;

    /** the data to push through the filter. */
    protected T m_Data;

    /** the filtered data. */
    protected T m_FilteredData;

    /**
     * Initializes the job.
     *
     * @param filter	the filter to use for filtering
     * @param data	the data to pass through the filter
     */
    public FilterJob(Filter filter, T data) {
      super();

      m_Filter       = filter;
      m_Data         = data;
      m_FilteredData = null;
    }

    /**
     * Returns the filter being used.
     *
     * @return		the filter in use
     */
    public Filter getFilter() {
      return m_Filter;
    }

    /**
     * The input data.
     *
     * @return		the input data
     */
    public T getData() {
      return m_Data;
    }

    /**
     * The output data, if any.
     *
     * @return		the output data, or null in case of an error
     */
    public T getFilteredData() {
      return m_FilteredData;
    }

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      if (m_Filter == null)
	return "No filter set!";

      if (m_Data == null)
	return "No data set!";

      return null;
    }

    /**
     * Does the actual execution of the job.
     * 
     * @throws Exception if fails to execute job
     */
    @Override
    protected void process() throws Exception {
      m_FilteredData = (T) m_Filter.filter(m_Data);
    }

    /**
     * Checks whether all post-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String postProcessCheck() {
      if (m_FilteredData == null)
	return "Result of filter is null!";

      return null;
    }

    /**
     * Cleans up data structures, frees up memory.
     * Sets the input data to null.
     */
    @Override
    public void cleanUp() {
      super.cleanUp();

      m_Filter.destroy();
      m_Filter       = null;
      m_FilteredData = null;
      m_Data         = null;
    }

    /**
     * Returns additional information to be added to the error message.
     *
     * @return		the additional information
     */
    @Override
    protected String getAdditionalErrorInformation() {
      if (m_Data instanceof NotesHandler)
	return ((NotesHandler) m_Data).getNotes().toString();
      else
	return "";
    }

    /**
     * Returns a string representation of the job.
     *
     * @return		a string representation
     */
    @Override
    public String toString() {
      String	result;

      result = "data:" + m_Data.getID() + ", ";
      if (m_Data instanceof DatabaseIDHandler)
	result += "db-id: " + ((DatabaseIDHandler) m_Data).getDatabaseID() + ", ";
      result += "filter: " + OptionUtils.getCommandLine(m_Filter);

      return result;
    }
  }

  /** whether to suppress updating of ID. */
  protected boolean m_DontUpdateID;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"no-id-update", "dontUpdateID",
	false);
  }

  /**
   * Sets whether ID update is suppressed.
   *
   * @param value 	true if to suppress
   */
  public void setDontUpdateID(boolean value) {
    m_DontUpdateID = value;
    reset();
  }

  /**
   * Returns whether ID update is suppressed.
   *
   * @return 		true if suppressed
   */
  public boolean getDontUpdateID() {
    return m_DontUpdateID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dontUpdateIDTipText() {
    return "If enabled, suppresses updating the ID of " + IDHandler.class.getName() + " data containers.";
  }

  /**
   * Resets the filter.
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  @Override
  public void reset() {
    super.reset();
  }

  /**
   * Cleans up data structures, frees up memory.
   * Sets the input and generated data to null.
   */
  public void cleanUp() {
    reset();
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <br><br>
   * Calls cleanUp() and cleans up the options.
   */
  @Override
  public void destroy() {
    cleanUp();
    super.destroy();
  }

  /**
   * Returns the filtered data.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  public T filter(T data) {
    T	result;

    checkData(data);
    result = processData(data);

    if (!m_DontUpdateID) {
      if (result instanceof IDHandler)
	result.setID(result.getID() + "'");
    }

    if (result instanceof NotesHandler)
      ((NotesHandler) result).getNotes().addProcessInformation(this);

    return result;
  }

  /**
   * The default implementation only checks whether there is any data set.
   *
   * @param data	the data to filter
   */
  protected void checkData(T data) {
    if (data == null)
      throw new IllegalStateException("No input data provided!");
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  protected abstract T processData(T data);

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public Filter<T> shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public Filter<T> shallowCopy(boolean expand) {
    return (Filter<T>) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Instantiates the filter from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			filter to instantiate
   * @return		the instantiated filter
   * 			or null if an error occurred
   */
  public static Filter forCommandLine(String cmdline) {
    return (Filter) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
