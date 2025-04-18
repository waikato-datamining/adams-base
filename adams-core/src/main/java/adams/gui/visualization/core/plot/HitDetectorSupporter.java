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
 * HitDetectorSupporter.java
 * Copyright (C) 2012-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core.plot;

/**
 * Interface for classes that use hit detectors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of hit detector to use
 */
public interface HitDetectorSupporter<T extends HitDetector> {

  /**
   * Returns the hit detector to use for this paintlet.
   *
   * @return		the detector
   */
  public T getHitDetector();
}
