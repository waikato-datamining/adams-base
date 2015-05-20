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
 * AbstractScriptlet.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core.gnuplot;

import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;

/**
 * Ancestor for scriplets that generate Gnuplot scripts (or parts of it).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractScriptlet
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 8269710957096517396L;

  /** the owning actor. */
  protected Actor m_Owner;

  /** the character for comments in Gnuplot scripts. */
  public final static String COMMENT = "#";

  /** stores the error message if the check failed. */
  protected String m_LastError;

  /**
   * Resets the scriptlet.
   */
  protected void reset() {
    super.reset();

    m_LastError = null;
  }

  /**
   * Sets the owning actor.
   *
   * @param value	the owner
   */
  public void setOwner(Actor value) {
    m_Owner = value;
  }

  /**
   * Returns the owning actor.
   *
   * @return		the owner
   */
  public Actor getOwner() {
    return m_Owner;
  }

  /**
   * Checks whether an error was encountered during the last generation.
   *
   * @return		true if an error was encountered
   */
  public boolean hasLastError() {
    return (m_LastError != null);
  }

  /**
   * Returns the error that occurred during the last generation.
   *
   * @return		the error, null if none occurred
   */
  public String getLastError() {
    return m_LastError;
  }
  
  /**
   * Hook method for performing checks.
   * <br><br>
   * Default implementation does nothing, only warns if not owner set.
   *
   * @return		null if all checks passed, otherwise error message
   */
  public String check() {
    if (getOwner() == null)
      getLogger().warning("No owning actor set!");
    return null;
  }

  /**
   * Generates the actual script code.
   *
   * @return		the script code, null in case of an error
   */
  protected abstract String doGenerate();
  
  /**
   * Returns the generated script-code string.
   *
   * @return		the script-code, null in case of an error
   */
  public String generate() {
    m_LastError = check();
    if (hasLastError())
      return null;
    return doGenerate();
  }
}
