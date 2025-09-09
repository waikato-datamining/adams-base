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
 * CanvasPanel.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation;

import adams.data.RoundingUtils;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.KeyUtils;
import adams.gui.core.MouseUtils;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Used for drawing on.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CanvasPanel
  extends BasePanel {

  private static final long serialVersionUID = -1128271862782338556L;

  /** the owner. */
  protected SegmentationPanel m_Owner;

  /** whether the left mouse button is down. */
  protected boolean m_LeftMouseDown;

  /** whether the right mouse button is down. */
  protected boolean m_RightMouseDown;

  /** whether the image is being dragged. */
  protected boolean m_Dragging;

  /** the last point position while dragging. */
  protected Point m_LastDraggingPoint;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_LeftMouseDown     = false;
    m_RightMouseDown    = false;
    m_Dragging          = false;
    m_LastDraggingPoint = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        m_LeftMouseDown  = MouseUtils.isLeftDown(e);
        m_RightMouseDown = MouseUtils.isRightDown(e);
	m_Dragging       = MouseUtils.isLeftClick(e) && KeyUtils.isOnlyShiftDown(e.getModifiersEx());
	if (m_Dragging) {
	  m_LastDraggingPoint = e.getPoint();
	  setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	else {
	  m_LastDraggingPoint = null;
	}
	super.mousePressed(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        m_LeftMouseDown     = false;
        m_RightMouseDown    = false;
	m_Dragging          = false;
	m_LastDraggingPoint = null;
	super.mouseReleased(e);
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
	if (m_Dragging) {
	  int diffX = m_LastDraggingPoint.x - e.getX();
	  int diffY = m_LastDraggingPoint.y - e.getY();
	  BaseScrollPane scrollPane = getOwner().getScrollPane();
	  Point curPos = scrollPane.getViewport().getViewPosition();
	  Point newPos = new Point(curPos.x + diffX, curPos.y + diffY);
	  scrollPane.getViewport().setViewPosition(newPos);
	  e.consume();
	}

	if (!e.isConsumed())
	  super.mouseDragged(e);
      }
    });

    addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        double oldZoom = m_Owner.getZoom();
        double newZoom;
        int rotation = e.getWheelRotation();
	if (rotation < 0)
	  newZoom = oldZoom * Math.pow(SegmentationPanel.ZOOM_FACTOR, -rotation);
	else
	  newZoom = oldZoom / Math.pow(SegmentationPanel.ZOOM_FACTOR, rotation);
	newZoom = RoundingUtils.round(newZoom, 1);
	m_Owner.setZoom(newZoom);
      }
    });
  }

  /**
   * Sets the owner.
   *
   * @param value	the owner
   */
  public void setOwner(SegmentationPanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner, null if none set
   */
  public SegmentationPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns whether the left mouse button is down.
   *
   * @return		true if down
   */
  public boolean isLeftMouseDown() {
    return m_LeftMouseDown;
  }

  /**
   * Returns whether the right mouse button is down.
   *
   * @return		true if down
   */
  public boolean isRightMouseDown() {
    return m_RightMouseDown;
  }

  /**
   * Returns whether the image is currently being dragged.
   *
   * @return		true if dragged
   */
  public boolean isDragging() {
    return m_Dragging;
  }

  /**
   * Returns the last point of the image being dragged.
   *
   * @return		the last point, null if not set
   */
  public Point getLastDraggingPoint() {
    return m_LastDraggingPoint;
  }

  /**
   * Paints the component.
   *
   * @param g		the context
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (m_Owner != null) {
      m_Owner.getManager().draw((Graphics2D) g);
      if (m_Owner.getPaintOperation() != null)
        m_Owner.getPaintOperation().performPaint((Graphics2D) g);
    }
  }

  /**
   * Returns the help information on dragging the image.
   *
   * @return		the help string
   */
  public static String draggingHelp() {
    return "Use SHIFT+left-click to drag the image.";
  }
}
