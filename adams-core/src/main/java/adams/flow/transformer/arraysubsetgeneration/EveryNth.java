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
 * EveryNth.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.arraysubsetgeneration;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Picks every nth element to be used in the new array.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EveryNth
  extends AbstractArraySubsetGenerator {

  private static final long serialVersionUID = 2489403745843315230L;

  /** the offset. */
  protected int m_Offset;

  /** which elements to pick. */
  protected int m_Nth;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Picks every nth element to be used in the new array.";
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
      "nth", "nth",
      1, 1, null);
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
   * Sets the elements to pick.
   *
   * @param value	the elements to pick
   */
  public void setNth(int value) {
    if (getOptionManager().isValid("nth", value)) {
      m_Nth = value;
      reset();
    }
  }

  /**
   * Returns the elements to pick.
   *
   * @return		the elements to pick
   */
  public int getNth() {
    return m_Nth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nthTipText() {
    return "Which elements to pick.";
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
    result += QuickInfoHelper.toString(this, "nth", m_Nth, ", nth: ");

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

    values = new ArrayList<>();
    len    = Array.getLength(array);
    i      = m_Offset - 1 + m_Nth;
    while (i < len) {
      if (i >= 0)
	values.add(Array.get(array, i));
      i += m_Nth;
    }

    result = newArray(array, values.size());
    for (i = 0; i < values.size(); i++)
      Array.set(result, i, values.get(i));

    return result;
  }
}
