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
 * AbstractDenoiser.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.noise;

import java.util.ArrayList;
import java.util.List;

import adams.core.ClassLister;
import adams.core.CleanUpHandler;
import adams.core.Performance;
import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.data.NotesHandler;
import adams.data.RegionRecorder;
import adams.data.container.DataContainer;
import adams.data.id.DatabaseIDHandler;
import adams.multiprocess.Job;
import adams.multiprocess.JobList;
import adams.multiprocess.JobRunner;

/**
 * An abstract super class for algorithms that remove noise from data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data the denoiser is handling
 */
public abstract class AbstractDenoiser<T extends DataContainer>
  extends AbstractOptionHandler
  implements RegionRecorder<T>, Comparable, CleanUpHandler, ShallowCopySupporter<AbstractDenoiser> {

  /** for serialization. */
  private static final long serialVersionUID = -1247356707842924341L;

  /**
   * A job class specific to denoisers.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class DenoiserJob<T extends DataContainer>
    extends Job {

    /** for serialization. */
    private static final long serialVersionUID = -6605973712371846881L;

    /** the denoiser to use. */
    protected AbstractDenoiser m_Denoiser;

    /** the data to push through the denoiser. */
    protected T m_Data;

    /** the denoised data. */
    protected T m_DenoisedData;

    /**
     * Initializes the job.
     *
     * @param denoiser	the denoiser to use for denoising
     * @param data	the data to pass through the denoiser
     */
    public DenoiserJob(AbstractDenoiser denoiser, T data) {
      super();

      m_Denoiser     = denoiser;
      m_Data         = data;
      m_DenoisedData = null;
    }

    /**
     * Returns the denoiser being used.
     *
     * @return		the denoiser in use
     */
    public AbstractDenoiser getDenoiser() {
      return m_Denoiser;
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
    public T getDenoisedData() {
      return m_DenoisedData;
    }

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      if (m_Denoiser == null)
	return "No denoiser set!";

      if (m_Data == null)
	return "No data set!";

      return null;
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
     * Does the actual execution of the job.
     * 
     * @throws Exception if fails to execute job
     */
    @Override
    protected void process() throws Exception {
      m_DenoisedData = (T) m_Denoiser.denoise(m_Data);
    }

    /**
     * Checks whether all post-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String postProcessCheck() {
      if (m_DenoisedData == null)
	return "Result from denoiser is null!";

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
      m_DenoisedData = null;
      m_Denoiser.destroy();
      m_Denoiser     = null;
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
      result += "denoiser: " + OptionUtils.getCommandLine(m_Denoiser);

      return result;
    }
  }

  /** whether to record elution regions as well. */
  protected boolean m_RecordRegions;

  /** the collected elution regions. */
  protected List<T> m_Regions;

  /**
   * Resets the noise algorithm.
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Regions = new ArrayList<T>();
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
   * <p/>
   * Calls cleanUp() and cleans up the options.
   */
  @Override
  public void destroy() {
    cleanUp();
    super.destroy();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "regions", "recordRegions",
	    false);
  }

  /**
   * Sets whether the noisy regions are recorded as well.
   *
   * @param value 	if true the regions will be recorded
   * @see		#getRegions()
   */
  public void setRecordRegions(boolean value) {
    m_RecordRegions = value;
    reset();
  }

  /**
   * Returns whether noisy regions are recorded.
   *
   * @return 		true if the regions are recorded
   * @see		#getRegions()
   */
  public boolean getRecordRegions() {
    return m_RecordRegions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String recordRegionsTipText() {
    return "If set to true, the noisy regions will be recorded as well.";
  }

  /**
   * Returns the noisy regions that were recorded.
   *
   * @return		the regions
   * @see		#m_RecordRegions
   */
  public List<T> getRegions() {
    return new ArrayList<T>(m_Regions);
  }

  /**
   * The default implementation only checks whether there is any data set.
   *
   * @param data	the data to process
   */
  protected void checkData(T data) {
    if (data == null)
      throw new IllegalStateException("No input data provided!");
  }

  /**
   * Performs the actual denoising.
   *
   * @param data	the data to process
   * @return		the denoised data
   */
  protected abstract T processData(T data);

  /**
   * Post-processes the data.
   * <p/>
   * Default implementation only returns the denoised data as it is.
   *
   * @param original	the original input data
   * @param denoised	the denoised data
   * @return		the postprocessed data
   */
  protected T postprocessData(T original, T denoised) {
    return denoised;
  }

  /**
   * Returns the denoised data.
   *
   * @param data	the data to denoise
   * @return		the denoised data
   */
  public T denoise(T data) {
    T 	result;

    checkData(data);
    result = processData(data);
    result = postprocessData(data, result);
    if (result instanceof NotesHandler)
      ((NotesHandler) result).getNotes().addProcessInformation(this);

    return result;
  }

  /**
   * Turns the recorded regions as a string.
   *
   * @return		the regions
   */
  public String toStringRegions() {
    String	result;
    int		i;

    result = "";

    if (m_RecordRegions) {
      result += "\n";
      result += "Regions: " + m_Regions.size();
      for (i = 0; i < m_Regions.size(); i++) {
	result += "\n";
	result +=   (i+1) + ". "
	+ m_Regions.get(i).toTreeSet().first()
	+ "-"
	+ m_Regions.get(i).toTreeSet().last()
	+ " (= " + m_Regions.get(i).size() + " points)";
      }
    }

    return result;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <p/>
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
   * <p/>
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
  public AbstractDenoiser shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractDenoiser shallowCopy(boolean expand) {
    return (AbstractDenoiser) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of denoiser algorithms.
   *
   * @return		the denoiser algorithm classnames
   */
  public static String[] getDenoisers() {
    return ClassLister.getSingleton().getClassnames(AbstractDenoiser.class);
  }

  /**
   * Instantiates the denoiser algorithm with the given options.
   *
   * @param classname	the classname of the denoiser algorithm to instantiate
   * @param options	the options for the denoiser algorithm
   * @return		the instantiated denoiser algorithm or null if an
   * 			error occurred
   */
  public static AbstractDenoiser forName(String classname, String[] options) {
    AbstractDenoiser	result;

    try {
      result = (AbstractDenoiser) OptionUtils.forName(AbstractDenoiser.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the denoiser algorithm from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			denoiser algorithm to instantiate
   * @return		the instantiated denoiser algorithm
   * 			or null if an error occurred
   */
  public static AbstractDenoiser forCommandLine(String cmdline) {
    return (AbstractDenoiser) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }

  /**
   * Uses the (initialized) denoiser algorithm to remove the noise from the
   * data.
   *
   * @param denoiser	the denoiser algorithm to use
   * @param data	the data to cleanse
   * @return		the noise-free data
   */
  public static DataContainer denoise(AbstractDenoiser denoiser, DataContainer data) {
    List<DataContainer>	dataList;
    List<DataContainer>	denoisedList;
    DataContainer	result;

    dataList     = new ArrayList<DataContainer>();
    dataList.add(data);
    denoisedList = denoise(denoiser, dataList);
    result       = denoisedList.get(0);

    return result;
  }

  /**
   * Uses the (initialized) denoiser algorithm to remove the noise from the
   * data vector.
   *
   * @param denoiser	the denoiser algorithm to use
   * @param data	the data to cleanse
   * @return		the noise-free data
   */
  public static List<DataContainer> denoise(AbstractDenoiser denoiser, List<DataContainer> data) {
    List<DataContainer>		result;
    AbstractDenoiser		threadDenoiser;
    JobRunner<DenoiserJob> 	runner;
    JobList<DenoiserJob>	jobs;
    DenoiserJob			job;
    int				i;

    result = new ArrayList<DataContainer>();

    if (Performance.getMultiProcessingEnabled()) {
      runner = new JobRunner<DenoiserJob>();
      jobs   = new JobList<DenoiserJob>();

      // fill job list
      for (i = 0; i < data.size(); i++) {
	threadDenoiser = denoiser.shallowCopy(true);
	jobs.add(new DenoiserJob(threadDenoiser, data.get(i)));
      }
      runner.add(jobs);
      runner.start();
      runner.stop();

      // gather results
      for (i = 0; i < jobs.size(); i++) {
	job = jobs.get(i);
	// success? If not, just add the header of the original data
	if (job.getDenoisedData() != null)
	  result.add(job.getDenoisedData());
	else
	  result.add(job.getData().getHeader());
      }
    }
    else {
      for (i = 0; i < data.size(); i++) {
	threadDenoiser = denoiser.shallowCopy(true);
	result.add(threadDenoiser.denoise(data.get(i)));
      }
    }

    return result;
  }
}
