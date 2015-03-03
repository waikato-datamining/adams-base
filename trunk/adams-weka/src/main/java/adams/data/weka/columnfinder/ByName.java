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
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.columnfinder;

import java.util.ArrayList;

import weka.core.Instances;
import adams.core.Utils;
import adams.core.base.BaseRegExp;

/**
 * Returns indices of attributes which names match the regular expression.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByName
  extends AbstractColumnFinder {

  /** for serialization. */
  private static final long serialVersionUID = 2989233908194930918L;
  
  /** the regular expression to match the attribute names against. */
  protected BaseRegExp m_RegExp;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the indices of attributes which names match the provided regular expression.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reg-exp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));
  }

  /**
   * Sets the regular expression to use.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression in use.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String regExpTipText() {
    return "The regular expression to match the attribute names against.";
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
    
    result = new ArrayList<Integer>();
    
    for (i = 0; i < data.numAttributes(); i++) {
      if (m_RegExp.isMatch(data.attribute(i).name()))
	result.add(i);
    }
    
    return Utils.toIntArray(result);
  }
}
