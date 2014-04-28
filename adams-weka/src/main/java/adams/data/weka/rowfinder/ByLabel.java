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
 * ByLabel.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.rowfinder;

import java.util.ArrayList;

import weka.core.Instances;
import adams.core.Index;
import adams.core.Utils;
import adams.core.base.BaseRegExp;

/**
 * Returns indices of rows which label match the regular expression.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByLabel
  extends AbstractRowFinder {

  /** for serialization. */
  private static final long serialVersionUID = 2989233908194930918L;
  
  /** the attribute index to work on. */
  protected Index m_AttributeIndex;
  
  /** the regular expression to match the labels against. */
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
	    "att-index", "attributeIndex",
	    new Index(Index.LAST));

    m_OptionManager.add(
	    "reg-exp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));
  }

  /**
   * Sets the index of the attribute to perform the matching on.
   *
   * @param value	the index
   */
  public void setAttributeIndex(Index value) {
    m_AttributeIndex = value;
    reset();
  }

  /**
   * Returns the index of the attribute to perform the matching on.
   *
   * @return		the index
   */
  public Index getAttributeIndex() {
    return m_AttributeIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String attributeIndexTipText() {
    return "The index of the attribute to use for matching.";
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
    return "The regular expression to match the attribute's labels against.";
  }

  /**
   * Returns the rows of interest in the dataset.
   * 
   * @param data	the dataset to inspect
   * @return		the rows of interest
   */
  @Override
  protected int[] doFindRows(Instances data) {
    ArrayList<Integer>	result;
    int			i;
    int			index;
    
    result = new ArrayList<Integer>();
    
    m_AttributeIndex.setMax(data.numAttributes());
    index = m_AttributeIndex.getIntIndex();
    if (index == -1)
      throw new IllegalStateException("Invalid index '" + m_AttributeIndex.getIndex() + "'?");
    if (!data.attribute(index).isNominal())
      throw new IllegalStateException("Attribute at index '" + m_AttributeIndex.getIndex() + "' is not nominal!");
    
    for (i = 0; i < data.numInstances(); i++) {
      if (m_RegExp.isMatch(data.instance(i).stringValue(index)))
	result.add(i);
    }
    
    return Utils.toIntArray(result);
  }
}
