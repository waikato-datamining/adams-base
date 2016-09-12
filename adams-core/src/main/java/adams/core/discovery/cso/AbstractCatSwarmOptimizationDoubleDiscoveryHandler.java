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
 * AbstractCatSwarmOptimizationDoubleDiscoveryHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery.cso;

import adams.core.Utils;
import adams.core.discovery.NumericValueType;
import adams.core.discovery.PropertyPath.PropertyContainer;
import adams.data.statistics.StatUtils;

/**
 * Ancestor for genetic discovery handlers that handle integer properties.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCatSwarmOptimizationDoubleDiscoveryHandler
  extends AbstractCatSwarmOptimizationDiscoveryHandler {

  private static final long serialVersionUID = -5442076178374142588L;

  /** the type of values to use. */
  protected NumericValueType m_Type;

  /** the minimum. */
  protected double m_Minimum;

  /** the maximum. */
  protected double m_Maximum;

  /** the list of values. */
  protected double[] m_List;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      getDefaultType());

    m_OptionManager.add(
      "minimum", "minimum",
      getDefaultMinimum());

    m_OptionManager.add(
      "maximum", "maximum",
      getDefaultMaximum());

    m_OptionManager.add(
      "list", "list",
      getDefaultList());
  }

  /**
   * Returns the default type.
   *
   * @return		the default
   */
  protected NumericValueType getDefaultType() {
    return NumericValueType.RANGE;
  }

  /**
   * Sets the type.
   *
   * @param value	the type
   */
  public void setType(NumericValueType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type.
   *
   * @return		the type
   */
  public NumericValueType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of value to use.";
  }

  /**
   * Returns the default minimum.
   *
   * @return		the default
   */
  protected abstract double getDefaultMinimum();

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
    String[]	parts;
    int		i;
    double[]	list;

    parts = value.replaceAll("  ", " ").split(" ");
    list  = new double[parts.length];
    for (i = 0; i < parts.length; i++) {
      try {
	list[i] = Double.parseDouble(parts[i]);
      }
      catch (Exception e) {
	getLogger().warning("Failed to parse '" + parts[i] + "' from list: " + value);
	return;
      }
    }
    m_List = list;

    reset();
  }

  /**
   * Returns the list of values to use (blank-separated).
   *
   * @return		the list
   */
  public String getList() {
    return Utils.flatten(StatUtils.toNumberArray(m_List), " ");
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
   * Returns the random double values for the swarm to initialize with.
   *
   * @return		the random double values
   */
  public double[] random() {
    double[]	result;
    int		i;

    result = new double[getDimensions()];

    for (i = 0; i < result.length; i++) {
      switch (m_Type) {
	case RANGE:
	  result[i] = m_Random.nextDouble() * (m_Maximum - m_Minimum) + m_Minimum;
	  break;

	case LIST:
	  result[i] = m_Random.nextInt(m_List.length);
	  break;

	default:
	  throw new IllegalStateException("Unhandled numeric value type: " + m_Type);
      }
    }

    return result;
  }

  /**
   * Returns the double values from the property container.
   *
   * @param cont	the container
   * @return		the values
   */
  protected abstract double[] getValue(PropertyContainer cont);

  /**
   * Returns the values for the swarm.
   *
   * @param cont	the container to obtain the value from
   * @return		the values
   */
  @Override
  protected double[] doObtain(PropertyContainer cont) {
    return getValue(cont);
  }

  /**
   * Sets the double value in the property container.
   *
   * @param cont	the container
   * @param value	the values to set
   */
  protected abstract void setValue(PropertyContainer cont, double[] value);

  /**
   * Unpacks and applies the values from the swarm.
   *
   * @param cont	the container to set the value for
   * @param value	the values to set
   */
  @Override
  protected void doApply(PropertyContainer cont, double[] value) {
    setValue(cont, value);
  }
}
