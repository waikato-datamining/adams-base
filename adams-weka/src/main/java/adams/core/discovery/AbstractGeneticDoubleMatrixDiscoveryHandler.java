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
 * AbstractGeneticIntegerArrayDiscoveryHandler.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

import adams.core.Utils;
import adams.core.discovery.PropertyPath.PropertyContainer;
import weka.core.matrix.Matrix;

/**
 * Ancestor for genetic discovery handlers that handle matrix properties.
 *
 * @author Dale (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGeneticDoubleMatrixDiscoveryHandler
  extends AbstractGeneticDiscoveryHandler {

  private static final long serialVersionUID = 765007046767066355L;
  /** the minimum. */
  protected double m_Minimum;

  /** the maximum. */
  protected double m_Maximum;

  /** num rows */
  protected int m_Rows;

  /** num columns */
  protected int m_Columns;

  /** the number of splits. */
  protected int m_Splits;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "minimum", "minimum",
      getDefaultMinimum());

    m_OptionManager.add(
      "maximum", "maximum",
      getDefaultMaximum());

    m_OptionManager.add(
      "splits", "splits",
      getDefaultSplits());

    m_OptionManager.add(
      "rows", "rows",
      getDefaultRows());
    m_OptionManager.add(
      "columns", "columns",
      getDefaultColumns());

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
   * Returns the default size.
   *
   * @return		the default
   */
  protected abstract int getDefaultRows();

  /**
   * Returns the default size.
   *
   * @return		the default
   */
  protected abstract int getDefaultColumns();

  /**
   * Returns the default minimum.
   *
   * @return		the default
   */
  protected abstract double getDefaultMinimum();

  /**
   * Sets the size of array.
   *
   * @param value	the size
   */
  public void setColumns(int value) {
    if (getOptionManager().isValid("columns", value)) {
      m_Columns = value;
      reset();
    }
  }

  /**
   * Returns the size.
   *
   * @return		the size
   */
  public int getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsTipText() {
    return "The columns to use.";
  }
  /**
   * Sets the size of array.
   *
   * @param value	the size
   */
  public void setRows(int value) {
    if (getOptionManager().isValid("rows", value)) {
      m_Rows = value;
      reset();
    }
  }

  /**
   * Returns the size.
   *
   * @return		the size
   */
  public int getRows() {
    return m_Rows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowsTipText() {
    return "The rows to use.";
  }

  /**
   * Sets the minimum.
   *
   * @param value	the minimum
   */
  public void setMinimum(double value) {
    if (getOptionManager().isValid("minimum", value)) {
      m_Minimum = value;
      reset();
    }
  }

  /**
   * Returns the minimum.
   *
   * @return		the minimum
   */
  public double getMinimum() {
    return m_Minimum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minimumTipText() {
    return "The minimum to use.";
  }


  /**
   * Returns the default maximum.
   *
   * @return		the default
   */
  protected abstract double getDefaultMaximum();

  /**
   * Sets the maximum.
   *
   * @param value	the maximum
   */
  public void setMaximum(double value) {
    if (getOptionManager().isValid("maximum", value)) {
      m_Maximum = value;
      reset();
    }
  }

  /**
   * Returns the maximum.
   *
   * @return		the maximum
   */
  public double getMaximum() {
    return m_Maximum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maximumTipText() {
    return "The maximum to use.";
  }

  /**
   * Returns the integer value from the property container.
   *
   * @param cont	the container
   * @return		the value
   */
  protected abstract Matrix getValue(PropertyContainer cont);

  /**
   * Returns the packed bits for the genetic algorithm.
   *
   * @param cont	the container to obtain the value from to turn into a string
   * @return		the bits
   */
  @Override
  protected String doPack(PropertyContainer cont) {
    return GeneticHelper.matrixToBits(getValue(cont), getMinimum(), getMaximum(), calcNumBits(),getSplits(),getRows(),getColumns());
  }

  /**
   * Sets the integer value in the property container.
   *
   * @param cont	the container
   * @param value	the value to set
   */
  protected abstract void setValue(PropertyContainer cont, Matrix value);

  /**
   * Unpacks and applies the bits from the genetic algorithm.
   *
   * @param cont	the container to set the value for created from the string
   * @param bits	the bits to use
   */
  @Override
  protected void doUnpack(PropertyContainer cont, String bits) {
    setValue(cont, GeneticHelper.bitsToMatrix(bits, getMinimum(), getMaximum(), calcNumBits(), getSplits(), getRows(),getColumns()));
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
   * Returns the number of required bits.
   *
   * @return		the number of bits
   */
  public int getNumBits() {
    return calcNumBits() * getRows() * getColumns();
  }
}
