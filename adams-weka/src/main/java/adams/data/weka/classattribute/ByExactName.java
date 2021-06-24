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
package adams.data.weka.classattribute;

import weka.core.Instances;

/**
 * Returns indices of columns which names match the exact name.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ByExactName
  extends AbstractClassAttributeHeuristic {

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
    return "Returns the attribute which name matches exactly the provided name.";
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
   * Determines the class attribute index for the given dataset.
   *
   * @param data	the dataset to inspect
   * @return		the index, -1 if failed to determine
   */
  @Override
  public int determineClassAttribute(Instances data) {
    int		result;
    int		i;

    result = -1;

    for (i = 0; i < data.numAttributes(); i++) {
      if (m_Name.equals(data.attribute(i).name())) {
	result = i;
	break;
      }
    }

    return result;
  }
}
