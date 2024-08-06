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
 * WekaPackageUtils.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package weka.core;

import weka.core.packageManagement.Package;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility functions for Weka packages.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaPackageUtils {

  /**
   * Checks whether the package is an official one.
   *
   * @param pkg		the package to check
   * @return		true if official
   * @throws Exception	if querying package manager fails
   */
  public static boolean isOfficial(Package pkg) throws Exception {
    return isOfficial(pkg.getName());
  }

  /**
   * Checks whether the package is an official one.
   *
   * @param name	the name of the package to check
   * @return		true if official
   * @throws Exception	if querying package manager fails
   */
  public static boolean isOfficial(String name) throws Exception {
    boolean		result;
    List<Package>	pkgs;

    result = false;
    pkgs   = WekaPackageManager.getAllPackages();
    for (Package pkg: pkgs) {
      if (pkg.getName().equals(name)) {
	result = true;
	break;
      }
    }

    return result;
  }

  /**
   * Turns the package into a map.
   *
   * @param pkg		the package to convert
   * @return		the generated map
   * @throws Exception	if conversion fails
   */
  public static Map<String,Object> toMap(Package pkg) throws Exception {
    Map<String,Object>	result;

    result = new HashMap<>();
    result.put("Name", pkg.getName());
    result.put("URL", pkg.getPackageURL().toString());
    result.put("Official", isOfficial(pkg));
    for (Object key: pkg.getPackageMetaData().keySet())
      result.put(key.toString(), pkg.getPackageMetaData().get(key));

    return result;
  }
}
