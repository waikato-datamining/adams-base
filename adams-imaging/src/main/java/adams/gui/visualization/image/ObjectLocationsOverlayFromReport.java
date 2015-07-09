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
 * ObjectLocationsOverlayFromReport.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Displays the locations of objects in the image, using data from the attached report.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-enabled &lt;boolean&gt; (property: enabled)
 * &nbsp;&nbsp;&nbsp;If enabled, this overlay is painted over the image.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix of fields in the report to identify as object location, eg 'Object.
 * &nbsp;&nbsp;&nbsp;'.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color to use for the objects.
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 198 $
 */
public class ObjectLocationsOverlayFromReport
  extends AbstractImageOverlay {

  /** for serialization. */
  private static final long serialVersionUID = 6356419097401574024L;

  /** the prefix for the objects in the report. */
  protected String m_Prefix;

  /** the color for the objects. */
  protected Color m_Color;
  
  /** the cached locations. */
  protected List<Rectangle> m_Locations;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the locations of objects in the image, using data from the attached report.";
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
	"color", "color",
	Color.RED);
  }

  /**
   * Sets the prefix to use for the objects in the report.
   *
   * @param value 	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to use for the objects in the report.
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
    return "The prefix of fields in the report to identify as object location, eg 'Object.'.";
  }

  /**
   * Sets the color to use for the objects.
   *
   * @param value 	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color to use for the objects.
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
    return "The color to use for the objects.";
  }

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  @Override
  protected synchronized void doImageChanged(PaintPanel panel) {
    m_Locations = null;
  }
  
  /**
   * Determines the locations of the bugs.
   * 
   * @param report	the report to inspect
   */
  protected void determineLocations(Report report) {
    List<AbstractField>	fields;
    String		current;
    int			x;
    int			y;
    int			width;
    int			height;
    Rectangle		rect;

    if (m_Locations != null)
      return;

    fields = report.getFields();
    
    m_Locations = new ArrayList<Rectangle>();
    for (AbstractField field: fields) {
      if (field.getName().startsWith(m_Prefix)) {
	try {
	  current = field.getName().substring(0, field.getName().lastIndexOf('.'));
	  if (    report.hasValue(current + LocatedObjects.KEY_X) 
	       && report.hasValue(current + LocatedObjects.KEY_Y) 
	       && report.hasValue(current + LocatedObjects.KEY_WIDTH) 
	       && report.hasValue(current + LocatedObjects.KEY_HEIGHT) ) {
	    x      = report.getDoubleValue(current + LocatedObjects.KEY_X).intValue();
	    y      = report.getDoubleValue(current + LocatedObjects.KEY_Y).intValue();
	    width  = report.getDoubleValue(current + LocatedObjects.KEY_WIDTH).intValue();
	    height = report.getDoubleValue(current + LocatedObjects.KEY_HEIGHT).intValue();
	    rect   = new Rectangle(new Point(x, y), new Dimension(width, height));
	    m_Locations.add(rect);
	  }
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }
  }

  /**
   * Performs the actual painting of the overlay.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  @Override
  protected synchronized void doPaintOverlay(PaintPanel panel, Graphics g) {
    determineLocations(panel.getOwner().getAdditionalProperties());
    
    if (m_Locations.size() > 0) {
      g.setColor(m_Color);
      for (Rectangle rect: m_Locations) {
	g.drawRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
      }
    }
  }
}
