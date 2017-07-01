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
 * RandomBoundingBox.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.leftclick;

import adams.core.Randomizable;
import adams.core.Utils;
import adams.core.base.BaseInterval;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.SelectionRectangle;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 <!-- globalinfo-start -->
 * Allows the user to create randomly sized bounding boxes around the left-click position.
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
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the random bounding boxes.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-range-width &lt;adams.core.base.BaseInterval&gt; (property: rangeWidth)
 * &nbsp;&nbsp;&nbsp;The range of pixels for the bounding box width.
 * &nbsp;&nbsp;&nbsp;default: [10;50]
 * </pre>
 * 
 * <pre>-range-height &lt;adams.core.base.BaseInterval&gt; (property: rangeHeight)
 * &nbsp;&nbsp;&nbsp;The range of pixels for the bounding box height.
 * &nbsp;&nbsp;&nbsp;default: [10;50]
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RandomBoundingBox
  extends AbstractSelectionRectangleBasedLeftClickProcessor
  implements Randomizable {

  private static final long serialVersionUID = 4069769951854697560L;

  /** the seed value. */
  protected long m_Seed;

  /** the range for the width. */
  protected BaseInterval m_RangeWidth;

  /** the range for the height. */
  protected BaseInterval m_RangeHeight;

  /** the random number generator to use. */
  protected Random m_Random;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Allows the user to create randomly sized bounding boxes around the "
        + "left-click position (<ctrl> left-click in box removes it).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "range-width", "rangeWidth",
      new BaseInterval(10, 50));

    m_OptionManager.add(
      "range-height", "rangeHeight",
      new BaseInterval(10, 50));
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Random = null;
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  @Override
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  @Override
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String seedTipText() {
    return "The seed value for the random bounding boxes.";
  }

  /**
   * Sets the pixel range for the bounding box width.
   *
   * @param value 	the range
   */
  public void setRangeWidth(BaseInterval value) {
    m_RangeWidth = value;
    reset();
  }

  /**
   * Returns the pixel range for the bounding box width.
   *
   * @return 		the range
   */
  public BaseInterval getRangeWidth() {
    return m_RangeWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rangeWidthTipText() {
    return "The range of pixels for the bounding box width.";
  }

  /**
   * Sets the pixel range for the bounding box height.
   *
   * @param value 	the range
   */
  public void setRangeHeight(BaseInterval value) {
    m_RangeHeight = value;
    reset();
  }

  /**
   * Returns the pixel range for the bounding box height.
   *
   * @return 		the range
   */
  public BaseInterval getRangeHeight() {
    return m_RangeHeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rangeHeightTipText() {
    return "The range of pixels for the bounding box height.";
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
    int				w;
    int				h;
    boolean			modified;
    List<SelectionRectangle>	queue;
    String			current;
    SelectionRectangle 		rect;
    int				lastIndex;

    report = panel.getAdditionalProperties().getClone();
    if (m_Locations == null)
      m_Locations = getLocations(report);
    if (m_Random == null)
      m_Random = new Random(m_Seed);

    x         = panel.mouseToPixelLocation(position).x;
    y         = panel.mouseToPixelLocation(position).y;
    queue     = new ArrayList<>();
    modified  = false;
    if ((modifiersEx & MouseEvent.CTRL_DOWN_MASK) != 0) {
      for (SelectionRectangle r: m_Locations) {
	if (r.contains(x, y)) {
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
      w = (int) (m_RangeWidth.getLower()  + m_Random.nextDouble() * (m_RangeWidth.getUpper()  - m_RangeWidth.getLower()));
      h = (int) (m_RangeHeight.getLower() + m_Random.nextDouble() * (m_RangeHeight.getUpper() - m_RangeHeight.getLower()));
      rect = new SelectionRectangle(x - w / 2, y - h / 2, w, h);
      if (!m_Locations.contains(rect)) {
	modified  = true;
	lastIndex = findLastIndex(report);
	current   = m_Prefix + (Utils.padLeft("" + (lastIndex + 1), '0', m_NumDigits));
	report.setNumericValue(current + KEY_X, rect.getX());
	report.setNumericValue(current + KEY_Y, rect.getY());
	report.setNumericValue(current + KEY_WIDTH, rect.getWidth());
	report.setNumericValue(current + KEY_HEIGHT, rect.getHeight());
	m_Locations.add(rect);
      }
    }

    if (modified)
      panel.setAdditionalProperties(report);
  }
}
