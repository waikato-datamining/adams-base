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

import adams.core.Mergeable;
import adams.core.logging.LoggingObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Encapsulates the capabilities for an algorithm.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Capabilities
  extends LoggingObject
  implements Mergeable<Capabilities> {

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

  /** the minimum number of class attributes (-1 is undefined). */
  protected int m_MinClassColumns;

  /** the maximum number of class attributes (-1 is undefined). */
  protected int m_MaxClassColumns;

  /**
   * Initializes the capabilities with no owner.
   */
  public Capabilities() {
    this(null);
  }

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

    reset();
  }

  /**
   * Resets the capabilities.
   */
  protected void reset() {
    m_Capabilities.clear();
    m_DependentCapabilities.clear();
    m_MinRows         = -1;
    m_MaxRows         = -1;
    m_MinColumns      = -1;
    m_MaxColumns      = -1;
    m_MinClassColumns = -1;
    m_MaxClassColumns = -1;
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
    result.assign(this);

    return result;
  }

  /**
   * Uses the capabilities from the provided capabilities object.
   *
   * @param other	the capabilities to use instead
   */
  public void assign(Capabilities other) {
    reset();
    mergeWith(other);
  }

  /**
   * Widens the min/max values.
   *
   * @param current	the current value
   * @param proposed	the proposed value
   * @param up		whether a maximum (= true) or minimum (= false)
   * @return		the new value
   */
  protected int widen(int current, int proposed, boolean up) {
    // same?
    if (current == proposed)
      return current;

    // different
    if (up) {
      if (current == -1)
	return proposed;
      else if (proposed == -1)
	return current;
      else
	return Math.max(current, proposed);
    }
    else {
      if (current == -1)
	return proposed;
      else if (proposed == -1)
	return current;
      else
	return Math.min(current, proposed);
    }
  }

  /**
   * Merges with the capabilities from the provided capabilities object.
   * Min/Max properties get widened, if necessary, to accommodate both
   * capabilities. Capabilities and dependent ones simply get added.
   *
   * @param other	the capabilities to merge with
   */
  public void mergeWith(Capabilities other) {
    for (Capability cap: other.capabilities())
      enable(cap);
    for (Capability cap: other.dependentCapabilities())
      enableDependent(cap);
    
    setMinRows(widen(getMinRows(), other.getMinRows(), false));
    setMaxRows(widen(getMaxRows(), other.getMaxRows(), true));
    setMinColumns(widen(getMinColumns(), other.getMinColumns(), false));
    setMaxColumns(widen(getMaxColumns(), other.getMaxColumns(), true));
    setMinClassColumns(widen(getMinClassColumns(), other.getMinClassColumns(), false));
    setMaxClassColumns(widen(getMaxClassColumns(), other.getMaxClassColumns(), true));
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
    m_Capabilities.remove(cap);
  }

  /**
   * Enables all capabilities.
   *
   * @param caps	the capabilities to enable
   */
  public void enableAll(Collection<Capability> caps) {
    for (Capability cap: caps)
      enable(cap);
  }

  /**
   * Disables all capabilities.
   *
   * @param caps	the capabilities to disable
   */
  public void disableAll(Collection<Capability> caps) {
    for (Capability cap: caps)
      disable(cap);
  }

  /**
   * Enables all capabilities.
   */
  public void enableAll() {
    for (Capability cap: Capability.values())
      enable(cap);
  }

  /**
   * Disables all capabilities.
   */
  public void disableAll() {
    for (Capability cap: Capability.values())
      disable(cap);
  }

  /**
   * Enables all class capabilities.
   */
  public void enableAllClass() {
    for (Capability cap: Capability.values()) {
      if (cap.isClassRelated())
	enable(cap);
    }
  }

  /**
   * Disables all class capabilities.
   */
  public void disableAllClass() {
    for (Capability cap: Capability.values()) {
      if (cap.isClassRelated())
	disable(cap);
    }
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
    m_DependentCapabilities.remove(cap);
  }

  /**
   * Enables the dependent capabilities.
   *
   * @param caps	the capabilities to enable
   */
  public void enableAllDependent(Collection<Capability> caps) {
    for (Capability cap: caps)
      enableDependent(cap);
  }

  /**
   * Disables the dependent capabilities.
   *
   * @param caps	the capabilities to disable
   */
  public void disableAllDependent(Collection<Capability> caps) {
    for (Capability cap: caps)
      disableDependent(cap);
  }

  /**
   * Enables the dependent capabilities for all currently set capabilities.
   */
  public void enableAllDependent() {
    for (Capability cap: capabilities())
      enableDependent(cap);
  }

  /**
   * Disables all the dependent capabilities.
   */
  public void disableAllDependent() {
    for (Capability cap: dependentCapabilities())
      disableDependent(cap);
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
   * Sets the minimum number of required class columns.
   *
   * @param value	the minimum, -1 for undefined
   */
  public void setMinClassColumns(int value) {
    if (value < -1)
      value = -1;
    m_MinClassColumns = value;
  }

  /**
   * Returns the minimum number of required class columns.
   *
   * @return		the minimum, -1 if undefined
   */
  public int getMinClassColumns() {
    return m_MinClassColumns;
  }

  /**
   * Sets the maximum number of required class columns.
   *
   * @param value	the maximum, -1 for undefined
   */
  public void setMaxClassColumns(int value) {
    if (value < -1)
      value = -1;
    m_MaxClassColumns = value;
  }

  /**
   * Returns the maximum number of required class columns.
   *
   * @return		the maximum, -1 if undefined
   */
  public int getMaxClassColumns() {
    return m_MaxClassColumns;
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
    result.append("\n");
    result.append("Class columns: ").append("min=").append(getMinClassColumns()).append(", max=").append(getMaxClassColumns());

    return result.toString();
  }
}
