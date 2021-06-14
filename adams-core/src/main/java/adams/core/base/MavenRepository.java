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

/*
 * MavenRepository.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.core.base;

import adams.core.Utils;

/**
 * Encapsulates Maven repositories.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MavenRepository
  extends AbstractBaseString {

  private static final long serialVersionUID = 1516586362049381050L;

  public final static String SEPARATOR = ";";

  /**
   * Initializes the string with length 0.
   */
  public MavenRepository() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public MavenRepository(String s) {
    super(s);
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    return (value != null) && (value.split(SEPARATOR).length == 3);
  }

  /**
   * Returns the specified part of the coordinate triplet.
   *
   * @param index	the index from the triplet to return
   * @return		the value or empty string if invalid string or index
   */
  protected String getPart(int index) {
    String[]	parts;

    if (isEmpty())
      return "";

    parts = getValue().split(SEPARATOR);
    if (parts.length != 3)
      return "";

    if ((index < 0) || (index >= 3))
      return "";

    return parts[index];
  }

  /**
   * Returns the ID part, if possible.
   *
   * @return		the ID
   */
  public String idValue() {
    return getPart(0);
  }

  /**
   * Returns the name part, if possible.
   *
   * @return		the name
   */
  public String nameValue() {
    return getPart(1);
  }

  /**
   * Returns the URL part, if possible.
   *
   * @return		the URL
   */
  public String urlValue() {
    return getPart(2);
  }

  /**
   * Returns the backquoted String value.
   *
   * @return		the backquoted String value
   */
  @Override
  public String stringValue() {
    return Utils.backQuoteChars(getValue());
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return the tool tip
   */
  @Override
  public String getTipText() {
    return "The three coordinates of a Maven repository: id;name;url";
  }
}
