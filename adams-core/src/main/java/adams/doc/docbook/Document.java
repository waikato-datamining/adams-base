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
 * Document.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.doc.docbook;

import java.io.Serializable;

import adams.doc.xml.AbstractTag;

/**
 * Represents a <a href="http://en.wikipedia.org/wiki/DocBook" target="_blank">DocBook</a> document.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Document
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -5462121524853958988L;

  /** the XML prolog. */
  public static final String XML_PROLOG = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

  /** the DocBook namespace. */
  public static final String NS_DOCBOOK = "http://docbook.org/ns/docbook";

  /** the "xmlns" attribute. */
  public static final String ATT_XMLNS = "xmlns";

  /** the "version" attribute. */
  public static final String ATT_VERSION = "version";

  /** the root element. */
  protected AbstractTag m_Root;

  /**
   * Initializes the document.
   */
  public Document() {
    super();
    setRoot(new Article());
  }

  /**
   * Sets the root element.
   *
   * @param value	the new root element
   */
  public void setRoot(AbstractTag value) {
    if (m_Root != null) {
      m_Root.removeAttribute(ATT_XMLNS);
      m_Root.removeAttribute(ATT_VERSION);
    }

    m_Root = value;

    m_Root.setAttribute(ATT_XMLNS, NS_DOCBOOK);
    m_Root.setAttribute(ATT_VERSION, "5.0");
  }

  /**
   * Returns the current root element.
   *
   * @return		the root element
   */
  public AbstractTag getRoot() {
    return m_Root;
  }

  /**
   * Turns the XML tree into its string representation.
   *
   * @param buffer	the buffer to append the tag to
   */
  public void toXML(StringBuilder buffer) {
    m_Root.validate();
    buffer.append(XML_PROLOG);
    buffer.append("\n");
    buffer.append("\n");
    m_Root.toXML(buffer);
    buffer.append("\n");
  }
}
