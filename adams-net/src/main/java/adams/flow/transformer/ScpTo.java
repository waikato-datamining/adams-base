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
 * ScpTo.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 * Copyright (C) JSch
 */

package adams.flow.transformer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.core.annotation.MixedCopyright;
import adams.core.io.PlaceholderFile;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.standalone.SSHConnection;

import com.jcraft.jsch.ChannelExec;

/**
 <!-- globalinfo-start -->
 * Uploads a file to a remote directory using secure copy (SCP).<br/>
 * The file name of a successful upload gets forwarded.<br/>
 * <br/>
 * For more information see:<br/>
 * <br/>
 *  (2011). JSch - JSch is a pure Java implementation of SSH2..
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ScpTo
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-remote-dir &lt;java.lang.String&gt; (property: remoteDir)
 * &nbsp;&nbsp;&nbsp;The remote directory to upload the file to.
 * &nbsp;&nbsp;&nbsp;default: &#47;pub
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "JCraft",
    license = License.BSD3,
    url = "http://www.jcraft.com/jsch/"
)
public class ScpTo
  extends AbstractTransformer
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5015637337437403790L;

  /** the directory to upload the file to. */
  protected String m_RemoteDir;

  /** the SSH connection to use. */
  protected SSHConnection m_Connection;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Uploads a file to a remote directory using secure copy (SCP).\n"
      + "The file name of a successful upload gets forwarded.\n\n"
      + "For more information see:\n\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.YEAR, "2011");
    result.setValue(Field.TITLE, "JSch - JSch is a pure Java implementation of SSH2.");
    result.setValue(Field.HTTP, "http://www.jcraft.com/jsch/");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "remote-dir", "remoteDir",
	    "/pub");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "remoteDir", m_RemoteDir, "upload to ");
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
      m_Connection = (SSHConnection) ActorUtils.findClosestType(this, SSHConnection.class);
      if (m_Connection == null)
	result = "No " + SSHConnection.class.getName() + " actor found!";
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
    String		result;
    String		filename;
    File		file;
    String		remotefile;
    ChannelExec		channel;
    OutputStream	out;
    InputStream		in;
    byte[]		buffer;
    String		command;
    long 		filesize;
    FileInputStream	fis;
    int			len;

    result = null;

    filename   = (String) m_InputToken.getPayload();
    file       = new PlaceholderFile(filename);
    remotefile = m_RemoteDir + "/" + file.getName();
    channel    = null;
    try {
      channel = (ChannelExec) m_Connection.getSession().openChannel("exec");
      channel.setCommand("scp -p -t " + remotefile);
      if (isLoggingEnabled())
	getLogger().info("Uploading " + file + " to " + remotefile);
      in     = channel.getInputStream();
      out    = channel.getOutputStream();
      buffer = new byte[1024];

      channel.connect();

      if (SSHConnection.checkAck(in) != 0) {
	result = "Input stream check failed after opening channel!";
	return result;
      }

      // send "C0644 filesize filename", where filename should not include '/'
      filesize = file.length();
      command  = "C0644 " + filesize + " " + file.getName() + "\n";
      out.write(command.getBytes());
      out.flush();
      if (SSHConnection.checkAck(in) != 0)
	result = "Sending of filename failed!";

      // send a content of lfile
      fis    = new FileInputStream(file.getAbsoluteFile());
      buffer = new byte[1024];
      while (true) {
	len = fis.read(buffer, 0, buffer.length);
	if (len <= 0)
	  break;
	out.write(buffer, 0, len);
      }
      fis.close();
      fis = null;

      // send '\0'
      buffer[0]=0;
      out.write(buffer, 0, 1);
      out.flush();

      if (SSHConnection.checkAck(in) != 0)
	result = "Left-over data in input stream!";
      out.close();

      if (result == null)
	m_OutputToken = new Token(filename);
    }
    catch (Exception e) {
      result = handleException("Failed to upload file '" + file + "' to '" + remotefile + "': ", e);
      m_OutputToken = null;
    }
    finally {
      if (channel != null) {
	channel.disconnect();
      }
    }

    return result;
  }
}
