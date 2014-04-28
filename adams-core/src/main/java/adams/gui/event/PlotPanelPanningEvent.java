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
 * PlotPanelPanningEvent.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import java.util.EventObject;

import adams.gui.visualization.core.PlotPanel;

/**
 * Event that gets sent in case of a panning event in the {@link PlotPanel}.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PlotPanelPanningEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 6890099792235484607L;

  /**
   * The type of panning event.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum PanningEventType {
    /** panning. */
    PANNING,
    /** resets panning. */
    RESET
  }

  /** the event type. */
  protected PanningEventType m_EventType;
  
  /**
   * Initializes the event.
   *
   * @param source	the source of the event
   * @param type	the event type
   */
  public PlotPanelPanningEvent(PlotPanel source, PanningEventType type) {
    super(source);
    
    m_EventType = type;
  }
  
  /**
   * Returns the plot panel that triggered the event.
   * 
   * @return		the plot panel
   */
  public PlotPanel getPlotPanel() {
    return (PlotPanel) getSource();
  }
  
  /**
   * Returns the event type.
   * 
   * @return		the type
   */
  public PanningEventType getEventType() {
    return m_EventType;
  }
}
