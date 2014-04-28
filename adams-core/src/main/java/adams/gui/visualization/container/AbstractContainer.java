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
 * Container.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

import java.io.Serializable;

/**
 * A container that is displayed in the list.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractContainer
  implements Serializable, Comparable<AbstractContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 8203562082477741415L;

  /** the manager this container belongs to. */
  protected AbstractContainerManager m_Manager;

  /** the payload. */
  protected Comparable m_Payload;

  /** whether an update is currently in progress and notifications are
   * suppressed. */
  protected boolean m_Updating;

  /**
   * Initializes the container.
   *
   * @param manager	the manager this container belongs to
   * @param payload	the payload of this container
   */
  protected AbstractContainer(AbstractContainerManager manager, Comparable payload) {
    super();
    setManager(manager);
    setPayload(payload);
    initialize();
  }

  /**
   * Initializes members.
   */
  protected void initialize() {
    m_Updating = false;
  }

  /**
   * For invalidating cached data.
   * <p/>
   * Default implementation does nothing.
   */
  protected void invalidate() {
  }

  /**
   * Sets the container manager to use.
   *
   * @param value	the manager
   */
  public void setManager(AbstractContainerManager value) {
    m_Manager = value;
  }

  /**
   * Returns the current manager.
   *
   * @return		the manager
   */
  public AbstractContainerManager getManager() {
    return m_Manager;
  }

  /**
   * Sets the payload. Calls invalidate().
   *
   * @param value	the new payload
   * @see		#invalidate()
   * @see		#postProcessPayload()
   */
  public void setPayload(Comparable value) {
    invalidate();
    m_Payload = value;
    postProcessPayload();
  }

  /**
   * For post-processing the payload, just after it got set.
   * <p/>
   * Default implementation does nothing.
   */
  protected void postProcessPayload() {
  }

  /**
   * Returns the current payload.
   *
   * @return		the payload
   */
  public Comparable getPayload() {
    return m_Payload;
  }

  /**
   * Updates itself with the values from given container (the manager is
   * excluded!). Derived classes need to override this method.
   *
   * @param c		the container to get the values from
   */
  public void assign(AbstractContainer c) {
    m_Updating = true;

    setPayload(c.getPayload());

    m_Updating = false;
  }

  /**
   * Returns a shallow copy of itself.
   *
   * @return		the copy
   */
  public AbstractContainer copy() {
    AbstractContainer		result;

    if (getManager() != null) {
      result = getManager().newContainer(getPayload());
      result.assign(this);
    }
    else {
      result = null;
    }

    return result;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  public int compareTo(AbstractContainer o) {
    if (o == null)
      return 1;

    if ((getPayload() == null) || (o.getPayload() == null)) {
      if ((getPayload() == null) && (o.getPayload() == null))
	return 0;
      else if (getPayload() == null)
	return -1;
      else
	return 1;
    }
    else {
      return getPayload().compareTo(o.getPayload());
    }
  }

  /**
   * Returns whether the two containers have the same ID.
   *
   * @param o		the object to compare with
   * @return		true if the ID is the same
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AbstractContainer))
      return false;
    else
      return (compareTo((AbstractContainer) o) == 0);
  }

  /**
   * Returns a string representation of the payload.
   *
   * @return		a string representation
   */
  @Override
  public String toString() {
    return "" + m_Payload;
  }
}