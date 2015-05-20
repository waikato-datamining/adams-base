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
 * XMLToDOM.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

/**
 <!-- globalinfo-start -->
 * Turns an XML string into a org.w3c.dom.Document DOM object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-validating &lt;boolean&gt; (property: validating)
 * &nbsp;&nbsp;&nbsp;If enabled, the parser will validate the XML.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-name-space-aware &lt;boolean&gt; (property: nameSpaceAware)
 * &nbsp;&nbsp;&nbsp;If enabled, the parser will be namespace aware.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-x-include-aware &lt;boolean&gt; (property: XIncludeAware)
 * &nbsp;&nbsp;&nbsp;If enabled, the parser will be X-include aware.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-expand-entity-references &lt;boolean&gt; (property: expandEntityReferences)
 * &nbsp;&nbsp;&nbsp;If enabled, the parser will expand entity references.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-ignoring-comments &lt;boolean&gt; (property: ignoringComments)
 * &nbsp;&nbsp;&nbsp;If enabled, the parser will ignore comments.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-coalescing &lt;boolean&gt; (property: coalescing)
 * &nbsp;&nbsp;&nbsp;If enabled, then parser will convert CDATA nodes to Text nodes and append 
 * &nbsp;&nbsp;&nbsp;it to the adjacent (if any) text node.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-ignoring-whitespace &lt;boolean&gt; (property: ignoringWhitespace)
 * &nbsp;&nbsp;&nbsp;If enabled, the parser will ignore whitespaces in element content.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XMLToDOM
  extends AbstractConversionFromString {

  /** for serialization. */
  private static final long serialVersionUID = -7337362020786223426L;

  /** whether the parser is validating or not. */
  protected boolean m_Validating;

  /** whether the parser is namespace aware. */
  protected boolean m_NameSpaceAware;

  /** Set state of XInclude processing.*/
  protected boolean m_XIncludeAware;
  
  /** Specifies that the parser produced by this code will expand entity reference nodes. */
  protected boolean m_ExpandEntityReferences;
  
  /** Specifies that the parser produced by this code will ignore comments. */
  protected boolean m_IgnoringComments;
  
  /** Specifies that the parser produced by this code will convert CDATA nodes 
   * to Text nodes and append it to the adjacent (if any) text node. */
  protected boolean m_Coalescing;
  
  /** Specifies that the parsers created by this factory must eliminate whitespace in element content.*/
  protected boolean m_IgnoringWhitespace;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns an XML string into a " + Document.class.getName() + " DOM object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "validating", "validating",
	    false);

    m_OptionManager.add(
	    "name-space-aware", "nameSpaceAware",
	    false);

    m_OptionManager.add(
	    "x-include-aware", "XIncludeAware",
	    false);

    m_OptionManager.add(
	    "expand-entity-references", "expandEntityReferences",
	    false);

    m_OptionManager.add(
	    "ignoring-comments", "ignoringComments",
	    false);

    m_OptionManager.add(
	    "coalescing", "coalescing",
	    false);

    m_OptionManager.add(
	    "ignoring-whitespace", "ignoringWhitespace",
	    false);
  }

  /**
   * Sets whether to use a validating parser.
   *
   * @param value	true if to use validating parser
   */
  public void setValidating(boolean value) {
    m_Validating = value;
    reset();
  }

  /**
   * Returns whether a validating parser is used.
   *
   * @return 		true if validating parser
   */
  public boolean getValidating() {
    return m_Validating;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String validatingTipText() {
    return "If enabled, the parser will validate the XML.";
  }

  /**
   * Sets whether to use a namespace aware parser.
   *
   * @param value	true if to use namespace aware parser
   */
  public void setNameSpaceAware(boolean value) {
    m_NameSpaceAware = value;
    reset();
  }

  /**
   * Returns whether a namespace aware parser used.
   *
   * @return 		true if namespace aware
   */
  public boolean getNameSpaceAware() {
    return m_NameSpaceAware;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nameSpaceAwareTipText() {
    return "If enabled, the parser will be namespace aware.";
  }

  /**
   * Sets whether to use a X-include aware parser.
   *
   * @param value	true if to use X-include aware parser
   */
  public void setXIncludeAware(boolean value) {
    m_XIncludeAware = value;
    reset();
  }

  /**
   * Returns whether a X-include aware parser is used.
   *
   * @return 		true if X-include aware parser
   */
  public boolean getXIncludeAware() {
    return m_XIncludeAware;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XIncludeAwareTipText() {
    return "If enabled, the parser will be X-include aware.";
  }

  /**
   * Sets whether to expand entity references.
   *
   * @param value	true if to expand entity references
   */
  public void setExpandEntityReferences(boolean value) {
    m_ExpandEntityReferences = value;
    reset();
  }

  /**
   * Returns whether a parser expands entity references.
   *
   * @return 		true if parser expands entity references
   */
  public boolean getExpandEntityReferences() {
    return m_ExpandEntityReferences;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expandEntityReferencesTipText() {
    return "If enabled, the parser will expand entity references.";
  }

  /**
   * Sets whether to ignore comments.
   *
   * @param value	true if to ignore comments
   */
  public void setIgnoringComments(boolean value) {
    m_IgnoringComments = value;
    reset();
  }

  /**
   * Returns whether parser ignores comments
   *
   * @return 		true if comments ignored
   */
  public boolean getIgnoringComments() {
    return m_IgnoringComments;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String ignoringCommentsTipText() {
    return "If enabled, the parser will ignore comments.";
  }

  /**
   * Sets whether to use a coalescing parser.
   *
   * @param value	true if to use coalescing parser
   */
  public void setCoalescing(boolean value) {
    m_Coalescing = value;
    reset();
  }

  /**
   * Returns whether a coalescing parser is used.
   *
   * @return 		true if coalescing parser
   */
  public boolean getCoalescing() {
    return m_Coalescing;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String coalescingTipText() {
    return "If enabled, then parser will convert CDATA nodes to Text nodes and append it to the adjacent (if any) text node.";
  }

  /**
   * Sets whether to ignore whitespaces in element content.
   *
   * @param value	true if to ignore whitespaces in element content
   */
  public void setIgnoringWhitespace(boolean value) {
    m_IgnoringWhitespace = value;
    reset();
  }

  /**
   * Returns whether parser ignores whitespaces in element content.
   *
   * @return 		true if whitespaces in element content ignored
   */
  public boolean getIgnoringWhitespace() {
    return m_IgnoringWhitespace;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String ignoringWhitespaceTipText() {
    return "If enabled, the parser will ignore whitespaces in element content.";
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Document.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    String			input;
    DocumentBuilderFactory 	factory;
    DocumentBuilder 		builder;
    Document 			doc;
    
    try {
      input = (String) m_Input;
      factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(m_Validating);
      factory.setNamespaceAware(m_NameSpaceAware);
      factory.setXIncludeAware(m_XIncludeAware);
      factory.setExpandEntityReferences(m_ExpandEntityReferences);
      factory.setIgnoringComments(m_IgnoringComments);
      factory.setIgnoringElementContentWhitespace(m_IgnoringWhitespace);
      builder = factory.newDocumentBuilder();
      doc     = builder.parse(new ByteArrayInputStream(input.getBytes()));
    }
    catch (Exception e) {
      throw new Exception("Failed to parse: " + m_Input, e);
    }
    
    return doc;
  }
}
