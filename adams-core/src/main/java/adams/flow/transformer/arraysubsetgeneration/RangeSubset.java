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
 * RangeSubset.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.arraysubsetgeneration;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.core.classmanager.ClassManager;

import java.lang.reflect.Array;

/**
 * Generates a subset of the array, using the specified array elements.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RangeSubset
  extends AbstractArraySubsetGenerator {

  private static final long serialVersionUID = 5301190370847086064L;

  /** the elements of the subset to extract. */
  protected Range m_Elements;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates a subset of the array, using the specified array elements.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "elements", "elements",
      new Range(Range.ALL));
  }

  /**
   * Sets the array elements to pick.
   *
   * @param value	the range of elements
   */
  public void setElements(Range value) {
    m_Elements = value;
    reset();
  }

  /**
   * Returns the array elements to pick.
   *
   * @return		the range of elements
   */
  public Range getElements() {
    return m_Elements;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String elementsTipText() {
    return "The range of elements to pick from the array.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "elements", m_Elements);
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
    Object 	result;
    int[]	indices;
    int		i;

    m_Elements.setMax(Array.getLength(array));
    indices  = m_Elements.getIntIndices();
    result = Array.newInstance(array.getClass().getComponentType(), indices.length);
    for (i = 0; i < indices.length; i++)
      Array.set(result, i, ClassManager.getSingleton().deepCopy(Array.get(array, indices[i])));

    return result;
  }
}
