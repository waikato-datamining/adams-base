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
 * DOMToProperties.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import adams.core.Properties;

/**
 <!-- globalinfo-start -->
 * Flattens a DOM object (or node) into a Properties object.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-path-separator &lt;java.lang.String&gt; (property: pathSeparator)
 * &nbsp;&nbsp;&nbsp;The separator to use in the path.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 * 
 * <pre>-add-index &lt;boolean&gt; (property: addIndex)
 * &nbsp;&nbsp;&nbsp;If enabled, the index gets added to the path, to disambiguate.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-store-attributes &lt;boolean&gt; (property: storeAttributes)
 * &nbsp;&nbsp;&nbsp;If enabled, attribute values get stored as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-skip-root &lt;boolean&gt; (property: skipRoot)
 * &nbsp;&nbsp;&nbsp;If enabled, the root element name is excluded from the path.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DOMToProperties
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 6744245717394758406L;

  /** the path separator. */
  protected String m_PathSeparator;
  
  /** whether to add index to path. */
  protected boolean m_AddIndex;
  
  /** whether to store attributes of elements as well. */
  protected boolean m_StoreAttributes;
  
  /** whether to exclude the root element from the path. */
  protected boolean m_SkipRoot;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Flattens a DOM object (or node) into a Properties object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "path-separator", "pathSeparator",
	    ".");

    m_OptionManager.add(
	    "add-index", "addIndex",
	    false);

    m_OptionManager.add(
	    "store-attributes", "storeAttributes",
	    false);

    m_OptionManager.add(
	    "skip-root", "skipRoot",
	    false);
  }

  /**
   * Sets the separator in use.
   *
   * @param value	the separator
   */
  public void setPathSeparator(String value) {
    m_PathSeparator = value;
    reset();
  }

  /**
   * Returns the separator in use.
   *
   * @return 		the separator
   */
  public String getPathSeparator() {
    return m_PathSeparator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pathSeparatorTipText() {
    return "The separator to use in the path.";
  }

  /**
   * Sets whether to add the index to the path to disambiguate values.
   *
   * @param value	true if to add index
   */
  public void setAddIndex(boolean value) {
    m_AddIndex = value;
    reset();
  }

  /**
   * Returns whether to add the index to the path to disambiguate values.
   *
   * @return 		true if to add index
   */
  public boolean getAddIndex() {
    return m_AddIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addIndexTipText() {
    return "If enabled, the index gets added to the path, to disambiguate.";
  }

  /**
   * Sets whether to store the attributes as well.
   *
   * @param value	true if to store attributes
   */
  public void setStoreAttributes(boolean value) {
    m_StoreAttributes = value;
    reset();
  }

  /**
   * Returns whether to store the attributes as well.
   *
   * @return 		true if attributes are stored as well
   */
  public boolean getStoreAttributes() {
    return m_StoreAttributes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storeAttributesTipText() {
    return "If enabled, attribute values get stored as well.";
  }

  /**
   * Sets whether to exclude the root element name from the path.
   *
   * @param value	true if to exclude the root
   */
  public void setSkipRoot(boolean value) {
    m_SkipRoot = value;
    reset();
  }

  /**
   * Returns whether to exclude the root element name from the path.
   *
   * @return 		true if root excluded
   */
  public boolean getSkipRoot() {
    return m_SkipRoot;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipRootTipText() {
    return "If enabled, the root element name is excluded from the path.";
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
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return java.util.Properties.class;
  }

  /**
   * Returns whether the node has any text content or just other nested nodes.
   * 
   * @param node	the node to test
   * @return		true if actual textual content available
   */
  protected boolean hasTextContent(Node node) {
    boolean	result;
    int		i;
    
    result = (node.getChildNodes().getLength() > 0);
    
    for (i = 0; i < node.getChildNodes().getLength(); i++) {
      if (node.getChildNodes().item(i).getNodeType() != Node.TEXT_NODE) {
	result = false;
	break;
      }
    }
    
    return result;
  }
  
  /**
   * Flattens the node structure.
   * 
   * @param path	the current path, null for root element
   * @param node	the current node
   * @param index	the index of the node
   * @param props	the properties to store the data in
   * @return		true if node was added
   */
  protected boolean flatten(String path, Node node, int index, Properties props) {
    int		i;
    int		count;
    String	content;
    
    if (node.getNodeType() == Node.TEXT_NODE)
      return false;
    
    if (path == null) {
      if (m_SkipRoot)
	path = "";
      else
	path = node.getNodeName();
    }
    else {
      if (path.length() > 0)
	path += m_PathSeparator;
      path += (m_AddIndex ? "[" + (index+1) + "]-" : "") + node.getNodeName();
    }
    
    // node value
    if (hasTextContent(node)) {
      try {
	content = node.getTextContent();
	if (content != null)
	  props.setProperty(path, content);
      }
      catch (DOMException e) {
	// ignored
      }
    }
    
    // node attributes
    if (m_StoreAttributes && node.hasAttributes()) {
      for (i = 0; i < node.getAttributes().getLength(); i++) {
	props.setProperty(
	    path + m_PathSeparator + node.getAttributes().item(i).getNodeName(), 
	    node.getAttributes().item(i).getNodeValue());
      }
    }

    // recurse
    count = 0;
    for (i = 0; i < node.getChildNodes().getLength(); i++) {
      if (flatten(path, node.getChildNodes().item(i), count, props))
	count++;
    }
    
    return true;
  }
  
  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Properties			result;
    
    result = new Properties();
    flatten(null, (Node) m_Input, 0, result);
    
    return result;
  }
}
