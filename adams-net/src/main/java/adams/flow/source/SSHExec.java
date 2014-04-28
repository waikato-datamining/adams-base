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
 * SSHExec.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 * Copyright (C) JCraft
 */

package adams.flow.source;

import java.io.InputStream;

import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.core.annotation.MixedCopyright;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.standalone.SSHConnection;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;

/**
 <!-- globalinfo-start -->
 * Runs a system command via ssh on a remote machine and broadcasts the generated output (stdout or stderr).<br/>
 * <br/>
 * For more information see:<br/>
 * <br/>
 *  (2011). JSch - JSch is a pure Java implementation of SSH2..
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
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
 * &nbsp;&nbsp;&nbsp;default: SSHExec
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
 * <pre>-cmd &lt;java.lang.String&gt; (property: command)
 * &nbsp;&nbsp;&nbsp;The external command to run.
 * &nbsp;&nbsp;&nbsp;default: ls -l .
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
public class SSHExec
  extends AbstractSimpleSource
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -132045002653940359L;

  /** the command to run. */
  protected String m_Command;

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
        "Runs a system command via ssh on a remote machine and broadcasts the generated output "
        + "(stdout or stderr).\n\n"
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
	    "cmd", "command",
	    "ls -l .");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "command", m_Command);
  }

  /**
   * Sets the command to run.
   *
   * @param value	the command
   */
  public void setCommand(String value) {
    m_Command = value;
    reset();
  }

  /**
   * Returns the command to run.
   *
   * @return 		the command
   */
  public String getCommand() {
    return m_Command;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String commandTipText() {
    return "The external command to run.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class};
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    StringBuilder	output;
    Channel		channel;
    InputStream		in;
    byte[]		buffer;
    int			read;

    result = null;

    try {
      channel = m_Connection.getSession().openChannel("exec");
      ((ChannelExec) channel).setCommand(m_Command);
      channel.setXForwarding(m_Connection.getForwardX());
      channel.setInputStream(null);
      ((ChannelExec)channel).setErrStream(System.err);
      in     = channel.getInputStream();
      buffer = new byte[1024];
      output = new StringBuilder();
      channel.connect();
      while (true) {
        while (in.available() > 0) {
          read = in.read(buffer, 0, buffer.length);
          if (read < 0)
            break;
          output.append(new String(buffer, 0, read));
        }
        if (channel.isClosed()) {
          if (channel.getExitStatus() != 0)
            result = "Exit code: " + channel.getExitStatus();
          break;
        }
        try {
          Thread.sleep(200);
        }
        catch (Exception ee) {
          handleException("Failed to sleep", ee);
        }
      }
      channel.disconnect();
      if (result == null)
	m_OutputToken = new Token(output.toString());
    }
    catch (Exception e) {
      result = handleException("Failed to execute remote command: " + m_Command, e);
    }

    return result;
  }
}
