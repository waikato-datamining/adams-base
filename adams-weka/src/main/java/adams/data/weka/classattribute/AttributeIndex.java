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
 * AttributeIndex.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.weka.classattribute;

import adams.data.weka.WekaAttributeIndex;
import weka.core.Instances;

/**
 * Uses the supplied attribute index to select the class attribute.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AttributeIndex
  extends AbstractClassAttributeHeuristic {

  private static final long serialVersionUID = -912826971225798159L;

  /** the attribute index. */
  protected WekaAttributeIndex m_Index;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the supplied attribute index to select the class attribute.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "index", "index",
      new WekaAttributeIndex("last"));
  }

  /**
   * Sets the index of the attribute to select.
   *
   * @param value	the index
   */
  public void setIndex(WekaAttributeIndex value) {
    m_Index = value;
    reset();
  }

  /**
   * Returns the index of the attribute to select.
   *
   * @return		the index
   */
  public WekaAttributeIndex getIndex() {
    return m_Index;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indexTipText() {
    return "The index of the attribute to select.";
  }

  /**
   * Determines the class attribute index for the given dataset.
   *
   * @param data	the dataset to inspect
   * @return		the index, -1 if failed to determine
   */
  @Override
  public int determineClassAttribute(Instances data) {
    m_Index.setData(data);
    return m_Index.getIntIndex();
  }
}
