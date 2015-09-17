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
 * ScpJobRunner.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.multiprocess;

import adams.core.SerializationHelper;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.core.net.InternetHelper;
import adams.core.net.Scp;
import adams.event.JobCompleteListener;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.SSHConnection;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Wraps another jobrunner and serializes it, scp's it to the specified remote file and then waits for the specified import file (containing the serialized, executed jobs) to appear.<br>
 * Requires a adams.flow.standalone.SSHConnection standalone for defining the SSH connection.
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
 * <pre>-remote-file &lt;adams.core.io.PlaceholderFile&gt; (property: remoteFile)
 * &nbsp;&nbsp;&nbsp;The remote file for the un-executed jobs.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-local-file &lt;adams.core.io.PlaceholderFile&gt; (property: localFile)
 * &nbsp;&nbsp;&nbsp;The file to deserialize the executed jobs from.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScpJobRunner
  extends AbstractMetaJobRunner {

  private static final long serialVersionUID = 6656064128031953130L;

  protected static class RemoteJobRunner
    extends AbstractMetaJobRunner {

    private static final long serialVersionUID = 1416016602112933887L;

    /** the host that sent the jobs in the first place. */
    protected String m_Host;

    /** the SSH port. */
    protected int m_Port;

    /** the remote file to serialize the unexecuted jobs to. */
    protected PlaceholderFile m_RemoteFile;

    /** the ssh connection to use. */
    protected transient SSHConnection m_Connection;

    @Override
    public String globalInfo() {
      return "Performs the actual execution of the jobs on the remote machine and sends the results back.";
    }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "host", "host",
      "");

    m_OptionManager.add(
      "port", "port",
      22, 1, 65535);

    m_OptionManager.add(
      "remote-file", "remoteFile",
      getDefaultRemoteFile());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Connection = null;
  }

  /**
   * Sets the host to connect to.
   *
   * @param value	the host name/ip
   */
  public void setHost(String value) {
    m_Host = value;
    reset();
  }

  /**
   * Returns the host to connect to.
   *
   * @return		the host name/ip
   */
  public String getHost() {
    return m_Host;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hostTipText() {
    return "The host (name/IP address) to connect to.";
  }

  /**
   * Sets the port to connect to.
   *
   * @param value	the port
   */
  public void setPort(int value) {
    m_Port = value;
    reset();
  }

  /**
   * Returns the port to connect to.
   *
   * @return 		the port
   */
  public int getPort() {
    return m_Port;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String portTipText() {
    return "The port to connect to.";
  }

  /**
   * Returns the default remote file.
   *
   * @return		the remote file
   */
  protected PlaceholderFile getDefaultRemoteFile() {
    return new PlaceholderFile(".");
  }

  /**
   * Sets the remote file for the executed jobs.
   *
   * @param value 	the remote file
   */
  public void setRemoteFile(PlaceholderFile value) {
    m_RemoteFile = value;
    reset();
  }

  /**
   * Returns the remote file for the executed jobs.
   *
   * @return		the remote file
   */
  public PlaceholderFile getRemoteFile() {
    return m_RemoteFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String remoteFileTipText() {
    return "The remote file for the executed jobs on the host that sent the jobs.";
  }

    /**
     * Before actual start up.
     *
     * @return		null if successful, otherwise error message
     */
    @Override
    protected String preStart() {
      String	result;

      result = super.preStart();
      if (result != null)
	return result;

      if (m_Connection == null) {
	if (getFlowContext() == null) {
	  return "No flow context set, aborting!";
	}
	m_Connection = (SSHConnection) ActorUtils.findClosestType((AbstractActor) getFlowContext(), SSHConnection.class);
	if (m_Connection == null) {
	  return "No " + SSHConnection.class.getName() + " actor found, aborting!";
	}
      }

      return null;
    }

  /**
   * Performing actual start up.
   * Only gets executed if {@link #preStart()} was successful.
   *
   * @return		null if successful, otherwise error message
   */
    @Override
    protected String doStart() {
      m_ActualJobRunner.start();
      m_ActualJobRunner.stop();
      return null;
    }

    /**
     * Performing actual stop.
     *
     * @return		null if successful, otherwise error message
     */
    @Override
    protected String doStop() {
      File	tmpFile;
      String	msg;

      m_ActualJobRunner.stop();

      // serialize jobs
      tmpFile = TempUtils.createTempFile("adams-jobs-" + m_Connection.getHost(), ".ser");
      try {
	SerializationHelper.write(tmpFile.getAbsolutePath(), m_ActualJobRunner);
      }
      catch (Exception e) {
	if (tmpFile.exists())
	  tmpFile.delete();
	return Utils.handleException(this, "Failed to serialize jobrunner to: " + m_RemoteFile, e);
      }

      // scp to remote host
      try {
	msg = Scp.copyTo(this, m_Connection, m_Host, m_Port, tmpFile, m_RemoteFile.getAbsolutePath());
	if (msg != null)
	  getLogger().severe(
	    "Failed to copy serialized jobrunner to remote host "
	      + m_Host + ":" + m_Connection.getPort() + ": " + msg);
      }
      catch (Exception e) {
	tmpFile.delete();
	return Utils.handleException(this, "Failed to copy serialized jobrunner to remote host "
	  + m_Connection.getHost() + ":" + m_Connection.getPort(), e);
      }

      return null;
    }

    /**
     * Performing actual terminate up.
     *
     * @return		null if successful, otherwise error message
     */
    @Override
    protected String doTerminate() {
      m_ActualJobRunner.terminate();
      return null;
    }

    /**
     * Ignored.
     *
     * @param j		job
     * @param jr	job result
     */
    @Override
    public void complete(Job j, JobResult jr) {
      getLogger().warning("complete(Job,JobResult) - ignored");
    }
  }

  /** the remote file to serialize the unexecuted jobs to. */
  protected PlaceholderFile m_RemoteFile;

  /** the local file to deserialize the finished jobs from. */
  protected PlaceholderFile m_LocalFile;

  /** the ssh connection to use. */
  protected transient SSHConnection m_Connection;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Wraps another jobrunner and serializes it, scp's it to the specified remote file "
	+ "and then waits for the specified import file (containing the "
	+ "serialized, executed jobs) to appear.\n"
	+ "Requires a " + SSHConnection.class.getName() + " standalone for defining the SSH connection.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "remote-file", "remoteFile",
      getDefaultRemoteFile());

    m_OptionManager.add(
      "local-file", "localFile",
      getDefaultLocalFile());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Connection = null;
  }

  /**
   * Returns the default remote file.
   *
   * @return		the remote file
   */
  protected PlaceholderFile getDefaultRemoteFile() {
    return new PlaceholderFile(".");
  }

  /**
   * Sets the remote file for the un-executed jobs.
   *
   * @param value 	the remote file
   */
  public void setRemoteFile(PlaceholderFile value) {
    m_RemoteFile = value;
    reset();
  }

  /**
   * Returns the remote file for the un-executed jobs.
   *
   * @return		the remote file
   */
  public PlaceholderFile getRemoteFile() {
    return m_RemoteFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String remoteFileTipText() {
    return "The remote file for the un-executed jobs.";
  }

  /**
   * Returns the default local file.
   *
   * @return		the local file
   */
  protected PlaceholderFile getDefaultLocalFile() {
    return new PlaceholderFile(".");
  }

  /**
   * Sets the local file to import the executed jobs from.
   *
   * @param value 	the local file
   */
  public void setLocalFile(PlaceholderFile value) {
    m_LocalFile = value;
    reset();
  }

  /**
   * Returns the local file to import the executed jobs from.
   *
   * @return		the local file
   */
  public PlaceholderFile getLocalFile() {
    return m_LocalFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String localFileTipText() {
    return "The file to deserialize the executed jobs from.";
  }

  /**
   * Returns whether to transfer the listeners to the actual job runner.
   *
   * @return		true if to transfer
   * @see		#addJobCompleteListener(JobCompleteListener)
   * @see		#removeJobCompleteListener(JobCompleteListener)
   */
  protected boolean getTransferJobCompleteListeners() {
    return false;
  }

  /**
   * Returns an instance of the actual job runner to use.
   *
   * @return		the job runner to use
   */
  @Override
  protected JobRunner newActualJobRunner() {
    RemoteJobRunner	result;

    result = new RemoteJobRunner();
    result.setHost(InternetHelper.getHostnameFromNetworkInterface());
    // result.setPort(??);  TODO
    result.setRemoteFile(getLocalFile());
    result.setJobRunner(getJobRunner());

    return result;
  }

  /**
   * Before actual start up.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String preStart() {
    String	result;

    result = super.preStart();
    if (result != null)
      return result;

    if (m_Connection == null) {
      if (getFlowContext() == null) {
	return "No flow context set, aborting!";
      }
      m_Connection = (SSHConnection) ActorUtils.findClosestType((AbstractActor) getFlowContext(), SSHConnection.class);
      if (m_Connection == null) {
	return "No " + SSHConnection.class.getName() + " actor found, aborting!";
      }
    }

    return null;
  }

  /**
   * Serializes the jobs to the specified export file.
   */
  @Override
  protected String doStart() {
    File	tmpFile;
    String	msg;

    // serialize jobs
    tmpFile = TempUtils.createTempFile("adams-jobs-" + m_Connection.getHost(), ".ser");
    try {
      SerializationHelper.write(tmpFile.getAbsolutePath(), m_ActualJobRunner);
    }
    catch (Exception e) {
      if (tmpFile.exists())
	tmpFile.delete();
      return Utils.handleException(this, "Failed to serialize jobrunner to: " + m_RemoteFile, e);
    }

    // scp to remote host
    try {
      msg = Scp.copyTo(this, m_Connection, tmpFile, m_RemoteFile.getAbsolutePath());
      if (msg != null)
	getLogger().severe(
	  "Failed to copy serialized jobrunner to remote host "
	    + m_Connection.getHost() + ":" + m_Connection.getPort() + ": " + msg);
    }
    catch (Exception e) {
      tmpFile.delete();
      return Utils.handleException(this, "Failed to copy serialized jobrunner to remote host "
	+ m_Connection.getHost() + ":" + m_Connection.getPort(), e);
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
    while (isRunning() || isPaused()) {
      if (!isPaused()) {
	if (m_LocalFile.exists())
	  break;
      }
      Utils.wait(this, 100, 100);
    }
    if (isRunning()) {
      try {
	m_ActualJobRunner = (JobRunner) SerializationHelper.read(m_LocalFile.getAbsolutePath());
      }
      catch (Exception e) {
	m_ActualJobRunner = null;
	return Utils.handleException(this, "Failed to deserialize jobrunner form: " + m_LocalFile, e);
      }
    }

    return null;
  }

  /**
   * Has no influence on the actual execution of the jobs.
   */
  @Override
  public String doTerminate() {
    return null;
  }

  /**
   * Ingored.
   *
   * @param j        job
   * @param jr        job result
   */
  @Override
  public void complete(Job j, JobResult jr) {
    getLogger().warning("complete(Job,JobResult) - ignored");
  }
}
