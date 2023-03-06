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
 * ToolKeyAdapter.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.tool;

import java.awt.event.KeyAdapter;

/**
 * {@link KeyAdapter} with an owning tool. The {@link ToolMouseAdapter} in the tool must call
 * the {@link ToolMouseAdapter#requestFocus()} method, otherwise key input won't be recognized
 * (due to not focused).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ToolKeyAdapter
  extends KeyAdapter {

  /** the owning tool. */
  protected Tool m_Owner;

  /**
   * Initializes the adapter.
   *
   * @param owner	the owning tool
   */
  public ToolKeyAdapter(Tool owner) {
    m_Owner = owner;
  }

  /**
   * Returns the owning tool.
   *
   * @return		the owning tool
   */
  public Tool getOwner() {
    return m_Owner;
  }
}
