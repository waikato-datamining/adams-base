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
 * Mat5ArrayPropertyExtractor.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.debug.propertyextractor;

import adams.data.matlab.MatlabUtils;
import nz.ac.waikato.cms.locator.ClassLocator;
import us.hebi.matlab.mat.types.Array;

/**
 * Property extractor for Matlab array data structures.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Mat5ArrayPropertyExtractor
  extends AbstractPropertyExtractor {

  /**
   * Checks whether this extractor actually handles this type of class.
   *
   * @param cls the class to check
   * @return true if the extractor handles the object/class
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(Array.class, cls);
  }

  /**
   * The number of properties that are available.
   *
   * @return the number of properties
   */
  @Override
  public int size() {
    return 6;
  }

  /**
   * Returns the label for the specified property.
   *
   * @param index the index of the property to get the label for
   * @return the label for the property
   */
  @Override
  public String getLabel(int index) {
    switch (index) {
      case 0:
        return "# dimensions";
      case 1:
	return "dimensions";
      case 2:
        return "# rows";
      case 3:
        return "# cols";
      case 4:
        return "# elements";
      case 5:
	return "type";
      default:
        throw new IllegalStateException("Unhandled index: " + index);
    }
  }

  /**
   * Returns the current value of the specified property.
   *
   * @param index the index of the property to retrieve
   * @return the current value of the property
   */
  @Override
  public Object getValue(int index) {
    switch (index) {
      case 0:
	return ((Array) m_Current).getNumDimensions();
      case 1:
	return MatlabUtils.arrayDimensionsToString((Array) m_Current);
      case 2:
	return ((Array) m_Current).getNumRows();
      case 3:
	return ((Array) m_Current).getNumCols();
      case 4:
	return ((Array) m_Current).getNumElements();
      case 5:
	return ((Array) m_Current).getType().name();
      default:
	throw new IllegalStateException("Unhandled index: " + index);
    }
  }
}
