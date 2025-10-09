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
 * WatermarkedPanel.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.watermark;

import adams.gui.core.BasePanel;

import java.awt.BorderLayout;
import java.awt.Graphics;

/**
 * For overlaying watermarks on panels.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @see Watermark
 * @see BasePanel
 */
public class WatermarkedPanel
  extends BasePanel {

  /** the watermark to apply. */
  protected Watermark m_Watermark;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Watermark = new Null();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    setLayout(new BorderLayout());
  }

  /**
   * Sets the watermark to use.
   *
   * @param value	the watermark
   */
  public void setWatermark(Watermark value) {
    m_Watermark = value;
    repaint();
  }

  /**
   * Returns the current watermark in use.
   *
   * @return		the watermark
   */
  public Watermark getWatermark() {
    return m_Watermark;
  }

  /**
   * Paints the component.
   *
   * @param g 		the graphics context
   */
  @Override
  public void paint(Graphics g) {
    super.paint(g);
    m_Watermark.applyWatermark(g, getSize());
  }

  /**
   * Paints the component.
   *
   * @param g 		the graphics context
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    m_Watermark.applyWatermark(g, getSize());
  }

  /**
   * Paints the component when printing.
   *
   * @param g 		the graphics context
   */
  @Override
  protected void printComponent(Graphics g) {
    super.printComponent(g);
    m_Watermark.applyWatermark(g, getSize());
  }
}
