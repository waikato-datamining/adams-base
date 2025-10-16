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
 * NumericBounds.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option.constraint;

import adams.core.logging.LoggingObject;
import adams.core.option.AbstractOption;

/**
 * Constraint that enforces lower/upper bounds for numeric values.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class NumericBounds<T extends Number>
  extends AbstractOptionConstraint<T> {

  private static final long serialVersionUID = -809728819172638457L;

  /** the lower bound. */
  protected T m_LowerBound;

  /** the upper bound. */
  protected T m_UpperBound;

  /**
   * Initializes the constraint.
   *
   * @param lowerBound	the lower bound, can be null
   * @param upperBound	the upper bound, can be null
   */
  public NumericBounds(T lowerBound, T upperBound) {
    this(null, lowerBound, upperBound);
  }

  /**
   * Initializes the constraint.
   *
   * @param owner 	the option this constraint is for
   * @param lowerBound	the lower bound, can be null
   * @param upperBound	the upper bound, can be null
   */
  public NumericBounds(AbstractOption owner, T lowerBound, T upperBound) {
    super(owner);
    m_LowerBound = lowerBound;
    m_UpperBound = upperBound;
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
   * Checks the value against the constraints.
   * If it violates the constraints, uses the owner's logger to output a warning message.
   *
   * @param value the value to check
   * @return true if valid
   */
  @Override
  public boolean isValid(Number value) {
    boolean	result;
    String	msg;
    String	expr;

    result = true;

    if (hasLowerBound() && (value.doubleValue() < getLowerBound().doubleValue()))
      result = false;

    if (hasUpperBound() && (value.doubleValue() > getUpperBound().doubleValue()))
      result = false;

    if (!result) {
      if (hasLowerBound() && hasUpperBound())
	expr = getLowerBound() + " <= x <= " + getUpperBound();
      else if (hasLowerBound())
	expr = getLowerBound() + " <= x";
      else
	expr = getUpperBound() + " >= x";
      msg = getOwner().getProperty() + "/-" + getOwner().getCommandline() + " must satisfy " + expr + ", provided: " + value;
      if (getOptionHandler() instanceof LoggingObject)
	((LoggingObject) getOptionHandler()).getLogger().warning(msg);
      else
	System.err.println(msg);
    }

    return result;
  }

  /**
   * Checks whether the number is within the specified bounds (if any).
   *
   * @param number	the number to check
   * @return		the default value for this option, if the bounds
   * 			were defined and the value was outside
   */
  public T checkBounds(T number) {
    T		result;
    boolean	invalid;

    result  = number;
    invalid = false;

    if (hasLowerBound() && (number.doubleValue() < getLowerBound().doubleValue()))
      invalid = true;

    if (hasUpperBound() && (number.doubleValue() > getUpperBound().doubleValue()))
      invalid = true;

    if (invalid)
      result = (T) getOwner().getDefaultValue();

    return result;
  }
}
