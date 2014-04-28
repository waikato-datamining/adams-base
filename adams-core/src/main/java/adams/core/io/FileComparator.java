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
 * FileComparator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import java.io.File;
import java.util.Comparator;

/**
 * Comparator for files.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileComparator
  implements Comparator<File> {

  /** whether to compare the files case-sensitive. */
  protected boolean m_CaseSensitive;
  
  /** whether to compare only the name, not the path. */
  protected boolean m_OnlyName;
  
  /**
   * Initializes the comparator.
   * 
   * @param caseSensitive	whether to perform case-sensitive comparison
   * @param onlyName		whether to compare only the name
   */
  public FileComparator(boolean caseSensitive, boolean onlyName) {
    super();
    
    m_CaseSensitive = caseSensitive;
    m_OnlyName      = onlyName;
  }
  
  /**
   * Compares its two arguments for order.  Returns a negative integer,
   * zero, or a positive integer as the first argument is less than, equal
   * to, or greater than the second.<p>
   *
   * In the foregoing description, the notation
   * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
   * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
   * <tt>0</tt>, or <tt>1</tt> according to whether the value of
   * <i>expression</i> is negative, zero or positive.<p>
   *
   * @param o1 the first object to be compared.
   * @param o2 the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the
   * 	       first argument is less than, equal to, or greater than the
   *	       second.
   * @throws ClassCastException if the arguments' types prevent them from
   * 	       being compared by this comparator.
   */
  @Override
  public int compare(File o1, File o2) {
    String	s1;
    String	s2;
    
    if ((o1 == null) && (o2 == null))
      return 0;
    if (o1 == null)
      return 1;
    if (o2 == null)
      return -1;

    if (m_OnlyName) {
      s1 = o1.getName();
      s2 = o2.getName();
    }
    else {
      s1 = o1.getAbsolutePath();
      s2 = o2.getAbsolutePath();
    }
    
    if (!m_CaseSensitive) {
      s1 = s1.toLowerCase();
      s2 = s2.toLowerCase();
    }
    
    return s1.compareTo(s2);
  }

}
