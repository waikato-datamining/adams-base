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
 * GetPropertyNames.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;

/**
 <!-- globalinfo-start -->
 * Outputs all the keys of the properties object passing through.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.util.Properties<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: GetPropertyNames
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
 * &nbsp;&nbsp;&nbsp;If enabled, the keys will get output as array rather than one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-reg-exp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the property names must match in order to be 
 * &nbsp;&nbsp;&nbsp;listed.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 * <pre>-sort-keys &lt;boolean&gt; (property: sortKeys)
 * &nbsp;&nbsp;&nbsp;If enabled, the keys will get sorted.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GetPropertyNames
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -8757919765508522198L;

  /** the regular expression that the property names must match. */
  protected BaseRegExp m_RegExp;
  
  /** whether to sort the keys. */
  protected boolean m_SortKeys;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reg-exp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "sort-keys", "sortKeys",
	    false);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs all the keys of the properties object passing through.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the keys will get output as array rather than one-by-one.";
  }

  /**
   * Sets the regular expression that the property names must match.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression that the property names must match.
   *
   * @return 		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return
        "The regular expression that the property names must match in order to "
      + "be listed.";
  }

  /**
   * Sets whether to sort the keys.
   *
   * @param value	if true then keys get sorted
   */
  public void setSortKeys(boolean value) {
    m_SortKeys = value;
    reset();
  }

  /**
   * Returns whether to sort the keys.
   *
   * @return		true if keys get sorted
   */
  public boolean getSortKeys() {
    return m_SortKeys;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortKeysTipText() {
    return "If enabled, the keys will get sorted.";
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

    result = QuickInfoHelper.toString(this, "regExp", m_RegExp, "regexp: ");
    
    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "outputArray", (getOutputArray() ? "array" : "one-by-one")));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "sortKeys", m_SortKeys, "sort keys"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{java.util.Properties.class};
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
    String			result;
    adams.core.Properties	props;
    
    result = null;

    if (m_InputToken.getPayload() instanceof adams.core.Properties)
      props = (adams.core.Properties) m_InputToken.getPayload();
    else
      props = new adams.core.Properties((java.util.Properties) m_InputToken.getPayload());
    
    if (result == null) {
      if (m_RegExp.isMatchAll())
	m_Queue.addAll(props.keySetAll());
      else
	m_Queue.addAll(props.keySetAll(m_RegExp));
      if (m_SortKeys)
	Collections.sort(m_Queue);
    }
    
    return result;
  }
}
