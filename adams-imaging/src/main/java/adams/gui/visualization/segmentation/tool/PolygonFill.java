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
 * PolygonFill.java
 * Copyright (C) 2023-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.tool;

import adams.data.geometry.PolygonUtils;
import adams.gui.core.BaseColorTextField;
import adams.gui.core.Cursors;
import adams.gui.core.ImageManager;
import adams.gui.core.MouseUtils;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.BoundedNumberCheckModel;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.visualization.segmentation.paintoperation.PaintOperation;
import adams.gui.visualization.segmentation.paintoperation.PolygonOverlay;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Polygon fill.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PolygonFill
  extends AbstractToolWithParameterPanel {

  private static final long serialVersionUID = 2574859830274268039L;

  /** the default size. */
  public final static int DEFAULT_SIZE = 15;

  /** the text field for the polygon color. */
  protected BaseColorTextField m_TextPolygonColor;

  /** the text field for the polygon stroke thickness. */
  protected NumberTextField m_TextPolygonStroke;

  /** the marker size. */
  protected NumberTextField m_TextMarkerSize;

  /** the radio button for background. */
  protected JRadioButton m_RadioBackground;

  /** the radio button for foreground. */
  protected JRadioButton m_RadioForeground;

  /** the text field for the size. */
  protected NumberTextField m_TextZoom;

  /** the polygon color. */
  protected Color m_PolygonColor;

  /** the polygon stroke thickness. */
  protected float m_PolygonStroke;

  /** the marker size. */
  protected int m_MarkerSize;

  /** whether to fill in foreground. */
  protected boolean m_Foreground;

  /** the current size. */
  protected int m_Size;

  /** the current zoom. */
  protected double m_Zoom;

  /** the polygon points. */
  protected List<Point> m_Points;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "After the user selects a polygon (left-click=add vertex, ENTER=accept, ESC=discard), "
      + "fills in pixels using either the background (= black) or the foreground (color of the active layer).";
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();
    m_PolygonColor  = Color.RED;
    m_PolygonStroke = 1.0f;
    m_MarkerSize    = 15;
    m_Foreground    = true;
    m_Size          = DEFAULT_SIZE;
    m_Zoom          = 100.0;
    m_Points        = new ArrayList<>();
  }

  /**
   * The name of the tool.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Polygon fill";
  }

  /**
   * The icon of the tool.
   *
   * @return		the icon
   */
  @Override
  public Icon getIcon() {
    return ImageManager.getIcon("polygon_fill.png");
  }

  /**
   * Returns the mouse cursor to use.
   *
   * @return		the cursor
   */
  @Override
  protected Cursor createCursor() {
    return Cursors.crosshair(m_Size, m_Zoom / 100.0);
  }

  /**
   * Creates the paint operation to use.
   *
   * @return		the operation
   */
  @Override
  protected PaintOperation createPaintOperation() {
    return new PolygonOverlay();
  }

  /**
   * Returns the color for the overlay.
   *
   * @return		the color
   */
  public Color getPolygonColor() {
    return m_PolygonColor;
  }

  /**
   * Returns the thickness for the overlay.
   *
   * @return		the thickness
   */
  public float getPolygonStroke() {
    return m_PolygonStroke;
  }

  /**
   * Returns the size of the vertex markers.
   *
   * @return		the size
   */
  public int getMarkerSize() {
    return m_MarkerSize;
  }

  /**
   * Returns the polygon to paint.
   *
   * @return		the polygon, null if not enough points
   */
  public Polygon getPolygon() {
    if (m_Points.size() < 2)
      return null;
    else
      return PolygonUtils.toPolygon(m_Points);
  }

  /**
   * Returns the underlying points.
   *
   * @return		the points
   */
  public List<Point> getPoints() {
    return m_Points;
  }

  /**
   * Fills in the polygon.
   */
  protected void fill() {
    Graphics2D  g2d;
    Polygon	poly;

    if (!hasAnyActive())
      return;
    if (m_Points.size() < 3)
      return;

    if (isAutomaticUndoEnabled())
      getCanvas().getOwner().addUndoPoint();


    poly = PolygonUtils.toPolygon(m_Points);
    g2d  = (Graphics2D) getActiveImage().getGraphics();

    // draw polygon
    if (m_Foreground)
      g2d.setColor(getActiveColor());
    else
      g2d.setColor(new Color(0, 0, 0, 0));
    g2d.fillPolygon(poly);

    g2d.dispose();
    m_Points.clear();

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
	  Point scaled = new Point((int) (e.getX() / getZoom()), (int) (e.getY() / getZoom()));
	  m_Points.add(scaled);
	  e.consume();
	  m_Owner.getCanvas().repaint();
	}
	super.mouseClicked(e);
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
   * Creates the key listener to use.
   *
   * @return		the listener, null if not applicable
   */
  @Override
  protected ToolKeyAdapter createKeyListener() {
    return new ToolKeyAdapter(this) {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	  m_Points.clear();
	  e.consume();
	  m_Owner.getCanvas().repaint();
	}
	else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	  fill();
	  e.consume();
	}
	super.keyPressed(e);
      }
    };
  }

  /**
   * Applies the settings.
   */
  @Override
  protected void doApply() {
    m_PolygonColor  = m_TextPolygonColor.getColor();
    m_PolygonStroke = m_TextPolygonStroke.getValue().floatValue();
    m_MarkerSize    = m_TextMarkerSize.getValue().intValue();
    m_Foreground    = m_RadioForeground.isSelected();
    m_Zoom          = m_TextZoom.getValue().doubleValue();
    m_Size          = DEFAULT_SIZE;
  }

  /**
   * Fills the parameter panel with the options.
   *
   * @param paramPanel  for adding the options to
   */
  @Override
  protected void addOptions(ParameterPanel paramPanel) {
    ButtonGroup		group;

    m_TextPolygonColor = new BaseColorTextField(m_PolygonColor);
    m_TextPolygonColor.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter("Polygon color", m_TextPolygonColor);

    m_TextPolygonStroke = new NumberTextField(Type.FLOAT, "" + m_PolygonStroke);
    m_TextPolygonStroke.setColumns(5);
    m_TextPolygonStroke.setToolTipText("The width of the stroke in pixels");
    m_TextPolygonStroke.setCheckModel(new BoundedNumberCheckModel(Type.FLOAT, 1.0f, null));
    m_TextPolygonStroke.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter("Stroke width", m_TextPolygonStroke);

    m_TextMarkerSize = new NumberTextField(Type.INTEGER, "" + m_MarkerSize);
    m_TextMarkerSize.setColumns(5);
    m_TextMarkerSize.setToolTipText("The size of the vertex markers");
    m_TextMarkerSize.setCheckModel(new BoundedNumberCheckModel(Type.INTEGER, 1, null));
    m_TextMarkerSize.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter("Marker size", m_TextMarkerSize);

    group = new ButtonGroup();

    m_RadioBackground = new JRadioButton();
    m_RadioBackground.setSelected(!m_Foreground);
    m_RadioBackground.addActionListener((ActionEvent e) -> setApplyButtonState(m_ButtonApply, true));
    group.add(m_RadioBackground);
    paramPanel.addParameter("Background", m_RadioBackground);

    m_RadioForeground = new JRadioButton();
    m_RadioForeground.setSelected(m_Foreground);
    m_RadioForeground.addActionListener((ActionEvent e) -> setApplyButtonState(m_ButtonApply, true));
    group.add(m_RadioForeground);
    paramPanel.addParameter("Foreground", m_RadioForeground);

    m_TextZoom = new NumberTextField(Type.DOUBLE, "" + m_Zoom);
    m_TextZoom.setColumns(5);
    m_TextZoom.setToolTipText("100 = original cursor size");
    m_TextZoom.setCheckModel(new BoundedNumberCheckModel(Type.DOUBLE, 1.0, null));
    m_TextZoom.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter("Cursor size (%)", m_TextZoom);
  }
}
