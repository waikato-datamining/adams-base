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
 * AbstractShapeTool.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.tool;

import adams.gui.core.MouseUtils;

import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 * TODO: What class does.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractShapeTool
  extends AbstractTool {

  private static final long serialVersionUID = -4106386739843618810L;

  /**
   * Draws the currently selected shape at the specified location.
   *
   * @param p		the location
   */
  protected abstract void doDrawShape(Point p);

  /**
   * Draws the currently selected shape at the specified location.
   * Skips drawing if no active layer.
   * Updates the canvas after a successful draw.
   *
   * @param p		the location
   */
  protected void drawShape(Point p) {
    if (!hasActiveLayer())
      return;
    doDrawShape(p);
    getCanvas().getOwner().getManager().update();
  }

  /**
   * Creates the mouse listener to use.
   *
   * @return		the listener, null if not applicable
   */
  @Override
  protected ToolMouseAdapter createMouseListener() {
    if (m_Listener == null) {
      m_Listener = new ToolMouseAdapter(this) {
	@Override
	public void mousePressed(MouseEvent e) {
	  if (MouseUtils.isLeftClick(e)) {
	    drawShape(e.getPoint());
	    e.consume();
	  }
	  else {
	    super.mouseClicked(e);
	  }
	}
      };
    }
    return m_Listener;
  }

  /**
   * Creates the mouse motion listener to use.
   *
   * @return		the listener, null if not applicable
   */
  @Override
  protected ToolMouseMotionAdapter createMouseMotionListener() {
    if (m_MotionListener == null) {
      m_MotionListener = new ToolMouseMotionAdapter(this) {
	@Override
	public void mouseDragged(MouseEvent e) {
	  if (getOwner().getCanvas().isLeftMouseDown())
	    drawShape(e.getPoint());
	  else
	    super.mouseDragged(e);
	}
      };
    }
    return m_MotionListener;
  }
}
