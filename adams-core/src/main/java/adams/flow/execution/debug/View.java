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
 * View.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.execution.debug;

/**
 * The breakpoint views available.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10983 $
 */
public enum View {
  /** the source code. */
  SOURCE,
  /** the expressions. */
  EXPRESSIONS,
  /** the variables. */
  VARIABLES,
  /** the storage. */
  STORAGE,
  /** inspection of the current token. */
  INSPECT_TOKEN,
  /** the breakpoints. */
  BREAKPOINTS
}
