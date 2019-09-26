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
 * DOMUtils.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.xml;

import adams.core.Properties;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/**
 * Helper class for DOM operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DOMUtils {

  /**
   * Returns whether the node has any text content or just other nested nodes.
   *
   * @param node	the node to test
   * @return		true if actual textual content available
   */
  protected static boolean hasTextContent(Node node) {
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
  protected static boolean flatten(String pathSeparator, boolean addIndex, boolean storeAttributes, boolean skipRoot, String path, Node node, int index, Properties props) {
    int		i;
    int		count;
    String	content;

    if (node.getNodeType() == Node.TEXT_NODE)
      return false;

    if (path == null) {
      if (skipRoot)
	path = "";
      else
	path = node.getNodeName();
    }
    else {
      if (path.length() > 0)
	path += pathSeparator;
      path += (addIndex ? "[" + (index+1) + "]-" : "") + node.getNodeName();
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
    if (storeAttributes && node.hasAttributes()) {
      for (i = 0; i < node.getAttributes().getLength(); i++) {
	props.setProperty(
	    path + pathSeparator + node.getAttributes().item(i).getNodeName(),
	    node.getAttributes().item(i).getNodeValue());
      }
    }

    // recurse
    count = 0;
    for (i = 0; i < node.getChildNodes().getLength(); i++) {
      if (flatten(pathSeparator, addIndex, storeAttributes, skipRoot, path, node.getChildNodes().item(i), count, props))
	count++;
    }

    return true;
  }

  /**
   * Flattens the DOM and turns them into properties.
   *
   * @param pathSeparator	the path separator to use
   * @param addIndex		whether to add the index
   * @param storeAttributes	whether to store the attributes as well
   * @param skipRoot		whether to skip the root
   * @param node		the node to process
   * @return			the generated properties
   */
  public static Properties toProperties(String pathSeparator, boolean addIndex, boolean storeAttributes, boolean skipRoot, Node node) {
    Properties			result;

    result = new Properties();
    flatten(pathSeparator, addIndex, storeAttributes, skipRoot, null, node, 0, result);

    return result;
  }
}
