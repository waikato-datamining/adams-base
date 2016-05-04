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
 * Capabilities.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.capabilities;

import adams.core.logging.LoggingObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Encapsulates the capabilities for an algorithm.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Capabilities
  extends LoggingObject {

  private static final long serialVersionUID = -3901300565162711500L;

  /** the owner. */
  protected CapabilitiesHandler m_Owner;

  /** the capabilities. */
  protected Set<Capability> m_Capabilities;

  /** the dependent capabilities. */
  protected Set<Capability> m_DependentCapabilities;

  /** the minimum number of columns (-1 is undefined). */
  protected int m_MinRows;

  /** the maximum number of columns (-1 is undefined). */
  protected int m_MaxRows;

  /** the minimum number of columns (-1 is undefined). */
  protected int m_MinColumns;

  /** the maximum number of columns (-1 is undefined). */
  protected int m_MaxColumns;

  /**
   * Initializes the capabilities.
   *
   * @param owner	the owner
   */
  public Capabilities(CapabilitiesHandler owner) {
    super();

    setOwner(owner);

    m_Capabilities          = new HashSet<>();
    m_DependentCapabilities = new HashSet<>();
    m_MinRows               = -1;
    m_MaxRows               = -1;
    m_MinColumns            = -1;
    m_MaxRows               = -1;
  }

  /**
   * Sets the owner of these capabilities.
   *
   * @param value	the owner
   */
  public void setOwner(CapabilitiesHandler value) {
    m_Owner = value;
  }

  /**
   * Returns the owner of these capabilities.
   *
   * @return		the owner
   */
  public CapabilitiesHandler getOwner() {
    return m_Owner;
  }

  /**
   * Creates a clone of itself.
   *
   * @return		the clone
   */
  public Capabilities clone() {
    Capabilities	result;

    result = new Capabilities(getOwner());
    for (Capability cap: capabilities())
      result.enable(cap);
    for (Capability cap: dependentCapabilities())
      result.enableDependent(cap);

    result.setMinRows(getMinRows());
    result.setMaxRows(getMaxRows());
    result.setMinColumns(getMinColumns());
    result.setMaxColumns(getMaxColumns());

    return result;
  }

  /**
   * Returns the capabilities.
   *
   * @return		the capabilities
   */
  public Set<Capability> capabilities() {
    return m_Capabilities;
  }

  /**
   * Returns the dependent capabilities.
   *
   * @return		the capabilities
   */
  public Set<Capability> dependentCapabilities() {
    return m_DependentCapabilities;
  }

  /**
   * Enables the capability.
   *
   * @param cap		the capability to enable
   */
  public void enable(Capability cap) {
    m_Capabilities.add(cap);
  }

  /**
   * Disables the capability.
   *
   * @param cap		the capability to disable
   */
  public void disable(Capability cap) {
    m_Capabilities.add(cap);
  }

  /**
   * Returns whether the specified capability is enabled, i.e., supported.
   *
   * @param cap		the capability to check
   * @return		true if enabled
   */
  public boolean isEnabled(Capability cap) {
    return m_Capabilities.contains(cap);
  }

  /**
   * Enables the dependent capability.
   *
   * @param cap		the capability to enable
   */
  public void enableDependent(Capability cap) {
    m_DependentCapabilities.add(cap);
  }

  /**
   * Disables the dependent capability.
   *
   * @param cap		the capability to disable
   */
  public void disableDependent(Capability cap) {
    m_DependentCapabilities.add(cap);
  }

  /**
   * Returns whether the specified dependent capability is enabled, i.e.,
   * supported.
   *
   * @param cap		the capability to check
   * @return		true if enabled
   */
  public boolean isDependentEnabled(Capability cap) {
    return m_DependentCapabilities.contains(cap);
  }

  /**
   * Sets the minimum number of required columns.
   *
   * @param value	the minimum, -1 for undefined
   */
  public void setMinRows(int value) {
    if (value < -1)
      value = -1;
    m_MinRows = value;
  }

  /**
   * Returns the minimum number of required columns.
   *
   * @return		the minimum, -1 if undefined
   */
  public int getMinRows() {
    return m_MinRows;
  }

  /**
   * Sets the maximum number of required columns.
   *
   * @param value	the maximum, -1 for undefined
   */
  public void setMaxRows(int value) {
    if (value < -1)
      value = -1;
    m_MaxRows = value;
  }

  /**
   * Returns the maximum number of required columns.
   *
   * @return		the maximum, -1 if undefined
   */
  public int getMaxRows() {
    return m_MaxRows;
  }

  /**
   * Sets the minimum number of required columns.
   *
   * @param value	the minimum, -1 for undefined
   */
  public void setMinColumns(int value) {
    if (value < -1)
      value = -1;
    m_MinColumns = value;
  }

  /**
   * Returns the minimum number of required columns.
   *
   * @return		the minimum, -1 if undefined
   */
  public int getMinColumns() {
    return m_MinColumns;
  }

  /**
   * Sets the maximum number of required columns.
   *
   * @param value	the maximum, -1 for undefined
   */
  public void setMaxColumns(int value) {
    if (value < -1)
      value = -1;
    m_MaxColumns = value;
  }

  /**
   * Returns the maximum number of required columns.
   *
   * @return		the maximum, -1 if undefined
   */
  public int getMaxColumns() {
    return m_MaxColumns;
  }

  /**
   * Returns a short description of itself.
   *
   * @return		the description
   */
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder();

    result.append("Owner: ").append(getOwner().getClass().getName());
    result.append("\n");
    result.append("Capabilities: ").append(capabilities().toString());
    result.append("\n");
    result.append("Dependent capabilities: ").append(dependentCapabilities().toString());
    result.append("\n");
    result.append("Rows: ").append("min=").append(getMinRows()).append(", max=").append(getMaxRows());
    result.append("\n");
    result.append("Columns: ").append("min=").append(getMinColumns()).append(", max=").append(getMaxColumns());

    return result.toString();
  }
}
