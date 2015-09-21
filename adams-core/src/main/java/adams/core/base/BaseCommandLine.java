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
 * BaseCommandLine.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.option.OptionUtils;

/**
 * Wrapper for a commandline object to be editable in the GOE. Basically the same
 * as BaseString, but used for class names and their associated options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseCommandLine
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = -7223597009565454854L;

  /**
   * Initializes the string with length 0.
   */
  public BaseCommandLine() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public BaseCommandLine(String s) {
    super(s);
  }

  /**
   * Initializes the object with the class to use.
   *
   * @param cls		the class to use
   */
  public BaseCommandLine(Class cls) {
    this(cls.getName());
  }

  /**
   * Initializes the object with the commandline object to use.
   *
   * @param obj		the object to use
   */
  public BaseCommandLine(Object obj) {
    this(OptionUtils.getCommandLine(obj));
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    try {
      OptionUtils.forAnyCommandLine(Object.class, value);
      return true;
    }
    catch (Exception e) {
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
    return "Command-line (classname + options).";
  }

  /**
   * Returns the commandline as object.
   *
   * @return		the instantiated object, null if invalid (or empty)
   */
  public Object objectValue() {
    try {
      return OptionUtils.forAnyCommandLine(Object.class, getValue());
    }
    catch (Exception e) {
      return null;
    }
  }
}
