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
 * SelectObjects.java
 * Copyright (C) 2017-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.selection;

import adams.core.Utils;
import adams.data.report.Report;
import adams.data.statistics.StatUtils;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.SelectionRectangle;
import adams.gui.visualization.image.interactionlogging.InteractionEvent;
import adams.gui.visualization.image.interactionlogging.InteractionLoggingSupporter;
import adams.gui.visualization.image.leftclick.AddMetaData;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Allows the user to select objects in the image.<br>
 * The locations get stored in the attached report.<br>
 * If the &lt;ctrl&gt; key is pressed while drawing a selection rectangle, all enclosed locations get removed.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color to use for painting.
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 * 
 * <pre>-stroke-thickness &lt;float&gt; (property: strokeThickness)
 * &nbsp;&nbsp;&nbsp;The thickness of the stroke.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.01
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use for the fields in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 * 
 * <pre>-num-digits &lt;int&gt; (property: numDigits)
 * &nbsp;&nbsp;&nbsp;The number of digits to use for left-padding the index with zeroes.
 * &nbsp;&nbsp;&nbsp;default: 4
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-label &lt;java.lang.String&gt; (property: label)
 * &nbsp;&nbsp;&nbsp;The label to use for the objects, not set if empty.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-label-suffix &lt;java.lang.String&gt; (property: labelSuffix)
 * &nbsp;&nbsp;&nbsp;The suffix to use for storing the label in the report.
 * &nbsp;&nbsp;&nbsp;default: .type
 * </pre>
 *
 * <pre>-add-meta-data &lt;boolean&gt; (property: addMetaData)
 * &nbsp;&nbsp;&nbsp;If enabled, a dialog gets shown to add meta-data to the object.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SelectObjects
  extends AbstractSelectionRectangleBasedSelectionProcessor
  implements SelectionProcessorWithLabelSupport, InteractionLoggingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -5879410661391670242L;

  /** the label to use. */
  protected String m_Label;

  /** the label suffix to use. */
  protected String m_LabelSuffix;

  /** whether to add meta-data. */
  protected boolean m_AddMetaData;

  /** the AddMetaData instance. */
  protected transient AddMetaData m_AddMetaDataInstance;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	  "Allows the user to select objects in the image.\n"
	+ "The locations get stored in the attached report.\n"
	+ "If the <ctrl> key is pressed while drawing a selection rectangle, "
	+ "all enclosed locations get removed.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "label", "label",
      getDefaultLabel());

    m_OptionManager.add(
      "label-suffix", "labelSuffix",
      getDefaultLabelSuffix());

    m_OptionManager.add(
      "add-meta-data", "addMetaData",
      false);
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
   * Sets the label to use for the objects.
   *
   * @param value 	the prefix
   */
  public void setLabel(String value) {
    m_Label = value;
    reset();
  }

  /**
   * Returns the label to use for the objects.
   *
   * @return 		the label
   */
  public String getLabel() {
    return m_Label;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelTipText() {
    return "The label to use for the objects, not set if empty.";
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
  public void setLabelSuffix(String value) {
    m_LabelSuffix = value;
    reset();
  }

  /**
   * Returns the suffix to use for the label.
   *
   * @return 		the suffix
   */
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
   * Sets whether to allow adding meta-data to the object.
   *
   * @param value 	true if to add
   */
  public void setAddMetaData(boolean value) {
    m_AddMetaData = value;
    reset();
  }

  /**
   * Returns whether to allow adding meta-data to the object.
   *
   * @return 		true if to add
   */
  public boolean getAddMetaData() {
    return m_AddMetaData;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addMetaDataTipText() {
    return "If enabled, a dialog gets shown to add meta-data to the object.";
  }

  /**
   * Returns the default color to use.
   *
   * @return		the color
   */
  @Override
  protected Color getDefaultColor() {
    return Color.RED;
  }

  /**
   * Logs the adding of an object.
   *
   * @param panel	the panel to use for logging
   * @param x		the x coordinate
   * @param y		the y coordinate
   * @param w		the width
   * @param h		the height
   * @param poly_x	the x coordinates of the polygon, can be null
   * @param poly_y	the y coordinates of the polygon, can be null
   */
  protected void logAdd(ImagePanel panel, int x, int y, int w, int h, int[] poly_x, int[] poly_y) {
    InteractionEvent	e;
    Map<String,Object> data;

    data = new HashMap<>();
    data.put("x", x);
    data.put("y", y);
    data.put("width", w);
    data.put("height", h);
    if (poly_x != null)
      data.put("poly_x", poly_x);
    if (poly_y != null)
      data.put("poly_y", poly_y);
    if (!m_Label.isEmpty())
      data.put("label", m_Label);
    e = new InteractionEvent(panel, new Date(), "add", data);
    panel.getInteractionLoggingFilter().filterInteractionLog(e);
  }

  /**
   * Logs the removal of the of objects.
   *
   * @param panel	the panel to use for logging
   * @param x		the x of the removal rectangle
   * @param y		the y of the removal rectangle
   * @param w		the width of the removal rectangle
   * @param h		the height of the removal rectangle
   * @param removed	the removed objects
   */
  protected void logRemove(ImagePanel panel, int x, int y, int w, int h, List<Map<String,Object>> removed) {
    InteractionEvent	e;
    Map<String,Object> data;

    data = new HashMap<>();
    data.put("x", x);
    data.put("y", y);
    data.put("width", w);
    data.put("height", h);
    data.put("removed", removed);
    e = new InteractionEvent(panel, new Date(), "remove", data);
    panel.getInteractionLoggingFilter().filterInteractionLog(e);
  }

  /**
   * Process the selection that occurred in the image panel.
   * 
   * @param panel	the origin
   * @param topLeft	the top-left position of the selection
   * @param bottomRight	the bottom-right position of the selection
   * @param trace	the trace from the selection
   * @param modifiersEx	the associated modifiers
   */
  @Override
  protected void doProcessSelection(ImagePanel panel, Point topLeft, Point bottomRight, List<Point> trace, int modifiersEx) {
    int				lastIndex;
    Report			report;
    String			current;
    int				x;
    int				y;
    int				w;
    int				h;
    Polygon			poly;
    int[]			poly_x;
    int[]			poly_y;
    int				i;
    SelectionRectangle 		rect;
    boolean			modified;
    List<Map<String,Object>>	objects;
    List<SelectionRectangle>	queue;
    Rectangle			bounds;
    Point			center;

    report = panel.getAdditionalProperties().getClone();
    if (m_Locations == null)
      m_Locations = getLocations(report);

    // polygon overrides rectangle corners
    poly   = null;
    poly_x = null;
    poly_y = null;
    if (trace.size() > 0) {
      poly   = panel.traceToPolygon(trace);
      bounds = poly.getBounds();
      x = (int) bounds.getX();
      y = (int) bounds.getY();
      w = (int) bounds.getWidth();
      h = (int) bounds.getHeight();
    }
    else {
      x = panel.mouseToPixelLocation(topLeft).x;
      y = panel.mouseToPixelLocation(topLeft).y;
      w = panel.mouseToPixelLocation(bottomRight).x - panel.mouseToPixelLocation(topLeft).x + 1;
      h = panel.mouseToPixelLocation(bottomRight).y - panel.mouseToPixelLocation(topLeft).y + 1;
    }
    rect = new SelectionRectangle(x, y, w, h, -1);

    // ignore empty rectangles (which can occur with a stylus)
    if ((w == 0) || (h == 0))
      return;

    queue    = new ArrayList<>();
    objects  = new ArrayList<>();
    modified = false;
    if ((modifiersEx & MouseEvent.CTRL_DOWN_MASK) != 0) {
      for (SelectionRectangle r: m_Locations) {
	if (rect.contains(r)) {
	  objects.add(valuesForIndex(report, r.getIndex()));
	  if (removeIndex(report, r.getIndex())) {
	    modified = true;
	    queue.add(r);
	  }
	}
      }
      m_Locations.removeAll(queue);
      logRemove(panel, x, y, w, h, objects);
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
	// polygon
	if (poly != null) {
	  poly_x = new int[trace.size()];
	  poly_y = new int[trace.size()];
	  for (i = 0; i < trace.size(); i++) {
	    poly_x[i] = (int) panel.mouseToPixelLocation(trace.get(i)).getX();
	    poly_y[i] = (int) panel.mouseToPixelLocation(trace.get(i)).getY();
	  }
	  report.setStringValue(current + KEY_POLY_X, Utils.flatten(StatUtils.toNumberArray(poly_x), ","));
	  report.setStringValue(current + KEY_POLY_Y, Utils.flatten(StatUtils.toNumberArray(poly_y), ","));
        }
	if (!m_Label.isEmpty())
	  report.setStringValue(current + m_LabelSuffix, m_Label);
	m_Locations.add(rect);
	logAdd(panel, x, y, w, h, poly_x, poly_y);
      }
    }
    
    if (modified)
      panel.setAdditionalProperties(report);

    if (m_AddMetaData) {
      if (m_AddMetaDataInstance == null) {
	m_AddMetaDataInstance = new AddMetaData();
	m_AddMetaDataInstance.setPrefix(m_Prefix);
      }
      center = panel.pixelToMouseLocation(new Point(x + w/2, y + h/2));
      m_AddMetaDataInstance.processClick(panel, center, 0, true);
    }
  }

  /**
   * Returns whether interaction logging is supported.
   *
   * @return		true if supported
   */
  public boolean supportsInteractionLogging() {
    return true;
  }
}
