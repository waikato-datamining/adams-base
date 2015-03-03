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
 * DefaultSimpleTag.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.doc.xml;

/**
 * A default simple tag.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultSimpleTag
  extends AbstractSimpleTag {

  /** for serialization. */
  private static final long serialVersionUID = -6126545140096452210L;

  /**
   * Initializes the tag.
   *
   * @param tag		the name of the tag
   */
  public DefaultSimpleTag(String tag) {
    super(tag);
  }

  /**
   * Initializes the paragraph tag.
   *
   * @param tag		the name of the tag
   * @param content	the content of the tag
   */
  public DefaultSimpleTag(String tag, String content) {
    super(tag, content);
  }
}
