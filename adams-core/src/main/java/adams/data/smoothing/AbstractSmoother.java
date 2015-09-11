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
 * AbstractSmoother.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.smoothing;

import adams.core.ClassLister;
import adams.core.CleanUpHandler;
import adams.core.Performance;
import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.data.NotesHandler;
import adams.data.container.DataContainer;
import adams.data.id.DatabaseIDHandler;
import adams.multiprocess.AbstractJob;
import adams.multiprocess.JobList;
import adams.multiprocess.JobRunner;
import adams.multiprocess.LocalJobRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for smoothing schemes.
 *
 * Derived classes only have to override the <code>processData(T)</code>
 * method. The <code>reset()</code> method can be used to reset an
 * algorithms internal state.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of container to process
 */
public abstract class AbstractSmoother<T extends DataContainer>
  extends AbstractOptionHandler
  implements Comparable, CleanUpHandler, ShallowCopySupporter<AbstractSmoother> {

  /** for serialization. */
  private static final long serialVersionUID = 5504652439909198344L;

  /**
   * A job class specific to smoothing schemes.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class SmootherJob<T extends DataContainer>
    extends AbstractJob {

    /** for serialization. */
    private static final long serialVersionUID = 5544327082749651329L;

    /** the smoothing scheme to use. */
    protected AbstractSmoother m_Smoother;

    /** the data to push through the smoothing scheme. */
    protected T m_Data;

    /** the smoothed data. */
    protected T m_SmoothedData;

    /**
     * Initializes the job.
     *
     * @param smoother	the smoothing scheme to use for smoothing
     * @param data	the data to pass through the smoothing
     */
    public SmootherJob(AbstractSmoother smoother, T data) {
      super();

      m_Smoother     = smoother;
      m_Data         = data;
      m_SmoothedData = null;
    }

    /**
     * Returns the smoothing scheme being used.
     *
     * @return		the smoothing scheme in use
     */
    public AbstractSmoother getSmoother() {
      return m_Smoother;
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
    public T getSmoothedData() {
      return m_SmoothedData;
    }

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      if (m_Smoother == null)
	return "No smoothing scheme set!";

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
      m_SmoothedData = (T) m_Smoother.smooth(m_Data);
    }

    /**
     * Checks whether all post-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String postProcessCheck() {
      if (m_SmoothedData == null)
	return "Result of smoothing scheme is null!";

      return null;
    }

    /**
     * Cleans up data structures, frees up memory.
     * Sets the input data to null.
     */
    @Override
    public void cleanUp() {
      super.cleanUp();

      m_Data         = null;
      m_Smoother.destroy();
      m_Smoother     = null;
      m_SmoothedData = null;
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
      result += "smoother: " + OptionUtils.getCommandLine(m_Smoother);

      return result;
    }
  }

  /**
   * Resets the smoothing scheme (but does not clear the input data!).
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  @Override
  protected void reset() {
    super.reset();
  }

  /**
   * Cleans up data structures, frees up memory.
   * Sets the input data to null.
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
   * Returns the smoothed data, smooths the original data first if
   * necessary.
   *
   * @param data	the data to smooth
   * @return		the smoothed data
   */
  public T smooth(T data) {
    T	result;

    checkData(data);
    result = processData(data);

    result.setID(data.getID() + "'");
    if (result instanceof NotesHandler)
      ((NotesHandler) result).getNotes().addProcessInformation(this);

    return result;
  }

  /**
   * The default implementation only checks whether there is any data set.
   *
   * @param data	the data to check
   */
  protected void checkData(T data) {
    if (data == null)
      throw new IllegalStateException("No input data provided!");
  }

  /**
   * Performs the actual smoothing.
   *
   * @param data	the to smooth
   * @return		the smoothed data
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
  public AbstractSmoother shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractSmoother shallowCopy(boolean expand) {
    return (AbstractSmoother) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of smoothing schemes.
   *
   * @return		the smoothing scheme classnames
   */
  public static String[] getSmoothers() {
    return ClassLister.getSingleton().getClassnames(AbstractSmoother.class);
  }

  /**
   * Instantiates the smoothing scheme with the given options.
   *
   * @param classname	the classname of the smoothing scheme to instantiate
   * @param options	the options for the smoothing scheme
   * @return		the instantiated smoothing scheme or null if an error occurred
   */
  public static AbstractSmoother forName(String classname, String[] options) {
    AbstractSmoother	result;

    try {
      result = (AbstractSmoother) OptionUtils.forName(AbstractSmoother.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the smoothing scheme from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			smoothing scheme to instantiate
   * @return		the instantiated smoothing scheme
   * 			or null if an error occurred
   */
  public static AbstractSmoother forCommandLine(String cmdline) {
    return (AbstractSmoother) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }

  /**
   * Passes the data through the given smoothing scheme and returns it.
   *
   * @param smoother	the smoothing scheme to use for smoothing
   * @param data	the data to pass through the smoothing scheme
   * @return		the smoothed data
   */
  public static DataContainer smooth(AbstractSmoother smoother, DataContainer data) {
    DataContainer		result;
    List<List<DataContainer>>	smoothed;
    List<DataContainer>		dataList;
    List<AbstractSmoother>	smootherList;

    dataList     = new ArrayList<DataContainer>();
    dataList.add(data);
    smootherList = new ArrayList<AbstractSmoother>();
    smootherList.add(smoother);
    smoothed     = smooth(smootherList, dataList);
    result       = smoothed.get(0).get(0);

    return result;
  }

  /**
   * Passes the data through the given smoothing scheme and returns it. Makes use of
   * multiple cores, i.e., for each dataset a new thread will be run with a
   * copy of the smoothing scheme.
   *
   * @param smoother	the smoothing scheme to use for smoothing (a new smoothing scheme with the
   * 			same options will be created and used in each thread)
   * @param data	the data to pass through the smoothing scheme
   * @return		the smoothed data, the index corresponds to the
   * 			data index
   */
  public static List<DataContainer> smooth(AbstractSmoother smoother, List<DataContainer> data) {
    List<DataContainer>		result;
    List<List<DataContainer>>	smoothed;
    List<DataContainer>		dataList;
    List<AbstractSmoother>		smootherList;

    dataList     = new ArrayList<DataContainer>();
    dataList.addAll(data);
    smootherList = new ArrayList<AbstractSmoother>();
    smootherList.add(smoother);
    smoothed     = smooth(smootherList, dataList);
    result       = smoothed.get(0);

    return result;
  }

  /**
   * Passes the data through the given smoothing schemes and returns it. Makes use of
   * multiple cores, i.e., for each dataset a new thread will be run with a
   * copy of the smoothing scheme.
   *
   * @param smoother	the smoothing schemes to use for smoothing (a new smoothing scheme with the
   * 			same options will be created and used in each thread)
   * @param data	the data to pass through the smoothing scheme
   * @return		the smoothed data, the index corresponds to the smoothing scheme
   * 			index
   */
  public static List<DataContainer> smooth(List<AbstractSmoother> smoother, DataContainer data) {
    List<DataContainer>		result;
    List<List<DataContainer>>	smoothed;
    List<DataContainer>		dataList;
    List<AbstractSmoother>	smootherList;
    int				i;

    dataList     = new ArrayList<DataContainer>();
    dataList.add(data);
    smootherList = new ArrayList<AbstractSmoother>();
    smootherList.addAll(smoother);
    smoothed     = smooth(smootherList, dataList);
    result       = new ArrayList<DataContainer>();
    for (i = 0; i < smoothed.size(); i++)
      result.add(smoothed.get(i).get(0));

    return result;
  }

  /**
   * Passes the data through the given smoothing schemes and returns it. Makes use of
   * multiple cores, i.e., for each dataset a new thread will be run with a
   * copy of the smoothing scheme.
   *
   * @param smoother	the smoothing schemes to use for smoothing (a new smoothing scheme with the
   * 			same options will be created and used in each thread)
   * @param data	the data to pass through the smoothing scheme
   * @return		the smoothed data, the indices in the outer Vector
   * 			correspond to the smoothing scheme index, the inner Vector to
   * 			the index of the data
   */
  public static List<List<DataContainer>> smooth(List<AbstractSmoother> smoother, List<DataContainer> data) {
    List<List<DataContainer>>	result;
    List<DataContainer>		subresult;
    AbstractSmoother		threadSmoother;
    JobRunner<SmootherJob> 	runner;
    JobList<SmootherJob>	jobs;
    SmootherJob			job;
    int				i;
    int				n;

    result = new ArrayList<List<DataContainer>>();

    if (Performance.getMultiProcessingEnabled()) {
      runner = new LocalJobRunner<SmootherJob>();
      jobs   = new JobList<SmootherJob>();

      // fill job list
      for (n = 0; n < smoother.size(); n++) {
	for (i = 0; i < data.size(); i++) {
	  threadSmoother = smoother.get(n).shallowCopy(true);
	  jobs.add(new SmootherJob(threadSmoother, data.get(i)));
	}
      }
      runner.add(jobs);
      runner.start();
      runner.stop();

      // gather results
      subresult = null;
      for (i = 0; i < jobs.size(); i++) {
	if (i % data.size() == 0) {
	  subresult = new ArrayList<DataContainer>();
	  result.add(subresult);
	}
	job = jobs.get(i);
	// success? If not, just add the header of the original data
	if (job.getSmoothedData() != null)
	  subresult.add(job.getSmoothedData());
	else
	  subresult.add(job.getData().getHeader());
	job.cleanUp();
      }
    }
    else {
      for (n = 0; n < smoother.size(); n++) {
	subresult = new ArrayList<DataContainer>();
	result.add(subresult);
	for (i = 0; i < data.size(); i++) {
	  threadSmoother = smoother.get(n).shallowCopy(true);
	  subresult.add(threadSmoother.smooth(data.get(i)));
	}
      }
    }

    return result;
  }
}
