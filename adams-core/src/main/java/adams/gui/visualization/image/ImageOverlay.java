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
 * ImageOverlay.java
 * Copyright (C) 2011-2021 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import adams.core.CleanUpHandler;
import adams.core.option.OptionHandler;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Graphics;

/**
 * Interface for classes that put overlays over an image.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ImageOverlay
  extends OptionHandler, CleanUpHandler {

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  public void imageChanged(PaintPanel panel);

  /**
   * Paints the overlay over the image.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  public void paintOverlay(PaintPanel panel, Graphics g);

  /**
   * Gets called when the image overlay got added to a paintable panel.
   *
   * @param panel	the panel it got added to
   */
  public void overlayAdded(PaintPanel panel);

  /**
   * Gets called when the image overlay got removed from a paintable panel.
   *
   * @param panel	the panel it got removed from
   */
  public void overlayRemoved(PaintPanel panel);
}
