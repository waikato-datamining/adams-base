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
 * Socket.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.io.EncodingSupporter;
import adams.core.net.PortManager;
import adams.flow.control.Flow;
import adams.flow.core.Token;
import gnu.trove.list.array.TByteArrayList;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 <!-- globalinfo-start -->
 * Listens on the specified port for incoming data.<br>
 * Can either output raw byte arrays or strings (using the specified encoding).<br>
 * <br>
 * See also:<br>
 * adams.flow.sink.Socket
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;byte[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: Socket
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
 * <pre>-port &lt;int&gt; (property: port)
 * &nbsp;&nbsp;&nbsp;The port to listen on.
 * &nbsp;&nbsp;&nbsp;default: 8000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 65535
 * </pre>
 *
 * <pre>-timeout &lt;int&gt; (property: timeout)
 * &nbsp;&nbsp;&nbsp;The timeout in milli-second for waiting on new client connections.
 * &nbsp;&nbsp;&nbsp;default: 3000
 * &nbsp;&nbsp;&nbsp;minimum: 100
 * </pre>
 *
 * <pre>-output-string &lt;boolean&gt; (property: outputString)
 * &nbsp;&nbsp;&nbsp;If enabled, a string with the specified encoding is generated from the incoming 
 * &nbsp;&nbsp;&nbsp;byte array.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding for sending the data.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Socket
  extends AbstractSimpleSource
  implements EncodingSupporter, ClassCrossReference {

  private static final long serialVersionUID = 4824594325548647716L;

  /** the port to listen on. */
  protected int m_Port;

  /** the timeout for the socket. */
  protected int m_Timeout;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** whether to output a string. */
  protected boolean m_OutputString;

  /** the socket in use. */
  protected transient ServerSocket m_ServerSocket;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Listens on the specified port for incoming data.\n"
      + "Can either output raw byte arrays or strings (using the specified encoding).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "port", "port",
      8000, 1, 65535);

    m_OptionManager.add(
      "timeout", "timeout",
      3000, 100, null);

    m_OptionManager.add(
      "output-string", "outputString",
      false);

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());
  }

  /**
   * Sets the port to connect to.
   *
   * @param value	the port
   */
  public void setPort(int value) {
    if (getOptionManager().isValid("port", value)) {
      m_Port = value;
      reset();
    }
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
    return "The port to listen on.";
  }

  /**
   * Sets the timeout in milli-second to wait for new connections.
   *
   * @param value	the timeout in msec
   */
  public void setTimeout(int value) {
    if (getOptionManager().isValid("timeout", value)) {
      m_Timeout = value;
      reset();
    }
  }

  /**
   * Returns the timeout in milli-second to wait for new connections.
   *
   * @return		the timeout in msec
   */
  public int getTimeout() {
    return m_Timeout;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String timeoutTipText() {
    return "The timeout in milli-second for waiting on new client connections.";
  }

  /**
   * Sets whether to output a string.
   *
   * @param value	true if to output a string
   */
  public void setOutputString(boolean value) {
    m_OutputString = value;
    reset();
  }

  /**
   * Returns whether to output a string.
   *
   * @return		true if to output a string
   */
  public boolean getOutputString() {
    return m_OutputString;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputStringTipText() {
    return "If enabled, a string with the specified encoding is generated from the incoming byte array.";
  }

  /**
   * Sets the encoding to use.
   *
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }

  /**
   * Returns the encoding to use.
   *
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encodingTipText() {
    return "The type of encoding for sending the data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "port", m_Port, "listening on ");
    result += QuickInfoHelper.toString(this, "outputString", (m_OutputString ? "string" : "byte[]"), ", outputting: ");
    result += QuickInfoHelper.toString(this, "encoding", m_Encoding, ", encoding: ");

    return result;
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{adams.flow.sink.Socket.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    if (m_OutputString)
      return new Class[]{String.class};
    else
      return new Class[]{byte[].class};
  }

  /**
   * Returns whether the flow is paused.
   *
   * @return		true if flow paused
   */
  protected boolean isPaused() {
    return (getRoot() instanceof Flow) && ((Flow) getRoot()).isPaused();
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    java.net.Socket	client;
    TByteArrayList 	bytes;
    InputStream 	in;
    int			b;

    result = null;

    if (m_ServerSocket == null) {
      try {
	m_ServerSocket = new ServerSocket(m_Port);
	m_ServerSocket.setSoTimeout(m_Timeout);
	PortManager.getSingleton().bind(this, m_Port);
      }
      catch (Exception e) {
	result   = handleException("Failed to listen on port: " + m_Port, e);
	m_ServerSocket = null;
      }
    }

    if (m_ServerSocket != null) {
      while (!m_Stopped && (m_OutputToken == null)) {
	while (isPaused() && !m_Stopped)
	  Utils.wait(this, this, 1000, 50);
	if (m_ServerSocket.isClosed())
	  break;
	try {
	  client = m_ServerSocket.accept();
	  in     = client.getInputStream();
	  bytes  = new TByteArrayList();
	  while ((b = in.read()) != -1)
	    bytes.add((byte) b);
	  client.close();
	  if (m_OutputString)
	    m_OutputToken = new Token(new String(bytes.toArray(), m_Encoding.charsetValue()));
	  else
	    m_OutputToken = new Token(bytes.toArray());
	}
	catch (SocketTimeoutException stoe) {
	  // ignored
	}
	catch (SocketException se) {
	  if (!m_Stopped)
	    result = handleException("Failed to accept connection!", se);
	}
	catch (Exception e) {
	  result = handleException("Failed to accept connection!", e);
	}
      }
    }

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_ServerSocket != null) {
      try {
	if (!m_ServerSocket.isClosed())
	  m_ServerSocket.close();
      }
      catch (Exception e) {
	// ignored
      }
      m_ServerSocket = null;
    }

    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_ServerSocket != null) {
      try {
	if (!m_ServerSocket.isClosed())
	  m_ServerSocket.close();
      }
      catch (Exception e) {
	// ignored
      }
      m_ServerSocket = null;
    }

    super.wrapUp();
  }

  /**
   * Returns whether the item has finished. The <code>execute()</code> will be
   * called as long as the <code>isFinished()</code> method returns false.
   *
   * @return		true if finished, false if further calls to execute()
   * 			are necessary. Default implementation returns always
   * 			true, i.e., fires only once.
   */
  @Override
  public boolean isFinished() {
    return (m_ServerSocket == null);
  }
}
