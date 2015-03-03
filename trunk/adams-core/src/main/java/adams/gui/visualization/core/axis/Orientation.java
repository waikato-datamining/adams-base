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
 * Orientation.java
 * Copyright (C) 2008-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.axis;

/**
 * Enumeration for the axis orientation.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum Orientation {
  /** ticks on the left (bottom), line on the right (top). */
  LEFT_TO_RIGHT,
  /** ticks on the right (top), line on the left (bottom). */
  RIGHT_TO_LEFT
}