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
 * Heading.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.doc.xhtml;

/**
 * Represents the H{x} tag.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Heading
  extends AbstractMixedTag {

  /** for serialization. */
  private static final long serialVersionUID = -7501143393940807106L;

  /**
   * Initializes the heading tag.
   */
  public Heading(int level) {
    super("h" + level);
  }

  /**
   * Initializes the heading tag.
   *
    * @param content	the content of the heading.
   */
  public Heading(int level, String content) {
    super("h" + level, content);
  }
}
