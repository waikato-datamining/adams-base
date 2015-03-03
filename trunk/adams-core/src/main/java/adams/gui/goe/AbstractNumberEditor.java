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
 * AbstractNumberEditor.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

/**
 * An abstract ancestor for custom editors for numbers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractNumberEditor
  extends AbstractBasicTypePropertyEditor {

  /** the current value. */
  protected Number m_CurrentValue;

  /** the default value. May get ignored by the concrete editor. */
  protected Number m_DefaultValue;

  /** the lower bound. */
  protected Number m_LowerBound;

  /** the upper bound. */
  protected Number m_UpperBound;

  /**
   * Initializes the editor.
   */
  public AbstractNumberEditor() {
    super();

    m_CurrentValue = null;
    m_DefaultValue = null;
    m_LowerBound   = null;
    m_UpperBound   = null;
  }

  /**
   * Sets the default value. May get ignored by the concrete editor.
   *
   * @param value	the default value
   */
  public void setDefaultValue(Number value) {
    m_DefaultValue = value;
  }

  /**
   * Returns the default value.
   *
   * @return		the default value
   */
  public Number getDefaultValue() {
    return m_DefaultValue;
  }

  /**
   * Sets the optional lower bound.
   *
   * @param value	the lower bound to use, use null to use no bound
   */
  public void setLowerBound(Number value) {
    m_LowerBound = value;
    updateBounds();
  }

  /**
   * Returns the optional lower bound.
   *
   * @return		the lower bound, can be null if none set
   */
  public Number getLowerBound() {
    return m_LowerBound;
  }

  /**
   * Sets the optional upper bound.
   *
   * @param upper	the upper bound to use, use null to use no bound
   */
  public void setUpperBound(Number upper) {
    m_UpperBound = upper;
    updateBounds();
  }

  /**
   * Returns the optional upper bound.
   *
   * @return		the upper bound, can be null if none set
   */
  public Number getUpperBound() {
    return m_UpperBound;
  }

  /**
   * Updates the bounds.
   */
  protected abstract void updateBounds();
}
