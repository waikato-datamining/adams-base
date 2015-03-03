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
 * ByteOption.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

/**
 * Handles options with Byte arguments.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByteOption
  extends AbstractNumericOption<Byte> {

  /** for serialization. */
  private static final long serialVersionUID = -7412891415495630059L;

  /**
   * Initializes the option. Will always output the default value.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option (no leading dash)
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   */
  protected ByteOption(OptionManager owner, String commandline, String property,
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
  protected ByteOption(OptionManager owner, String commandline, String property,
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
  protected ByteOption(OptionManager owner, String commandline, String property,
      Object defValue, Byte lower, Byte upper) {

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
  protected ByteOption(OptionManager owner, String commandline, String property,
      Object defValue, boolean outputDefValue, Byte lower, Byte upper) {

    super(owner, commandline, property, defValue, outputDefValue, lower, upper);
  }
}
