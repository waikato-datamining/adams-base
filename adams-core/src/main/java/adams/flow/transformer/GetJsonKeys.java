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
 * GetJsonKeys.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;
import adams.core.QuickInfoHelper;

/**
 <!-- globalinfo-start -->
 * Outputs all the keys of the JSON Object passing through.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;org.json.simple.JSONAware<br/>
 * &nbsp;&nbsp;&nbsp;org.json.simple.JSONObject<br/>
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
 * &nbsp;&nbsp;&nbsp;default: GetJsonKeys
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
 * <pre>-output-array (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the keys will get output as array rather than one-by-one.
 * </pre>
 * 
 * <pre>-sort-keys (property: sortKeys)
 * &nbsp;&nbsp;&nbsp;If enabled, the keys will get sorted.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GetJsonKeys
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -8757919765508522198L;

  /** whether to sort the keys. */
  protected boolean m_SortKeys;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

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
    return "Outputs all the keys of the JSON Object passing through.";
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

    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "outputArray", (getOutputArray() ? "array" : "one-by-one")));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "sortKeys", m_SortKeys, "sort keys"));
    result = QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{JSONAware.class, JSONObject.class};
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
    String	result;
    JSONObject	json;
    
    result = null;
    
    json = null;
    if (!(m_InputToken.getPayload() instanceof JSONObject))
      result = "Input is not of type " + JSONObject.class.getName() + "!";
    else
      json = (JSONObject) m_InputToken.getPayload();
    
    if (result == null) {
      m_Queue.addAll(json.keySet());
      if (m_SortKeys)
	Collections.sort(m_Queue);
    }
    
    return result;
  }
}
