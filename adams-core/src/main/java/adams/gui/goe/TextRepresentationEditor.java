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
 * TextRepresentationEditor.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe;

import java.beans.PropertyEditor;

/**
 * Interface for editors that may support the {@link #setAsText(String)}
 * and {@link #getAsText()} methods.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface TextRepresentationEditor
  extends PropertyEditor {

  /**
   * Indicates whether it supports setting/getting via the {@link #setAsText(String)}
   * and {@link #getAsText()} methods.
   *
   * @return		true if supported
   * @see 		#getAsText()
   * @see 		#setAsText(String)
   */
  public boolean supportsTextRepresentation();
}
