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
 * Title.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.doc.docbook;

import adams.doc.xml.AbstractSimpleTag;

/**
 * Represents the "title" tag.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Title
  extends AbstractSimpleTag {

  /** for serialization. */
  private static final long serialVersionUID = -5686620774872611944L;

  /**
   * Initializes the tag.
   */
  public Title() {
    super("title");
  }

  /**
   * Initializes the tag.
   *
   * @param title	the actual title
   */
  public Title(String title) {
    super("title", title);
  }
}
