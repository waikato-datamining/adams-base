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
 * StringLength.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option.constraint;

import adams.core.option.AbstractOption;

/**
 * Constraint for enforcing min/max length of strings.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class StringLength
  extends AbstractOptionConstraint<String> {

  private static final long serialVersionUID = -6592293880948984286L;

  /** the minimum length. */
  protected Integer m_Min;

  /** the maximum length. */
  protected Integer m_Max;

  /**
   * Initializes the constraint.
   *
   * @param min 	the minimum length, null for no limit
   * @param max 	the maximum length, null for no limit
   */
  public StringLength(Integer min, Integer max) {
    this(null, min, max);
  }

  /**
   * Initializes the constraint.
   *
   * @param owner 	the option this constraint is for
   * @param min 	the minimum length, null for no limit
   * @param max 	the maximum length, null for no limit
   */
  public StringLength(AbstractOption owner, Integer min, Integer max) {
    super(owner);

    if ((min != null) && (max != null) && (max <= min))
      throw new IllegalArgumentException("'max' must be larger than 'min': min=" + min + ", max=" + max);

    m_Min = min;
    m_Max = max;
  }

  /**
   * Returns whether a lower limit is set.
   *
   * @return		true if limit set
   */
  public boolean hasMin() {
    return (m_Min != null);
  }

  /**
   * Returns the lower limit, if any.
   *
   * @return		the limit, null if no limit
   */
  public Integer getMin() {
    return m_Min;
  }

  /**
   * Returns whether a upper limit is set.
   *
   * @return		true if limit set
   */
  public boolean hasMax() {
    return (m_Max != null);
  }

  /**
   * Returns the upper limit, if any.
   *
   * @return		the limit, null if no limit
   */
  public Integer getMax() {
    return m_Max;
  }

  /**
   * Checks the value against the constraints.
   * If it violates the constraints, uses the owner's logger to output a warning message.
   *
   * @param value the value to check
   * @return true if valid
   */
  @Override
  public boolean isValid(String value) {
    if (hasMin() && (value.length() < getMin()))
      return false;
    if (hasMax() && (value.length() > getMax()))
      return false;
    return true;
  }
}
