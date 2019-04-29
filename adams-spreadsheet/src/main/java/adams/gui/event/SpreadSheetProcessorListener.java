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
 * SpreadSheetProcessorListener.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

/**
 * Interface for listeners that react to changes in a {@link adams.gui.tools.spreadsheetprocessor.SpreadSheetProcessorPanel}.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface SpreadSheetProcessorListener {

  /**
   * Gets triggered whenever the processor changes state.
   *
   * @param e		the event
   */
  public void processorStateChanged(SpreadSheetProcessorEvent e);
}
