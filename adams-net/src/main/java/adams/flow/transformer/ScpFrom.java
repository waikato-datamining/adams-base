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
 * ScpFrom.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.standalone.SSHConnection;
import com.jcraft.jsch.ChannelExec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 <!-- globalinfo-start -->
 * Downloads a remote file and forwards the local file name using secure copy (SCP).<br>
 * <br>
 * For more information see:<br>
 * <br>
 *  (2011). JSch - JSch is a pure Java implementation of SSH2..
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
 * Valid options are: <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: ScpFrom
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
 * &nbsp;&nbsp;&nbsp;The remote directory to download the file from.
 * &nbsp;&nbsp;&nbsp;default: &#47;pub
 * </pre>
 * 
 * <pre>-output-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: outputDirectory)
 * &nbsp;&nbsp;&nbsp;The directory to store the downloaded files in.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScpFrom
  extends AbstractTransformer
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5015637337437403790L;

  /** the directory to list. */
  protected String m_RemoteDir;

  /** the output directory. */
  protected PlaceholderDirectory m_OutputDirectory;

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
	"Downloads a remote file and forwards the local file name using secure "
      + "copy (SCP).\n\n"
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

    m_OptionManager.add(
	    "output-dir", "outputDirectory",
	    new PlaceholderDirectory("."));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "remoteDir", m_RemoteDir, "download from ");
    result += QuickInfoHelper.toString(this, "outputDirectory", m_OutputDirectory, " to ");

    return result;
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
    return "The remote directory to download the file from.";
  }

  /**
   * Sets the directory to store the downloaded files in.
   *
   * @param value	the directory
   */
  public void setOutputDirectory(PlaceholderDirectory value) {
    m_OutputDirectory = value;
    reset();
  }

  /**
   * Returns the directory to store the downloaded files in.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getOutputDirectory() {
    return m_OutputDirectory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDirectoryTipText() {
    return "The directory to store the downloaded files in.";
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
    String		file;
    String		remotefile;
    String		outFile;
    ChannelExec		channel;
    OutputStream	out;
    InputStream		in;
    byte[]		buffer;
    int			c;
    long 		filesize;
    FileOutputStream	fos;
    int 		foo;
    int			i;

    result = null;

    file       = (String) m_InputToken.getPayload();
    remotefile = m_RemoteDir + "/" + file;
    outFile    = m_OutputDirectory.getAbsolutePath() + File.separator + file;
    channel    = null;
    fos        = null;
    try {
      channel = (ChannelExec) m_Connection.getSession().openChannel("exec");
      channel.setCommand("scp -f " + remotefile);
      if (isLoggingEnabled())
	getLogger().info("Downloading " + remotefile);
      in     = channel.getInputStream();
      out    = channel.getOutputStream();
      buffer = new byte[1024];

      channel.connect();

      // send '\0'
      buffer[0] = 0;
      out.write(buffer, 0, 1);
      out.flush();

      while (true) {
	c = SSHConnection.checkAck(in);
        if (c != 'C')
	  break;

        // read '0644 '
        in.read(buffer, 0, 5);

        filesize = 0L;
        while(true){
          // error?
          if (in.read(buffer, 0, 1) < 0)
            break;
          if (buffer[0]== ' ')
            break;
          filesize = filesize * 10L + (long) (buffer[0] - '0');
        }

        for (i = 0; ; i++) {
          in.read(buffer, i, 1);
          if(buffer[i] == (byte) 0x0a)
            break;
        }

        // send '\0'
        buffer[0] = 0;
        out.write(buffer, 0, 1);
        out.flush();

        // read a content of lfile
        fos = new FileOutputStream(outFile);
        while(true){
          if (buffer.length < filesize)
            foo = buffer.length;
	  else
	    foo = (int) filesize;
          foo = in.read(buffer, 0, foo);
          // error
          if (foo < 0)
            break;
          fos.write(buffer, 0, foo);
          filesize -= foo;
          if (filesize == 0L)
            break;
        }
        FileUtils.closeQuietly(fos);
        fos = null;

	if (SSHConnection.checkAck(in) != 0)
	  result = "Error occurred!";

        // send '\0'
        buffer[0] = 0;
        out.write(buffer, 0, 1);
        out.flush();
      }
      if (result == null)
	m_OutputToken = new Token(outFile);
    }
    catch (Exception e) {
      result = handleException("Failed to download file '" + remotefile + "' to '" + outFile + "': ", e);
      m_OutputToken = null;
    }
    finally {
      FileUtils.closeQuietly(fos);
      if (channel != null) {
	channel.disconnect();
      }
    }

    return result;
  }
}
