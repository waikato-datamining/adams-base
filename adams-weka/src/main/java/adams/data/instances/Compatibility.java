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
 * Compatibility.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.instances;

import weka.core.Attribute;
import weka.core.Instances;

/**
 * Checks the compatibility of datasets.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Compatibility {

  /**
   * Does not require the attribute names to match.
   *
   * @param data1	the first dataset in the comparison
   * @param data2	the second dataset in the comparison
   * @return		null if compatible, otherwise mismatch message
   */
  public static String compareStrict(Instances data1, Instances data2) {
    return data1.equalHeadersMsg(data2);
  }

  /**
   * Does not require the attribute names to match.
   *
   * @param data1	the first dataset in the comparison
   * @param data2	the second dataset in the comparison
   * @return		null if compatible, otherwise mismatch message
   */
  public static String compareLenient(Instances data1, Instances data2) {
      if (data1.classIndex() != data2.classIndex())
	return "Class index differ: " + (data1.classIndex() + 1) + " != " + (data2.classIndex() + 1);

      if (data1.numAttributes() != data2.numAttributes())
	return "Different number of attributes: " + data1.numAttributes() + " != " + data2.numAttributes();

      for (int i = 0; i < data1.numAttributes(); i++) {
	if (data1.attribute(i).type() != data2.attribute(i).type())
	  return "Attributes differ at position " + (i + 1) + ":\n" + Attribute.typeToString(data1.attribute(i).type()) + " != " + Attribute.typeToString(data2.attribute(i).type());
      }

      return null;
  }

  /**
   * Checks the compatibility of two datasets.
   *
   * @param data1	the first dataset
   * @param data2	the second dataset
   * @param strict	if enabled, attribute names must match as well
   * @return		null if compatible, other mismatch message
   */
  public static String isCompatible(Instances data1, Instances data2, boolean strict) {
    if (strict)
      return compareStrict(data1, data2);
    else
      return compareLenient(data1, data2);
  }
}
