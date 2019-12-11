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
 * NumberedTextRenderer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.textrenderer;

/**
 * Ancestor for renderers that can output numbers with each line of output.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface LineNumberTextRenderer
  extends TextRenderer {

  /**
   * Sets whether to output line numbers.
   *
   * @param value	true if to output
   */
  public void setOutputLineNumbers(boolean value);

  /**
   * Returns whether to output line numbers.
   *
   * @return		true if to output
   */
  public boolean getOutputLineNumbers();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String outputLineNumbersTipText();
}
