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
 * RemoveDataListener.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output.classifiererrors;

import adams.data.spreadsheet.SpreadSheet;

/**
 * Interface for listeners that remove the data.
 */
public interface RemoveDataListener {

  /**
   * Gets called when the user clicks on the "Remove" button.
   *
   * @param data the data to remove
   */
  public void removeData(SpreadSheet data);
}
