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
 * AbstractInvestigatorTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.gui.core.BasePanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;

/**
 * Ancestor for tabs in the Investigator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInvestigatorTab
  extends BasePanel {

  private static final long serialVersionUID = 1860821657853747908L;

  /** the owner. */
  protected InvestigatorPanel m_Owner;

  /**
   * Sets the owner for this tab.
   *
   * @param value	the owner
   */
  public void setOwner(InvestigatorPanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owner of this tab.
   *
   * @return		the owner, null if none set
   */
  public InvestigatorPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  public abstract String getTitle();

  /**
   * Notifies the tab that the data changed.
   */
  public abstract void dataChanged();
}
