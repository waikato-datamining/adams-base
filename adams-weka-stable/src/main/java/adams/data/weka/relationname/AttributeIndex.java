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
 * NoChange.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.weka.relationname;

import adams.data.weka.WekaAttributeIndex;
import weka.core.Instances;

import java.io.File;

/**
 * Uses the name of the specified attribute as relation name.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AttributeIndex
  extends AbstractRelationNameHeuristic {

  private static final long serialVersionUID = 5951436518450210725L;

  /** the attribute index. */
  protected WekaAttributeIndex m_Index;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the name of the specified attribute as relation name.";
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
    return "The index of the attribute to use for the relation name.";
  }

  /**
   * Determines the relation name for the given file/dataset pair.
   *
   * @param file	the file the dataset was loaded from (maybe null)
   * @param data	the loaded dataset
   * @return		the relation name, null if failed to determine
   */
  @Override
  public String determineRelationName(File file, Instances data) {
    int		index;

    m_Index.setData(data);
    index = m_Index.getIntIndex();
    if (index == -1)
      return null;

    return data.attribute(index).name();
  }
}
