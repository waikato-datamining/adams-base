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
 * AbstractPropertiesDefinition.java
 * Copyright (C) 2011-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.env;

import java.io.Serializable;
import java.util.List;

/**
 * Ancestor for properties definitions, whether they are merged or replaced.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPropertiesDefinition
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -3693649083932752468L;

  /**
   * Returns the key this definition is for.
   *
   * @return		the key
   */
  public abstract String getKey();

  /**
   * Returns the properties file name (no path) this definition is for.
   *
   * @return		the key
   */
  public abstract String getFile();

  /**
   * Returns whether an alternative extension is to be used besides the
   * default one ("props"). Useful if distinguishing props files from
   * different projects.
   * Only used in "add(...) methods".
   *
   * @return		true if alternative extension to be used as well
   */
  public boolean hasAlternativeExtension() {
    return (getAlternativeExtension() != null);
  }

  /**
   * Returns an alternative extension to use besides the default one ("props").
   * Useful if distinguishing props files from different projects.
   * Only used in "add(...) methods".
   *
   * @return		the alternative extension (without dot), null if none
   * 			available
   */
  public String getAlternativeExtension() {
    return null;
  }

  /**
   * Creates a full path for the props file, from its path and filename.
   *
   * @param dir		the directory the props file is located in
   * @return		the full path
   * @see		#getFile()
   */
  public String createPath(String dir) {
    return createPath(dir, null);
  }

  /**
   * Creates a full path for the props file, from its path and filename.
   *
   * @param dir		the directory the props file is located in
   * @param ext		the extension to replace current one, null to use default
   * 			one (without the dot)
   * @return		the full path
   * @see		#getFile()
   */
  public String createPath(String dir, String ext) {
    String 	result;

    result = dir;
    if (!dir.endsWith("/"))
      result += "/";
    if (ext == null)
      result += getFile();
    else
      result += getFile().replace(".props", "." + ext);

    return result;
  }

  /**
   * Adds the props file under they specified key.
   *
   * @param env		the environment to update
   * @param propsdir	the location of the props file (e.g., "adams/gui")
   * @param overrides	the keys to override with the values from props files added later on
   */
  protected void add(AbstractEnvironment env, String propsdir, String[] overrides) {
    env.add(getKey(), createPath(propsdir), overrides);
    if (hasAlternativeExtension())
      env.add(getKey(), createPath(propsdir, getAlternativeExtension()), overrides);
  }

  /**
   * Adds the props file under they specified key.
   *
   * @param env		the environment to update
   * @param propsdir	the location of the props file (e.g., "adams/gui")
   * @param home	the home directory to use
   * @param overrides	the keys to override with the values from props files added later on
   */
  protected void add(AbstractEnvironment env, String propsdir, String home, String[] overrides) {
    env.add(getKey(), createPath(propsdir), home, overrides);
    if (hasAlternativeExtension())
      env.add(getKey(), createPath(propsdir, getAlternativeExtension()), home, overrides);
  }

  /**
   * Adds the props file under they specified key.
   *
   * @param env		the environment to update
   * @param propsdir	the location of the props file (e.g., "adams/gui")
   * @param dirs	the directories to look for
   * @param overrides	the keys (or regular expression of keys) to override with the values from props files added later on
   */
  protected void add(AbstractEnvironment env, String propsdir, List<String> dirs, String[] overrides) {
    env.add(getKey(), createPath(propsdir), dirs, overrides);
    if (hasAlternativeExtension())
      env.add(getKey(), createPath(propsdir, getAlternativeExtension()), dirs, overrides);
  }

  /**
   * Adds the props file under they specified key. Previously added
   * props files are removed first.
   *
   * @param env		the environment to update
   * @param propsdir	the location of the props file (e.g., "adams/gui")
   */
  protected void replace(AbstractEnvironment env, String propsdir) {
    env.replace(getKey(), createPath(propsdir));
  }

  /**
   * Adds the props file under they specified key. Previously added
   * props files are removed first.
   *
   * @param env		the environment to update
   * @param propsdir	the location of the props file (e.g., "adams/gui")
   * @param dirs	the directories to look for
   */
  protected void replace(AbstractEnvironment env, String propsdir, List<String> dirs) {
    env.replace(getKey(), createPath(propsdir), dirs);
  }

  /**
   * Updates the environment object with its definition for the props file
   * (whether to add/replace/etc the values).
   *
   * @param env		the environment object to update
   */
  public abstract void update(AbstractEnvironment env);
  
  /**
   * Returns a short description.
   * 
   * @return		the short description
   */
  @Override
  public String toString() {
    return getKey() + ": " + getFile();
  }
}
