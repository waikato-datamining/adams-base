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
 * LDD.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.core.management;

import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;

/**
 * Helper class for determining ldd/glibc version.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class LDD {

  /** the determined version. */
  public static String[] LDD_VERSION;

  public final static String[] LDD_DUMMY_VERSION = {"0", "0"};

  /**
   * Flattens the version into a string.
   *
   * @param version	the version array to flatten
   * @return		the generated string
   */
  protected static String flatten(String[] version) {
    StringBuilder	result;
    int			i;

    result = new StringBuilder();
    for (i = 0; i < version.length; i++) {
      if (i > 0)
        result.append(".");
      result.append(version[i]);
    }

    return result.toString();
  }

  /**
   * Returns (under Linux) the version of ldd (= glibc version). For non-linux or in case of error, returns {@link #LDD_DUMMY_VERSION}.
   *
   * @return		the version (major, minor[, patch])
   */
  public static String[] version() {
    CollectingProcessOutput	output;
    String[]			lines;
    String line;
    String[] result;

    if (LDD_VERSION != null)
      return LDD_VERSION;

    // determine version only once
    result = null;
    if (OS.isLinux()) {
      try {
	output = ProcessUtils.execute(new String[]{"/usr/bin/ldd", "--version"});
	lines  = output.getStdOut().split("\n");
	line   = lines[0].toLowerCase().replaceAll(".*\\)", "").replace(" ", "");
	result = line.split("\\.");
      }
      catch (Exception e) {
        System.err.println("Failed to determine ldd version!");
        e.printStackTrace();
      }
    }
    if (result == null)
      result = LDD_DUMMY_VERSION;

    LDD_VERSION = result;

    return LDD_VERSION;
  }

  /**
   * Returns this detected version of ldd with the provided one and returns if detected version is less than (< 0),
   * equal to (= 0) or larger (> 0) than the supplied one.
   *
   * @param otherVersion	the version to compare against
   * @return			the comparison result
   */
  public static int compareTo(String[] otherVersion) {
    int		result;
    String[]	thisVersion;
    int[] 	thisInt;
    int[] 	otherInt;
    int		thisValue;
    int		otherValue;
    int		i;

    result = 0;
    try {
      thisVersion = version();
      // convert into int
      thisInt = new int[thisVersion.length];
      for (i = 0; i < thisVersion.length; i++)
        thisInt[i] = Integer.parseInt(thisVersion[i]);
      otherInt = new int[otherVersion.length];
      for (i = 0; i < otherVersion.length; i++)
	otherInt[i] = Integer.parseInt(otherVersion[i]);
      // compare
      for (i = 0; i < Math.max(thisInt.length, otherInt.length); i++) {
        thisValue  = (i < thisInt.length) ? thisInt[i] : 0;
	otherValue = (i < otherInt.length) ? otherInt[i] : 0;
	result = Integer.compare(thisValue, otherValue);
	if (result != 0)
	  break;
      }
    }
    catch (Exception e) {
      System.err.println("Failed to compare detected version with this version: " + flatten(otherVersion));
      result = -1;
    }
    return result;
  }

  public static void main(String[] args) {
    System.out.println("ldd version: " + flatten(version()));
  }
}
