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
 * AddDOMNode.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Adds a new node below the incoming org.w3c.dom.Node and forwards the new node.<br>
 * In case of an incoming org.w3c.dom.Document, the node gets added below the root node.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;org.w3c.dom.Document<br>
 * &nbsp;&nbsp;&nbsp;org.w3c.dom.Node<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;org.w3c.dom.Node<br>
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
 * &nbsp;&nbsp;&nbsp;default: AddDOMNode
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
 * <pre>-node-name &lt;java.lang.String&gt; (property: nodeName)
 * &nbsp;&nbsp;&nbsp;The name of the node to add.
 * &nbsp;&nbsp;&nbsp;default: node
 * </pre>
 * 
 * <pre>-has-value &lt;boolean&gt; (property: hasValue)
 * &nbsp;&nbsp;&nbsp;If enabled, the node gets the specified textual value.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-node-value &lt;java.lang.String&gt; (property: nodeValue)
 * &nbsp;&nbsp;&nbsp;The value of the node to add; new lines, carriage returns or tabs can be 
 * &nbsp;&nbsp;&nbsp;escaped like '\n', '\r' or '\t'.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8036 $
 */
public class AddDOMNode
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 665475246547884486L;
  
  /** the name of the node to add. */
  protected String m_NodeName;
  
  /** whether the node has a (textual) value. */
  protected boolean m_HasValue;
  
  /** the node value. */
  protected String m_NodeValue;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Adds a new node below the incoming " + Node.class.getName() + " and "
	+ "forwards the new node.\n"
	+ "In case of an incoming " + Document.class.getName() + ", the node "
	+ "gets added below the root node.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "node-name", "nodeName",
	    "node");

    m_OptionManager.add(
	    "has-value", "hasValue",
	    false);

    m_OptionManager.add(
	    "node-value", "nodeValue",
	    "");
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
    
    result  = QuickInfoHelper.toString(this, "nodeName", m_NodeName, "name: ");
    result += QuickInfoHelper.toString(this, "hasValue", m_HasValue, "with value", ", ");
    value   = QuickInfoHelper.toString(this, "nodeValue", m_NodeValue, ", value: ");
    if (value != null)
      result += value;
    
    return result;
  }

  /**
   * Sets the name of the node to add.
   *
   * @param value	the name
   */
  public void setNodeName(String value) {
    m_NodeName = value;
    reset();
  }

  /**
   * Returns the name of the node to add.
   *
   * @return		the name
   */
  public String getNodeName() {
    return m_NodeName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nodeNameTipText() {
    return "The name of the node to add.";
  }

  /**
   * Sets whether the node has a textual value.
   *
   * @param value	true if textual value
   */
  public void setHasValue(boolean value) {
    m_HasValue = value;
    reset();
  }

  /**
   * Returns whether the node has a textual value.
   *
   * @return		true if textual value
   */
  public boolean getHasValue() {
    return m_HasValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hasValueTipText() {
    return "If enabled, the node gets the specified textual value.";
  }

  /**
   * Sets the value of the node to add.
   *
   * @param value	the value
   */
  public void setNodeValue(String value) {
    m_NodeValue = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the value of the node to add.
   *
   * @return		the value
   */
  public String getNodeValue() {
    return Utils.backQuoteChars(m_NodeValue);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nodeValueTipText() {
    return "The value of the node to add; new lines, carriage returns or tabs can be escaped like '\\n', '\\r' or '\\t'.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Document.class, Node.class};
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
    Document		doc;
    Node		node;
    Node		child;
    Node		text;
    
    result = null;

    node  = null;
    child = null;
    
    if (m_InputToken.getPayload() instanceof Document) {
      doc  = (Document) m_InputToken.getPayload();
      node = doc.getDocumentElement();
      if (node == null) {
	child = doc.createElement(m_NodeName);
	doc.appendChild(child);
      }
    }
    
    if (child == null) {
      node  = (Node) m_InputToken.getPayload();
      doc   = node.getOwnerDocument();
      child = node.getOwnerDocument().createElement(m_NodeName);
      node.appendChild(child);
    }

    if (m_HasValue) {
      text = node.getOwnerDocument().createTextNode(m_NodeValue);
      child.appendChild(text);
    }
    
    m_OutputToken = new Token(child);
    
    return result;
  }
}
