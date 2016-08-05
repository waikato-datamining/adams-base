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
 * ImagePanelLeftClickListener.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.util.EventListener;

/**
 * Interface for listening for left-click events in the {@link ImagePanel}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ImagePanelLeftClickListener
  extends EventListener {

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  public void imageChanged(PaintPanel panel);

  /**
   * Invoked when a left-click happened in a {@link ImagePanel}.
   * 
   * @param e		the event
   */
  public void clicked(ImagePanelLeftClickEvent e);
}
