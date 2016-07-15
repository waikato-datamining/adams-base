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
 * DOMToString.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseCharset;
import adams.core.io.PrettyPrintingSupporter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringWriter;

/**
 <!-- globalinfo-start -->
 * Turns a org.w3c.dom.Node or org.w3c.dom.Document DOM object into a String.<br>
 * In case of org.w3c.dom.Node objects, the owner document is converted to String.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use for the string.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 * 
 * <pre>-pretty-printing &lt;boolean&gt; (property: prettyPrinting)
 * &nbsp;&nbsp;&nbsp;If enabled, the XML is output in pretty-print format.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DOMToString
  extends AbstractConversionToString
  implements PrettyPrintingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 6744245717394758406L;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** whether to use pretty printing. */
  protected boolean m_PrettyPrinting;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Turns a " + Node.class.getName() + " or " 
	+ Document.class.getName() + " DOM object into a String.\n"
	+ "In case of " + Node.class.getName() + " objects, the owner "
	+ "document is converted to String.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());

    m_OptionManager.add(
      "pretty-printing", "prettyPrinting",
      false);
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
    return "The type of encoding to use for the string.";
  }

  /**
   * Sets whether to use pretty-printing or not.
   *
   * @param value	true if to use pretty-printing
   */
  public void setPrettyPrinting(boolean value) {
    m_PrettyPrinting = value;
    reset();
  }

  /**
   * Returns whether pretty-printing is used or not.
   *
   * @return		true if to use pretty-printing
   */
  public boolean getPrettyPrinting() {
    return m_PrettyPrinting;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prettyPrintingTipText() {
    return "If enabled, the XML is output in pretty-print format.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "encoding", m_Encoding, "encoding: ");
    result += QuickInfoHelper.toString(this, "prettyPrinting", (m_PrettyPrinting ? "pretty" : "not-so-pretty"), ", ");

    return result;
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Node.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Document			doc;
    XPath 			xPath;
    NodeList 			nodeList;
    int 			i;
    Node 			node;
    StringWriter 		swriter;
    TransformerFactory 		factory;
    Transformer 		transformer;
    
    if (m_Input instanceof Document)
      doc = (Document) m_Input;
    else
      doc = ((Node) m_Input).getOwnerDocument();

    synchronized(doc) {
      if (m_PrettyPrinting) {
	doc = (Document) doc.cloneNode(true);
	xPath = XPathFactory.newInstance().newXPath();
	nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", doc, XPathConstants.NODESET);
	for (i = 0; i < nodeList.getLength(); ++i) {
	  node = nodeList.item(i);
	  node.getParentNode().removeChild(node);
	}
      }

      factory = TransformerFactory.newInstance();
      if (m_PrettyPrinting)
	factory.setAttribute("indent-number", 2);
      transformer = factory.newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, m_Encoding.charsetValue().toString());
      if (m_PrettyPrinting)
	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      swriter = new StringWriter();
      transformer.transform(new DOMSource(doc), new StreamResult(swriter));
    }

    return swriter.toString();
  }
}
