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
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.doc.xhtml;

import java.io.Serializable;

/**
 * Represents an XHTML document.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Document
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -5462121524853958988L;

  /** the DOCTYPE prolog. */
  public static final String DOCTYPE_PROLOG = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";

  /** the root element. */
  protected Html m_Root;

  /**
   * Initializes the document.
   */
  public Document() {
    super();
    m_Root = new Html();
  }

  /**
   * Returns the current root element, i.e., the HTML tag.
   *
   * @return		the root element
   */
  public Html getRoot() {
    return m_Root;
  }

  /**
   * Turns the XML tree into its string representation.
   *
   * @param buffer	the buffer to append the tag to
   */
  public void toXML(StringBuilder buffer) {
    m_Root.validate();
    buffer.append(DOCTYPE_PROLOG);
    buffer.append("\n");
    buffer.append("\n");
    m_Root.toXML(buffer);
    buffer.append("\n");
  }
}
