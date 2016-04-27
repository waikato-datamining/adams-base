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
 * SerializingJobRunner.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.multiprocess;

import adams.core.MultiAttemptWithWaitSupporter;
import adams.core.SerializationHelper;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;

/**
 <!-- globalinfo-start -->
 * Wraps another jobrunner and serializes it to the specified export file and then waits for the specified import file (containing the serialized, executed jobs) to appear.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-job-runner &lt;adams.multiprocess.JobRunner&gt; (property: jobRunner)
 * &nbsp;&nbsp;&nbsp;The base jobrunner to use.
 * &nbsp;&nbsp;&nbsp;default: adams.multiprocess.LocalJobRunner
 * </pre>
 * 
 * <pre>-export &lt;adams.core.io.PlaceholderFile&gt; (property: export)
 * &nbsp;&nbsp;&nbsp;The file to serialize the un-executed jobs to.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-import &lt;adams.core.io.PlaceholderFile&gt; (property: import)
 * &nbsp;&nbsp;&nbsp;The file to deserialize the executed jobs from.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-max-attempts &lt;int&gt; (property: numAttempts)
 * &nbsp;&nbsp;&nbsp;The maximum number of intervals to wait.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-attempt-nterval &lt;int&gt; (property: attemptInterval)
 * &nbsp;&nbsp;&nbsp;The interval in milli-seconds to wait before continuing with the execution.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SerializingJobRunner
  extends AbstractMetaJobRunner
  implements MultiAttemptWithWaitSupporter {

  private static final long serialVersionUID = 6656064128031953130L;

  /** the file to serialize the unexecuted jobs to. */
  protected PlaceholderFile m_Export;

  /** the file to deserialize the finished jobs from. */
  protected PlaceholderFile m_Import;

  /** the maximum number of interval to wait. */
  protected int m_NumAttempts;

  /** the interval in milli-seconds to wait. */
  protected int m_AttemptInterval;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Wraps another jobrunner and serializes it to the specified export file "
	+ "and then waits for the specified import file (containing the "
	+ "serialized, executed jobs) to appear.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "export", "export",
      getDefaultExport());

    m_OptionManager.add(
      "import", "import",
      getDefaultImport());

    m_OptionManager.add(
      "max-attempts", "numAttempts",
      10, 1, null);

    m_OptionManager.add(
      "attempt-nterval", "attemptInterval",
      100, 1, null);
  }

  /**
   * Returns the default export file.
   *
   * @return		the export file
   */
  protected PlaceholderFile getDefaultExport() {
    return new PlaceholderFile(".");
  }

  /**
   * Sets the file to export the un-executed jobs to.
   *
   * @param value 	the export file
   */
  public void setExport(PlaceholderFile value) {
    m_Export = value;
    reset();
  }

  /**
   * Returns the file to export the un-executed jobs to.
   *
   * @return		the export file
   */
  public PlaceholderFile getExport() {
    return m_Export;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String exportTipText() {
    return "The file to serialize the un-executed jobs to.";
  }

  /**
   * Returns the default import file.
   *
   * @return		the import file
   */
  protected PlaceholderFile getDefaultImport() {
    return new PlaceholderFile(".");
  }

  /**
   * Sets the file to import the executed jobs from.
   *
   * @param value 	the import file
   */
  public void setImport(PlaceholderFile value) {
    m_Import = value;
    reset();
  }

  /**
   * Returns the file to import the executed jobs from.
   *
   * @return		the import file
   */
  public PlaceholderFile getImport() {
    return m_Import;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String importTipText() {
    return "The file to deserialize the executed jobs from.";
  }

  /**
   * Sets the maximum number of intervals to wait.
   *
   * @param value	the maximum
   */
  public void setNumAttempts(int value) {
    if (getOptionManager().isValid("numAttempts", value)) {
      m_NumAttempts = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of intervals to wait.
   *
   * @return		the maximum
   */
  public int getNumAttempts() {
    return m_NumAttempts;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numAttemptsTipText() {
    return "The maximum number of intervals to wait.";
  }

  /**
   * Sets the interval in milli-seconds to wait.
   *
   * @param value	the interval
   */
  public void setAttemptInterval(int value) {
    if (getOptionManager().isValid("attemptInterval", value)) {
      m_AttemptInterval = value;
      reset();
    }
  }

  /**
   * Returns the interval to wait in milli-seconds.
   *
   * @return		the interval
   */
  public int getAttemptInterval() {
    return m_AttemptInterval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attemptIntervalTipText() {
    return "The interval in milli-seconds to wait before continuing with the execution.";
  }

  /**
   * Serializes the jobs to the specified export file.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doStart() {
    try {
      SerializationHelper.write(m_Export.getAbsolutePath(), m_ActualJobRunner);
    }
    catch (Exception e) {
      return Utils.handleException(this, "Failed to serialize jobrunner to: " + m_Export, e);
    }

    return null;
  }

  /**
   * Waits for the import file to appear.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doStop() {
    int		count;
    boolean	inUse;

    // wait for file to appear
    while (isRunning() || isPaused()) {
      if (!isPaused()) {
	if (m_Import.exists())
	  break;
      }
      Utils.wait(this, 100, 100);
    }

    // file still in use?
    count = 0;
    if (isRunning()) {
      while ((count < m_NumAttempts) && isRunning()) {
	if (!FileUtils.isOpen(m_Import))
	  break;
	count++;
	Utils.wait(this, m_AttemptInterval, Math.min(100, m_AttemptInterval));
      }
    }

    // read jobs
    if (isRunning()) {
      inUse = FileUtils.isOpen(m_Import);
      if (isLoggingEnabled())
	getLogger().info("count=" + count + ", inUse=" + inUse + ", file=" + m_Import);

      // still open?
      if ((count == m_NumAttempts) && inUse) {
	return "File '" + m_Import + "' is still in use after " + m_NumAttempts + " * " + m_AttemptInterval + "msec!";
      }

      try {
	m_ActualJobRunner = (JobRunner) SerializationHelper.read(m_Import.getAbsolutePath());
      }
      catch (Exception e) {
	m_ActualJobRunner = null;
	return Utils.handleException(this, "Failed to deserialize jobrunner form: " + m_Import, e);
      }
    }

    return null;
  }

  /**
   * Has no influence on the actual execution of the jobs.
   *
   * @return		always null
   */
  @Override
  protected String doTerminate() {
    return null;
  }

  /**
   * Ignored.
   *
   * @param j        job
   * @param jr        job result
   */
  @Override
  public void complete(Job j, JobResult jr) {
    getLogger().warning("complete(Job,JobResult) - ignored");
  }
}
