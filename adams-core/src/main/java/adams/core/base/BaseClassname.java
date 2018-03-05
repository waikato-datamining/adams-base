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
 * BaseClassname.java
 * Copyright (C) 2015-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.Utils;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;

/**
 * Wrapper for a class name object to be editable in the GOE. Basically the same
 * as BaseString, but used for class names.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseClassname
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -7223597009565454854L;

  /** for logging. */
  private static Logger LOGGER = LoggingHelper.getLogger(BaseClassname.class);

  /**
   * Initializes the string with length 0.
   */
  public BaseClassname() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseClassname(String s) {
    super(s);
  }

  /**
   * Initializes the object with the class to use.
   *
   * @param cls		the class to use
   */
  public BaseClassname(Class cls) {
    this(cls.getName());
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    if ((Utils.stringToClass(value) != null)) {
      return true;
    }
    else {
       if (!value.isEmpty())
	 LOGGER.warning("Invalid classname: " + value);
      return false;
    }
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Java classname.";
  }

  /**
   * Returns the value as class.
   *
   * @return		the class value, null if class invalid (or empty)
   */
  public Class classValue() {
    return Utils.stringToClass(getValue());
  }

  /**
   * Whether this object should have favorites support.
   *
   * @return		true if to support favorites
   */
  @Override
  public boolean hasFavoritesSupport() {
    return true;
  }
}
