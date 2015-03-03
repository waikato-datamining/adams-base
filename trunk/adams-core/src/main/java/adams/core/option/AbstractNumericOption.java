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
 * AbstractNumericOption.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

/**
 * Handles options with numeric arguments.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of number
 */
public abstract class AbstractNumericOption<T extends Number>
  extends AbstractArgumentOption {

  /** for serialization. */
  private static final long serialVersionUID = 5499914416554286605L;

  /** the lower bound for numeric values. */
  protected T m_LowerBound;

  /** the upper bound for numeric values. */
  protected T m_UpperBound;

  /**
   * Initializes the option. Will always output the default value.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option (no leading dash)
   * @param property 		the name of bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   */
  protected AbstractNumericOption(OptionManager owner, String commandline, String property,
      Object defValue) {

    this(owner, commandline, property, defValue, true);
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
  protected AbstractNumericOption(OptionManager owner, String commandline, String property,
      Object defValue, boolean outputDefValue) {

    this(owner, commandline, property, defValue, outputDefValue, null, null);
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
  protected AbstractNumericOption(OptionManager owner, String commandline, String property,
      Object defValue, T lower, T upper) {

    this(owner, commandline, property, defValue, true, lower, upper);
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
  protected AbstractNumericOption(OptionManager owner, String commandline, String property,
      Object defValue, boolean outputDefValue, T lower, T upper) {

    super(owner, commandline, property, defValue, outputDefValue);

    m_LowerBound = lower;
    m_UpperBound = upper;
  }

  /**
   * Checks whether a lower bound has been defined for this option.
   *
   * @return		true if lower bound exists
   */
  public boolean hasLowerBound() {
    return (m_LowerBound != null);
  }

  /**
   * Returns the lower bound for this option.
   *
   * @return		the lower bound, can be null if none defined
   */
  public T getLowerBound() {
    return m_LowerBound;
  }

  /**
   * Checks whether a lower bound has been defined for this option.
   *
   * @return		true if lower bound exists
   */
  public boolean hasUpperBound() {
    return (m_UpperBound != null);
  }

  /**
   * Returns the lower bound for this option.
   *
   * @return		the lower bound, can be null if none defined
   */
  public T getUpperBound() {
    return m_UpperBound;
  }

  /**
   * Compares the two values.
   *
   * @param value	the value to compare against the default value
   * @param defValue	the default value to compare against
   * @return		true if both are equal
   */
  protected boolean compareValues(Object value, Object defValue) {
    T	numValue;
    T	numDefValue;

    numValue    = (T) value;
    numDefValue = (T) defValue;

    return (numValue.doubleValue() == numDefValue.doubleValue());
  }

  /**
   * Checks whether the number is within the specified bounds (if any).
   *
   * @param number	the number to check
   * @return		the default value for this option, if the bounds
   * 			were defined and the value was outside
   */
  protected T checkBounds(T number) {
    T		result;
    boolean	invalid;

    result  = number;
    invalid = false;

    if (hasLowerBound() && (number.doubleValue() < getLowerBound().doubleValue()))
      invalid = true;

    if (hasUpperBound() && (number.doubleValue() > getUpperBound().doubleValue()))
      invalid = true;

    if (invalid)
      result = (T) getDefaultValue();

    return result;
  }

  /**
   * Turns the string into the appropriate number.
   *
   * @param s		the string to parse
   * @return		the generated number
   * @throws Exception	if parsing of string fails
   * @see		#checkBounds(Number)
   */
  public T valueOf(String s) throws Exception {
    T		result;

    result = (T) OptionUtils.valueOf(m_BaseClass, s);
    if (result != null)
      result = checkBounds(result);
    else
      throw new IllegalArgumentException("Unhandled numeric type: " + m_BaseClass);

    return result;
  }

  /**
   * Returns a string representation of the specified object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toString(Object obj) {
    String	result;

    // byte
    if ((m_BaseClass == Byte.class) || (m_BaseClass == Byte.TYPE))
      result = Byte.toString((Byte) obj);
    // short
    else if ((m_BaseClass == Short.class) || (m_BaseClass == Short.TYPE))
      result = Short.toString((Short) obj);
    // int
    else if ((m_BaseClass == Integer.class) || (m_BaseClass == Integer.TYPE))
      result = Integer.toString((Integer) obj);
    // long
    else if ((m_BaseClass == Long.class) || (m_BaseClass == Long.TYPE))
      result = Long.toString((Long) obj);
    // float
    else if ((m_BaseClass == Float.class) || (m_BaseClass == Float.TYPE))
      result = Float.toString((Float) obj);
    // double
    else if ((m_BaseClass == Double.class) || (m_BaseClass == Double.TYPE))
      result = Double.toString((Double) obj);
    else
      throw new IllegalArgumentException("Unhandled numeric type: " + m_BaseClass);

    return result;
  }
}
