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
 * Round.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data;

/**
 * The type of rounding to perform.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum RoundingType {
  /** performs a Math.round(double). */
  ROUND,
  /** performs a Math.ceil(double). */
  CEILING,
  /** performs a Math.floor(double). */
  FLOOR
}