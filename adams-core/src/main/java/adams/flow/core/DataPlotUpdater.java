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
 * DataPlotUpdater.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.option.OptionHandler;
import adams.gui.visualization.container.DataContainerPanel;

/**
 * Interface for classes that determine when to update the data container panel,
 * i.e., repaint all of it.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5737 $
 * @param <P> the type of panel this updater handles
 * @param <C> the type of container this updater handles
 */
public interface DataPlotUpdater<P extends DataContainerPanel, C extends Object>
  extends OptionHandler {

  /**
   * Updates the data container panel if necessary.
   * 
   * @param panel	the data container panel to potentially update
   * @param cont	the current data container
   * @return		true if the change listeners were notified
   */
  public boolean update(P panel, C cont);

  /**
   * Updates the data container panel regardless, notifying the listeners.
   * 
   * @param panel	the data container panel to update
   */
  public void update(final P panel);
}
