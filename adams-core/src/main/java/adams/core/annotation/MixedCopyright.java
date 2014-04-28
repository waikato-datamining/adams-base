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
 * MixedCopyright.java
 * Copyright (C) 2010-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.core.annotation;

import adams.core.License;

/**
 * An annotation for classes with mixed copyright.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public @interface MixedCopyright {
  String copyright() default "";
  String author() default "";
  License license() default License.TODO;
  String url() default "";
  String note() default "";
}
