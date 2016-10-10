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
 * AbstractInvestigatorTabHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.workspace;

import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab;

/**
 * Handler for serializing/deserializing a tab of the Weka Investigator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInvestigatorTabHandler {

  /**
   * Checks whether this handler can process the given tab.
   *
   * @param tab		the tab to check
   * @return		true if it can be processed
   */
  public abstract boolean handles(AbstractInvestigatorTab tab);

  /**
   * Generates a view of the tab that can be serialized.
   *
   * @param tab		the tab to serialize
   * @return		the data to serialize
   */
  public abstract Object serialize(AbstractInvestigatorTab tab);

  /**
   * Deserializes the data and configures the tab.
   *
   * @param tab		the tab to update
   * @param data	the serialized data to restore the tab with
   */
  public abstract void deserialize(AbstractInvestigatorTab tab, Object data);
}
