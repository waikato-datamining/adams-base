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
 * AbstractTag.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.doc.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import adams.core.net.HtmlUtils;

/**
 * The ancestor of all DocBook and HTML tags.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTag
  extends DefaultMutableTreeNode {

  /** for serialization. */
  private static final long serialVersionUID = 6472066010192166545L;

  /** the attributes. */
  protected Hashtable<String,String> m_Attributes;

  /**
   * Initializes the tag.
   *
   * @param tag		the name of the tag
   */
  public AbstractTag(String tag) {
    super(tag);

    m_Attributes = new Hashtable<String,String>();
  }

  /**
   * Returns the tag name.
   *
   * @return		the tag
   */
  public String getTag() {
    return (String) getUserObject();
  }

  /**
   * Sets the value of the specified attribute.
   *
   * @param name	the name of the attribute
   * @param value	the value of the attribute
   */
  public void setAttribute(String name, String value) {
    if (name.replaceAll("[a-zA-Z0-9]", "").length() > 0)
      throw new IllegalArgumentException(
	  "Only attribute names consisting of alphanumeric characters are allowed: " + name);

    m_Attributes.put(name, value);
  }

  /**
   * Checks whether an attribute with the specified name is available.
   *
   * @param name	the name of the attribute to check
   * @return		true if the attribute exists
   */
  public boolean hasAttribute(String name) {
    return m_Attributes.containsKey(name);
  }

  /**
   * Returns the value of an attribute.
   *
   * @param name	the name of the attribute to return
   * @return		the value, can be null if attribute does not exist
   */
  public String getAttribute(String name) {
    return m_Attributes.get(name);
  }

  /**
   * Removes the specified attribute.
   *
   * @param name	the name of the attribute to remove
   * @return 		the previously stored value of the attribute
   */
  public String removeAttribute(String name) {
    return m_Attributes.remove(name);
  }

  /**
   * Checks whether the child is valid and can be added.
   * <br><br>
   * Default implementation returns always true.
   *
   * @param child	the child to check
   * @return		true if valid
   */
  protected boolean isValidChild(AbstractTag child) {
    return true;
  }

  /**
   * Hook-method before inserting a child element. Checks whether the child
   * is valid.
   *
   * @param newChild	the child to insert
   * @param childIndex	the index
   * @see		#isValidChild(AbstractTag)
   */
  protected void preInsert(MutableTreeNode newChild, int childIndex) {
    if (!isValidChild((AbstractTag) newChild))
      throw new IllegalArgumentException("Invalid child tag: " + newChild);
  }

  /**
   * Performs the actual inserting of a child element.
   *
   * @param newChild	the child to insert
   * @param childIndex	the index
   */
  protected void doInsert(MutableTreeNode newChild, int childIndex) {
    super.insert(newChild, childIndex);
  }

  /**
   * Hook-method after inserting a child element.
   * <br><br>
   * Default implementation does nothing.
   *
   * @param newChild	the child to insert
   * @param childIndex	the index
   */
  protected void postInsert(MutableTreeNode newChild, int childIndex) {
  }

  /**
   * Removes <code>newChild</code> from its present parent (if it has a
   * parent), sets the child's parent to this node, and then adds the child
   * to this node's child array at index <code>childIndex</code>.
   * <code>newChild</code> must not be null and must not be an ancestor of
   * this node.
   *
   * @param	newChild	the MutableTreeNode to insert under this node
   * @param	childIndex	the index in this node's child array
   *				where this node is to be inserted
   * @see		#isValidChild(AbstractTag)
   */
  @Override
  public void insert(MutableTreeNode newChild, int childIndex) {
    preInsert(newChild, childIndex);
    doInsert(newChild, childIndex);
    postInsert(newChild, childIndex);
  }

  /**
   * Returns the indentation string, depending on the elements level in the
   * XML tree.
   *
   * @return		the indentation string
   */
  protected String getIndentation() {
    StringBuilder	result;
    int			i;
    int			level;

    result = new StringBuilder();

    level = getLevel();
    for (i = 0; i < level; i++)
      result.append("  ");

    return result.toString();
  }

  /**
   * Appends the start tag to the buffer.
   *
   * @param buffer	the buffer to append the start tag to
   */
  protected void appendStartTag(StringBuilder buffer) {
    List<String>	keys;

    buffer.append("<" + getTag());

    if (m_Attributes.size() > 0) {
      keys = new ArrayList<String>(m_Attributes.keySet());
      Collections.sort(keys);
      for (String key: keys) {
	buffer.append(" ");
	buffer.append(key);
	buffer.append("=\"");
	buffer.append(toEntities(getAttribute(key)));
	buffer.append("\"");
      }
    }

    if (!hasContent())
      buffer.append("/");
    
    buffer.append(">");
  }

  /**
   * Appends the end tag to the buffer.
   *
   * @param buffer	the buffer to append the end tag to
   */
  protected void appendEndTag(StringBuilder buffer) {
    buffer.append("</" + getTag() + ">");
    buffer.append("\n");
  }

  /**
   * Checks whether there is any content to append.
   *
   * @return		true if content available
   */
  public abstract boolean hasContent();

  /**
   * Appends the content of the element to the buffer.
   *
   * @param buffer	the buffer to append the content to
   */
  protected abstract void appendContent(StringBuilder buffer);

  /**
   * Hook method for validating/fixing the XML tree.
   * <br><br>
   * Default implementation does nothing
   */
  protected void doValidate() {
  }

  /**
   * Validates/fixes the XML tree.
   */
  public void validate() {
    int			i;
    AbstractTag	child;

    doValidate();

    for (i = 0; i < getChildCount(); i++) {
      child = (AbstractTag) getChildAt(i);
      child.validate();
    }
  }

  /**
   * Turns the XML tree into its string representation.
   *
   * @param buffer	the buffer to append the tag to
   */
  public void toXML(StringBuilder buffer) {
    String	indent;

    indent = getIndentation();
    buffer.append(indent);
    appendStartTag(buffer);
    if (hasContent()) {
      if (getChildCount() > 0) {
	buffer.append("\n");
	appendContent(buffer);
	buffer.append(indent);
      }
      else {
	appendContent(buffer);
      }
      appendEndTag(buffer);
    }
  }

  /**
   * Turns the string into valid XML.
   *
   * @param s		the string to convert to valid XML
   * @return		the XML string
   * @see		HtmlUtils#toHTML(String)
   */
  public static String toEntities(String s) {
    String	result;

    result = s;
    result = result.replace("&", "&amp;");
    result = result.replace("<", "&lt;");
    result = result.replace(">", "&gt;");
    result = result.replace("@", "&#64;");
    result = result.replace("$", "&#36;");
    result = result.replace("\"", "&quot;");
    result = result.replace("/", "&#47;");
    result = result.replace("%", "&#37;");

    return result;
  }
}
