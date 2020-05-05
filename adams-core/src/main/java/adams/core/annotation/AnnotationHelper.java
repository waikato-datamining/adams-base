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
 * AnnotationHelper.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.core.annotation;

import adams.core.Utils;

/**
 * Helper class around annotations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AnnotationHelper {

  /**
   * Returns whether the class has the specified annotation.
   *
   * @param cls		the class to check for annotation
   * @param annotation	the annotation to look for
   * @return		true if annotation is present
   */
  public static boolean hasAnnotation(Class cls, Class annotation) {
    return (cls.getAnnotation(annotation) != null);
  }

  /**
   * Returns whether the class is marked as deprecated ({@link Deprecated}
   * or {@link DeprecatedClass}).
   *
   * @param cls		the class to check
   * @return		true if marked as deprecated
   */
  public static boolean isDeprecated(Class cls) {
    return hasAnnotation(cls, Deprecated.class)
      || hasAnnotation(cls, DeprecatedClass.class);
  }

  /**
   * Generates a deprecation warning if the class is marked as such.
   *
   * @param cls		the class to check
   * @return		null if not deprecated, otherwise warning message
   */
  public static String getDeprecationWarning(Class cls) {
    DeprecatedClass dep;

    if (cls.isAnnotationPresent(DeprecatedClass.class)) {
      dep = (DeprecatedClass) cls.getAnnotation(DeprecatedClass.class);
      return cls.getName() + " is deprecated!\n" + "Use instead: " + Utils.classesToString(dep.useInstead());
    }
    else if (cls.isAnnotationPresent(Deprecated.class)) {
      return cls.getName() + " is deprecated!";
    }

    return null;
  }
}
