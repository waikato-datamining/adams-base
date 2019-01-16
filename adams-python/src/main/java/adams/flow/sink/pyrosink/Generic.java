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

package adams.flow.sink.pyrosink;

import adams.core.Utils;
import adams.flow.core.Unknown;

/**
 * Generic call, just forwards any data to the remote object.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Generic
  extends AbstractPyroSink {

  private static final long serialVersionUID = -1549570432184636739L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generic call, just forwards any data to the remote object.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
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
      m_RemoteObject.call_oneway(m_MethodName, m_Input);
    }
    catch (Exception e) {
      result = Utils.handleException(
        this, "Failed to call remote method '" + m_MethodName + "' on remote object '" + m_RemoteObjectName + "'!", e);
    }

    return result;
  }
}
