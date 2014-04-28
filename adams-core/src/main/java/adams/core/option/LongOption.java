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
 * LongOption.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

/**
 * Handles options with Long arguments.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LongOption
  extends AbstractNumericOption<Long> {

  /** for serialization. */
  private static final long serialVersionUID = -5482444006802407378L;

  /**
   * Initializes the option. Will always output the default value.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option (no leading dash)
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   */
  protected LongOption(OptionManager owner, String commandline, String property,
      Object defValue) {

    super(owner, commandline, property, defValue);
  }

  /**
   * Initializes the option.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option (no leading dash)
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   * @param outputDefValue	whether to output the default value or not
   */
  protected LongOption(OptionManager owner, String commandline, String property,
      Object defValue, boolean outputDefValue) {

    super(owner, commandline, property, defValue, outputDefValue);
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
  protected LongOption(OptionManager owner, String commandline, String property,
      Object defValue, Long lower, Long upper) {

    super(owner, commandline, property, defValue, lower, upper);
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
   * @param lower		the lower bound (incl; only for numeric values),
   * 				use null to use unbounded
   * @param upper		the upper bound (incl; only for numeric values),
   * 				use null to use unbounded
   */
  protected LongOption(OptionManager owner, String commandline, String property,
      Object defValue, boolean outputDefValue, Long lower, Long upper) {

    super(owner, commandline, property, defValue, outputDefValue, lower, upper);
  }
}
