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
 * EventRunnable.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

/**
 * Specialized {@link Runnable} class for events.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class EventRunnable<T extends Event>
  extends RunnableWithLogging {

  /** for serialization. */
  private static final long serialVersionUID = -5192907871210498502L;
  
  /** the owning event. */
  protected T m_Owner;
  
  /**
   * Sets the owning event.
   * 
   * @param owner	the owning event
   */
  public EventRunnable(T owner) {
    m_Owner = owner;
  }
  
  /**
   * Returns the owner.
   * 
   * @return		the event
   */
  public T getOwner() {
    return m_Owner;
  }
}
