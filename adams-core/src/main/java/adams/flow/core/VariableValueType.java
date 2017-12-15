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
 * VariableValueType.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

/**
 * Determines how {@link adams.flow.standalone.SetVariable} and
 * {@link adams.flow.transformer.SetVariable} interpret their "values".
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see adams.flow.standalone.SetVariable
 * @see adams.flow.transformer.SetVariable
 */
public enum VariableValueType {
  /** simple string. */
  STRING,
  /** mathematical expression. */
  MATH_EXPRESSION,
  /** mathematical expression (rounded). */
  MATH_EXPRESSION_ROUND,
  /** boolean expression. */
  BOOL_EXPRESSION,
  /** string expression. */
  STRING_EXPRESSION,
}
