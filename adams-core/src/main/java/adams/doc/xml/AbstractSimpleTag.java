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
 * AbstractSimpleTag.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.doc.xml;


/**
 * Ancestor for tags that have no children (only string content).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSimpleTag
  extends AbstractTag {

  /** for serialization. */
  private static final long serialVersionUID = -2865475570583425896L;

  /** the content. */
  protected String m_Content;

  /** whether to convert HTML entities. */
  protected boolean m_ConvertEntities;
  
  /**
   * Initializes the tag.
   *
   * @param tag		the name of the tag
   */
  public AbstractSimpleTag(String tag) {
    this(tag, "");
  }

  /**
   * Initializes the tag.
   *
   * @param tag		the name of the tag
   * @param content	the content of the tag
   */
  public AbstractSimpleTag(String tag, String content) {
    super(tag);
    setContent(content);
    m_ConvertEntities = true;
  }

  /**
   * Does not allow children.
   *
   * @param allows	ignored, always false
   */
  @Override
  public void setAllowsChildren(boolean allows) {
    super.setAllowsChildren(false);
  }

  /**
   * Returns true if this node is allowed to have children.
   *
   * @return		always false
   */
  @Override
  public boolean getAllowsChildren() {
    return false;
  }

  /**
   * Sets the content of the tag.
   *
   * @param value	the string content
   */
  public void setContent(String value) {
    if (value == null)
      value = "";
    m_Content = value;
  }

  /**
   * Returns the string content.
   *
   * @return		the content
   */
  public String getContent() {
    return m_Content;
  }
  
  @Override
  public boolean hasContent() {
    return (m_Content.length() > 0);
  }
  
  /**
   * Sets whether to convert HTML entities or not. 
   * <p/>
   * CAUTION: caller must ensure that content is XHTML compliant if this is 
   * turned off!
   * 
   * @param value	if true then HTML entities get converted
   */
  public void setConvertEntities(boolean value) {
    m_ConvertEntities = value;
  }
  
  /**
   * Returns whether HTML entities get converted or not.
   * 
   * @return		true if HTML entities get converted
   */
  public boolean getConvertEntities() {
    return m_ConvertEntities;
  }

  /**
   * Appends the content of the element to the buffer.
   *
   * @param buffer	the buffer to append the content to
   */
  @Override
  protected void appendContent(StringBuilder buffer) {
    if (m_ConvertEntities)
      buffer.append(toEntities(m_Content));
    else
      buffer.append(m_Content);
  }
}
