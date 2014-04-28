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
 * AbstractDisplayPanel.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import adams.flow.core.Token;
import adams.gui.core.BasePanel;

/**
 * Ancestor for panels that can be created from tokens.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDisplayPanel
  extends BasePanel
  implements DisplayPanel {

  /** for serialization. */
  private static final long serialVersionUID = -5927414957277106664L;

  /** the name of the panel. */
  protected String m_PanelName;

  /**
   * Initializes the panel.
   *
   * @param name	the name of the panel
   */
  public AbstractDisplayPanel(String name) {
    super();

    m_PanelName = name;
  }

  /**
   * Returns the name of the panel.
   *
   * @return		the name
   */
  public String getPanelName() {
    return m_PanelName;
  }

  /**
   * Displays the token.
   *
   * @param token	the token to display
   */
  public abstract void display(Token token);
  
  /**
   * Clears the content of the panel.
   */
  public abstract void clearPanel();
}