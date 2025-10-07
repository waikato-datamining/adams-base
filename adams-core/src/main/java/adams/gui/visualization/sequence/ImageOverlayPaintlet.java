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
 * ImageOverlayPaintlet.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

/**
 * Paints the image at the specified location.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageOverlayPaintlet
  extends adams.gui.visualization.core.ImageOverlayPaintlet
  implements XYSequencePaintlet {

  private static final long serialVersionUID = -939223491959967434L;

  /**
   * Returns the XY sequence panel currently in use.
   *
   * @return		the panel in use
   */
  @Override
  public XYSequencePanel getSequencePanel() {
    return (XYSequencePanel) getPanel();
  }

  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return the hit detector
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    return null;
  }
}
