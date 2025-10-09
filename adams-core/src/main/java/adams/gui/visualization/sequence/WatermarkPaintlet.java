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
 * WatermarkPaintlet.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.sequence;

/**
 * Overlays a watermark.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WatermarkPaintlet
  extends adams.gui.visualization.core.WatermarkPaintlet
  implements XYSequencePaintlet {

  private static final long serialVersionUID = 7923819857566247771L;

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
