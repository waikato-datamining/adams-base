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
 * SortContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.lister;

import adams.core.io.FileWrapper;

/**
 * A helper class for sorting files.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 13700 $
 */
public class SortContainer
  implements Comparable<SortContainer> {

  /** the file to be sorted. */
  protected FileWrapper m_File;

  /** used for sorting. */
  protected Comparable m_Sort;

  /**
   * Initializes the sort container.
   *
   * @param file	the file to sort
   * @param sorting	the type of sorting to perform
   */
  public SortContainer(FileWrapper file, Sorting sorting) {
    super();

    m_File = file;

    if (sorting == Sorting.NO_SORTING)
      m_Sort = null;
    else if (sorting == Sorting.SORT_BY_NAME)
      m_Sort = file.getActualFile().getAbsolutePath();
    else if (sorting == Sorting.SORT_BY_LAST_MODIFIED)
      m_Sort = file.getLastModified().getTime();
    else
      throw new IllegalArgumentException("Unhandled sorting: " + sorting);
  }

  /**
   * Returns the stored file.
   *
   * @return		the stored file
   */
  public FileWrapper getFile() {
    return m_File;
  }

  /**
   * Compares this container with the specified container for order. Returns a
   * negative integer, zero, or a positive integer as this container is less
   * than, equal to, or greater than the specified container.
   *
   * @param   o the subrange to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   */
  @Override
  public int compareTo(SortContainer o) {
    // no sorting?
    if (m_Sort == null)
      return 0;
    // some kind of sorting
    else
      return m_Sort.compareTo(o.m_Sort);
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj		the reference object with which to compare.
   * @return		true if this object is the same as the obj argument;
   * 			false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof SortContainer))
      return false;
    else
      return (compareTo((SortContainer) obj) == 0);
  }

  /**
   * Hashcode so can be used as hashtable key. Returns the hashcode of the
   * file.
   *
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    return m_File.hashCode();
  }

  /**
   * Returns a string representation of the file and the object used for
   * sorting.
   *
   * @return		the representation
   */
  @Override
  public String toString() {
    return "file=" + m_File.toString() + ", sorting=" + m_Sort;
  }
}
