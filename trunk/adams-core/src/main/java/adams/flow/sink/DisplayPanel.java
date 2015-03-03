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
 * DisplayPanel.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import adams.core.CleanUpHandler;
import adams.flow.core.Token;

/**
 * Interface for panels that can be created from tokens.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface DisplayPanel
  extends CleanUpHandler {

  /**
   * Returns the name of the panel.
   *
   * @return		the name
   */
  public String getPanelName();

  /**
   * Displays the token.
   *
   * @param token	the token to display
   */
  public void display(Token token);
  
  /**
   * Clears the content of the panel.
   */
  public void clearPanel();
}