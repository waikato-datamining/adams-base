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
 * SMBSend.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.core.net.SMB;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.standalone.SMBConnection;
import com.hierynomus.smbj.share.DiskShare;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Uploads a file to a remote directory (SMB, Windows share).<br>
 * The file name of a successful upload gets forwarded.<br>
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SMBSend
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-host &lt;java.lang.String&gt; (property: host)
 * &nbsp;&nbsp;&nbsp;The host (name&#47;IP address) to connect to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-remote-dir &lt;java.lang.String&gt; (property: remoteDir)
 * &nbsp;&nbsp;&nbsp;The remote directory to upload the file to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SMBSend
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5015637337437403790L;

  /** the share to access. */
  protected String m_Share;

  /** the directory to upload the file to. */
  protected String m_RemoteDir;

  /** the SMB connection to use. */
  protected transient SMBConnection m_Connection;

  /** the disk share instance. */
  protected transient DiskShare m_DiskShare;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Uploads a file to a remote directory (SMB, Windows share).\n"
        + "The file name of a successful upload gets forwarded.\n";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "share", "share",
      "");

    m_OptionManager.add(
      "remote-dir", "remoteDir",
      "");
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    cleanUpSmb();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "share", (m_Share.isEmpty() ? "-none-" : m_Share), "share: ");
    result += QuickInfoHelper.toString(this, "remoteDir", (m_RemoteDir.isEmpty() ? "-none-" : m_RemoteDir), ", remote dir: ");

    return result;
  }

  /**
   * Sets the share to access.
   *
   * @param value	the share
   */
  public void setShare(String value) {
    m_Share = value;
    reset();
  }

  /**
   * Returns the share to access.
   *
   * @return		the share
   */
  public String getShare() {
    return m_Share;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shareTipText() {
    return "The share to access.";
  }

  /**
   * Sets the remote directory.
   *
   * @param value	the remote directory
   */
  public void setRemoteDir(String value) {
    m_RemoteDir = value;
    reset();
  }

  /**
   * Returns the remote directory.
   *
   * @return		the remote directory.
   */
  public String getRemoteDir() {
    return m_RemoteDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String remoteDirTipText() {
    return "The remote directory to upload the file to.";
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_Connection = (SMBConnection) ActorUtils.findClosestType(this, SMBConnection.class);
      if (m_Connection == null)
        result = "No " + SMBConnection.class.getName() + " actor found!";
    }

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String	filename;
    File 	localFile;
    String 	remoteFile;

    result     = null;
    filename   = (String) m_InputToken.getPayload();
    localFile  = new PlaceholderFile(filename);
    remoteFile = m_RemoteDir + "/" + localFile.getName();

    if (m_DiskShare == null) {
      if (isLoggingEnabled())
	getLogger().info("Connection to share: " + m_Share);
      m_DiskShare = (DiskShare) m_Connection.getSession().connectShare(m_Share);
    }

    if (isLoggingEnabled())
      getLogger().info("Copying '" + localFile + "' to '" + remoteFile + "'");
    result = SMB.copyTo(this, m_Connection, localFile, m_DiskShare, remoteFile);

    if (result == null)
      m_OutputToken = new Token(filename);
    else
      m_OutputToken = null;

    return result;
  }

  /**
   * Cleans up SMB related resources.
   */
  protected void cleanUpSmb() {
    if (m_DiskShare != null) {
      try {
	m_DiskShare.close();
      }
      catch (Exception e) {
	// ignored
      }
      m_DiskShare = null;
    }
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();
    cleanUpSmb();
  }
}
