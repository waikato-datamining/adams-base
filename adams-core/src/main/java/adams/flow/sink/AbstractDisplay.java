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
 * AbstractDisplay.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.flow.core.InputConsumer;
import adams.flow.core.Token;

/**
 * Ancestor for actors that display stuff.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDisplay
  extends adams.flow.core.AbstractDisplay
  implements InputConsumer {

  /** for serialization. */
  private static final long serialVersionUID = 8175993838879683118L;

  /**
   * Before the token is displayed.
   * <br><br>
   * Default implementation does nothing.
   *
   * @param token	the token to display
   */
  protected void preDisplay(Token token) {
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  protected abstract void display(Token token);

  /**
   * After the token has been displayed.
   * <br><br>
   * Default implementation does nothing.
   *
   * @param token	the token to display
   */
  protected void postDisplay(Token token) {
  }

  /**
   * Returns a runnable that displays frame, etc.
   * Must call notifyAll() on the m_Self object and set m_Updating to false.
   *
   * @return		the runnable
   */
  @Override
  protected Runnable newDisplayRunnable() {
    Runnable	result;
    final Token	input;

    input  = m_InputToken;
    result = new Runnable() {
      public void run() {
	if (m_CreateFrame && !m_Frame.isVisible())
	  m_Frame.setVisible(true);
	if (input != null) {
	  try {
	    preDisplay(input);
	    display(input);
	    postDisplay(input);
	  }
	  catch (Exception e) {
	    handleException("Failed to display token " + input + ": ", e);
	  }
	}
	synchronized(m_Self) {
	  m_Self.notifyAll();
	}
	m_Updating = false;
      }
    };

    m_InputToken = null;

    return result;
  }
}
