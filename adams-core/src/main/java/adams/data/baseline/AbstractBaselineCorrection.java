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
 * AbstractBaselineCorrection.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.baseline;

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
 * Abstract base class for baseline correction schemes.
 *
 * Derived classes only have to override the <code>processData(T)</code>
 * method. The <code>reset()</code> method can be used to reset an
 * algorithms internal state.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to process
 */
public abstract class AbstractBaselineCorrection<T extends DataContainer>
  extends AbstractOptionHandler
  implements Comparable, CleanUpHandler, ShallowCopySupporter<AbstractBaselineCorrection> {

  /** for serialization. */
  private static final long serialVersionUID = 2530174550698703218L;

  /**
   * A job class specific to baseline correction schemes.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class BaselineCorrectionJob<T extends DataContainer>
    extends AbstractJob {

    /** for serialization. */
    private static final long serialVersionUID = 5544327082749651329L;

    /** the baseline correction scheme to use. */
    protected AbstractBaselineCorrection m_BaselineCorrection;

    /** the data to push through the baseline correction scheme. */
    protected T m_Data;

    /** the corrected data. */
    protected T m_CorrectedData;

    /**
     * Initializes the job.
     *
     * @param baseline	the baseline correction scheme to use for correcting
     * @param data	the data to pass through the baseline correction scheme
     */
    public BaselineCorrectionJob(AbstractBaselineCorrection baseline, T data) {
      super();

      m_BaselineCorrection = baseline;
      m_Data               = data;
      m_CorrectedData      = null;
    }

    /**
     * Returns the baseline correction scheme being used.
     *
     * @return		the baseline correction scheme in use
     */
    public AbstractBaselineCorrection getBaselineCorretion() {
      return m_BaselineCorrection;
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
    public T getCorrectedData() {
      return m_CorrectedData;
    }

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      if (m_BaselineCorrection == null)
	return "No baseline correction scheme set!";

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
      m_CorrectedData = (T) m_BaselineCorrection.correct(m_Data);
    }

    /**
     * Checks whether all post-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String postProcessCheck() {
      if (m_CorrectedData == null)
	return "Result of baseline correction scheme is null!";

      return null;
    }

    /**
     * Cleans up data structures, frees up memory.
     * Sets the input data to null.
     */
    @Override
    public void cleanUp() {
      super.cleanUp();

      m_Data               = null;
      m_BaselineCorrection.destroy();
      m_BaselineCorrection = null;
      m_CorrectedData      = null;
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
      result += "baseline correction: " + OptionUtils.getCommandLine(m_BaselineCorrection);

      return result;
    }
  }

  /**
   * Resets the baseline correction scheme.
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
   * Corrects the data.
   *
   * @param data	the data to correct
   * @return		the corrected data
   */
  public T correct(T data) {
    T	result;

    result = doCorrect(data);
    if (result instanceof NotesHandler)
      ((NotesHandler) result).getNotes().addProcessInformation(this);

    return result;
  }

  /**
   * Performs checks and corrects the data.
   *
   * @param data	the data to correct
   * @return		the corrected data
   */
  protected T doCorrect(T data) {
    T	result;

    checkData(data);
    result = processData(data);

    result.setID(data.getID() + "'");

    return result;
  }

  /**
   * The default implementation only checks whether there is any data set.
   *
   * @param data	the data to correct
   */
  protected void checkData(T data) {
    if (data == null)
      throw new IllegalStateException("No input data provided!");
  }

  /**
   * Performs the actual correcting.
   *
   * @param data	the data to correct
   * @return		the corrected data
   */
  protected abstract T processData(T data);

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractBaselineCorrection shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractBaselineCorrection shallowCopy(boolean expand) {
    return (AbstractBaselineCorrection) OptionUtils.shallowCopy(this, expand);
  }

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
   * Instantiates the baseline correction scheme from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			baseline correction scheme to instantiate
   * @return		the instantiated baseline correction scheme
   * 			or null if an error occurred
   */
  public static AbstractBaselineCorrection forCommandLine(String cmdline) {
    return (AbstractBaselineCorrection) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }

  /**
   * Passes the data through the given baseline correction scheme and returns it.
   *
   * @param baseline	the baseline correction scheme to use for correcting
   * @param data	the data to pass through the baseline correction scheme
   * @return		the corrected data
   */
  public static DataContainer correct(AbstractBaselineCorrection baseline, DataContainer data) {
    DataContainer			result;
    List<List<DataContainer>>	corrected;
    List<DataContainer>		dataList;
    List<AbstractBaselineCorrection>	baselineList;

    dataList   = new ArrayList<DataContainer>();
    dataList.add(data);
    baselineList = new ArrayList<AbstractBaselineCorrection>();
    baselineList.add(baseline);
    corrected  = correct(baselineList, dataList);
    result     = corrected.get(0).get(0);

    return result;
  }

  /**
   * Passes the data through the given baseline correction scheme and returns it.
   * Makes use of multiple cores, i.e., for each dataset a new thread will be run
   * with a copy of the baseline correction scheme.
   *
   * @param baseline	the baseline correction scheme to use for correcting
   * 			(a new baseline correction scheme with the
   * 			same options will be created and used in each thread)
   * @param data	the data to pass through the baseline correction scheme
   * @return		the corrected data, the index corresponds to the
   * 			data index
   */
  public static List<DataContainer> correct(AbstractBaselineCorrection baseline, List<DataContainer> data) {
    List<DataContainer>			result;
    List<List<DataContainer>>		corrected;
    List<DataContainer>			dataList;
    List<AbstractBaselineCorrection>	baselineList;

    dataList   = new ArrayList<DataContainer>();
    dataList.addAll(data);
    baselineList = new ArrayList<AbstractBaselineCorrection>();
    baselineList.add(baseline);
    corrected  = correct(baselineList, dataList);
    result     = corrected.get(0);

    return result;
  }

  /**
   * Passes the data through the given baseline correction schemes and returns it.
   * Makes use of multiple cores, i.e., for each dataset a new thread will be run with a
   * copy of the baseline correction scheme.
   *
   * @param baseline	the baseline correction schemes to use for correcting
   * 			(a new baseline correction scheme with the
   * 			same options will be created and used in each thread)
   * @param data	the data to pass through the baseline correction scheme
   * @return		the corrected data, the index corresponds to the
   * 			baseline correction scheme index
   */
  public static List<DataContainer> correct(List<AbstractBaselineCorrection> baseline, DataContainer data) {
    List<DataContainer>		result;
    List<List<DataContainer>>		corrected;
    List<DataContainer>			dataList;
    List<AbstractBaselineCorrection>	baselineList;
    int					i;

    dataList   = new ArrayList<DataContainer>();
    dataList.add(data);
    baselineList = new ArrayList<AbstractBaselineCorrection>();
    baselineList.addAll(baseline);
    corrected = correct(baselineList, dataList);
    result    = new ArrayList<DataContainer>();
    for (i = 0; i < corrected.size(); i++)
      result.add(corrected.get(i).get(0));

    return result;
  }

  /**
   * Passes the data through the given baseline correction schemes and returns it.
   * Makes use of multiple cores, i.e., for each dataset a new thread will be run
   * with a copy of the baseline correction scheme.
   *
   * @param baseline	the baseline correction scheme to use for correcting
   * 			(a new baseline correction scheme with the
   * 			same options will be created and used in each thread)
   * @param data	the data to pass through the baseline correction scheme
   * @return		the corrected data, the indices in the outer Vector
   * 			correspond to the baseline correction scheme index,
   * 			the inner Vector to the index of the data
   */
  public static List<List<DataContainer>> correct(List<AbstractBaselineCorrection> baseline, List<DataContainer> data) {
    List<List<DataContainer>>		result;
    List<DataContainer>			subresult;
    AbstractBaselineCorrection		threadBaseline;
    JobRunner<BaselineCorrectionJob> 	runner;
    JobList<BaselineCorrectionJob>	jobs;
    BaselineCorrectionJob		job;
    int					i;
    int					n;

    result = new ArrayList<List<DataContainer>>();

    if (Performance.getMultiProcessingEnabled()) {
      runner = new LocalJobRunner<BaselineCorrectionJob>();
      jobs   = new JobList<BaselineCorrectionJob>();

      // fill job list
      for (n = 0; n < baseline.size(); n++) {
	for (i = 0; i < data.size(); i++) {
	  threadBaseline = baseline.get(n).shallowCopy(true);
	  jobs.add(new BaselineCorrectionJob(threadBaseline, data.get(i)));
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
	if (job.getCorrectedData() != null)
	  subresult.add(job.getCorrectedData());
	else
	  subresult.add((DataContainer) job.getData().getHeader());
	job.cleanUp();
      }
    }
    else {
      for (n = 0; n < baseline.size(); n++) {
	subresult = new ArrayList<DataContainer>();
	result.add(subresult);
	for (i = 0; i < data.size(); i++) {
	  threadBaseline = baseline.get(n).shallowCopy(true);
	  subresult.add(threadBaseline.correct(data.get(i)));
	}
      }
    }

    return result;
  }
}
