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
 * StringInsert.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.List;

import adams.core.Index;
import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseString;

/**
 <!-- globalinfo-start -->
 * Inserts a user-specified string at a specific position into tokens coming through.<br>
 * The actor is most useful when attaching a variable to the 'value' option, which allows the value to change dynamically then.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: StringInsert
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
 * <pre>-position &lt;adams.core.Index&gt; (property: position)
 * &nbsp;&nbsp;&nbsp;The position where to insert the string; An index is a number starting with 
 * &nbsp;&nbsp;&nbsp;1; the following placeholders can be used as well: first, second, third, 
 * &nbsp;&nbsp;&nbsp;last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: last
 * </pre>
 * 
 * <pre>-after (property: after)
 * &nbsp;&nbsp;&nbsp;If enabled, the string is inserted after the position instead of at the 
 * &nbsp;&nbsp;&nbsp;position.
 * </pre>
 * 
 * <pre>-value &lt;java.lang.String&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The value to insert in the string; you can use '\t' for tab, '\n' for line-feed 
 * &nbsp;&nbsp;&nbsp;and '\r' for carriage-return.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-placeholder (property: valueContainsPlaceholder)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic placeholder expansion for the value 
 * &nbsp;&nbsp;&nbsp;string.
 * </pre>
 * 
 * <pre>-variable (property: valueContainsVariable)
 * &nbsp;&nbsp;&nbsp;Set this to true to enable automatic variable expansion for the value string.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringInsert
  extends AbstractStringOperation {

  /** for serialization. */
  private static final long serialVersionUID = 9030574317512531337L;
  
  /** the position where to insert the string. */
  protected Index m_Position;  
  
  /** whether to insert after the position instead of at. */
  protected boolean m_After;
  
  /** the value to insert. */
  protected BaseString m_Value;

  /** whether the value string contains a placeholder, which needs to be
   * expanded first. */
  protected boolean m_ValueContainsPlaceholder;

  /** whether the value string contains a variable, which needs to be
   * expanded first. */
  protected boolean m_ValueContainsVariable;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Inserts a user-specified string at a specific position into tokens "
	+ "coming through.\n"
	+ "The actor is most useful when attaching a variable to the 'value' "
	+ "option, which allows the value to change dynamically then.";
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Position = new Index();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "position", "position",
	    new Index(Index.LAST));

    m_OptionManager.add(
	    "after", "after",
	    false);

    m_OptionManager.add(
	    "value", "value",
	    new BaseString(""));

    m_OptionManager.add(
	    "placeholder", "valueContainsPlaceholder",
	    false);

    m_OptionManager.add(
	    "variable", "valueContainsVariable",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    if (QuickInfoHelper.hasVariable(this, "after"))
      result = QuickInfoHelper.getVariable(this, "after") + ": ";
    else if (m_After)
      result = "after: ";
    else
      result = "at: ";
    result += QuickInfoHelper.toString(this, "position", m_Position);
    result += QuickInfoHelper.toString(this, "value", "'" + getValue() + "'", ", insert: ");

    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "valueContainsPlaceholder", m_ValueContainsPlaceholder, "PH"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "valueContainsVariable", m_ValueContainsVariable, "Var"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the position where to insert the string.
   *
   * @param value	the position
   */
  public void setPosition(Index value) {
    m_Position = value;
    reset();
  }

  /**
   * Returns the position where to insert the string.
   *
   * @return		the position
   */
  public Index getPosition() {
    return m_Position;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String positionTipText() {
    return
        "The position where to insert the string.";
  }

  /**
   * Sets whether to insert at or after the position.
   *
   * @param value	true if to add after
   */
  public void setAfter(boolean value) {
    m_After = value;
    reset();
  }

  /**
   * Returns whether to insert at or after the position.
   *
   * @return		true if to add after
   */
  public boolean getAfter() {
    return m_After;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String afterTipText() {
    return
        "If enabled, the string is inserted after the position instead of at "
	+ "the position.";
  }

  /**
   * Sets the value to insert.
   *
   * @param value	the value
   */
  public void setValue(BaseString value) {
    m_Value = new BaseString(Utils.unbackQuoteChars(value.getValue()));
    reset();
  }

  /**
   * Returns the value to insert.
   *
   * @return		the value
   */
  public BaseString getValue() {
    return new BaseString(Utils.backQuoteChars(m_Value.getValue()));
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The value to insert in the string; you can use '\\t' for tab, '\\n' for line-feed and '\\r' for carriage-return.";
  }

  /**
   * Sets whether the value string contains a placeholder which needs to be
   * expanded first.
   *
   * @param value	true if value string contains a placeholder
   */
  public void setValueContainsPlaceholder(boolean value) {
    m_ValueContainsPlaceholder = value;
    reset();
  }

  /**
   * Returns whether the vaue string contains a placeholder which needs to be
   * expanded first.
   *
   * @return		true if value string contains a placeholder
   */
  public boolean getValueContainsPlaceholder() {
    return m_ValueContainsPlaceholder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueContainsPlaceholderTipText() {
    return "Set this to true to enable automatic placeholder expansion for the value string.";
  }

  /**
   * Sets whether the value string contains a variable which needs to be
   * expanded first.
   *
   * @param value	true if value string contains a variable
   */
  public void setValueContainsVariable(boolean value) {
    m_ValueContainsVariable = value;
    reset();
  }

  /**
   * Returns whether the value string contains a variable which needs to be
   * expanded first.
   *
   * @return		true if value string contains a variable
   */
  public boolean getValueContainsVariable() {
    return m_ValueContainsVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueContainsVariableTipText() {
    return "Set this to true to enable automatic variable expansion for the value string.";
  }

  /**
   * Processes the string.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  @Override
  protected String process(String s) {
    StringBuilder	result;
    int			pos;
    int			i;
    String		value;

    // do we need to expand stuff?
    value = m_Value.getValue();
    if (m_ValueContainsVariable)
      value = getVariables().expand(value);
    if (m_ValueContainsPlaceholder)
      value = Placeholders.getSingleton().expand(value).replace("\\", "/");
    
    // determine position
    if (s.length() == 0) {
      pos = 0;
    }
    else {
      m_Position.setMax(s.length());
      pos = m_Position.getIntIndex();
      if (pos == -1)
	return null;
      if (m_After)
	pos++;
    }
    
    // assemble string
    result = new StringBuilder();
    for (i = 0; (i < pos) && (i < s.length()); i++)
      result.append(s.charAt(i));
    
    result.append(value);
    
    for (i = pos; i < s.length(); i++)
      result.append(s.charAt(i));
    
    return result.toString();
  }
}
