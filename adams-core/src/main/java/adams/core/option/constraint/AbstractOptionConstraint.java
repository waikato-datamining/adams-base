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
 * AbstractOptionConstraint.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option.constraint;

import adams.core.option.AbstractOption;
import adams.core.option.OptionHandler;
import adams.core.option.OptionManager;

import java.io.Serializable;

/**
 * Ancestor for constraints for options, e.g., lower/upper bound for numbers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the option type
 */
public abstract class AbstractOptionConstraint<T> 
  implements Serializable {

  private static final long serialVersionUID = -8953099946786199494L;

  /** the owner. */
  protected AbstractOption m_Owner;

  /**
   * Initializes the constraint.
   *
   * @param owner	the option this constraint is for; if null must be set later via {@link #setOwner(AbstractOption)}
   */
  protected AbstractOptionConstraint(AbstractOption owner) {
    m_Owner = owner;
  }

  /**
   * Sets the option that this constraint is for.
   *
   * @param value	the option
   */
  public void setOwner(AbstractOption value) {
    m_Owner = value;
  }

  /**
   * Returns the option this constraint is for.
   *
   * @return		the option
   */
  public AbstractOption getOwner() {
    return m_Owner;
  }

  /**
   * Returns the option manager this option belongs to.
   *
   * @return		the owning OptionManager
   */
  public OptionManager getOptionManager() {
    return m_Owner.getOwner();
  }

  /**
   * Returns the option handler this option belongs to.
   *
   * @return		the owning OptionHandler
   */
  public OptionHandler getOptionHandler() {
    return getOptionManager().getOwner();
  }

  /**
   * Checks the value against the constraints.
   * If it violates the constraints, uses the owner's logger to output a warning message.
   *
   * @param owner 	the owner
   * @param value	the value to check
   * @return		true if valid
   */
  public abstract boolean isValid(T value);
}
