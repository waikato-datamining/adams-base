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
 * XMLFileWriter.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseCharset;
import adams.core.io.FileUtils;
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
 * Writes a org.w3c.dom.Document to an XML file.<br>
 * In case of org.w3c.dom.Node objects, the owning document is written to disk.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;org.w3c.dom.Document<br>
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
 * &nbsp;&nbsp;&nbsp;default: XMLFileWriter
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The name of the output file.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when writing to the file.
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
public class XMLFileWriter
  extends AbstractFileWriter
  implements PrettyPrintingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 3613018887562327088L;

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
      "Writes a " + Document.class.getName() + " to an XML file.\n"
	+ "In case of " + Node.class.getName() + " objects, the owning "
	+ "document is written to disk.";
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
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The name of the output file.";
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
    return "The type of encoding to use when writing to the file.";
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

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "encoding", m_Encoding, ", encoding: ");
    result += QuickInfoHelper.toString(this, "prettyPrinting", (m_PrettyPrinting ? "pretty" : "not-so-pretty"), ", ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->org.w3c.dom.Document.class, org.w3c.dom.Node.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Document.class, Node.class};
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
    XPath 		xPath;
    NodeList 		nodeList;
    int 		i;
    Node 		node;
    TransformerFactory 	factory;
    Transformer 	transformer;
    StringWriter 	swriter;

    result = null;

    try {
      if (m_InputToken.getPayload() instanceof Document)
	doc = (Document) m_InputToken.getPayload();
      else
	doc = ((Node) m_InputToken.getPayload()).getOwnerDocument();

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
	result = FileUtils.writeToFileMsg(m_OutputFile.getAbsolutePath(), swriter.toString(), false, m_Encoding.charsetValue().name());
      }
    }
    catch (Exception e) {
      result = handleException("Failed to write DOM document to " + m_OutputFile, e);
    }

    return result;
  }
}
