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
 * AutoAdvanceAnnotator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotator;

/**
 * Interface for annotators that allow auto advancing labels.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface AutoAdvanceAnnotator {

  /**
   * Sets whether to auto advance labels once one has been applied.
   *
   * @param value 	true if to auto advance
   */
  public void setAutoAdvanceLabels(boolean value);

  /**
   * Returns whether to auto advance labels once one has been applied.
   *
   * @return 		true if auto advance
   */
  public boolean getAutoAdvanceLabels();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String autoAdvanceLabelsTipText();
}
