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
 * PlotPanelZoomListener.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import java.util.EventListener;

import adams.gui.visualization.core.PlotPanel;

/**
 * Interface for listening for zoom events in the {@link PlotPanel}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface PlotPanelZoomListener
  extends EventListener {

  /**
   * Invoked when a {@link PlotPanel} got zoomed in/out.
   * 
   * @param e		the event
   */
  public void painted(PlotPanelZoomEvent e);
}
