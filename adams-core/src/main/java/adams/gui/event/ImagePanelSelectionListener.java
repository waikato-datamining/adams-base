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
 * ImagePanelSelectionListener.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import java.util.EventListener;

import adams.gui.visualization.image.ImagePanel;

/**
 * Interface for listening for selection events in the {@link ImagePanel}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ImagePanelSelectionListener
  extends EventListener {

  /**
   * Invoked when a selection happened in a {@link ImagePanel}.
   * 
   * @param e		the event
   */
  public void selected(ImagePanelSelectionEvent e);
}
