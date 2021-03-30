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
 * MonitoringDataContainer.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.data;

/**
 * Interface for data containers that monitor their source for changes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface MonitoringDataContainer
  extends DataContainer {

  /**
   * Returns true if the source has changed.
   *
   * @return		true if changed
   */
  public boolean hasSourceChanged();
}
