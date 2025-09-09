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
 * Eraser.java
 * Copyright (C) 2020-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.tool;

import adams.gui.core.Cursors;
import adams.gui.core.ImageManager;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.BoundedNumberCheckModel;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.visualization.segmentation.CanvasPanel;
import adams.gui.visualization.segmentation.ImageUtils;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Removes pixels using the background.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Eraser
    extends AbstractShapeTool {

  /** the default size. */
  public final static int DEFAULT_SIZE = 9;

  private static final long serialVersionUID = -3058489939334040466L;

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
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes pixels using the background.\n" + CanvasPanel.draggingHelp();
  }

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
    return "Eraser";
  }

  /**
   * The icon of the tool.
   *
   * @return		the icon
   */
  @Override
  public Icon getIcon() {
    return ImageManager.getIcon("eraser.png");
  }

  /**
   * Returns the mouse cursor to use.
   *
   * @return		the cursor
   */
  @Override
  protected Cursor createCursor() {
    if (m_Round)
      return Cursors.circleWithPointer(m_Size);
    else
      return Cursors.squareWithPointer(m_Size);
  }

  /**
   * Draws the currently selected shape at the specified locations.
   *
   * @param points	the locations
   */
  @Override
  protected void doDrawShape(List<Point> points) {
    Graphics2D 		g2d;
    BufferedImage 	img;
    double		zoom;
    int			x;
    int			y;
    int[]		rangeX;
    int[]		rangeY;

    zoom  = getZoom();
    img   = getActiveImage();
    g2d   = img.createGraphics();
    g2d.scale(1 / zoom, 1 / zoom);
    g2d.setColor(Color.BLACK);
    rangeX = new int[]{img.getWidth(), 0};
    rangeY = new int[]{img.getHeight(), 0};
    for (Point p: points) {
      x = (int) (p.getX() - m_Size / 2);
      y = (int) (p.getY() - m_Size / 2);
      rangeX[0] = Math.min((int) (x / zoom), rangeX[0]);
      rangeX[1] = Math.max((int) ((x + m_Size) / zoom), rangeX[1]);
      rangeY[0] = Math.min((int) (y / zoom), rangeY[0]);
      rangeY[1] = Math.max((int) ((y + m_Size) / zoom), rangeY[1]);
      if (m_RadioRound.isSelected())
	g2d.fillOval(x, y, m_Size - 1, m_Size - 1);
      else
	g2d.fillRect(x, y, m_Size - 1, m_Size - 1);
    }
    g2d.dispose();
    // unfortunately, we can't draw with transparent black
    // so we have to replace opaque black with transparent black
    ImageUtils.replaceColor(getActiveImage(), Color.BLACK, new Color(0, 0, 0, 0), rangeX, rangeY);
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
   * Fills the parameter panel with the options.
   *
   * @param paramPanel  for adding the options to
   */
  @Override
  protected void addOptions(ParameterPanel paramPanel) {
    ButtonGroup 	group;

    group = new ButtonGroup();

    m_RadioSquare = new JRadioButton();
    m_RadioSquare.setSelected(!m_Round);
    m_RadioSquare.addActionListener((ActionEvent e) -> setApplyButtonState(m_ButtonApply, true));
    group.add(m_RadioSquare);
    paramPanel.addParameter("Square", m_RadioSquare);

    m_RadioRound = new JRadioButton();
    m_RadioRound.setSelected(m_Round);
    m_RadioRound.addActionListener((ActionEvent e) -> setApplyButtonState(m_ButtonApply, true));
    group.add(m_RadioRound);
    paramPanel.addParameter("Round", m_RadioRound);

    m_TextSize = new NumberTextField(Type.INTEGER, "" + m_Size);
    m_TextSize.setColumns(5);
    m_TextSize.setToolTipText("The size in on-screen pixels");
    m_TextSize.setCheckModel(new BoundedNumberCheckModel(Type.INTEGER, 1, null));
    m_TextSize.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter("Size", m_TextSize);
  }
}
