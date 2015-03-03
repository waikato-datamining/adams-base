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
 * XPathResult.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;

/**
 * Enumeration for the types of output XPath can generate.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum XPathResult {

  /** nodeset. */
  NODESET(XPathConstants.NODESET),
  /** node. */
  NODE(XPathConstants.NODE),
  /** string. */
  STRING(XPathConstants.STRING),
  /** boolean. */
  BOOLEAN(XPathConstants.BOOLEAN),
  /** number. */
  NUMBER(XPathConstants.NUMBER);
  
  /** the associated QName. */
  private QName m_QName;
  
  /**
   * Initializes the enum.
   * 
   * @param qname	the associated qname
   */
  private XPathResult(QName qname) {
    m_QName = qname;
  }
  
  /**
   * Returns the associated qname.
   * 
   * @return		the qname
   */
  public QName getQName() {
    return m_QName;
  }
}
