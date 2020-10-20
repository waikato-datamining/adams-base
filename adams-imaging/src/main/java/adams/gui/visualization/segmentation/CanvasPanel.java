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

import adams.gui.core.BasePanel;
import adams.gui.core.MouseUtils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_LeftMouseDown  = false;
    m_RightMouseDown = false;
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
	super.mousePressed(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        m_LeftMouseDown  = false;
        m_RightMouseDown = false;
	super.mouseReleased(e);
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
   * Paints the component.
   *
   * @param g		the context
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (m_Owner != null)
      m_Owner.getManager().draw((Graphics2D) g);
  }
}
