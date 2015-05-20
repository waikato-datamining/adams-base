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
 * StringSplit.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Splits a string using a regular expression.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: StringSplit
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
 * <pre>-expression &lt;java.lang.String&gt; (property: expression)
 * &nbsp;&nbsp;&nbsp;The regular expression used for splitting the string; \t\n\r\b\f get automatically 
 * &nbsp;&nbsp;&nbsp;converted into their character counterparts.
 * &nbsp;&nbsp;&nbsp;default: \\t
 * </pre>
 * 
 * <pre>-delimiter &lt;DISCARD|APPEND|PREPEND&gt; (property: delimiter)
 * &nbsp;&nbsp;&nbsp;Defines what to do with the delimiters (= expression).
 * &nbsp;&nbsp;&nbsp;default: DISCARD
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringSplit
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -3687113148170774846L;

  /**
   * Defines the action what to do with the delimiter.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Delimiter {
    /** discards the delimiters. */
    DISCARD,
    /** appends the delimiters. */
    APPEND,
    /** prepends the delimiters. */
    PREPEND
  }

  /** the regular expression to use for splitting the string. */
  protected BaseRegExp m_Expression;

  /** what to do with the delimiters. */
  protected Delimiter m_Delimiter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits a string using a regular expression.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "expression", "expression",
	    "\\t");

    m_OptionManager.add(
	    "delimiter", "delimiter",
	    Delimiter.DISCARD);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Expression = new BaseRegExp();
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "expression", m_Expression);
    if (result != null)
      result += " (" + QuickInfoHelper.toString(this, "delimiter", m_Delimiter) + ")";

    return result;
  }

  /**
   * Sets the regular expression used for splitting the string.
   *
   * @param value	the expression
   */
  public void setExpression(String value) {
    m_Expression.setValue(Utils.unbackQuoteChars(value));
    reset();
  }

  /**
   * Returns the regular expression for splitting the string.
   *
   * @return		the expression
   */
  public String getExpression() {
    return Utils.backQuoteChars(m_Expression.getValue());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expressionTipText() {
    return
        "The regular expression used for splitting the string; \\t\\n\\r\\b\\f get "
      + "automatically converted into their character counterparts.";
  }

  /**
   * Sets what to do with the delimiter (= expression).
   *
   * @param value	the action
   */
  public void setDelimiter(Delimiter value) {
    m_Delimiter = value;
    reset();
  }

  /**
   * Returns what to do with the delimiter (= expression).
   *
   * @return		the action
   */
  public Delimiter getDelimiter() {
    return m_Delimiter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String delimiterTipText() {
    return "Defines what to do with the delimiters (= expression).";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String[].class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String	str;
    String[]	array;
    int		i;

    result = null;

    try {
      str   = (String) m_InputToken.getPayload();
      array = str.split(m_Expression.getValue());
      switch (m_Delimiter) {
	case DISCARD:
	  // don't do anything
	  break;

	case APPEND:
	  for (i = 0; i < array.length - 1; i++)
	    array[i] = array[i] + m_Expression.getValue();
      	  break;

	case PREPEND:
	  for (i = 1; i < array.length; i++)
	    array[i] = m_Expression.getValue() + array[i];
    	  break;

	default:
	  throw new IllegalStateException("Unhandled delimiter action: " + m_Delimiter);
      }
      m_OutputToken = new Token(array);
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to split string:", e);
    }

    return result;
  }
}
