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
 * AddDOMAttribute.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Adds a new attribute to the incoming org.w3c.dom.Node and forwards the new updated node.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;org.w3c.dom.Node<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;org.w3c.dom.Node<br/>
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
 * &nbsp;&nbsp;&nbsp;default: AddDOMAttribute
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
 * <pre>-attribute &lt;java.lang.String&gt; (property: attribute)
 * &nbsp;&nbsp;&nbsp;The name of the attribute to add.
 * &nbsp;&nbsp;&nbsp;default: name
 * </pre>
 * 
 * <pre>-value &lt;java.lang.String&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The value of the attribute to add.
 * &nbsp;&nbsp;&nbsp;default: value
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8036 $
 */
public class AddDOMAttribute
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 665475246547884486L;
  
  /** the name of the attribute to add. */
  protected String m_Attribute;
  
  /** the value of the attribute to add. */
  protected String m_Value;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Adds a new attribute to the incoming " + Node.class.getName() + " and "
	+ "forwards the new updated node.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "attribute", "attribute",
	    "name");

    m_OptionManager.add(
	    "value", "value",
	    "value");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "attribute", m_Attribute, "att: ");
    result += QuickInfoHelper.toString(this, "value", m_Value, ", value: ");
    
    return result;
  }

  /**
   * Sets the name of the attribute to add.
   *
   * @param value	the name
   */
  public void setAttribute(String value) {
    m_Attribute = value;
    reset();
  }

  /**
   * Returns the name of the attribute to add.
   *
   * @return		the name
   */
  public String getAttribute() {
    return m_Attribute;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeTipText() {
    return "The name of the attribute to add.";
  }

  /**
   * Sets the value of the attribute to add.
   *
   * @param value	the value
   */
  public void setValue(String value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the value of the attribute to add.
   *
   * @return		the value
   */
  public String getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The value of the attribute to add.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Node.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Node.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Node		node;
    
    result = null;

    node = (Node) m_InputToken.getPayload();
    if (node instanceof Element)
      ((Element) node).setAttribute(m_Attribute, m_Value);
    else
      result = "Cannot add attribute: " + node.getClass().getName() + "/" + node;
    
    m_OutputToken = new Token(node);
    
    return result;
  }
}
