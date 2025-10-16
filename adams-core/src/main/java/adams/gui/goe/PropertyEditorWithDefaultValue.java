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
 * PropertyEditorWithDefaultValue.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe;

import java.beans.PropertyEditor;

/**
 * Interface for property editors that support default values.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface PropertyEditorWithDefaultValue
  extends PropertyEditor {

  /**
   * Sets the default value. May get ignored by the concrete editor.
   *
   * @param value	the default value
   */
  public void setDefaultValue(Object value);

  /**
   * Returns the default value.
   *
   * @return		the default value
   */
  public Object getDefaultValue();
}
