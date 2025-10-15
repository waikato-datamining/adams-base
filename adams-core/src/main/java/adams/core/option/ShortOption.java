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
 * ShortOption.java
 * Copyright (C) 2010-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

/**
 * Handles options with Short arguments.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ShortOption
  extends AbstractNumericOption<Short> {

  /** for serialization. */
  private static final long serialVersionUID = 7238958288159703882L;

  /**
   * Initializes the option. Will always output the default value.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option (no leading dash)
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   */
  protected ShortOption(OptionManager owner, String commandline, String property, Object defValue) {
    super(owner, commandline, property, defValue);
  }

  /**
   * Initializes the option. Will always output the default value.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   * @param lower		the lower bound (incl; only for numeric values),
   * 				use null to use unbounded
   * @param upper		the upper bound (incl; only for numeric values),
   * 				use null to use unbounded
   */
  protected ShortOption(OptionManager owner, String commandline, String property, Object defValue, Short lower, Short upper) {
    super(owner, commandline, property, defValue, lower, upper);
  }
}
