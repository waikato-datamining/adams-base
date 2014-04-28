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
 * FileChooserBookmark.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.chooser;

import java.io.Serializable;

import adams.core.io.PlaceholderDirectory;

/**
 * Represents a single filechooser bookmark.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileChooserBookmark
  implements Serializable, Comparable<FileChooserBookmark> {
  
  /** for serialization. */
  private static final long serialVersionUID = 4284430561284693010L;

  /** the name of the bookmark. */
  protected String m_Name;
  
  /** the associated directory. */
  protected PlaceholderDirectory m_Directory;
  
  /**
   * Initializes the bookmark with the given directory. Uses the directory's
   * name as name for the bookmark.
   * 
   * @param dir	the directory to use
   */
  public FileChooserBookmark(PlaceholderDirectory dir) {
    this(dir.getAbsoluteFile().getName(), dir);
  }
  
  /**
   * Initializes the bookmark with the given directory.
   * 
   * @param name	the name of the bookmark
   * @param dir	the directory to use
   */
  public FileChooserBookmark(String name, PlaceholderDirectory dir) {
    if ((name == null) || name.isEmpty())
      throw new IllegalArgumentException("Name cannot be null or empty!");
    if (dir == null)
      throw new IllegalArgumentException("Directory cannot be null!");
    
    m_Name      = name;
    m_Directory = dir;
  }    
  
  /**
   * Returns the name of the bookmark.
   * 
   * @return		the name
   */
  public String getName() {
    return m_Name;
  }
  
  /**
   * Returns the associated directory of the bookmark.
   * 
   * @return		the directory
   */
  public PlaceholderDirectory getDirectory() {
    return m_Directory;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <p/>
   * Only uses the name for comparison.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   */
  @Override
  public int compareTo(FileChooserBookmark o) {
    return getName().compareTo(o.getName());
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param   obj   the reference object with which to compare.
   * @return  <code>true</code> if this object is the same as the obj
   *          argument; <code>false</code> otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof FileChooserBookmark)
      return (compareTo((FileChooserBookmark) obj) == 0);
    else
      return false;
  }
  
  /**
   * Returns the name.
   * 
   * @return		the name
   */
  @Override
  public String toString() {
    return m_Name;
  }
}