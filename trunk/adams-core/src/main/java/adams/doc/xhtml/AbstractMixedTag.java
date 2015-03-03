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
 * AbstractMixedTag.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.doc.xhtml;

import adams.doc.xml.AbstractTag;

/**
 * Ancestor for tags that have nested tags and textual content.
 * The textual content always comes first.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMixedTag
  extends AbstractTag {

  /** for serialization. */
  private static final long serialVersionUID = -43542338015882364L;

  /** whether to output empty tags. */
  protected boolean m_AllowEmpty;

  /** the content. */
  protected String m_Content;

  /**
   * Initializes the tag with not content.
   *
   * @param tag		the name of the tag
   */
  public AbstractMixedTag(String tag) {
    this(tag, null);
  }

  /**
   * Initializes the tag.
   *
   * @param tag		the name of the tag
   * @param content	the initial content, null to ignore
   */
  public AbstractMixedTag(String tag, String content) {
    super(tag);
    
    m_AllowEmpty = true;
    setContent(content);
  }

  /**
   * Sets the content of the tag.
   *
   * @param value	the string content
   */
  public void setContent(String value) {
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

  /**
   * Returns whether there is any "simple", textual content available.
   * 
   * @return		true if content available
   */
  @Override
  public boolean hasContent() {
    return (m_Content != null) || (getChildCount() > 0);
  }
  
  /**
   * Appends the content of the tag to the buffer.
   *
   * @param buffer	the buffer to append the content to
   */
  @Override
  protected void appendContent(StringBuilder buffer) {
    int				i;
    AbstractTag	child;

    if ((m_Content != null) && (m_Content.length() > 0))
      buffer.append(m_Content);
    
    for (i = 0; i < getChildCount(); i++) {
      child = (AbstractTag) getChildAt(i);
      child.toXML(buffer);
    }
  }

  /**
   * Turns the XML tree into its string representation.
   *
   * @param buffer	the buffer to append the tag to
   */
  @Override
  public void toXML(StringBuilder buffer) {
    if ((getChildCount() > 0) || m_AllowEmpty)
      super.toXML(buffer);
  }
}
