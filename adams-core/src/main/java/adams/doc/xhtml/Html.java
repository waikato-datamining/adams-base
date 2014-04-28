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
 * Html.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.doc.xhtml;

import adams.doc.xml.AbstractComplexTag;

/**
 * The outer HTML tag
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Html
  extends AbstractComplexTag {

  /** for serialization. */
  private static final long serialVersionUID = 6058312199083069305L;

  /** the head tag. */
  protected Head m_Head;
  
  /** the body tag. */
  protected Body m_Body;
  
  /**
   * Initializes the tag.
   */
  public Html() {
    super("html");

    setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
    
    m_Head = new Head();
    add(m_Head);
    
    m_Body = new Body();
    add(m_Body);
  }
  
  /**
   * Returns the HEAD tag.
   * 
   * @return		the tag
   */
  public Head getHead() {
    return m_Head;
  }
  
  /**
   * Returns the BODY tag.
   * 
   * @return		the tag
   */
  public Body getBody() {
    return m_Body;
  }
}
