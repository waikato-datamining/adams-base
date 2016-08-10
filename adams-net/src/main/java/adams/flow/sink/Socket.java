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

package adams.flow.sink;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseCharset;
import adams.core.base.BaseHostname;
import adams.core.io.EncodingSupporter;
import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Just outputs the data to the specified socket.<br>
 * Any incoming data that isn't a byte array gets converted to a string and its bytes (using the specified encoding) are then transmitted.<br>
 * <br>
 * See also:<br>
 * adams.flow.source.Socket
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br>
 * &nbsp;&nbsp;&nbsp;byte[]<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Byte[]<br>
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
 * <pre>-address &lt;adams.core.base.BaseHostname&gt; (property: address)
 * &nbsp;&nbsp;&nbsp;The address to connect to.
 * &nbsp;&nbsp;&nbsp;default: 127.0.0.1:8000
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
  extends AbstractSink
  implements EncodingSupporter, ClassCrossReference {

  private static final long serialVersionUID = 6275418140787412381L;

  /** the address to open. */
  protected BaseHostname m_Address;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** the socket to use. */
  protected transient java.net.Socket m_Socket;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Just outputs the data to the specified socket.\n"
      + "Any incoming data that isn't a byte array gets converted to a string "
      + "and its bytes (using the specified encoding) are then transmitted.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "address", "address",
      new BaseHostname("127.0.0.1:8000"));

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());
  }

  /**
   * Sets the address.
   *
   * @param value 	the address
   */
  public void setAddress(BaseHostname value) {
    m_Address = value;
    reset();
  }

  /**
   * Returns the address.
   *
   * @return 		the address
   */
  public BaseHostname getAddress() {
    return m_Address;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addressTipText() {
    return "The address to connect to.";
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

    result  = QuickInfoHelper.toString(this, "address", m_Address);
    result += QuickInfoHelper.toString(this, "encoding", m_Encoding, ", encoding: ");

    return result;
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{adams.flow.source.Socket.class};
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Object.class, byte[].class, Byte[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    byte[]	bytes;

    result = null;

    if (m_Socket == null) {
      try {
	m_Socket = new java.net.Socket(m_Address.hostnameValue(), m_Address.portValue());
      }
      catch (Exception e) {
	result   = handleException("Failed to open socket to: " + m_Address, e);
	m_Socket = null;
      }
    }

    if (m_Socket != null) {
      try {
	if (m_InputToken.getPayload() instanceof byte[])
	  bytes = (byte[]) m_InputToken.getPayload();
	else if (m_InputToken.getPayload() instanceof Byte[])
	  bytes = StatUtils.toByteArray((Byte[]) m_InputToken.getPayload());
	else
	  bytes = ("" + m_InputToken.getPayload()).getBytes(m_Encoding.charsetValue());
	m_Socket.getOutputStream().write(bytes);
	m_Socket.getOutputStream().flush();
      }
      catch (Exception e) {
	result = handleException("Failed to send data!", e);
      }
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    if (m_Socket != null) {
      try {
	m_Socket.close();
      }
      catch (Exception e) {
	// ignored
      }
      m_Socket = null;
    }
    super.wrapUp();
  }
}
