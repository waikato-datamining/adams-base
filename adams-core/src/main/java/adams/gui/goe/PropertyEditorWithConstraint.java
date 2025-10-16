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
 * PropertyEditorWithConstraint.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe;

import adams.core.option.constraint.AbstractOptionConstraint;

import java.beans.PropertyEditor;

/**
 * Interface for property editors that support constraints.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface PropertyEditorWithConstraint<T extends AbstractOptionConstraint>
  extends PropertyEditor {

  /**
   * The constraint to use.
   *
   * @param value	the constraint, null to remove
   */
  public void setConstraint(T value);

  /**
   * Return the constraint in use.
   *
   * @return		the constraint, null if none set
   */
  public T getConstraint();
}
