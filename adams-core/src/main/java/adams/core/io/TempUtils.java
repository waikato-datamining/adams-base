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
 * TempUtils.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import adams.core.management.ProcessUtils;

import java.io.File;

/**
 * Functionality related to temporary directory and temporary files.
 * <br>
 * The temp directory can be overridden using the {@link #PROPERTY_TMPDIR}
 * property.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TempUtils {

  /** property indicating the temp directory to use. */
  public final static String PROPERTY_TMPDIR = "adams.io.tmpdir";

  /** the counter for temp file names. */
  protected static long m_TempFileCounter;
  static {
    m_TempFileCounter = 0;
  }

  /** the temporary directory. */
  protected static String m_TempDir;
  static {
    m_TempDir = null;
  }

  /**
   * Creates a temp file name in the specified directory.
   *
   * @param dir		the directory for the temp file, use null for user's temp dir
   * @param prefix	the prefix for the name, can be null
   * @param suffix	the suffix, eg the extension, can be null
   * @return		the generated file name
   */
  public static synchronized File createTempFile(PlaceholderDirectory dir, String prefix, String suffix) {
    String	filename;

    m_TempFileCounter++;

    if (dir == null)
      filename = getTempDirectory() + File.separator;
    else
      filename = dir.getAbsolutePath() + File.separator;

    if (prefix != null)
      filename += prefix;

    filename += Long.toHexString(System.nanoTime())
	+ "-" + Long.toHexString(ProcessUtils.getVirtualMachinePID())
	+ "-" + Long.toHexString(m_TempFileCounter);

    if (suffix != null)
      filename += suffix;

    return new File(filename);
  }

  /**
   * Creates a temp file name in the user's temp directory.
   *
   * @param prefix	the prefix for the name, can be null
   * @param suffix	the suffix, eg the extension, can be null
   * @return		the generated file name
   */
  public static synchronized File createTempFile(String prefix, String suffix) {
    return createTempFile(null, prefix, suffix);
  }

  /**
   * Assembles a temp file in the user's temp directory.
   *
   * @param file	the file (w/o path) to place in the temp directory
   * @return		the generated file name
   */
  public static synchronized File createTempFile(String file) {
    return new File(getTempDirectoryStr() + File.separator + file);
  }

  /**
   * Returns the temporary directory as string.
   *
   * @return	the temp directory
   */
  public static synchronized String getTempDirectoryStr() {
    String dir;
    File	file;

    if (m_TempDir == null) {
      dir = System.getProperty(PROPERTY_TMPDIR);
      if (dir == null) {
	dir = System.getProperty("java.io.tmpdir");
      }
      else {
	file = new File(dir);
	if (!file.exists()) {
	  if (!file.mkdirs()) {
	    dir = System.getProperty("java.io.tmpdir");
	    System.err.println(
	      "Failed to create temp directory '" + file + "', reverting back to system's default: " + dir);
	  }
	}
      }
      m_TempDir = dir;
    }

    return m_TempDir;
  }

  /**
   * Returns the temporary directory.
   *
   * @return	the temp directory
   */
  public static File getTempDirectory() {
    return new File(getTempDirectoryStr());
  }
}
