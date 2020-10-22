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
 * Pencil.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.tool;

import adams.gui.core.BasePanel;
import adams.gui.core.Cursors;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
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
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * Pencil.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Pencil
  extends AbstractShapeTool {

  private static final long serialVersionUID = -1508997962532101115L;

  /** the default size. */
  public final static int DEFAULT_SIZE = 9;

  /** the radio button for round shape. */
  protected JRadioButton m_RadioRound;

  /** the radio button for square shape. */
  protected JRadioButton m_RadioSquare;

  /** the text field for the size. */
  protected NumberTextField m_TextSize;

  /** whether the shape is currently round. */
  protected boolean m_Round;

  /** the current size. */
  protected int m_Size;

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();
    m_Round = false;
    m_Size  = DEFAULT_SIZE;
  }

  /**
   * The name of the tool.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Pencil";
  }

  /**
   * The icon of the tool.
   *
   * @return		the icon
   */
  @Override
  public Icon getIcon() {
    return GUIHelper.getIcon("pencil.png");
  }

  /**
   * Returns the mouse cursor to use.
   *
   * @return		the cursor
   */
  @Override
  protected Cursor createCursor() {
    if (m_Round)
      return Cursors.circle(m_Size);
    else
      return Cursors.square(m_Size);
  }

  /**
   * Draws the currently selected shape at the specified location.
   *
   * @param p		the location
   */
  @Override
  protected void doDrawShape(Point p) {
    Color		color;
    Graphics2D		g2d;
    BufferedImage 	img;
    double		zoom;
    int			x;
    int			y;

    color = getActiveLayer().getColor();
    zoom  = getZoom();
    img   = getActiveLayer().getImage();
    x     = (int) (p.getX() - m_Size / 2);
    y     = (int) (p.getY() - m_Size / 2);
    g2d   = img.createGraphics();
    g2d.scale(1/zoom, 1/zoom);
    g2d.setColor(color);
    if (m_Round)
      g2d.fillOval(x, y, m_Size, m_Size);
    else
      g2d.fillRect(x, y, m_Size, m_Size);
    g2d.dispose();
  }

  /**
   * Applies the settings.
   */
  @Override
  protected void doApply() {
    m_Round = m_RadioRound.isSelected();
    m_Size  = m_TextSize.getValue().intValue();
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
    ButtonGroup 	group;

    result = new BasePanel();
    result.setBorder(BorderFactory.createTitledBorder(getName()));

    panel = new JPanel(new GridLayout(0, 1));
    result.add(panel, BorderLayout.NORTH);

    group = new ButtonGroup();
    m_RadioSquare = new JRadioButton("Square");
    m_RadioSquare.setSelected(!m_Round);
    group.add(m_RadioSquare);
    panel.add(Fonts.usePlain(m_RadioSquare));
    m_RadioRound = new JRadioButton("Round");
    m_RadioRound.setSelected(m_Round);
    group.add(m_RadioRound);
    panel.add(Fonts.usePlain(m_RadioRound));

    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(panel2);
    panel2.add(Fonts.usePlain(new JLabel("Size")));
    m_TextSize = new NumberTextField(Type.INTEGER, "" + m_Size);
    m_TextSize.setColumns(5);
    m_TextSize.setToolTipText("The size in on-screen pixels");
    m_TextSize.setCheckModel(new BoundedNumberCheckModel(Type.INTEGER, 1, null));
    panel2.add(m_TextSize);

    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(panel2);
    panel2.add(createApplyButton());

    return result;
  }
}
