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

/**
 * FixedBoundingBox.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.leftclick;

import adams.core.Utils;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.SelectionRectangle;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Allows the user to create fixed-sized bounding boxes around the left-click position (&lt;ctrl&gt; left-click in box removes it).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
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
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the bounding box.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the bounding box.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedBoundingBox
  extends AbstractSelectionRectangleBasedLeftClickProcessor {

  private static final long serialVersionUID = 4069769951854697560L;

  /** the width. */
  protected int m_Width;

  /** the height. */
  protected int m_Height;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Allows the user to create fixed-sized bounding boxes around the "
        + "left-click position (<ctrl> left-click in box removes it).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "width", "width",
      10, 1, null);

    m_OptionManager.add(
      "height", "height",
      10, 1, null);
  }

  /**
   * Sets the width of the bounding box.
   *
   * @param value 	the width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the width of the bounding box.
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
    return "The width of the bounding box.";
  }

  /**
   * Sets the height of the bounding box.
   *
   * @param value 	the height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the height of the bounding box.
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
    return "The height of the bounding box.";
  }

  /**
   * Process the click that occurred in the image panel.
   *
   * @param panel	the origin
   * @param position	the position of the click
   * @param modifiersEx	the associated modifiers
   */
  @Override
  protected void doProcessClick(ImagePanel panel, Point position, int modifiersEx) {
    Report			report;
    int				x;
    int				y;
    boolean			modified;
    List<SelectionRectangle>	queue;
    String			current;
    SelectionRectangle 		rect;
    int				lastIndex;

    report = panel.getAdditionalProperties().getClone();
    if (m_Locations == null)
      m_Locations = getLocations(report);

    x         = panel.mouseToPixelLocation(position).x;
    y         = panel.mouseToPixelLocation(position).y;
    queue     = new ArrayList<>();
    modified  = false;
    if ((modifiersEx & MouseEvent.CTRL_DOWN_MASK) != 0) {
      for (SelectionRectangle r: m_Locations) {
	if (r.contains(position)) {
	  modified  = true;
	  current   = m_Prefix + (Utils.padLeft("" + r.getIndex(), '0', m_NumDigits));
	  report.removeValue(new Field(current + KEY_X, DataType.NUMERIC));
	  report.removeValue(new Field(current + KEY_Y, DataType.NUMERIC));
	  report.removeValue(new Field(current + KEY_WIDTH, DataType.NUMERIC));
	  report.removeValue(new Field(current + KEY_HEIGHT, DataType.NUMERIC));
	  queue.add(r);
	}
      }
      m_Locations.removeAll(queue);
    }
    else {
      rect = new SelectionRectangle(x - m_Width / 2, y - m_Height / 2, m_Width, m_Height);
      if (!m_Locations.contains(rect)) {
	modified  = true;
	lastIndex = findLastIndex(report);
	current   = m_Prefix + (Utils.padLeft("" + (lastIndex + 1), '0', m_NumDigits));
	report.setNumericValue(current + KEY_X, x);
	report.setNumericValue(current + KEY_Y, y);
	report.setNumericValue(current + KEY_WIDTH, m_Width);
	report.setNumericValue(current + KEY_HEIGHT, m_Height);
	m_Locations.add(rect);
      }
    }

    if (modified)
      panel.setAdditionalProperties(report);
  }
}
