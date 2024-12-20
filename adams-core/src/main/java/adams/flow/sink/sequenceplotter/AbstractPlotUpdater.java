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
 * AbstractPlotUpdater.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import adams.flow.container.SequencePlotterContainer;
import adams.flow.core.AbstractDataPlotUpdater;

/**
 * Ancestor for classes that determine when to update the sequence plotter, 
 * i.e., repaint all of it.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPlotUpdater
  extends AbstractDataPlotUpdater<SequencePlotterPanel, SequencePlotterContainer> {

  /** for serialization. */
  private static final long serialVersionUID = -8785918718996153397L;

  /**
   * Updates the plotter regardless.
   * 
   * @param panel	the plotter to update
   * @param notify	whether to notify listeners
   */
  protected void doUpdate(final SequencePlotterPanel panel, boolean notify) {
    panel.getMarkerContainerManager().finishUpdate(notify);
    super.doUpdate(panel, notify);
  }
}
