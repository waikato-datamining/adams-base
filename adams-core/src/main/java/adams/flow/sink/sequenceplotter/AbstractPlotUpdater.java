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
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import adams.core.option.AbstractOptionHandler;
import adams.flow.container.SequencePlotterContainer;

/**
 * Ancestor for classes that determine when to update the sequence plotter, 
 * i.e., repaint all of it.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPlotUpdater
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8785918718996153397L;

  /**
   * Checks whether all conditions are met to notify the listeners for changes
   * in the plot.
   * 
   * @param plotter	the plotter to potentially update
   * @param cont	the current plot container
   * @return		true if the listeners can be notified
   */
  protected abstract boolean canNotify(SequencePlotterPanel plotter, SequencePlotterContainer cont);
  
  /**
   * Updates the plotter regardless.
   * 
   * @param plotter	the plotter to update
   * @param notify	whether to notify listeners
   */
  protected void doUpdate(final SequencePlotterPanel plotter, boolean notify) {
    plotter.getMarkerContainerManager().finishUpdate(notify);
    plotter.getContainerManager().finishUpdate(notify);
  }
  
  /**
   * Updates the plotter if necessary.
   * 
   * @param plotter	the plotter to potentially update
   * @param cont	the current plot container
   * @return		true if the change listeners were notified
   */
  public boolean update(SequencePlotterPanel plotter, SequencePlotterContainer cont) {
    boolean	result;
    
    result = (canNotify(plotter, cont));
    doUpdate(plotter, result);
    
    return result;
  }
  
  /**
   * Updates the plotter regardless.
   * 
   * @param plotter	the plotter to update
   */
  public void update(final SequencePlotterPanel plotter) {
    doUpdate(plotter, true);
  }
}
