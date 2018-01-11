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
 * ByName.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.weka.classattribute;

import adams.core.base.BaseRegExp;
import weka.core.Instances;

/**
 * The first attribute name that matches the regular expression is used as class attribute.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByName
  extends AbstractClassAttributeHeuristic {

  private static final long serialVersionUID = -912826971225798159L;

  /** the regular expression to use on the name. */
  protected BaseRegExp m_RegExp;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The first attribute name that matches the regular expression is used as class attribute.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp("class"));
  }

  /**
   * Sets the index of the attribute to select.
   *
   * @param value	the index
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to use for locating the class attribute.
   *
   * @return		the expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression to use on the attribute names for locating the class attribute.";
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
      if (m_RegExp.isMatch(data.attribute(i).name())) {
	result = i;
	break;
      }
    }

    return result;
  }
}
