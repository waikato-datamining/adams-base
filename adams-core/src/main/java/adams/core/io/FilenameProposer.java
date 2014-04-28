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
 * FilenameProposer.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import java.io.File;

import adams.core.logging.LoggingObject;
import adams.env.Environment;

/**
 * A helper class for proposing file names.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FilenameProposer
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -7253929564700735410L;

  /** the prefix for the files (no path). */
  protected String m_Prefix;

  /** the suffix for the files (no dot). */
  protected String m_Suffix;

  /** the default directory. */
  protected String m_Directory;

  /**
   * Initializes the proposer with the user's home directory as default
   * directory.
   *
   * @param prefix	the prefix to use
   * @param suffix	the suffix to use
   */
  public FilenameProposer(String prefix, String suffix) {
    this(prefix, suffix, System.getProperty("user.home"));
  }

  /**
   * Initializes the proposer.
   *
   * @param prefix	the prefix to use
   * @param suffix	the suffix to use
   * @param directory	the default directory
   */
  public FilenameProposer(String prefix, String suffix, String directory) {
    super();

    m_Prefix    = prefix;
    m_Suffix    = suffix;
    if (m_Suffix.indexOf('.') > -1)
      m_Suffix = m_Suffix.substring(m_Suffix.lastIndexOf('.') + 1);
    m_Directory = directory;
  }

  /**
   * Returns the prefix in use.
   *
   * @return		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the suffix in use.
   *
   * @return		the suffix
   */
  public String getSuffix() {
    return m_Suffix;
  }

  /**
   * Sets the directory to use.
   *
   * @param value	the directory
   */
  public void setDirectory(String value) {
    m_Directory = value;
  }

  /**
   * Returns the directory in use.
   *
   * @return		the directory
   */
  public String getDirectory() {
    return m_Directory;
  }

  /**
   * Proposes a file name based on the given file/dir.
   *
   * @param basis	the file/dir to base the propose name on, can be null
   * @return		the proposed file
   */
  public PlaceholderFile propose(File basis) {
    PlaceholderFile	result;
    String		dir;
    String		prefix;
    int			counter;
    boolean		gzipped;

    gzipped = false;
    if (basis == null) {
      dir    = m_Directory;
      prefix = m_Prefix;
    }
    else if (basis.isDirectory()) {
      dir    = basis.getAbsolutePath();
      prefix = m_Prefix;
    }
    else {
      gzipped = basis.getName().toLowerCase().endsWith(".gz");
      dir     = basis.getParentFile().getAbsolutePath();
      prefix  = basis.getName().substring(0, basis.getName().replaceAll("\\.[gG][zZ]$", "").lastIndexOf('.'));
      prefix  = prefix.replaceAll("[0-9][0-9]*$", "");
    }

    if (dir.endsWith(File.separator + "."))
      dir = dir.substring(0, dir.length() - 1);

    counter = 1;
    do {
      counter++;
      result = new PlaceholderFile(
	  dir + File.separator + prefix + counter + "." + m_Suffix + (gzipped ? ".gz" : ""));
    }
    while (result.exists());

    return result;
  }

  /**
   * Returns a string representation of the proposer.
   *
   * @return		the representation
   */
  @Override
  public String toString() {
    return
        "directory=" + m_Directory
      + ", prefix=" + m_Prefix
      + ", suffix=" + m_Suffix;
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    FilenameProposer 	prop;
    File		file;

    Environment.setEnvironmentClass(Environment.class);

    System.out.println("--> default directory is home");
    prop = new FilenameProposer("new", ".flow");
    file = null;
    System.out.println(file + "\n  -> " + prop.propose(file));
    file = new File(".");
    System.out.println(file + "\n  -> " + prop.propose(file));
    file = new File("./blah.flow");
    System.out.println(file + "\n  -> " + prop.propose(file));
    file = new File("./blah.flow.gz");
    System.out.println(file + "\n  -> " + prop.propose(file));
  }
}
