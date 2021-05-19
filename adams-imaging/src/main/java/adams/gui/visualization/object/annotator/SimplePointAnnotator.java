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
 * SimplePointAnnotator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotator;

import adams.core.Utils;
import adams.data.report.Report;
import adams.gui.core.MouseUtils;
import adams.gui.visualization.image.RectangleUtils;
import adams.gui.visualization.image.RectangleUtils.RectangleCorner;
import adams.gui.visualization.image.SelectionPoint;
import adams.gui.visualization.image.SelectionRectangle;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * For annotating single points.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SimplePointAnnotator
  extends AbstractPointAnnotator
  implements LabelSuffixHandler {

  private static final long serialVersionUID = -1849931714797487127L;

  /** the color to use. */
  protected Color m_Color;

  /** the thickness of the stroke. */
  protected float m_StrokeThickness;

  /** the label suffix to use. */
  protected String m_LabelSuffix;

  /** the mouse listener to install. */
  protected MouseListener m_MouseListener;

  /** whether dragging has happened at all. */
  protected boolean m_Dragged;

  /** the mouse motion listener to install. */
  protected MouseMotionListener m_MouseMotionListener;

  /** the starting corner of the selection box. */
  protected Point m_SelectionFrom;

  /** the finishing corner of the selection box. */
  protected Point m_SelectionTo;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For annotating single points.";
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
   * Returns the default label to use for the objects.
   *
   * @return		the default
   */
  protected String getDefaultLabel() {
    return "";
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
        if ((e.getButton() == MouseEvent.BUTTON1) && !MouseUtils.isDoubleClick(e)) {
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
        if (MouseUtils.isLeftClick(e) && !MouseUtils.isDoubleClick(e) && MouseUtils.hasNoModifierKey(e)) {
	  m_SelectionFrom = e.getPoint();
	  m_SelectionTo = e.getPoint();
	  m_Selecting = false;
	  m_Dragged = false;
	  processSelection(e.getModifiersEx());
	}
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
   * Processes the selection.
   *
   * @param modifiersEx	the associated modifiers
   */
  protected void processSelection(int modifiersEx) {
    int				lastIndex;
    Report 			report;
    String			current;
    Point			topLeft;
    Point			bottomRight;
    int				x;
    int				y;
    int				w;
    int				h;
    SelectionRectangle 		rect;
    SelectionPoint		point;
    boolean			modified;
    List<SelectionPoint> 	queue;
    String			comment;

    if ((m_SelectionFrom == null) || (m_SelectionTo == null))
      return;

    topLeft     = RectangleUtils.rectangleCorner(m_SelectionFrom, m_SelectionTo, RectangleCorner.TOP_LEFT);
    bottomRight = RectangleUtils.rectangleCorner(m_SelectionFrom, m_SelectionTo, RectangleCorner.BOTTOM_RIGHT);
    comment     = "";

    report = getOwner().getReport().getClone();
    if (m_Locations == null)
      m_Locations = getLocations(report);

    x = getOwner().mouseToPixelLocation(topLeft).x;
    y = getOwner().mouseToPixelLocation(topLeft).y;
    w = getOwner().mouseToPixelLocation(bottomRight).x - getOwner().mouseToPixelLocation(topLeft).x + 1;
    h = getOwner().mouseToPixelLocation(bottomRight).y - getOwner().mouseToPixelLocation(topLeft).y + 1;
    rect  = new SelectionRectangle(x, y, w, h, -1);
    point = new SelectionPoint(x, y, -1);

    // ignore empty rectangles (which can occur with a stylus)
    if ((w == 0) || (h == 0))
      return;

    queue    = new ArrayList<>();
    modified = false;
    if ((modifiersEx & MouseEvent.CTRL_DOWN_MASK) != 0) {
      for (SelectionPoint r: m_Locations) {
	if (rect.contains(r)) {
	  if (removeIndex(report, r.getIndex())) {
	    modified = true;
	    queue.add(r);
	  }
	}
      }
      m_Locations.removeAll(queue);
      comment = "Removing " + queue.size() + " points";
    }
    else {
      if (!m_Locations.contains(point)) {
	modified  = true;
	lastIndex = findLastIndex(report);
	point.setIndex(lastIndex + 1);
	current   = m_Prefix + (Utils.padLeft("" + point.getIndex(), '0', m_NumDigits));
	report.setNumericValue(current + KEY_X, x);
	report.setNumericValue(current + KEY_Y, y);
	if (hasCurrentLabel())
	  report.setStringValue(current + m_LabelSuffix, getCurrentLabel());
	m_Locations.add(point);
	comment = "Adding point: " + point;
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
    getOwner().getLabelSelectorPanel().setUnsetButtonVisible(false);
  }

  /**
   * Uninstalls the annotator with the owner.
   */
  @Override
  protected void doUninstall() {
    getOwner().getCanvas().removeMouseListener(m_MouseListener);
    getOwner().getCanvas().removeMouseMotionListener(m_MouseMotionListener);
    getOwner().getLabelSelectorPanel().setUnsetButtonVisible(true);
  }

  /**
   * Paints the selection.
   *
   * @param g		the graphics context
   */
  protected void doPaintSelection(Graphics g) {
    Point	topLeft;
    Point	bottomRight;
    int		topX;
    int		bottomX;
    int		topY;
    int		bottomY;
    int		tmp;
    float	width;

    if ((m_SelectionFrom == null) || (m_SelectionTo == null))
      return;

    width = getStrokeWidth(g, 1.0f);
    applyStroke(g, m_StrokeThickness);

    topLeft     = RectangleUtils.rectangleCorner(m_SelectionFrom, m_SelectionTo, RectangleCorner.TOP_LEFT);
    bottomRight = RectangleUtils.rectangleCorner(m_SelectionFrom, m_SelectionTo, RectangleCorner.BOTTOM_RIGHT);

    g.setColor(m_Color);

    topX    = (int) getOwner().mouseToPixelLocation(topLeft).getX();
    topY    = (int) getOwner().mouseToPixelLocation(topLeft).getY();
    bottomX = (int) getOwner().mouseToPixelLocation(bottomRight).getX();
    bottomY = (int) getOwner().mouseToPixelLocation(bottomRight).getY();

    // swap necessary?
    if (topX > bottomX) {
      tmp     = topX;
      topX    = bottomX;
      bottomX = tmp;
    }
    if (topY > bottomY) {
      tmp     = topY;
      topY    = bottomY;
      bottomY = tmp;
    }

    g.drawRect(
      topX,
      topY,
      (bottomX - topX + 1),
      (bottomY - topY + 1));

    applyStroke(g, width);
  }
}
