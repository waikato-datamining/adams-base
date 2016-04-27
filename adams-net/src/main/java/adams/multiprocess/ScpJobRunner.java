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
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.multiprocess;

import adams.core.MultiAttemptWithWaitSupporter;
import adams.core.SerializationHelper;
import adams.core.Utils;
import adams.core.base.BaseHostname;
import adams.core.base.HostnameUpdateSupporter;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.core.net.InternetHelper;
import adams.core.net.Scp;
import adams.event.JobCompleteListener;
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
 * <pre>-local-host &lt;adams.core.base.BaseHostname&gt; (property: localHost)
 * &nbsp;&nbsp;&nbsp;The host (name&#47;IP address:port) that the remote host will connect to when 
 * &nbsp;&nbsp;&nbsp;sending back the executed jobs; leave empty to use auto-detection.
 * &nbsp;&nbsp;&nbsp;default: :22
 * </pre>
 * 
 * <pre>-local-file &lt;adams.core.io.PlaceholderFile&gt; (property: localFile)
 * &nbsp;&nbsp;&nbsp;The file to deserialize the executed jobs from.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-max-wait &lt;int&gt; (property: maxWait)
 * &nbsp;&nbsp;&nbsp;The maximum time to wait in milli-seconds before giving up on remote jobs;
 * &nbsp;&nbsp;&nbsp; -1 for indefinite.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-max-attempts &lt;int&gt; (property: numAttempts)
 * &nbsp;&nbsp;&nbsp;The maximum number of intervals to wait.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-attempt-interval &lt;int&gt; (property: attemptInterval)
 * &nbsp;&nbsp;&nbsp;The interval in milli-seconds to wait before continuing with the execution.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-allow-local-execution &lt;boolean&gt; (property: allowLocalExecution)
 * &nbsp;&nbsp;&nbsp;If enabled, executes the jobs locally in case the SCP operation fails.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScpJobRunner
  extends AbstractMetaJobRunner
  implements MultiAttemptWithWaitSupporter, HostnameUpdateSupporter {

  private static final long serialVersionUID = 6656064128031953130L;

  protected static class RemoteJobRunner
    extends AbstractMetaJobRunner {

    private static final long serialVersionUID = 1416016602112933887L;

    /** the host that sent the jobs in the first place. */
    protected BaseHostname m_Host;

    /** the remote file to serialize the unexecuted jobs to. */
    protected File m_RemoteFile;

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
        new BaseHostname("remote:22"));

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
     * @param value	the host name/ip:port
     */
    public void setHost(BaseHostname value) {
      m_Host = value;
      reset();
    }

    /**
     * Returns the host to connect to.
     *
     * @return		the host name/ip:port
     */
    public BaseHostname getHost() {
      return m_Host;
    }

    /**
     * Returns the tip text for this property.
     *
     * @return 		tip text for this property suitable for
     * 			displaying in the GUI or for listing the options.
     */
    public String hostTipText() {
      return "The host (name/IP address:port) to connect to.";
    }

    /**
     * Returns the default remote file.
     *
     * @return		the remote file
     */
    protected File getDefaultRemoteFile() {
      return new File(".");
    }

    /**
     * Sets the remote file for the executed jobs.
     *
     * @param value 	the remote file
     */
    public void setRemoteFile(File value) {
      m_RemoteFile = value;
      reset();
    }

    /**
     * Returns the remote file for the executed jobs.
     *
     * @return		the remote file
     */
    public File getRemoteFile() {
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
        m_Connection = (SSHConnection) ActorUtils.findClosestType(getFlowContext(), SSHConnection.class);
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
      tmpFile = TempUtils.createTempFile("adams-jobs-" + m_Host + "-", ".ser");
      if (isLoggingEnabled())
        getLogger().info("Serializing jobs to " + tmpFile);
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
        if (isLoggingEnabled())
          getLogger().info("Scp'ing jobs to " + m_Host + m_RemoteFile.getAbsolutePath());
        msg = Scp.copyTo(this, m_Connection, m_Host.hostnameValue(), m_Host.portValue(22), tmpFile, m_RemoteFile.getAbsolutePath());
        if (msg != null)
          return "Failed to copy serialized jobrunner to original host " + m_Host + ": " + msg;
        tmpFile.delete();
      }
      catch (Exception e) {
        tmpFile.delete();
        return Utils.handleException(this, "Failed to copy serialized jobrunner to original host " + m_Host, e);
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

  /** the local host. */
  protected BaseHostname m_LocalHost;

  /** the local file to deserialize the finished jobs from. */
  protected PlaceholderFile m_LocalFile;

  /** the maxomum time in milli-seconds to wait. */
  protected int m_MaxWait;

  /** the maximum number of interval to wait. */
  protected int m_NumAttempts;

  /** the interval in milli-seconds to wait. */
  protected int m_AttemptInterval;

  /** whether to allow fallback for local execution. */
  protected boolean m_AllowLocalExecution;

  /** the ssh connection to use. */
  protected transient SSHConnection m_Connection;

  /** the actual host to connect to. */
  protected BaseHostname m_ActualHost;

  /** whether jobs are run locally as fallback. */
  protected boolean m_RunLocally;

  /** the start time. */
  protected long m_StartTime;

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
      "local-host", "localHost",
      new BaseHostname(":22"));

    m_OptionManager.add(
      "local-file", "localFile",
      getDefaultLocalFile());

    m_OptionManager.add(
      "max-wait", "maxWait",
      -1, -1, null);

    m_OptionManager.add(
      "max-attempts", "numAttempts",
      10, 1, null);

    m_OptionManager.add(
      "attempt-interval", "attemptInterval",
      1000, 1, null);

    m_OptionManager.add(
      "allow-local-execution", "allowLocalExecution",
      false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Connection = null;
    m_ActualHost = null;
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
   * Sets the host that the remote host will connect to for sending back
   * the executed jobs. Leave empty to use auto-discovery.
   *
   * @param value	the host name/ip:port
   */
  public void setLocalHost(BaseHostname value) {
    m_LocalHost = value;
    reset();
  }

  /**
   * Returns the host that the remote host will connect to for sending back
   * the executed jobs. Leave empty to use auto-discovery.
   *
   * @return		the host name/ip:port
   */
  public BaseHostname getLocalHost() {
    return m_LocalHost;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String localHostTipText() {
    return
      "The host (name/IP address:port) that the remote host will connect to when "
        + "sending back the executed jobs; leave empty to use auto-detection.";
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
   * Sets the maximum time in milli-seconds to wait before giving up on
   * the remote jobs.
   *
   * @param value	the time in milli-second, -1 for indefinite
   */
  public void setMaxWait(int value) {
    if (getOptionManager().isValid("maxWait", value)) {
      m_MaxWait = value;
      reset();
    }
  }

  /**
   * Returns the maximum time in milli-seconds to wait before giving up on
   * the remote jobs.
   *
   * @return		the time in milli-second, -1 for indefinite
   */
  public int getMaxWait() {
    return m_MaxWait;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxWaitTipText() {
    return "The maximum time to wait in milli-seconds before giving up on remote jobs; -1 for indefinite.";
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
   * Sets whether to allow local execution of jobs in case SCP fails.
   *
   * @param value	true if to allow local execution
   */
  public void setAllowLocalExecution(boolean value) {
    m_AllowLocalExecution = value;
    reset();
  }

  /**
   * Returns whether local execution of jobs is allowed in case SCP fails.
   *
   * @return		true if local execution allowed
   */
  public boolean getAllowLocalExecution() {
    return m_AllowLocalExecution;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String allowLocalExecutionTipText() {
    return "If enabled, executes the jobs locally in case the SCP operation fails.";
  }

  /**
   * Sets the actual host to use - overrides the one from the SSHConnection actor.
   *
   * @param host        the new hostname/port
   */
  @Override
  public void updateHostname(BaseHostname host) {
    m_ActualHost = host;
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
    if (m_LocalHost.hostnameValue().isEmpty())
      result.setHost(new BaseHostname(InternetHelper.getHostnameFromNetworkInterface() + ":" + m_LocalHost.portValue(22)));
    else
      result.setHost(getLocalHost());
    result.setRemoteFile(getLocalFile().getAbsoluteFile());
    result.setJobRunner(getJobRunner());
    result.setLoggingLevel(getLoggingLevel());
    if (isLoggingEnabled())
      getLogger().info("Remote jobrunner: " + result.toCommandLine());

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
      m_Connection = (SSHConnection) ActorUtils.findClosestType(getFlowContext(), SSHConnection.class);
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
    File		tmpFile;
    String		msg;
    String		host;
    int			port;
    String		actualHost;
    int			actualPort;
    int			i;

    m_RunLocally = false;
    if (m_ActualHost != null) {
      host = m_ActualHost.hostnameValue();
      port = m_ActualHost.portValue(22);
    }
    else {
      host = null;
      port = -1;
    }

    actualHost = (host == null ? m_Connection.getHost() : host);
    actualPort = (host == null ? m_Connection.getPort() : port);

    // serialize jobs
    tmpFile = TempUtils.createTempFile("adams-jobs-" + actualHost + "-", ".ser");
    try {
      if (isLoggingEnabled())
        getLogger().info("Serializing jobs to " + tmpFile);
      SerializationHelper.write(tmpFile.getAbsolutePath(), this.m_ActualJobRunner);
    }
    catch (Exception e) {
      if (tmpFile.exists())
        tmpFile.delete();
      return Utils.handleException(this, "Failed to serialize jobrunner to: " + m_RemoteFile, e);
    }

    m_StartTime = System.currentTimeMillis();

    // scp to remote host
    try {
      if (isLoggingEnabled())
        getLogger().info("Scp'ing jobs to " + actualHost + ":" + actualPort + m_RemoteFile.getAbsolutePath());
      msg = Scp.copyTo(this, m_Connection, host, port, tmpFile, m_RemoteFile.getAbsolutePath());
      if (msg != null)
        getLogger().severe(
          "Failed to copy serialized jobrunner to remote host "
            + actualHost + ":" + actualPort + ": " + msg);
      tmpFile.delete();
    }
    catch (Exception e) {
      tmpFile.delete();
      if (!m_AllowLocalExecution) {
	return Utils.handleException(this, "Failed to copy serialized jobrunner to remote host "
	  + actualHost + ":" + actualPort, e);
      }
      else {
	Utils.handleException(this, "Failed to copy serialized jobrunner to remote host "
	  + actualHost + ":" + actualPort, e);
	m_RunLocally = true;
      }
    }

    if (m_RunLocally) {
      getLogger().info("Executing jobs locally");
      m_ActualJobRunner = new LocalJobRunner();
      for (i = 0; i < m_Jobs.size(); i++)
	m_ActualJobRunner.add(m_Jobs.get(i));
      for (JobCompleteListener l: m_JobCompleteListeners)
	m_ActualJobRunner.addJobCompleteListener(l);
      m_ActualJobRunner.start();
      m_ActualJobRunner.stop();
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

    if (!m_RunLocally) {
      // wait for file to appear
      while (isRunning() || isPaused()) {
	if (!isPaused()) {
	  if (m_LocalFile.exists())
	    break;
	  if (m_MaxWait > -1) {
	    if (System.currentTimeMillis() >= m_StartTime + m_MaxWait) {
	      m_Running = false;
	      return "Max wait reached (" + m_MaxWait + "msec)";
	    }
	  }
	}
	Utils.wait(this, 100, 100);
      }

      // file still in use?
      count = 0;
      if (isRunning()) {
	while ((count < m_NumAttempts) && isRunning()) {
	  if (!FileUtils.isOpen(m_LocalFile))
	    break;
	  count++;
	  Utils.wait(this, m_AttemptInterval, Math.min(100, m_AttemptInterval));
	}
      }

      // read jobs
      if (isRunning()) {
	inUse = FileUtils.isOpen(m_LocalFile);
	if (isLoggingEnabled())
	  getLogger().info("count=" + count + ", inUse=" + inUse + ", file=" + m_LocalFile);

	// still open?
	if ((count == m_NumAttempts) && inUse) {
	  return "File '" + m_LocalFile + "' is still in use after " + m_NumAttempts + " * " + m_AttemptInterval + "msec!";
	}

	try {
	  if (isLoggingEnabled())
	    getLogger().info("Reading jobs from " + m_LocalFile);
	  m_ActualJobRunner = (JobRunner) SerializationHelper.read(m_LocalFile.getAbsolutePath());
	}
	catch (Exception e) {
	  m_ActualJobRunner = null;
	  return Utils.handleException(this, "Failed to deserialize jobrunner form: " + m_LocalFile, e);
	}
      }
    }

    return null;
  }

  /**
   * Has no influence on the actual execution of the remote jobs, only when
   * jobs are run locally (as fallback).
   */
  @Override
  public String doTerminate() {
    if (m_RunLocally && (m_ActualJobRunner != null))
      m_ActualJobRunner.terminate();
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
