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
 * ToolMouseAdapter.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.tools;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * {@link MouseAdapter} with an owning tool.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ToolMouseAdapter
  extends MouseAdapter {

  /** the owning tool. */
  protected AbstractTool m_Owner;

  /** whether to automatically request focus when clicking on the canvas. */
  protected boolean m_AutomaticallyRequestFocus;

  /**
   * Initializes the adapter. Automatically requests focus when clicking.
   *
   * @param owner	the owning tool
   */
  public ToolMouseAdapter(AbstractTool owner) {
    this(owner, true);
  }

  /**
   * Initializes the adapter.
   *
   * @param owner	the owning tool
   * @param automaticallyRequestFocus	automatically requests the focus when clicking on the canvas.
   */
  public ToolMouseAdapter(AbstractTool owner, boolean automaticallyRequestFocus) {
    m_Owner                     = owner;
    m_AutomaticallyRequestFocus = automaticallyRequestFocus;
  }

  /**
   * Returns the owning tool.
   *
   * @return		the owning tool
   */
  public AbstractTool getOwner() {
    return m_Owner;
  }

  /**
   * Returns whether the focus gets automatically requested when clicking on the canvas.
   *
   * @return		true if automatically requesting
   */
  public boolean getAutomaticallyRequestFocus() {
    return m_AutomaticallyRequestFocus;
  }

  /**
   * Called when a mouse button has been clicked (pressed and released).
   *
   * @param e		the event
   * @see		#getAutomaticallyRequestFocus()
   */
  @Override
  public void mouseClicked(MouseEvent e) {
    if (m_AutomaticallyRequestFocus)
      requestFocus();
  }

  /**
   * Requests the focus in the canvas panel.
   */
  protected void requestFocus() {
    getOwner().getCanvas().requestFocus();
  }
}
