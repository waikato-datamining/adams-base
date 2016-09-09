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
 * FileObjectComparator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for {@link FileObject} objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileObjectComparator
  implements Serializable, Comparator<FileObject> {

  private static final long serialVersionUID = -4630678890271018588L;

  /** whether to perform case-sensitive comparisons. */
  protected boolean m_CaseSensitive;

  /** whether to list directoris first. */
  protected boolean m_ListDirsFirst;

  /** whether to include parent directories in sorting. */
  protected boolean m_IncludeParentDirs;

  /**
   * Initializes the comparator.
   *
   * @param caseSensitive	true if to perform case-sensitive comparisons
   * @param listDirsFirst	whether to list directories first or to mix
   *                          them in with files
   * @param includeParentDirs	whether to include parent directories in the comparison
   */
  public FileObjectComparator(boolean caseSensitive, boolean listDirsFirst, boolean includeParentDirs) {
    m_CaseSensitive     = caseSensitive;
    m_ListDirsFirst     = listDirsFirst;
    m_IncludeParentDirs = includeParentDirs;
  }

  /**
   * Returns whether comparison is case-sensitive.
   *
   * @return		true if case-sensitive
   */
  public boolean isCaseSensitive() {
    return m_CaseSensitive;
  }

  /**
   * Returns whether to list directories first.
   *
   * @return		true if to list dirs first
   */
  public boolean isListDirsFirst() {
    return m_ListDirsFirst;
  }

  /**
   * Returns whether to include parent directories in the comparison.
   *
   * @return		true if included
   */
  public boolean isIncludeParentDirs() {
    return m_IncludeParentDirs;
  }

  /**
   * Compares the two file wrappers.
   *
   * @param o1	the first wrapper
   * @param o2	the second wrapper
   * @return		less than, equal to or greater than zero if the first
   * 			wrapper is less than, equal to or greater than the
   * 			second one.
   */
  @Override
  public int compare(FileObject o1, FileObject o2) {
    int	result;
    String	s1;
    String	s2;

    // set up comparison
    if (m_IncludeParentDirs) {
      s1 = o1.getFile().getAbsolutePath();
      s2 = o2.getFile().getAbsolutePath();
    }
    else {
      s1 = o1.getName();
      s2 = o2.getName();
    }
    if (!m_CaseSensitive) {
      s1 = s1.toLowerCase();
      s2 = s2.toLowerCase();
    }

    // compare
    if (m_ListDirsFirst) {
      if (o1.isDirectory() && o2.isDirectory())
	result = s1.compareTo(s2);
      else if (!o1.isDirectory() && !o2.isDirectory())
	result = s1.compareTo(s2);
      else if (o1.isDirectory())
	result = -1;
      else
	result = 1;
    }
    else {
      result = s1.compareTo(s2);
    }

    return result;
  }
}
