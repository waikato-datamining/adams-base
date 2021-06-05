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
 * Percentage.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.arraysubsetgeneration;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Uses the specified percentage to determine which nth elements to be used in the new array.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Percentage
  extends AbstractArraySubsetGenerator {

  private static final long serialVersionUID = 2489403745843315230L;

  /** the offset. */
  protected int m_Offset;

  /** the percentage to pick (0-100). */
  protected double m_Percentage;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified percentage to determine which nth elements to be used in the new array.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "offset", "offset",
      0);

    m_OptionManager.add(
      "percentage", "percentage",
      100.0, 0.0, 100.0);
  }

  /**
   * Sets the offset.
   *
   * @param value	the offset
   */
  public void setOffset(int value) {
    if (getOptionManager().isValid("offset", value)) {
      m_Offset = value;
      reset();
    }
  }

  /**
   * Returns the offset.
   *
   * @return		the offset
   */
  public int getOffset() {
    return m_Offset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String offsetTipText() {
    return "The offset; can be negative to shift the first element.";
  }

  /**
   * Sets the percentage to pick.
   *
   * @param value	the percentage to pick (0-100)
   */
  public void setPercentage(double value) {
    if (getOptionManager().isValid("percentage", value)) {
      m_Percentage = value;
      reset();
    }
  }

  /**
   * Returns the percentage to pick.
   *
   * @return		the percentage to pick (0-100)
   */
  public double getPercentage() {
    return m_Percentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nthTipText() {
    return "The percentage of elements to pick.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "offset", m_Offset, "offset: ");
    result += QuickInfoHelper.toString(this, "percentage", m_Percentage, ", %: ");

    return result;
  }

  /**
   * Generates the subset.
   *
   * @param array  the array to generate the subset from
   * @param errors for collecting errors
   * @return null in case of an error
   */
  @Override
  protected Object doGenerateSubset(Object array, MessageCollection errors) {
    Object		result;
    List<Object> 	values;
    int			i;
    int			len;
    double 		inc;

    values = new ArrayList<>();
    len    = Array.getLength(array);
    inc    = (double) len / (len * m_Percentage);
    i      = (int) Math.floor(m_Offset - 1 + inc);
    while (i < len) {
      if (i >= 0)
	values.add(Array.get(array, i));
      i = (int) Math.floor(i + inc);
    }

    result = newArray(array, values.size());
    for (i = 0; i < values.size(); i++)
      Array.set(result, i, values.get(i));

    return result;
  }
}
