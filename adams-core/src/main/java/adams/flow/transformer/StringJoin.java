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
 * StringJoin.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;

/**
 <!-- globalinfo-start -->
 * Creates a single string out of an array of strings.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: StringJoin
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the strings are output as an array rather than one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-glue &lt;java.lang.String&gt; (property: glue)
 * &nbsp;&nbsp;&nbsp;The string to use for 'glueing' the array elements together; \t\n\r\b\f 
 * &nbsp;&nbsp;&nbsp;get automatically converted into their character counterparts.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-max-length &lt;int&gt; (property: maxLength)
 * &nbsp;&nbsp;&nbsp;The maximum length the joined string should not exceed; use -1 for unlimited 
 * &nbsp;&nbsp;&nbsp;length.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringJoin
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -5524254075032084295L;

  /** the string to use for glueing the strings together. */
  protected String m_Glue;
  
  /** the maximum string length to allow. */
  protected int m_MaxLength;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Creates a single string out of an array of strings.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "glue", "glue",
	    "");

    m_OptionManager.add(
	    "max-length", "maxLength",
	    -1, -1, null);
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;
    
    result = QuickInfoHelper.toString(this, "glue", (m_Glue.length() > 0 ? Utils.backQuoteChars(m_Glue) : null), "glue: ");
    value = QuickInfoHelper.toString(this, "maxLength", (m_MaxLength > 0 ? m_MaxLength : null), ", max: ");
    if (value != null) {
      if (result == null)
	result = value;
      else
	result += value;
    }
    value = QuickInfoHelper.toString(this, "outputArray", m_OutputArray, "as array", ", ");
    if (value != null) {
      if (result == null)
	result = value;
      else
	result += value;
    }
    
    return result;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the strings are output as an array rather than one-by-one.";
  }

  /**
   * Sets the string to use as 'glue' between the array elements.
   *
   * @param value	the string
   */
  public void setGlue(String value) {
    m_Glue = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the string used as 'glue' between the array elements.
   *
   * @return		the string
   */
  public String getGlue() {
    return Utils.backQuoteChars(m_Glue);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String glueTipText() {
    return
        "The string to use for 'glueing' the array elements together; \\t\\n\\r\\b\\f get "
      + "automatically converted into their character counterparts.";
  }

  /**
   * Sets the maximum length for the generated string(s).
   *
   * @param value	the maximum length
   */
  public void setMaxLength(int value) {
    if (value >= -1) {
      m_MaxLength = value;
      reset();
    }
    else {
      getLogger().warning("Maximum length must be -1 (unlimited) or greater, provided: " + value);
    }
  }

  /**
   * Returns the maximum length for the generated string(s).
   *
   * @return		the maximum length
   */
  public int getMaxLength() {
    return m_MaxLength;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxLengthTipText() {
    return "The maximum length the joined string should not exceed; use -1 for unlimited length.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String[].class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return String.class;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    StringBuilder	str;
    String[]		array;
    int			i;

    result = null;

    try {
      array = (String[]) m_InputToken.getPayload();
      str = new StringBuilder();
      for (i = 0; i < array.length; i++) {
	if ((m_MaxLength > -1) && (str.length() > 0)) {
	  if (str.length() + m_Glue.length() + array[i].length() > m_MaxLength) {
	    m_Queue.add(str.toString());
	    str.delete(0, str.length());
	  }
	}
	if (str.length() > 0)
	  str.append(m_Glue);
	str.append(array[i]);
      }
      m_Queue.add(str.toString());
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to join strings: ", e);
    }

    return result;
  }
}
