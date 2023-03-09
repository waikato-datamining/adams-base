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
 * PolygonPointAnnotator.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotator;

import adams.core.Utils;
import adams.data.report.Report;
import adams.data.statistics.StatUtils;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.MouseUtils;
import adams.gui.visualization.image.PolygonUtils;
import adams.gui.visualization.image.SelectionRectangle;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * For annotating polygons by left-clicking on each vertex.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PolygonPointAnnotator
  extends AbstractRectangleBasedAnnotator
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

  /** the key listener to install. */
  protected KeyListener m_KeyListener;

  /** the points of the polygon. */
  protected List<Point> m_Points;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For annotating polygons by left-clicking on each vertex.\n"
      + "Pressing ENTER finishes the polygon.\n"
      + "Pressing ESC discards all points.";
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
    return "The color to use for drawing the box while building the polygon.";
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
    return "The thickness of the stroke for the polygon.";
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

    m_Points        = new ArrayList<>();
    m_MouseListener = createMouseListener();
    m_KeyListener   = createKeyListener();
  }

  /**
   * Creates the listener for mouse events.
   *
   * @return		the instance
   */
  protected MouseListener createMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	getOwner().getCanvas().logMouseButtonClick(e);
	if (MouseUtils.isLeftClick(e) && !MouseUtils.isDoubleClick(e) && MouseUtils.hasNoModifierKey(e)) {
	  m_Points.add(getOwner().mouseToPixelLocation(e.getPoint()));
	  m_Selecting = true;
	  getOwner().getCanvas().repaint();
	}
      }
    };
  }

  /**
   * Constructs the key listener to use.
   *
   * @return		the listener
   */
  protected KeyListener createKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          m_Selecting = false;
          processSelection();
          e.consume();
	}
        else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	  m_Selecting = false;
          m_Points.clear();
          getOwner().getCanvas().repaint();
          e.consume();
	}
	super.keyPressed(e);
      }
    };
  }

  /**
   * Processes the selection.
   */
  protected void processSelection() {
    int				lastIndex;
    Report 			report;
    String			current;
    Rectangle			bbox;
    SelectionRectangle 		rect;
    Polygon			poly;
    boolean			modified;
    String			comment;

    if (m_Points.size() < 3)
      return;

    bbox    = PolygonUtils.boundingBox(m_Points);
    comment = "";

    report = getOwner().getReport().getClone();
    if (m_Locations == null)
      m_Locations = getLocations(report);

    rect = new SelectionRectangle(bbox);

    // ignore empty rectangles (which can occur with a stylus)
    if ((bbox.width == 0) || (bbox.height == 0))
      return;

    modified = false;
    if (!m_Locations.contains(rect)) {
      modified  = true;
      lastIndex = findLastIndex(report);
      rect.setIndex(lastIndex + 1);
      current   = m_Prefix + (Utils.padLeft("" + rect.getIndex(), '0', m_NumDigits));
      report.setNumericValue(current + LocatedObjects.KEY_X, bbox.x);
      report.setNumericValue(current + LocatedObjects.KEY_Y, bbox.y);
      report.setNumericValue(current + LocatedObjects.KEY_WIDTH, bbox.width);
      report.setNumericValue(current + LocatedObjects.KEY_HEIGHT, bbox.height);
      poly = PolygonUtils.toPolygon(m_Points);
      report.setStringValue(current + LocatedObjects.KEY_POLY_X, Utils.flatten(StatUtils.toNumberArray(poly.xpoints), ","));
      report.setStringValue(current + LocatedObjects.KEY_POLY_Y, Utils.flatten(StatUtils.toNumberArray(poly.ypoints), ","));
      if (hasCurrentLabel())
	report.setStringValue(current + m_LabelSuffix, getCurrentLabel());
      m_Locations.add(rect);
      comment = "Adding polygon: " + rect;
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
    getOwner().getCanvas().addKeyListener(m_KeyListener);
    getOwner().getLabelSelectorPanel().setUnsetButtonVisible(false);
  }

  /**
   * Uninstalls the annotator with the owner.
   */
  @Override
  protected void doUninstall() {
    getOwner().getCanvas().removeMouseListener(m_MouseListener);
    getOwner().getCanvas().removeKeyListener(m_KeyListener);
    getOwner().getLabelSelectorPanel().setUnsetButtonVisible(true);
  }

  /**
   * Paints the selection.
   *
   * @param g		the graphics context
   */
  protected void doPaintSelection(Graphics g) {
    Polygon 	poly;
    float	width;

    if (m_Points.size() < 3)
      return;

    width = getStrokeWidth(g, 1.0f);
    applyStroke(g, m_StrokeThickness);

    g.setColor(m_Color);
    poly = PolygonUtils.toPolygon(m_Points);
    g.drawPolygon(poly.xpoints, poly.ypoints, poly.npoints);

    applyStroke(g, width);
  }
}
