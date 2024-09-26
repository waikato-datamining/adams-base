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
 * ThirdPartyCopyright.java
 * Copyright (C) 2012-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.core.annotation;

import adams.core.License;

/**
 * An annotation for classes with the copyright belonging completely
 * to a third-party. I.e., a contribution without copyright assignment.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public @interface ThirdPartyCopyright {
  String copyright() default "";
  String author() default "";
  License license() default License.TODO;
  String url() default "";
  String note() default "";
}
