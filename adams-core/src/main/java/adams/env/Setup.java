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
 * Setup.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.env;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import adams.core.Mergeable;

/**
 * A container class for a props file and the directories to look for.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Setup
  implements Serializable, Comparable, Mergeable<Setup> {

  /** for serialization. */
  private static final long serialVersionUID = 4280435277797929265L;

  /** the props file. */
  protected String m_PropertiesFile;

  /** the directories to look for. */
  protected List<String> m_Directories;

  /** keys (or regular expression of keys) that only the last props file is allowed to have. */
  protected List<String> m_Overrides;

  /**
   * Initializes the container.
   *
   * @param props	the properties file
   * @param dirs	the directories to search
   */
  public Setup(String props, List<String> dirs) {
    this(props, dirs, new String[0]);
  }

  /**
   * Initializes the container.
   *
   * @param props	the properties file
   * @param dirs	the directories to search
   * @param overrides	the keys (or regular expression of leys) to override with the last props file
   */
  public Setup(String props, List<String> dirs, String[] overrides) {
    super();

    m_PropertiesFile = props;
    m_Directories    = new ArrayList<String>(dirs);
    m_Overrides      = new ArrayList<String>();
    if (overrides != null) {
      for (int i = 0; i < overrides.length; i++)
        m_Overrides.add(overrides[i]);
    }
  }

  /**
   * Returns the properties file stored (incl path).
   *
   * @return		the props file
   */
  public String getPropertiesFile() {
    return m_PropertiesFile;
  }

  /**
   * Returns the filename of the properties file without the path.
   *
   * @return		the filename
   */
  public String getFilename() {
    return m_PropertiesFile.replaceAll(".*\\/", "");
  }

  /**
   * Returns the directories to search.
   *
   * @return		the directories
   */
  public List<String> getDirectories() {
    return m_Directories;
  }

  /**
   * Checks whether any overrides for keys are defined.
   *
   * @return		true if overrides available
   */
  public boolean hasOverrides() {
    return (m_Overrides.size() > 0);
  }

  /**
   * Returns the override keys (or regular expressions).
   *
   * @return		the keys (or regular expressions)
   */
  public List<String> getOverrides() {
    return m_Overrides;
  }

  /**
   * Merges its own data with the one provided by the specified setup.
   * But only if the filenames (without the path) are the same.
   *
   * @param other		the setup to merge with
   */
  public void mergeWith(Setup other) {
    HashSet<String>	cache;

    if (getFilename().equals(other.getFilename())) {
      // directories
      cache = new HashSet<String>(m_Directories);
      for (String dir: other.getDirectories()) {
        if (!cache.contains(dir))
          m_Directories.add(dir);
      }

      // overrides
      cache = new HashSet<String>(m_Overrides);
      for (String override: other.getOverrides()) {
        if (!cache.contains(override))
          m_Overrides.add(override);
      }
    }
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this
   * 			object is less than, equal to, or greater than the
   * 			specified object.
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    Setup	other;

    if (o == null)
      return 1;

    if (!(o instanceof Setup))
      return -1;

    other = (Setup) o;

    return m_PropertiesFile.compareTo(other.m_PropertiesFile);
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj	the reference object with which to compare.
   * @return		true if this object is the same as the obj argument;
   * 			false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    return (compareTo(obj) == 0);
  }

  /**
   * Hashcode so can be used as hashtable key. Returns the hashcode of the
   * props file string.
   *
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    return m_PropertiesFile.hashCode();
  }

  /**
   * Returns a string representation of the container.
   *
   * @return		the representation
   */
  @Override
  public String toString() {
    String	result;

    result = m_PropertiesFile + ": Directories=" + m_Directories;
    if (m_Overrides.size() > 0)
      result += ", Overrides=" + m_Overrides;

    return result;
  }
}