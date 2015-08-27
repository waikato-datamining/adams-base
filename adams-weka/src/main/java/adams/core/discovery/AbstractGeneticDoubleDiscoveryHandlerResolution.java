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
 * AbstractGeneticDoubleDiscoveryHandlerResolution.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

import adams.core.Utils;
import adams.core.discovery.PropertyPath.PropertyContainer;

/**
 * Ancestor for genetic discovery handlers that handle double properties with
 * a specified number of splits.
 *
 * @author Dale (dale at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGeneticDoubleDiscoveryHandlerResolution
  extends AbstractGeneticDoubleDiscoveryHandler{

  private static final long serialVersionUID = -4401650612139991644L;

  /** the number of splits. */
  protected int m_Splits;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "splits", "splits",
      getDefaultSplits());
  }

  /**
   * Returns the default splits.
   *
   * @return		the default
   */
  protected abstract int getDefaultSplits();

  /**
   * Sets the splits.
   *
   * @param value	the splits
   */
  public void setSplits(int value) {
    if (getOptionManager().isValid("splits", value)) {
      m_Splits = value;
      reset();
    }
  }

  /**
   * Returns the minimum.
   *
   * @return		the minimum
   */
  public int getSplits() {
    return m_Splits;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String splitsTipText() {
    return "The number of doubles to use between max and min.";
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
   * Calculates the number of bits to use.
   *
   * @return		the number of bits
   */
  protected int calcNumBits(){
    return((int)(Math.floor(Utils.log2(m_Splits))+1));
  }

  /**
   * Returns the double value from the property container.
   *
   * @param cont	the container
   * @return		the value
   */
  protected abstract double getValue(PropertyContainer cont);

  /**
   * Returns the packed bits for the genetic algorithm.
   *
   * @param cont	the container to obtain the value from to turn into a string
   * @return		the bits
   */
  @Override
  protected String doPack(PropertyContainer cont) {
    return GeneticHelper.doubleToBits(getValue(cont), getMinimum(), getMaximum(), calcNumBits(), getSplits());
  }

  /**
   * Sets the double value in the property container.
   *
   * @param cont	the container
   * @param value	the value to set
   */
  protected abstract void setValue(PropertyContainer cont, double value);

  /**
   * Unpacks and applies the bits from the genetic algorithm.
   *
   * @param cont	the container to set the value for created from the string
   * @param bits	the bits to use
   */
  @Override
  protected void doUnpack(PropertyContainer cont, String bits) {
    setValue(cont, GeneticHelper.bitsToDouble(bits, getMinimum(), getMaximum(), getSplits()));
  }
}
