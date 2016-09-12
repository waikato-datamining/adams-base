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
 * AbstractCatSwarmOptimizationIntegerDiscoveryHandler.java
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
public abstract class AbstractCatSwarmOptimizationIntegerDiscoveryHandler
  extends AbstractCatSwarmOptimizationDiscoveryHandler {

  private static final long serialVersionUID = -5442076178374142588L;

  /** the type of values to use. */
  protected NumericValueType m_Type;

  /** the minimum. */
  protected int m_Minimum;

  /** the maximum. */
  protected int m_Maximum;

  /** the list of values. */
  protected int[] m_List;

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
  protected abstract int getDefaultMinimum();

  /**
   * Sets the minimum.
   *
   * @param value	the minimum
   */
  public void setMinimum(int value) {
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
  public int getMinimum() {
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
  protected abstract int getDefaultMaximum();

  /**
   * Sets the maximum.
   *
   * @param value	the maximum
   */
  public void setMaximum(int value) {
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
  public int getMaximum() {
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
    int[]	list;

    parts = value.replaceAll("  ", " ").split(" ");
    list  = new int[parts.length];
    for (i = 0; i < parts.length; i++) {
      try {
	list[i] = Integer.parseInt(parts[i]);
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
	  result[i] = m_Random.nextInt(m_Maximum - m_Minimum) + m_Minimum;
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
   * Returns the integer value from the property container.
   *
   * @param cont	the container
   * @return		the values
   */
  protected abstract int[] getValue(PropertyContainer cont);

  /**
   * Returns the values for the swarm.
   *
   * @param cont	the containerto obtain the value from
   * @return		the values
   */
  @Override
  protected double[] doObtain(PropertyContainer cont) {
    double[]	result;
    int[]	value;
    int		index;
    int		i;
    int		n;

    value = getValue(cont);

    result = new double[value.length];
    for (n = 0; n < value.length; n++) {
      switch (m_Type) {
	case RANGE:
	  result[n] = value[n];
	  break;

	case LIST:
	  index = 0;
	  for (i = 0; i < m_List.length; i++) {
	    if (m_List[i] == value[n]) {
	      index = i;
	      break;
	    }
	  }
	  result[n] = index;
	  break;

	default:
	  throw new IllegalStateException("Unhandled numeric value type: " + m_Type);
      }
    }

    return result;
  }

  /**
   * Sets the integer value in the property container.
   *
   * @param cont	the container
   * @param value	the value to set
   */
  protected abstract void setValue(PropertyContainer cont, int[] value);

  /**
   * Unpacks and applies the bits from the genetic algorithm.
   *
   * @param cont	the container to set the value for created from the string
   * @param value	the bits to use
   */
  @Override
  protected void doApply(PropertyContainer cont, double[] value) {
    int index;
    int[] values;
    int n;

    values = new int[value.length];
    for (n = 0; n < value.length; n++) {
      switch (m_Type) {
	case RANGE:
	  values[n] = (int) Math.round(value[n]);
	  break;

	case LIST:
	  index = (int) Math.round(value[n]);
	  index = Math.max(0, index);
	  index = Math.min(m_List.length - 1, index);
	  values[n] = m_List[index];
	  break;

	default:
	  throw new IllegalStateException("Unhandled numeric value type: " + m_Type);
      }
    }
    setValue(cont, values);
  }
}
