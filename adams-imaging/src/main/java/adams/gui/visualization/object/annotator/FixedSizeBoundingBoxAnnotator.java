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
 * FixedSizeBoundingBoxAnnotator.java
 * Copyright (C) 2026 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotator;

import adams.core.Utils;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.image.SelectionRectangle;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * For annotating objects with bounding boxes of a fixed size.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FixedSizeBoundingBoxAnnotator
  extends AbstractRectangleBasedAnnotator
  implements LabelSuffixHandler {

  private static final long serialVersionUID = 1122040195846360397L;
  
  /** the width of the bounding box. */
  protected int m_Width;
  
  /** the height of the bounding box. */
  protected int m_Height;
  
  /** the color to use. */
  protected Color m_Color;

  /** the thickness of the stroke. */
  protected float m_StrokeThickness;

  /** the label suffix to use. */
  protected String m_LabelSuffix;

  /** the center of the box. */
  protected Point m_Center;

  /** the mouse listener to install. */
  protected MouseListener m_MouseListener;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For annotating objects with bounding boxes of a fixed size.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "width", "width",
      51, 1, null);

    m_OptionManager.add(
      "height", "height",
      51, 1, null);

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
   * Sets the width of the box.
   *
   * @param value 	the width
   */
  public void setWidth(int value) {
    if (getOptionManager().isValid("width", value)) {
      m_Width = value;
      reset();
    }
  }

  /**
   * Returns the width of the box.
   *
   * @return 		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the bounding, preferably an odd number.";
  }

  /**
   * Sets the height of the box.
   *
   * @param value 	the height
   */
  public void setHeight(int value) {
    if (getOptionManager().isValid("height", value)) {
      m_Height = value;
      reset();
    }
  }

  /**
   * Returns the height of the box.
   *
   * @return 		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the bounding, preferably an odd number.";
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

    m_Center        = null;
    m_MouseListener = createMouseListener();
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
	if (e.getButton() == MouseEvent.BUTTON1) {
	  // get top/left coordinates for selection
	  if (!e.isShiftDown()) {
	    m_Center = e.getPoint();
	    processSelection(e.getModifiersEx());
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
    int				x;
    int				y;
    int				w;
    int				h;
    SelectionRectangle 		rect;
    boolean			modified;
    List<SelectionRectangle>	queue;
    String			comment;

    if (m_Center == null)
      return;

    comment = "";

    report = getOwner().getReport().getClone();
    if (m_Locations == null)
      m_Locations = getLocations(report);

    // polygon overrides rectangle corners
    x = getOwner().mouseToPixelLocation(m_Center).x - m_Width / 2;
    y = getOwner().mouseToPixelLocation(m_Center).y - m_Height / 2;
    w = m_Width;
    h = m_Height;
    rect = new SelectionRectangle(x, y, w, h, -1);

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
      comment = "Removing " + queue.size() + " boxes";
    }
    else {
      if (!m_Locations.contains(rect)) {
	modified  = true;
	lastIndex = findLastIndex(report);
	rect.setIndex(lastIndex + 1);
	current   = m_Prefix + (Utils.padLeft("" + rect.getIndex(), '0', m_NumDigits));
	report.setNumericValue(current + LocatedObjects.KEY_X, x);
	report.setNumericValue(current + LocatedObjects.KEY_Y, y);
	report.setNumericValue(current + LocatedObjects.KEY_WIDTH, w);
	report.setNumericValue(current + LocatedObjects.KEY_HEIGHT, h);
	if (hasCurrentLabel())
	  report.setStringValue(current + m_LabelSuffix, getCurrentLabel());
	m_Locations.add(rect);
	comment = "Adding box: " + rect;
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
  }

  /**
   * Uninstalls the annotator with the owner.
   */
  @Override
  protected void doUninstall() {
    getOwner().getCanvas().removeMouseListener(m_MouseListener);
  }

  /**
   * Paints the selection.
   *
   * @param g		the graphics context
   */
  protected void doPaintSelection(Graphics g) {
    int		topX;
    int		bottomX;
    int		topY;
    int		bottomY;
    int		tmp;
    float	width;

    if (m_Center == null)
      return;

    width = getStrokeWidth(g, 1.0f);
    applyStroke(g, m_StrokeThickness);

    g.setColor(m_Color);

    topX    = (int) getOwner().mouseToPixelLocation(m_Center).getX() - m_Width / 2;
    topY    = (int) getOwner().mouseToPixelLocation(m_Center).getY() - m_Height / 2;
    bottomX = topX + m_Width - 1;
    bottomY = topY + m_Height - 1;

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
