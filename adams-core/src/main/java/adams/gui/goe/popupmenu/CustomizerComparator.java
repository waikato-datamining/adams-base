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
 * CustomizerComparator.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe.popupmenu;

import java.util.Comparator;

/**
 * Comparator for sorting the customizers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CustomizerComparator
  implements Comparator<GenericObjectEditorPopupMenuCustomizer> {

  /**
   * Returns the string comparison result of the names of the customizers.
   *
   * @param o1		the first customizer
   * @param o2		the second customizer
   * @return		the result
   */
  @Override
  public int compare(GenericObjectEditorPopupMenuCustomizer o1, GenericObjectEditorPopupMenuCustomizer o2) {
    return o1.getName().compareTo(o2.getName());
  }
}
