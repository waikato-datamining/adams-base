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
 * FileNameCompare.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.io.PlaceholderFile;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares two File objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FileNameCompare
  implements Comparator<Comparable>, Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -6202901070173617221L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return 
	"Comparator for files, only comparing the name and ignoring the path part.";
  }

  /**
   * Compares its two arguments for order.
   *
   * @param o1		the first object
   * @param o2		the second object
   * @return		-1 if o1&lt;o2, 0 if o1=o2 and 1 if o1&;gt;o2
   */
  public int compare(Comparable o1, Comparable o2) {
    File 	f1;
    File	f2;

    f1 = new PlaceholderFile("" + o1);
    f2 = new PlaceholderFile("" + o2);
    return f1.getName().compareTo(f2.getName());
  }

  /**
   * Indicates whether some other object is "equal to" this Comparator.
   *
   * @param obj	the object to compare with this Comparator
   * @return		true if the object is a DefaultCompare object as well
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof FileNameCompare);
  }
}