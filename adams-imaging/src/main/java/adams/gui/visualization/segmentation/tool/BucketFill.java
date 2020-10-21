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
 * BucketFill.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.tool;

import adams.gui.core.BasePanel;
import adams.gui.core.Cursors;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.BoundedNumberCheckModel;
import adams.gui.core.NumberTextField.Type;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * Bucket fill.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BucketFill
  extends AbstractTool {

  private static final long serialVersionUID = 2574859830274268039L;

  /** the default size. */
  public final static int DEFAULT_SIZE = 15;

  /** the radio button for background. */
  protected JRadioButton m_RadioBackground;

  /** the radio button for foreground. */
  protected JRadioButton m_RadioForeground;

  /** the text field for the size. */
  protected NumberTextField m_TextZoom;

  /** whether to fill in foreground. */
  protected boolean m_Foreground;

  /** the current size. */
  protected int m_Size;

  /** the current zoom. */
  protected double m_Zoom;

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();
    m_Foreground = true;
    m_Size       = DEFAULT_SIZE;
    m_Zoom       = 100.0;
  }

  /**
   * The name of the tool.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Bucket fill";
  }

  /**
   * The icon of the tool.
   *
   * @return		the icon
   */
  @Override
  public Icon getIcon() {
    return GUIHelper.getIcon("bucket.png");
  }

  /**
   * Returns the mouse cursor to use.
   *
   * @return		the cursor
   */
  @Override
  public Cursor getCursor() {
    return Cursors.crosshair(m_Size, m_Zoom / 100.0);
  }

  /**
   * Performs flood-fill on the provided image.
   * Based on pseudo code from here:
   * https://en.wikipedia.org/wiki/Flood_fill#Alternative_implementations
   *
   * @param image		the image to update
   * @param n			the starting point
   * @param targetColor		the color to replace
   * @param replacementColor	the replacement color
   */
  protected void fill(BufferedImage image, Point n, Color targetColor, Color replacementColor) {
    int 		target;
    int			replacement;
    LinkedList<Point>	queue;
    int			x;
    int			y;
    int			w;
    int			h;

    target      = targetColor.getRGB();
    replacement = replacementColor.getRGB();
    if (target == replacement)
      return;

    x = (int) n.getX();
    y = (int) n.getY();
    if (image.getRGB(x, y) != target)
      return;

    image.setRGB(x, y, replacement);
    w     = image.getWidth();
    h     = image.getHeight();
    queue = new LinkedList<>();
    queue.add(n);

    while (!queue.isEmpty()) {
      n = queue.removeFirst();
      x = (int) n.getX();
      y = (int) n.getY();
      // west
      if ((x > 0) && (image.getRGB(x - 1, y) == target)) {
	image.setRGB(x - 1, y, replacement);
	queue.add(new Point(x - 1, y));
      }
      // east
      if ((x < w - 1) && (image.getRGB(x + 1, y) == target)) {
	image.setRGB(x + 1, y, replacement);
	queue.add(new Point(x + 1, y));
      }
      // north
      if ((y > 0) && (image.getRGB(x, y - 1) == target)) {
	image.setRGB(x, y - 1, replacement);
	queue.add(new Point(x, y - 1));
      }
      // south
      if ((y < h - 1) && (image.getRGB(x, y + 1) == target)) {
	image.setRGB(x, y + 1, replacement);
	queue.add(new Point(x, y + 1));
      }
    }
  }

  /**
   * Performs flood fill at the position.
   *
   * @param p		the position to start
   */
  protected void fill(Point p) {
    if (!hasActiveLayer())
      return;

    if (m_Foreground)
      fill(getActiveLayer().getImage(), p, Color.BLACK, getActiveLayer().getColor());
    else
      fill(getActiveLayer().getImage(), p, getActiveLayer().getColor(), Color.BLACK);

    getCanvas().getOwner().getManager().update();
  }

  /**
   * Creates the mouse listener to use.
   *
   * @return		the listener, null if not applicable
   */
  @Override
  protected ToolMouseAdapter createMouseListener() {
    ToolMouseAdapter	result;

    result = new ToolMouseAdapter(this) {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isLeftClick(e)) {
	  fill(e.getPoint());
	  e.consume();
	}
	else {
	  super.mouseClicked(e);
	}
      }
    };

    return result;
  }

  /**
   * Creates the mouse motion listener to use.
   *
   * @return		the listener, null if not applicable
   */
  @Override
  protected ToolMouseMotionAdapter createMouseMotionListener() {
    return new ToolMouseMotionAdapter(this);
  }

  /**
   * Applies the settings.
   */
  @Override
  protected void doApply() {
    m_Foreground = m_RadioForeground.isSelected();
    m_Zoom       = m_TextZoom.getValue().doubleValue();
    m_Size       = DEFAULT_SIZE;
  }

  /**
   * Creates the panel for setting the options.
   *
   * @return		the options panel
   */
  @Override
  protected BasePanel createOptionPanel() {
    BasePanel		result;
    JPanel		panel;
    JPanel		panel2;
    ButtonGroup		group;

    result = new BasePanel();
    result.setBorder(BorderFactory.createTitledBorder(getName()));

    panel = new JPanel(new GridLayout(0, 1));
    result.add(panel, BorderLayout.NORTH);

    group = new ButtonGroup();
    m_RadioBackground = new JRadioButton("Background");
    m_RadioBackground.setSelected(!m_Foreground);
    group.add(m_RadioBackground);
    panel.add(Fonts.usePlain(m_RadioBackground));
    m_RadioForeground = new JRadioButton("Foreground");
    m_RadioForeground.setSelected(m_Foreground);
    group.add(m_RadioForeground);
    panel.add(Fonts.usePlain(m_RadioForeground));

    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(panel2);
    panel2.add(Fonts.usePlain(new JLabel("Cursor size (%)")));
    m_TextZoom = new NumberTextField(Type.DOUBLE, "" + m_Zoom);
    m_TextZoom.setColumns(5);
    m_TextZoom.setToolTipText("100 = original cursor size");
    m_TextZoom.setCheckModel(new BoundedNumberCheckModel(Type.DOUBLE, 1.0, null));
    panel2.add(m_TextZoom);

    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(panel2);
    panel2.add(createApplyButton());

    return result;
  }
}