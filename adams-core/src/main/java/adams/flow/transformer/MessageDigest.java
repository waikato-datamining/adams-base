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
 * MessageDigest.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.EnumWithCustomDisplay;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.AbstractOption;
import adams.flow.core.Token;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;

/**
 <!-- globalinfo-start -->
 * Generates a message digest and forwards that. The digest is either generated on the string being passed through or from the content of a file (if a File object is used as input).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
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
 * &nbsp;&nbsp;&nbsp;default: MessageDigest
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
 * <pre>-type &lt;MD2|MD5|SHA-1|SHA-256|SHA-384|SHA-512&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of message digest (algorithm) to use.
 * &nbsp;&nbsp;&nbsp;default: MD5
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MessageDigest
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8395316814322443892L;

  /**
   * Enumeration of available message digest algorithms.
   * <br><br>
   * See <a href="http://remington.cs.waikato.ac.nz/documentation/jdk-1.6.0/technotes/guides/security/StandardNames.html#MessageDigest"
   * target="_blank">here</a>.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum MessageDigestType
    implements EnumWithCustomDisplay<MessageDigestType>{

    /** MD2. */
    MD2("MD2"),
    /** MD5. */
    MD5("MD5"),
    /** SHA-1. */
    SHA1("SHA-1"),
    /** SHA-256. */
    SHA256("SHA-256"),
    /** SHA-256. */
    SHA384("SHA-384"),
    /** SHA-256. */
    SHA512("SHA-512");

    /** the algorithm name. */
    private String m_Algorithm;

    /** the raw enum string. */
    private String m_Raw;

    /**
     * Initializes the type.
     *
     * @param algorithm	the display string
     */
    private MessageDigestType(String algorithm) {
      m_Algorithm = algorithm;
      m_Raw       = super.toString();
    }

    /**
     * Returns the display string.
     *
     * @return		the display string
     */
    public String toDisplay() {
      return m_Algorithm;
    }

    /**
     * Returns the raw enum string.
     *
     * @return		the raw enum string
     */
    public String toRaw() {
      return m_Raw;
    }

    /**
     * Returns the display string.
     *
     * @return		the display string
     */
    @Override
    public String toString() {
      return m_Algorithm;
    }

    /**
     * Parses the given string and returns the associated enum.
     *
     * @param s		the string to parse
     * @return		the enum or null if not found
     */
    public MessageDigestType parse(String s) {
      return (MessageDigestType) valueOf((AbstractOption) null, s);
    }

    /**
     * Returns the enum as string.
     *
     * @param option	the current option
     * @param object	the enum object to convert
     * @return		the generated string
     */
    public static String toString(AbstractOption option, Object object) {
      return ((MessageDigestType) object).toRaw();
    }

    /**
     * Returns an enum generated from the string.
     *
     * @param option	the current option
     * @param str	the string to convert to an enum
     * @return		the generated enum or null in case of error
     */
    public static MessageDigestType valueOf(AbstractOption option, String str) {
      MessageDigestType	result;

      result = null;

      // default parsing
      try {
	result = valueOf(str);
      }
      catch (Exception e) {
	// ignored
      }

      // try display
      if (result == null) {
	for (MessageDigestType dt: values()) {
	  if (dt.toDisplay().equals(str)) {
	    result = dt;
	    break;
	  }
	}
      }

      return result;
    }
  }

  /** the type of message digest to use. */
  protected MessageDigestType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates a message digest and forwards that. The digest is either "
      + "generated on the string being passed through or from the content of a "
      + "file (if a File object is used as input).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "type",
	    MessageDigestType.MD5);
  }

  /**
   * Sets the type of digest to use.
   *
   * @param value	the type
   */
  public void setType(MessageDigestType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of digest to use.
   *
   * @return		the type
   */
  public MessageDigestType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of message digest (algorithm) to use.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    String			input;
    java.security.MessageDigest	md;
    byte[]			digest;
    StringBuilder		hex;
    DigestInputStream		stream;
    FileInputStream             fis;
    File			file;
    byte[]			buffer;

    result = null;

    fis    = null;
    stream = null;
    try {
      md = java.security.MessageDigest.getInstance(m_Type.toDisplay());
      if (m_InputToken.getPayload() instanceof String) {
	input = (String) m_InputToken.getPayload();
	md.update(input.getBytes());
      }
      else {
	file   = (File) m_InputToken.getPayload();
	fis    = new FileInputStream(file.getAbsolutePath());
	stream = new DigestInputStream(new BufferedInputStream(fis), md);
	buffer = new byte[1024];
	while (stream.read(buffer) != -1);
      }
      digest = md.digest();
      hex    = new StringBuilder();
      for (byte b: digest)
	hex.append(Utils.toHex(b));
      m_OutputToken = new Token(hex.toString());
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to generate digest:", e);
    }
    finally {
      FileUtils.closeQuietly(stream);
      FileUtils.closeQuietly(fis);
    }

    return result;
  }
}
