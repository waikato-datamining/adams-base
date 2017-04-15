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
 * ApplicationContext.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.application;

import adams.gui.event.RemoteScriptingEngineUpdateListener;
import adams.scripting.engine.RemoteScriptingEngine;

/**
 * The application context.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ApplicationContext {

  /**
   * Adds the scripting engine to execute. Doesn't stop any running engines.
   *
   * @param value	the engine to add
   */
  public void addRemoteScriptingEngine(RemoteScriptingEngine value);

  /**
   * Removes the scripting engine (and stops it). Doesn't stop any running engines.
   *
   * @param value	the engine to remove
   */
  public void removeRemoteScriptingEngine(RemoteScriptingEngine value);

  /**
   * Sets the scripting engine to execute. Any running engine is stopped first.
   *
   * @param value	the engine to use, null to turn off scripting
   */
  public void setRemoteScriptingEngine(RemoteScriptingEngine value);

  /**
   * Returns the current scripting engine if any.
   *
   * @return		the engine in use, null if none running
   */
  public RemoteScriptingEngine getRemoteScriptingEngine();

  /**
   * Adds the listener for remote scripting engine changes.
   *
   * @param l		the listener
   */
  public void addRemoteScriptingEngineUpdateListener(RemoteScriptingEngineUpdateListener l);

  /**
   * Removes the listener for remote scripting engine changes.
   *
   * @param l		the listener
   */
  public void removeRemoteScriptingEngineUpdateListener(RemoteScriptingEngineUpdateListener l);
}
