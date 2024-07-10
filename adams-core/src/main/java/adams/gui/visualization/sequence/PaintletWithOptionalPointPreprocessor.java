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
 * PaintletWithOptionalPointPreprocessor.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.sequence;

import adams.gui.visualization.core.Paintlet;
import adams.gui.visualization.sequence.pointpreprocessor.PointPreprocessor;

/**
 * Interface for XYSequence paintlets that support point preprocessing.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface PaintletWithOptionalPointPreprocessor
  extends Paintlet {

  /**
   * Sets the point preprocessor to use.
   *
   * @param value	the preprocessor
   */
  public void setPointPreprocessor(PointPreprocessor value);

  /**
   * Returns the point preprocessor in use.
   *
   * @return		the preprocessor
   */
  public PointPreprocessor getPointPreprocessor();

  /**
   * Returns whether point preprocessing is actually supported.
   *
   * @return		true if supported
   */
  public boolean supportsPointPreprocessor();
}
