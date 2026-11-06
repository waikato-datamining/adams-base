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
 * JsonClassDescriptionGenerator.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option.description;

import java.util.Map;

/**
 * Interface for classes that generate JSON-based descriptions from classes.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ClassDescriptionGenerator {

  /** the key for the class name. */
  public final static String KEY_CLASS = "class";

  /** the key for the options. */
  public final static String KEY_OPTIONS = "options";

  /**
   * Checks whether this object is handled by this generator.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  public boolean handles(Object obj);

  /**
   * Checks whether this class is handled by this generator.
   *
   * @param cls		the class to check
   * @return		true if handled
   */
  public boolean handles(Class cls);

  /**
   * Generates the description for the object.
   *
   * @param obj		the object to process
   * @return		the description, null if failed to generate
   */
  public Map<String,Object> generate(Object obj);

  /**
   * Generates the description for the class.
   *
   * @param cls		the class to process
   * @return		the description, null if failed to generate
   */
  public Map<String,Object> generate(Class cls);
}
