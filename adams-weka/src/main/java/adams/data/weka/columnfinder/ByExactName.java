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
 * ByExactName.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.columnfinder;

import adams.core.Utils;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Returns indices of columns which names match the exact name.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ByExactName
  extends AbstractColumnFinder {

  /** for serialization. */
  private static final long serialVersionUID = 2989233908194930918L;
  
  /** the string to match the attribute names against. */
  protected String m_Name;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the indices of columns which names match exactly the provided name.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "name", "name",
      "");
  }

  /**
   * Sets the name to use.
   *
   * @param value	the name
   */
  public void setName(String value) {
    m_Name = value;
    reset();
  }

  /**
   * Returns the name in use.
   *
   * @return		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String nameTipText() {
    return "The name to match the attribute names against.";
  }

  /**
   * Returns the columns of interest in the dataset.
   *
   * @param data	the dataset to inspect
   * @return		the columns of interest
   */
  @Override
  protected int[] doFindColumns(Instances data) {
    ArrayList<Integer>	result;
    int			i;

    result = new ArrayList<>();

    for (i = 0; i < data.numAttributes(); i++) {
      if (m_Name.equals(data.attribute(i).name()))
	result.add(i);
    }

    return Utils.toIntArray(result);
  }
}
