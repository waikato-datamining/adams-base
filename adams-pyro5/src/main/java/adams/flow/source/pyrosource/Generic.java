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
 * Generic.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.pyrosource;

import adams.core.logging.LoggingHelper;
import adams.flow.core.Unknown;

/**
 * Generic call, just retrieves any data from the remote object.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Generic
  extends AbstractPyroSource {

  private static final long serialVersionUID = -1549570432184636739L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generic call, just retrieves any data from the remote object.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the classes of the generated objects
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Performs the actual call.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    try {
      m_Output = m_RemoteObject.call(m_MethodName);
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(
        this, "Failed to call remote method '" + m_MethodName + "' on remote object '" + m_RemoteObjectName + "'!", e);
    }

    return result;
  }
}
