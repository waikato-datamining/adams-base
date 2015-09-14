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

import adams.core.SerializationHelper;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.event.JobCompleteListener;

import java.util.HashSet;
import java.util.logging.Level;

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
 * &nbsp;&nbsp;&nbsp;The file to serialized the un-executed jobs to.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-import &lt;adams.core.io.PlaceholderFile&gt; (property: import)
 * &nbsp;&nbsp;&nbsp;The file to serialized the executed jobs from.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SerializingJobRunner
  extends AbstractMetaJobRunner {

  private static final long serialVersionUID = 6656064128031953130L;

  /** call when job complete. */
  protected HashSet<JobCompleteListener> m_JobCompleteListeners;

  /** the file to serialize the unexecuted jobs to. */
  protected PlaceholderFile m_Export;

  /** the file to deserialize the finished jobs from. */
  protected PlaceholderFile m_Import;

  /** whether the jobs are being executed. */
  protected boolean m_Running;

  /** whether the execution is paused. */
  protected boolean m_Paused;

  /** the actual jobrunner. */
  protected JobRunner m_ActualJobRunner;

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
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_JobCompleteListeners = new HashSet<>();
    m_Running              = false;
    m_Paused               = false;
  }

  /**
   * Returns the default jobrunner.
   *
   * @return		the jobrunner
   */
  @Override
  protected JobRunner getDefaultJobRunner() {
    return new LocalJobRunner<>();
  }

  /**
   * Sets the base jobrunner.
   *
   * @param value 	the jobrunner
   */
  @Override
  public void setJobRunner(JobRunner value) {
    super.setJobRunner(value);
    m_ActualJobRunner = (JobRunner) OptionUtils.shallowCopy(m_JobRunner);
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
    return "The file to serialized the un-executed jobs to.";
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
    return "The file to serialized the executed jobs from.";
  }

  /**
   * Adds the listener.
   *
   * @param l		the listener to add
   */
  @Override
  public void addJobCompleteListener(JobCompleteListener l) {
    m_JobCompleteListeners.add(l);
  }

  /**
   * Removes the listener.
   *
   * @param l		the listener to remove
   */
  @Override
  public void removeJobCompleteListener(JobCompleteListener l) {
    m_JobCompleteListeners.remove(l);
  }

  /**
   * Adds the job to the execution queue.
   *
   * @param job		the job to add
   */
  @Override
  public void add(Job job) {
    m_ActualJobRunner.add(job);
  }

  /**
   * Adds the jobs to the execution queue.
   *
   * @param jobs	the jobs to add
   */
  @Override
  public void add(JobList jobs) {
    m_ActualJobRunner.add(jobs);
  }

  /**
   * Serializes the jobs to the specified export file.
   */
  @Override
  public void start() {
    try {
      SerializationHelper.write(m_Export.getAbsolutePath(), m_ActualJobRunner);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to serialize jobrunner to: " + m_Export);
    }
  }

  /**
   * Waits for the import file to appear.
   */
  @Override
  public void stop() {
    while (m_Running || m_Paused) {
      if (!m_Paused) {
	if (m_Import.exists())
	  break;
      }
      Utils.wait(this, 100, 100);
    }
    if (m_Running) {
      m_Paused  = false;
      m_Running = false;
      try {
	m_ActualJobRunner = (JobRunner) SerializationHelper.read(m_Import.getAbsolutePath());
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to deserialize jobrunner form: " + m_Import, e);
	m_ActualJobRunner = null;
      }
    }
  }

  /**
   * Has no influence on the actual execution of the jobs.
   */
  @Override
  public void terminate() {
    m_Paused  = false;
    m_Running = false;
  }

  /**
   * Ingored.
   *
   * @param j        job
   * @param jr        job result
   */
  @Override
  public void complete(Job j, JobResult jr) {
    // ignored
  }

  /**
   * Has no influence on the actual execution of the jobs.
   */
  @Override
  public void pauseExecution() {
    m_Paused = true;
  }

  /**
   * Has no influence on the actual execution of the jobs.
   *
   * @return		true if object is paused
   */
  @Override
  public boolean isPaused() {
    return m_Paused;
  }

  /**
   * Has no influence on the actual execution of the jobs.
   */
  @Override
  public void resumeExecution() {
    m_Paused = false;
  }
}
