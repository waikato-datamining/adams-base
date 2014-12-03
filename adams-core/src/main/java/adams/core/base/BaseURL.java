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
 * BaseURL.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import java.net.URL;

/**
 * Wrapper for an URL object to be editable in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseURL
  extends BaseObject {

  /** for serialization. */
  private static final long serialVersionUID = 4461135181234402629L;

  /** the default URL. */
  public final static String DEFAULT_URL = "http://localhost";

  /** the current URL. */
  protected URL m_Current;

  /**
   * Initializes the string with DEFAULT_URL.
   *
   * @see		#DEFAULT_URL
   */
  public BaseURL() {
    this(DEFAULT_URL);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseURL(String s) {
    super(s);
  }

  /**
   * Initializes the internal object.
   */
  @Override
  protected void initialize() {
    m_Internal = DEFAULT_URL;
  }

  /**
   * Initializes the object with the given value.
   *
   * @param value	the value to use
   */
  public BaseURL(URL value) {
    this(value.toString());
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if valid URL
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    try {
      new URL(value);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Sets the string value.
   *
   * @param value	the string value
   */
  @Override
  public void setValue(String value) {
    if (!isValid(value))
      return;

    try {
      m_Current  = new URL(value);
      m_Internal = value;
    }
    catch (Exception e) {
      e.printStackTrace();
      m_Internal = DEFAULT_URL;
      try {
	m_Current = new URL(value);
      }
      catch (Exception ex) {
	// ignored
      }
    }
  }

  /**
   * Returns the current string value.
   *
   * @return		the string value
   */
  @Override
  public String getValue() {
    return (String) m_Internal;
  }

  /**
   * Returns the URL value.
   *
   * @return		the URL value
   */
  public URL urlValue() {
    return m_Current;
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "An URL (uniform resource locator).";
  }
}
