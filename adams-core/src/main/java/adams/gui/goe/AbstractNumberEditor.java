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
 * AbstractNumberEditor.java
 * Copyright (C) 2009-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.core.option.constraint.NumericBounds;

/**
 * An abstract ancestor for custom editors for numbers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractNumberEditor
  extends AbstractBasicTypePropertyEditor
  implements PropertyEditorWithConstraint<NumericBounds>, PropertyEditorWithDefaultValue {

  /** the current value. */
  protected Number m_CurrentValue;

  /** the default value. May get ignored by the concrete editor. */
  protected Number m_DefaultValue;

  /** the constraint. */
  protected NumericBounds m_Constraint;

  /**
   * Initializes the editor.
   */
  public AbstractNumberEditor() {
    super();

    m_CurrentValue = null;
    m_DefaultValue = null;
    m_Constraint   = null;
  }

  /**
   * Sets the default value. May get ignored by the concrete editor.
   *
   * @param value	the default value
   */
  @Override
  public void setDefaultValue(Object value) {
    m_DefaultValue = (Number) value;
  }

  /**
   * Returns the default value.
   *
   * @return		the default value
   */
  @Override
  public Object getDefaultValue() {
    return m_DefaultValue;
  }

  /**
   * The constraint to use.
   *
   * @param value	the constraint, null to remove
   */
  @Override
  public void setConstraint(NumericBounds value) {
    m_Constraint = value;
    updateBounds();
  }

  /**
   * Return the constraint in use.
   *
   * @return		the constraint, null if none set
   */
  @Override
  public NumericBounds getConstraint() {
    return m_Constraint;
  }

  /**
   * Updates the bounds.
   */
  protected abstract void updateBounds();
}
