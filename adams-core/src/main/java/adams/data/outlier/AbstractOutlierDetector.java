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
 * AbstractOutlierDetector.java
 * Copyright (C) 2010-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.data.outlier;

import adams.core.ClassLister;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.data.NotesHandler;
import adams.data.container.DataContainer;
import adams.data.id.DatabaseIDHandler;
import adams.multiprocess.AbstractJob;

import java.util.List;

/**
 * Abstract base class for outlier detectors.
 *
 * Derived classes only have to override the <code>processData()</code>
 * method. The <code>reset()</code> method can be used to reset an
 * algorithms internal state, e.g., after setting options, which invalidate
 * the previously generated data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of data to process
 */
public abstract class AbstractOutlierDetector<T extends DataContainer>
  extends AbstractOptionHandler
  implements OutlierDetector<T>, QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 1238310227471192973L;

  /**
   * A job class specific to outlier detectors.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class DetectorJob<T extends DataContainer>
    extends AbstractJob {

    /** for serialization. */
    private static final long serialVersionUID = 4607715174153227144L;

    /** the detector to use. */
    protected AbstractOutlierDetector m_Detector;

    /** the data to push through the detector. */
    protected T m_Data;

    /** the result. */
    protected List<String> m_Result;

    /**
     * Initializes the job.
     *
     * @param detector	the detector to use
     * @param data	the data to pass through the detector
     */
    public DetectorJob(AbstractOutlierDetector detector, T data) {
      super();

      m_Detector = detector;
      m_Data     = data;
      m_Result   = null;
    }

    /**
     * Returns the detector being used.
     *
     * @return		the detector in use
     */
    public AbstractOutlierDetector getOutlierDetector() {
      return m_Detector;
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
     * The result, if any.
     *
     * @return		the result, or null if no outlier
     */
    public List<String> getResult() {
      return m_Result;
    }

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      if (m_Detector == null)
	return "No detector set!";

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
      m_Result = m_Detector.detect(m_Data);
    }

    /**
     * Checks whether all post-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String postProcessCheck() {
      if (m_Result == null)
	return "Result of detector is null!";

      return null;
    }

    /**
     * Cleans up data structures, frees up memory.
     * Sets the input data to null.
     */
    @Override
    public void cleanUp() {
      super.cleanUp();

      m_Data     = null;
      m_Detector.destroy();
      m_Detector = null;
      m_Result   = null;
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
      result += "filter: " + OptionUtils.getCommandLine(m_Detector);

      return result;
    }
  }

  /**
   * Resets the detector.
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  @Override
  public void reset() {
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
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the detections on the specified data.
   *
   * @param data	the data to process
   * @return		the detections
   */
  public List<String> detect(T data) {
    List<String>	result;

    checkData(data);
    result = processData(data);

    return result;
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
   * Performs the actual detection.
   *
   * @param data	the data to process
   * @return		the detections
   */
  protected abstract List<String> processData(T data);

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
  public AbstractOutlierDetector shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractOutlierDetector shallowCopy(boolean expand) {
    return (AbstractOutlierDetector) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of detector.
   *
   * @return		the detector classnames
   */
  public static String[] getOutlierDetectors() {
    return ClassLister.getSingleton().getClassnames(AbstractOutlierDetector.class);
  }

  /**
   * Instantiates the detector with the given options.
   *
   * @param classname	the classname of the detector to instantiate
   * @param options	the options for the detector
   * @return		the instantiated detector or null if an error occurred
   */
  public static AbstractOutlierDetector forName(String classname, String[] options) {
    AbstractOutlierDetector	result;

    try {
      result = (AbstractOutlierDetector) OptionUtils.forName(AbstractOutlierDetector.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the detector from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			detector to instantiate
   * @return		the instantiated detector
   * 			or null if an error occurred
   */
  public static AbstractOutlierDetector forCommandLine(String cmdline) {
    return (AbstractOutlierDetector) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
