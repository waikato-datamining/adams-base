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

/**
 * FlippableAxisModel.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core.axis;

/**
 * Interface for axis models that allow flipping the axis, i.e., instead
 * of left-to-right, using right-to-left.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface FlippableAxisModel {

  /**
   * Sets whether to flip the axis.
   * 
   * @param value	if true the axis gets flipped
   */
  public void setFlipped(boolean value);
  
  /**
   * Returns whether the axis is flipped.
   * 
   * @return		true if the axis is flipped
   */
  public boolean isFlipped();
}
