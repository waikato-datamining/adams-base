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
 * CustomizableTool.java
 * Copyright (C) 2023-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.segmentation.tool;

import java.util.Map;

/**
 * Interface for tools with options.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface CustomizableTool
  extends Tool {

  /**
   * Applies the options.
   */
  public void applyOptions();

  /**
   * Applies the options quietly, i.e., doesn't trigger an event.
   */
  public void applyOptionsQuietly();

  /**
   * Supplies initial options to use.
   *
   * @param value	the options to use
   */
  public void setInitialOptions(Map<String,Object> value);

  /**
   * Returns the current options as a map.
   *
   * @return		the options
   */
  public Map<String,Object> getCurrentOptions();
}
