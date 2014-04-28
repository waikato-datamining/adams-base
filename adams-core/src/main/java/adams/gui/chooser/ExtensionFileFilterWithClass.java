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
 * ExtensionFileFilterWithClass.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.gui.core.ExtensionFileFilter;

/**
 * A custom filter class that stores the associated class along the
 * description and extensions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExtensionFileFilterWithClass
  extends ExtensionFileFilter {

  /** for serialization. */
  private static final long serialVersionUID = 5863117558505811134L;

  /** the classname. */
  protected String m_Classname;

  /**
   * Constructs a filter that matches all files.
   *
   * @param classname		the classname this filter is for
   */
  public ExtensionFileFilterWithClass(String classname) {
    super();

    m_Classname = classname;
  }

  /**
   * Constructs a filter that matches files with the given extension, not
   * case-sensitive.
   *
   * @param classname		the classname this filter is for
   * @param description	the display string
   * @param extension		the extensions of the files (no dot!)
   */
  public ExtensionFileFilterWithClass(String classname, String description, String extension) {
    super(description, extension);

    m_Classname = classname;
  }

  /**
   * Constructs a filter that matches files with the given extension, not
   * case-sensitive.
   *
   * @param classname		the classname this filter is for
   * @param description	the display string
   * @param extensions	the extensions of the files (no dot!)
   */
  public ExtensionFileFilterWithClass(String classname, String description, String[] extensions) {
    super(description, extensions);

    m_Classname = classname;
  }

  /**
   * Constructs a filter that matches files with the given extension, not
   * case-sensitive.
   *
   * @param classname		the classname this filter is for
   * @param description	the display string
   * @param extension		the extensions of the files (no dot!)
   * @param caseSensitive	if true then the filter is case-sensitive
   */
  public ExtensionFileFilterWithClass(String classname, String description, String extension, boolean caseSensitive) {
    super(description, extension, caseSensitive);

    m_Classname = classname;
  }

  /**
   * Constructs a filter that matches files with the given extension, not
   * case-sensitive.
   *
   * @param classname		the classname this filter is for
   * @param description	the display string
   * @param extensions	the extensions of the files (no dot!)
   * @param caseSensitive	if true then the filter is case-sensitive
   */
  public ExtensionFileFilterWithClass(String classname, String description, String[] extensions, boolean caseSensitive) {
    super(description, extensions, caseSensitive);

    m_Classname = classname;
  }

  /**
   * Returns the associated classname.
   *
   * @return		the classname
   */
  public String getClassname() {
    return m_Classname;
  }

  /**
   * Compares this image format with the specified filter for order. Returns a
   * negative integer, zero, or a positive integer as this filter is less
   * than, equal to, or greater than the specified filter.
   *
   * @param   o the filter to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   */
  @Override
  public int compareTo(ExtensionFileFilter o) {
    int					result;
    ExtensionFileFilterWithClass	filter;

    result = super.compareTo(o);

    if (o instanceof ExtensionFileFilterWithClass) {
      filter = (ExtensionFileFilterWithClass) o;
      if (result == 0)
        result = m_Classname.compareTo(filter.m_Classname);
    }

    return result;
  }
}