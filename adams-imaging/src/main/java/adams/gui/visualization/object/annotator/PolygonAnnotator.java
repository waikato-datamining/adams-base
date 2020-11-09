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
 * PolygonAnnotator.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotator;

import adams.core.Utils;
import adams.data.report.Report;
import adams.data.statistics.StatUtils;
import adams.gui.visualization.image.SelectionRectangle;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * For annotating object shapes with polygons boxes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PolygonAnnotator
  extends AbstractRectangleBasedAnnotator
  implements LabelSuffixHandler {

  private static final long serialVersionUID = 1122040195846360397L;

  /** the color to use. */
  protected Color m_Color;

  /** the thickness of the stroke. */
  protected float m_StrokeThickness;

  /** the minimum distance in pixels that the trace pixels must be apart. */
  protected int m_MinDistance;

  /** the label suffix to use. */
  protected String m_LabelSuffix;

  /** whether dragging has happened at all. */
  protected boolean m_Dragged;

  /** the starting corner of the selection box. */
  protected Point m_SelectionFrom;

  /** the finishing corner of the selection box. */
  protected Point m_SelectionTo;

  /** the selection trace. */
  protected List<Point> m_SelectionTrace;

  /** the mouse listener to install. */
  protected MouseListener m_MouseListener;

  /** the mouse motion listener to install. */
  protected MouseMotionListener m_MouseMotionListener;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For annotating objects with bounding boxes.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color", "color",
      Color.GRAY);

    m_OptionManager.add(
      "stroke-thickness", "strokeThickness",
      1.0f, 0.01f, null);

    m_OptionManager.add(
      "min-distance", "minDistance",
      10, 1, null);

    m_OptionManager.add(
      "label-suffix", "labelSuffix",
      getDefaultLabelSuffix());
  }

  /**
   * Sets the color to use.
   *
   * @param value 	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color to use.
   *
   * @return 		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color to use for drawing the box while dragging.";
  }

  /**
   * Sets the stroke thickness to use.
   *
   * @param value	the thickness
   */
  public void setStrokeThickness(float value) {
    m_StrokeThickness = value;
    reset();
  }

  /**
   * Returns the current stroke thickness.
   *
   * @return		the thickness
   */
  public float getStrokeThickness() {
    return m_StrokeThickness;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String strokeThicknessTipText() {
    return "The thickness of the stroke for the box.";
  }

  /**
   * Sets the minimum distance in pixels that a new point must be away from
   * the last trace point.
   *
   * @param value 	the distance
   */
  public void setMinDistance(int value) {
    m_MinDistance = value;
    reset();
  }

  /**
   * Returns the minimum distance in pixels that a new point must be away from
   * the last trace point.
   *
   * @return 		the distance
   */
  public int getMinDistance() {
    return m_MinDistance;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minDistanceTipText() {
    return "The minimum distance in pixels that a new point must be away from the last trace point.";
  }

  /**
   * Returns the default suffix to use for the label.
   *
   * @return		the default
   */
  protected String getDefaultLabelSuffix() {
    return ".type";
  }

  /**
   * Sets the suffix to use for the label.
   *
   * @param value 	the suffix
   */
  @Override
  public void setLabelSuffix(String value) {
    m_LabelSuffix = value;
    reset();
  }

  /**
   * Returns the suffix to use for the label.
   *
   * @return 		the suffix
   */
  @Override
  public String getLabelSuffix() {
    return m_LabelSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelSuffixTipText() {
    return "The suffix to use for storing the label in the report.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Dragged             = false;
    m_SelectionFrom       = null;
    m_SelectionTo         = null;
    m_SelectionTrace      = new ArrayList<>();
    m_MouseListener       = createMouseListener();
    m_MouseMotionListener = createMouseMotionListener();
  }

  /**
   * Creates the listener for mouse events.
   *
   * @return		the instance
   */
  protected MouseListener createMouseListener() {
    return new MouseAdapter() {
      // start selection
      @Override
      public void mousePressed(MouseEvent e) {
	getOwner().getCanvas().logMouseButtonPressed(e);
        if (e.getButton() == MouseEvent.BUTTON1) {
          // get top/left coordinates for selection
          if (!e.isShiftDown()) {
            m_Selecting     = true;
            m_Dragged       = false;
            m_SelectionFrom = e.getPoint();
	    m_SelectionTrace.clear();
	    if (canAddTracePoint(e.getPoint()))
	      m_SelectionTrace.add(e.getPoint());
          }
        }
      }

      // start selection
      @Override
      public void mouseMoved(MouseEvent e) {
	if (e.getButton() == MouseEvent.BUTTON1) {
	  // get top/left coordinates for selection
	  if (!e.isShiftDown()) {
	    m_Selecting     = true;
	    m_Dragged       = false;
	    m_SelectionFrom = e.getPoint();
	  }
	}
      }

      // perform selection
      @Override
      public void mouseReleased(MouseEvent e) {
	getOwner().getCanvas().logMouseButtonReleased(e);
        if (e.getButton() == MouseEvent.BUTTON1) {
          // get bottom/right coordinates for selection
          if (m_Selecting && m_Dragged) {
            m_Selecting   = false;
            m_Dragged     = false;
            m_SelectionTo = e.getPoint();
            processSelection(e.getModifiersEx());
          }
        }
      }

      @Override
      public void mouseClicked(MouseEvent e) {
	getOwner().getCanvas().logMouseButtonClick(e);
      }
    };
  }

  /**
   * Creates the listener for mouse motion events.
   *
   * @return		the instance
   */
  protected MouseMotionListener createMouseMotionListener() {
    return new MouseMotionAdapter() {
      // for selection
      @Override
      public void mouseDragged(MouseEvent e) {
	// update zoom box
	if (m_Selecting && !e.isShiftDown()) {
	  m_Dragged     = true;
	  m_SelectionTo = e.getPoint();
          if (canAddTracePoint(e.getPoint()))
            m_SelectionTrace.add(e.getPoint());
	  getOwner().update();
	}
      }
      @Override
      public void mouseMoved(MouseEvent e) {
	if (e.getButton() == MouseEvent.BUTTON1) {
	  // get top/left coordinates for zoom
	  if (!e.isShiftDown()) {
	    m_Selecting     = true;
	    m_Dragged       = false;
	    m_SelectionFrom = e.getPoint();
	  }
	}
      }
    };
  }

  /**
   * Returns whether the current point is at least the specified distance
   * away from the last one.
   *
   * @param p		the point to check
   * @return		true if can be added
   */
  public boolean canAddTracePoint(Point p) {
    Point	last;
    double	dist;

    if (m_SelectionTrace.size() == 0)
      return true;

    last = m_SelectionTrace.get(m_SelectionTrace.size() - 1);
    dist = Math.sqrt(Math.pow(p.getX() - last.getX(), 2) + Math.pow(p.getY() - last.getY(), 2));

    return (dist >= m_MinDistance);
  }

  /**
   * Turns the trace into a polygon.
   *
   * @return		the polygon, null if unable to convert (eg empty trace)
   */
  public Polygon traceToPolygon() {
    Polygon	result;
    int[]	poly_x;
    int[]	poly_y;
    int		i;
    Point	p;

    result = null;

    if (m_SelectionTrace.size() > 0) {
      poly_x = new int[m_SelectionTrace.size()];
      poly_y = new int[m_SelectionTrace.size()];
      for (i = 0; i < m_SelectionTrace.size(); i++) {
        p         = getOwner().mouseToPixelLocation(m_SelectionTrace.get(i));
	poly_x[i] = (int) p.getX();
	poly_y[i] = (int) p.getY();
      }
      result = new Polygon(poly_x, poly_y, poly_x.length);
    }

    return result;
  }

  /**
   * Processes the selection.
   *
   * @param modifiersEx	the associated modifiers
   */
  protected void processSelection(int modifiersEx) {
    int				lastIndex;
    Report 			report;
    String			current;
    Polygon			poly;
    int[]			poly_x;
    int[]			poly_y;
    int				i;
    int				x;
    int				y;
    int				w;
    int				h;
    SelectionRectangle 		rect;
    boolean			modified;
    List<SelectionRectangle>	queue;
    Rectangle 			bounds;
    String			comment;

    if (m_SelectionTrace.size() == 0)
      return;

    comment = "";
    report  = getOwner().getReport().getClone();
    if (m_Locations == null)
      m_Locations = getLocations(report);

    // polygon overrides rectangle corners
    poly   = traceToPolygon();
    bounds = poly.getBounds();
    x = (int) bounds.getX();
    y = (int) bounds.getY();
    w = (int) bounds.getWidth();
    h = (int) bounds.getHeight();
    rect = new SelectionRectangle(x, y, w, h, -1);

    // ignore empty rectangles (which can occur with a stylus)
    if ((w == 0) || (h == 0))
      return;

    queue    = new ArrayList<>();
    modified = false;
    if ((modifiersEx & MouseEvent.CTRL_DOWN_MASK) != 0) {
      for (SelectionRectangle r: m_Locations) {
	if (rect.contains(r)) {
	  if (removeIndex(report, r.getIndex())) {
	    modified = true;
	    queue.add(r);
	  }
	}
      }
      m_Locations.removeAll(queue);
      comment = "Removing " + queue.size() + " polygons";
    }
    else {
      if (!m_Locations.contains(rect)) {
	modified  = true;
	lastIndex = findLastIndex(report);
	rect.setIndex(lastIndex + 1);
	current   = m_Prefix + (Utils.padLeft("" + rect.getIndex(), '0', m_NumDigits));
	report.setNumericValue(current + KEY_X, x);
	report.setNumericValue(current + KEY_Y, y);
	report.setNumericValue(current + KEY_WIDTH, w);
	report.setNumericValue(current + KEY_HEIGHT, h);
	poly_x = new int[m_SelectionTrace.size()];
	poly_y = new int[m_SelectionTrace.size()];
	for (i = 0; i < m_SelectionTrace.size(); i++) {
	  poly_x[i] = (int) getOwner().mouseToPixelLocation(m_SelectionTrace.get(i)).getX();
	  poly_y[i] = (int) getOwner().mouseToPixelLocation(m_SelectionTrace.get(i)).getY();
	}
	report.setStringValue(current + KEY_POLY_X, Utils.flatten(StatUtils.toNumberArray(poly_x), ","));
	report.setStringValue(current + KEY_POLY_Y, Utils.flatten(StatUtils.toNumberArray(poly_y), ","));
	if (hasCurrentLabel())
	  report.setStringValue(current + m_LabelSuffix, getCurrentLabel());
	m_Locations.add(rect);
	comment = "Adding polygon: " + rect;
      }
    }

    if (modified) {
      getOwner().addUndoPoint(comment);
      getOwner().setReport(report);
      getOwner().annotationsChanged(this);
      getOwner().update();
    }
  }

  /**
   * Installs the annotator with the owner.
   */
  @Override
  protected void doInstall() {
    getOwner().getCanvas().addMouseListener(m_MouseListener);
    getOwner().getCanvas().addMouseMotionListener(m_MouseMotionListener);
  }

  /**
   * Uninstalls the annotator with the owner.
   */
  @Override
  protected void doUninstall() {
    getOwner().getCanvas().removeMouseListener(m_MouseListener);
    getOwner().getCanvas().removeMouseMotionListener(m_MouseMotionListener);
  }

  /**
   * Paints the selection.
   *
   * @param g		the graphics context
   */
  protected void doPaintSelection(Graphics g) {
    Polygon	poly;
    float	width;

    if (m_SelectionTrace.size() == 0)
      return;

    width = getStrokeWidth(g, 1.0f);
    applyStroke(g, m_StrokeThickness);

    g.setColor(m_Color);
    poly = traceToPolygon();
    g.drawPolygon(poly.xpoints, poly.ypoints, poly.npoints);

    applyStroke(g, width);
  }
}
