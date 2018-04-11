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
 * AbstractGeneticStringDiscoveryHandler.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery.genetic;

import adams.core.Utils;
import adams.core.discovery.PropertyPath.PropertyContainer;
import adams.core.option.OptionUtils;

import java.util.logging.Level;

/**
 * Ancestor for genetic discovery handlers that handle string properties.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractGeneticStringDiscoveryHandler
  extends AbstractGeneticDiscoveryHandler {

  private static final long serialVersionUID = -5442076178374142588L;

  /** the list of values. */
  protected String[] m_List;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "list", "list",
      getDefaultList());
  }

  /**
   * Returns the default list.
   *
   * @return		the default
   */
  protected abstract String getDefaultList();

  /**
   * Sets the list of values to use (blank-separated).
   *
   * @param value	the list
   */
  public void setList(String value) {
    try {
      m_List = OptionUtils.splitOptions(value);
      reset();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to parse blank-separated list: " + value, e);
    }
  }

  /**
   * Returns the list of values to use (blank-separated).
   *
   * @return		the list
   */
  public String getList() {
    return OptionUtils.joinOptions(m_List);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String listTipText() {
    return "The list to values to use.";
  }

  /**
   * Calculates the number of bits.
   *
   * @return		the number of bits
   */
  protected int calcNumBits(){
    int range = m_List.length;
    return (int) (Math.floor(Utils.log2(range)) + 1);
  }

  /**
   * Returns the number of required bits.
   *
   * @return		the number of bits
   */
  public int getNumBits() {
    return calcNumBits();
  }

  /**
   * Returns the string value from the property container.
   *
   * @param cont	the container
   * @return		the value
   */
  protected abstract String getValue(PropertyContainer cont);

  /**
   * Returns the packed bits for the genetic algorithm.
   *
   * @param cont	the container to obtain the value from to turn into a string
   * @return		the bits
   */
  @Override
  protected String doPack(PropertyContainer cont) {
    String	value;
    int		index;
    int		i;

    value = getValue(cont);

    index = 0;
    for (i = 0; i < m_List.length; i++) {
      if (m_List[i].equals(value)) {
        index = i;
        break;
      }
    }
    return GeneticHelper.intToBits(index, 0, m_List.length, calcNumBits());
  }

  /**
   * Sets the string value in the property container.
   *
   * @param cont	the container
   * @param value	the value to set
   */
  protected abstract void setValue(PropertyContainer cont, String value);

  /**
   * Unpacks and applies the bits from the genetic algorithm.
   *
   * @param cont	the container to set the value for created from the string
   * @param bits	the bits to use
   */
  @Override
  protected void doUnpack(PropertyContainer cont, String bits) {
    int 	index;

    index = GeneticHelper.bitsToInt(bits, 0, m_List.length);
    index = Math.max(0, index);
    index = Math.min(m_List.length - 1, index);
    setValue(cont, m_List[index]);
  }
}
