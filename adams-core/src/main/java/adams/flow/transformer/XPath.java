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
 * XPath.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import adams.core.QuickInfoHelper;
import adams.core.XPathResult;
import adams.core.base.BaseString;
import adams.core.base.XPathExpression;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Applies XPath to the DOM document object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;org.w3c.dom.Document<br>
 * &nbsp;&nbsp;&nbsp;org.w3c.dom.NodeList<br>
 * &nbsp;&nbsp;&nbsp;org.w3c.dom.Node<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;org.w3c.dom.NodeList<br>
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
 * &nbsp;&nbsp;&nbsp;default: XPath
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
 * <pre>-expression &lt;adams.core.base.XPathExpression&gt; (property: expression)
 * &nbsp;&nbsp;&nbsp;The XPath expression to apply to the input.
 * &nbsp;&nbsp;&nbsp;default: &#47;*
 * </pre>
 * 
 * <pre>-result-type &lt;NODESET|NODE|STRING|BOOLEAN|NUMBER&gt; (property: resultType)
 * &nbsp;&nbsp;&nbsp;The type of result that the XPath expression generates.
 * &nbsp;&nbsp;&nbsp;default: NODESET
 * </pre>
 * 
 * <pre>-name-space &lt;adams.core.base.BaseString&gt; [-name-space ...] (property: nameSpaces)
 * &nbsp;&nbsp;&nbsp;The namespaces to use as context for evaluating the expression; format: '
 * &nbsp;&nbsp;&nbsp;prefix=URI'.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XPath
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -184602726110144511L;

  /** the XPath expression to apply. */
  protected XPathExpression m_Expression;
  
  /** the type of output to generate. */
  protected XPathResult m_ResultType;
  
  /** the namespaces to use (key=value). */
  protected BaseString[] m_NameSpaces;
  
  /** the namespace context. */
  protected NamespaceContext m_NameSpaceContext;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Applies XPath to the DOM document object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "expression", "expression",
	    new XPathExpression("/*"));

    m_OptionManager.add(
	    "result-type", "resultType",
	    XPathResult.NODESET);
    
    m_OptionManager.add(
	    "name-space", "nameSpaces",
	    new BaseString[0]);
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_NameSpaceContext = null;
  }
  
  /**
   * Sets the expression to apply.
   *
   * @param value	the type
   */
  public void setExpression(XPathExpression value) {
    m_Expression = value;
    reset();
  }

  /**
   * Returns the expression to apply.
   *
   * @return		the type
   */
  public XPathExpression getExpression() {
    return m_Expression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expressionTipText() {
    return "The XPath expression to apply to the input.";
  }

  /**
   * Sets the result type that the expression generates.
   *
   * @param value	the type
   */
  public void setResultType(XPathResult value) {
    m_ResultType = value;
    reset();
  }

  /**
   * Returns the result type that the expression generates.
   *
   * @return		the type
   */
  public XPathResult getResultType() {
    return m_ResultType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String resultTypeTipText() {
    return "The type of result that the XPath expression generates.";
  }

  /**
   * Sets the namespaces to use. format: name=url
   *
   * @param value	the namespaces in use
   */
  public void setNameSpaces(BaseString[] value) {
    m_NameSpaces = value;
    reset();
  }

  /**
   * Returns the namespaces in use. format: name=url
   *
   * @return 		the namespaces
   */
  public BaseString[] getNameSpaces() {
    return m_NameSpaces;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nameSpacesTipText() {
    return "The namespaces to use as context for evaluating the expression; format: 'prefix=URI'.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "expression", m_Expression, "expr: ");
    result += QuickInfoHelper.toString(this, "resultType", m_ResultType, ", type: ");
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->org.w3c.dom.Document.class, org.w3c.dom.NodeList.class, org.w3c.dom.Node.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Document.class, NodeList.class, Node.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->org.w3c.dom.NodeList.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    switch (m_ResultType) {
      case BOOLEAN:
	return new Class[]{Boolean.class};
      case STRING:
	return new Class[]{String.class};
      case NUMBER:
	return new Class[]{Double.class};
      case NODE:
	return new Class[]{Node.class};
      case NODESET:
	return new Class[]{NodeList.class};
      default:
	throw new IllegalStateException("Unhandled result type: " + m_ResultType);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    javax.xml.xpath.XPath 		xpath;
    Object				eval;
    int					pos;
    String[] 				parts;
    final HashMap<String,String>	table;
    final HashMap<String,String>	tableReverse;

    result = null;

    try {
      xpath = XPathFactory.newInstance().newXPath();

      // namespaces?
      if (m_NameSpaces.length > 0) {
	if (m_NameSpaceContext == null) {
	  table        = new HashMap<String,String>();
	  tableReverse = new HashMap<String,String>();
	  for (BaseString s: m_NameSpaces) {
	    pos = s.getValue().indexOf('=');
	    if (pos > -1) {
	      parts    = new String[2];
	      parts[0] = s.getValue().substring(0, pos);
	      parts[1] = s.getValue().substring(pos + 1);
	      table.put(parts[0], parts[1]);
	      tableReverse.put(parts[1], parts[0]);
	    }
	    else {
	      getLogger().severe("Invalid namespace format! Expected 'name=url', found: " + s.getValue());
	    }
	  }

	  m_NameSpaceContext = new NamespaceContext() {
	    @Override
	    public String getNamespaceURI(String prefix) {
	      return table.get(prefix);
	    }
	    @Override
	    public String getPrefix(String namespaceURI) {
	      return tableReverse.get(namespaceURI);
	    }
	    @Override
	    public Iterator getPrefixes(String namespaceURI) {
	      HashSet set = new HashSet();
	      set.add(tableReverse.get(namespaceURI));
	      return set.iterator();
	    }
	  };
	}
	
	xpath.setNamespaceContext(m_NameSpaceContext);
      }
      
      eval = xpath.evaluate(m_Expression.getValue(), m_InputToken.getPayload(), m_ResultType.getQName());
      
      m_OutputToken = new Token(eval);
    }
    catch (Exception e) {
      result = handleException("Failed to apply XPath expression: " + m_Expression, e);
    }

    return result;
  }
}
