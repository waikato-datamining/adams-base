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
 * SelectObjects.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.selection;

import adams.core.Utils;
import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 364 $
 */
public class SelectObjects
  extends AbstractPaintingSelectionProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -5879410661391670242L;

  /** the key for the X location. */
  public final static String KEY_X = LocatedObjects.KEY_X;

  /** the key for the Y location. */
  public final static String KEY_Y = LocatedObjects.KEY_Y;

  /** the key for the width. */
  public final static String KEY_WIDTH = LocatedObjects.KEY_WIDTH;

  /** the key for the height. */
  public final static String KEY_HEIGHT = LocatedObjects.KEY_HEIGHT;
  
  /** the prefix for the objects. */
  protected String m_Prefix;
  
  /** the number of digits to use for left-padding the index. */
  protected int m_NumDigits;
  
  /** the current rectangles. */
  protected List<SelectionRectangle> m_Locations;
  
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
	"prefix", "prefix",
	"Object.");

    m_OptionManager.add(
	"num-digits", "numDigits",
	4, 0, null);
  }
  
  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Locations = null;
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
   * Sets the prefix to use for the objects.
   *
   * @param value 	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to use for the objects.
   *
   * @return 		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix to use for the fields in the report.";
  }

  /**
   * Sets the number of digits to use for the left-padded index.
   *
   * @param value 	the number of digits
   */
  public void setNumDigits(int value) {
    m_NumDigits = value;
    reset();
  }

  /**
   * Returns the number of digits to use for the left-padded index.
   *
   * @return 		the number of digits
   */
  public int getNumDigits() {
    return m_NumDigits;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDigitsTipText() {
    return "The number of digits to use for left-padding the index with zeroes.";
  }

  /**
   * Determines the last index used with the given prefix.
   */
  protected int findLastIndex(Report report) {
    int			result;
    List<AbstractField>	fields;
    String		name;
    int			current;
    
    result = 0;
    fields = report.getFields();
    
    for (AbstractField field: fields) {
      if (field.getName().startsWith(m_Prefix)) {
	name = field.getName().substring(m_Prefix.length());
	if (name.indexOf('.') > -1)
	  name = name.substring(0, name.indexOf('.'));
	try {
	  current = Integer.parseInt(name);
	  if (current > result)
	    result = current;
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }
    
    return result;
  }
  
  /**
   * Retruns all currently stored locations.
   * 
   * @param report	the report to get the locations from
   * @return		the locations
   */
  protected List<SelectionRectangle> getLocations(Report report) {
    List<SelectionRectangle>	result;
    List<AbstractField>		fields;
    String			name;
    SelectionRectangle		rect;
    
    result = new ArrayList<>();
    fields = report.getFields();
    
    for (AbstractField field: fields) {
      if (field.getName().startsWith(m_Prefix)) {
	name = field.getName().substring(m_Prefix.length());
	if (name.indexOf('.') > -1)
	  name = name.substring(0, name.indexOf('.'));
	try {
	  rect = new SelectionRectangle(
	      report.getDoubleValue(m_Prefix + name + KEY_X).intValue(),
	      report.getDoubleValue(m_Prefix + name + KEY_Y).intValue(),
	      report.getDoubleValue(m_Prefix + name + KEY_WIDTH).intValue(),
	      report.getDoubleValue(m_Prefix + name + KEY_HEIGHT).intValue(),
	      Integer.parseInt(name));
	  if (!result.contains(rect))
	    result.add(rect);
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }
    
    return result;
  }

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  @Override
  protected void doImageChanged(PaintPanel panel) {
    m_Locations = null;
  }

  /**
   * Process the selection that occurred in the image panel.
   * 
   * @param panel	the origin
   * @param topLeft	the top-left position of the selection
   * @param bottomRight	the bottom-right position of the selection
   * @param modifiersEx	the associated modifiers
   */
  @Override
  protected void doProcessSelection(ImagePanel panel, Point topLeft, Point bottomRight, int modifiersEx) {
    int				lastIndex;
    Report			report;
    String			current;
    int				x;
    int				y;
    int				w;
    int				h;
    SelectionRectangle		rect;
    boolean			modified;
    List<SelectionRectangle>	queue;

    report = panel.getAdditionalProperties().getClone();
    if (m_Locations == null)
      m_Locations = getLocations(report);
    
    x         = panel.mouseToPixelLocation(topLeft).x;
    y         = panel.mouseToPixelLocation(topLeft).y;
    w         = panel.mouseToPixelLocation(bottomRight).x - panel.mouseToPixelLocation(topLeft).x + 1;
    h         = panel.mouseToPixelLocation(bottomRight).y - panel.mouseToPixelLocation(topLeft).y + 1;
    rect      = new SelectionRectangle(x, y, w, h, -1);
    queue     = new ArrayList<>();
    modified  = false;
    if ((modifiersEx & MouseEvent.CTRL_DOWN_MASK) != 0) {
      for (SelectionRectangle r: m_Locations) {
	if (rect.contains(r)) {
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
      if (!m_Locations.contains(rect)) {
	modified  = true;
	lastIndex = findLastIndex(report);
	current   = m_Prefix + (Utils.padLeft("" + (lastIndex + 1), '0', m_NumDigits));
	report.setNumericValue(current + KEY_X, x);
	report.setNumericValue(current + KEY_Y, y);
	report.setNumericValue(current + KEY_WIDTH, w);
	report.setNumericValue(current + KEY_HEIGHT, h);
	m_Locations.add(rect);
      }
    }
    
    if (modified)
      panel.setAdditionalProperties(report);
  }
}
