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
 * ActorTreeTab.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tab;

import java.awt.BorderLayout;

import adams.gui.core.BaseScrollPane;
import adams.gui.flow.ActorTreePanel;

/**
 * Shows all available actors in a class tree.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorTreeTab
  extends AbstractEditorTab {

  /** for serialization. */
  private static final long serialVersionUID = 5660819043419968252L;

  /** the panel with the actors. */
  protected ActorTreePanel m_ActorTreePanel;

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_ActorTreePanel = new ActorTreePanel();
    add(new BaseScrollPane(m_ActorTreePanel), BorderLayout.CENTER);
  }

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Actors";
  }
}
