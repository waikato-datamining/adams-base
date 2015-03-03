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
 * TokenEventHandler.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.event.TokenEvent;
import adams.event.TokenListener;

/**
 * Interface for actors that support {@link TokenEvent}s and handle 
 * {@link TokenListener}s.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface TokenEventHandler
  extends Actor {

  /**
   * Adds the specified listener.
   * 
   * @param l		the listener to add
   */
  public void addTokenListener(TokenListener l);

  /**
   * Removes the specified listener.
   * 
   * @param l		the listener to remove
   */
  public void removeTokenListener(TokenListener l);

  /**
   * Returns the current listeners.
   * 
   * @param l		the listeners
   */
  public TokenListener[] tokenListeners();
  
  /**
   * Notifies all the {@link TokenListener}s with the event.
   * 
   * @param e		the event to send
   */
  public void notifyTokenListeners(TokenEvent e);
}
