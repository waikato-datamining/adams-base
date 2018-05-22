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
 * SelectionProcessor.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.selection;

import adams.core.ShallowCopySupporter;
import adams.core.option.OptionHandler;
import adams.gui.event.ImagePanelSelectionListener;
import adams.gui.visualization.image.ImagePanel;

import java.awt.Point;
import java.util.List;

/**
 * Interface for selection processors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface SelectionProcessor
  extends ImagePanelSelectionListener, OptionHandler, ShallowCopySupporter<SelectionProcessor> {

  /**
   * Process the selection that occurred in the image panel.
   *
   * @param panel	the origin
   * @param topLeft	the top-left position of the selection
   * @param bottomRight	the bottom-right position of the selection
   * @param modifiersEx	the associated modifiers
   */
  public void processSelection(ImagePanel panel, Point topLeft, Point bottomRight, List<Point> trace, int modifiersEx);
}
