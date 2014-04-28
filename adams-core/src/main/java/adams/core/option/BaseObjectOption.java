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
 * BaseObjectOption.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.base.BaseObject;
import adams.core.base.BaseString;

/**
 * Option class for BaseObject derived classes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseObjectOption
  extends CustomHooksOption {

  /** for serialization. */
  private static final long serialVersionUID = 4706969677540350561L;

  /**
   * Initializes the option. Will always output the default value.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   */
  protected BaseObjectOption(OptionManager owner, String commandline, String property,
      Object defValue) {

    super(owner, commandline, property, defValue);
  }

  /**
   * Initializes the option.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   * @param outputDefValue	whether to output the default value or not
   */
  protected BaseObjectOption(OptionManager owner, String commandline, String property,
      Object defValue, boolean outputDefValue) {

    super(owner, commandline, property, defValue, outputDefValue);

    // since we don't list the class for handling the value as superclass
    // or in the string, the default value needs to declare it explicitly!
    if (getBaseClass() == BaseObject.class) {
      throw new IllegalArgumentException(
	  getOptionHandler().getClass().getName() + "/" + getProperty() + ": "
	  + "Cannot use abstract superclass " + BaseObject.class.getName() + " - "
	  + "Use a concrete subclass, e.g., " +  BaseString.class.getName() + "!");
    }
  }
}
