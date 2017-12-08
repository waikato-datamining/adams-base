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
 * ContainerHitDetector.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.core.plot;

import adams.gui.visualization.container.AbstractContainer;

import java.awt.event.MouseEvent;

/**
 * Interface for hit detectors that support detecting the affected containers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface ContainerHitDetector<H, P, C extends AbstractContainer>
  extends HitDetector<H, P> {

  /**
   * Detects hits and associates them with the containers.
   *
   * @param e		the mouse event to analyze for a hit
   * @return		optional result of processing the event
   */
  public C[] containers(MouseEvent e);
}
