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
 * InlineEditorSupportWithFavorites.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe;

/**
 * Interface for editors that support inline editing with support for
 * favorites.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface InlineEditorSupportWithFavorites
  extends InlineEditorSupport {

  /**
   * Checks whether favorites support is available.
   *
   * @return		true if enabled
   */
  public boolean isInlineFavoritesEnabled();

  /**
   * Returns the class to use for favorites.
   *
   * @return		the class to use
   */
  public Class getInlineFavoritesClass();
}
