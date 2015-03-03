/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/**
 * Copyright (C) OpenStreetMap
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */

package adams.data.mapobject;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.MapRectangleImpl;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle;

import adams.core.License;
import adams.core.annotation.MixedCopyright;

/**
 * Adapted {@link MapRectangleImpl} class.
 * 
 * @author Vincent (original code: {@link MapRectangleImpl})
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@MixedCopyright(
    copyright = "OpenStreetMap",
    author = "Vincent",
    license = License.GPL2,
    url = "http://svn.openstreetmap.org/applications/viewer/jmapviewer/releases/1.02/JMapViewer-1.02-Source.zip",
    note = "Original class: org.openstreetmap.gui.jmapviewer.MapRectangleImpl"
)
public class SimpleMapRectangle 
  extends AbstractMapObject 
  implements MapRectangle, HitIndicator {

  private Coordinate topLeft;
  
  private Coordinate bottomRight;

  public SimpleMapRectangle(Coordinate topLeft, Coordinate bottomRight) {
    this(null, null, topLeft, bottomRight);
  }
  
  public SimpleMapRectangle(String name, Coordinate topLeft, Coordinate bottomRight) {
    this(null, name, topLeft, bottomRight);
  }
  
  public SimpleMapRectangle(Layer layer, Coordinate topLeft, Coordinate bottomRight) {
    this(layer, null, topLeft, bottomRight);
  }
  
  public SimpleMapRectangle(Layer layer, String name, Coordinate topLeft, Coordinate bottomRight) {
    this(layer, name, topLeft, bottomRight, getDefaultStyle());
  }
  
  public SimpleMapRectangle(Layer layer, String name, Coordinate topLeft, Coordinate bottomRight, Style style) {
    super(layer, name, style);
    this.topLeft = topLeft;
    this.bottomRight = bottomRight;
  }

  @Override
  public Coordinate getTopLeft() {
    return topLeft;
  }

  @Override
  public Coordinate getBottomRight() {
    return bottomRight;
  }

  @Override
  public void paint(Graphics g, Point topLeft, Point bottomRight) {
    // Prepare graphics
    Color oldColor = g.getColor();
    g.setColor(getColor());
    Stroke oldStroke = null;
    if (g instanceof Graphics2D) {
      Graphics2D g2 = (Graphics2D) g;
      oldStroke = g2.getStroke();
      g2.setStroke(getStroke());
      g2.setPaint(getBackColor());
    }
    // Draw
    g.fillRect(topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y);
    g.setColor(getColor());
    g.drawRect(topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y);
    // Restore graphics
    g.setColor(oldColor);
    if (g instanceof Graphics2D) {
      ((Graphics2D) g).setStroke(oldStroke);
    }
    int width=bottomRight.x-topLeft.x;
    int height=bottomRight.y-topLeft.y;
    Point p= new Point(topLeft.x+(width/2), topLeft.y+(height/2));
    if(getLayer()==null||getLayer().isVisibleTexts()) paintText(g, p);
  }
  
  public static Style getDefaultStyle(){
    return new Style(Color.BLUE, new Color(100,100,100,50), new BasicStroke(2), getDefaultFont());
  }

  /**
   * Checks whether the given coordinate is a hit for a specific mapobject.
   * 
   * @param viewer	the underlying viewer that triggered the call
   * @param coord	the coordinate to check
   * @return		true if the mapobject is "hit" by this coordinate
   */
  public boolean isHit(JMapViewer viewer, Coordinate coord) {
    if (    (coord.getLat() <= topLeft.getLat())
	 && (coord.getLon() >= topLeft.getLon())
	 && (coord.getLat() >= bottomRight.getLat())
	 && (coord.getLon() <= bottomRight.getLon()) )
      return true;
    else
      return false;
  }
  
  @Override
  public String toString() {
    return "Rectangle: name=" + getName() + ", topleft=" + getTopLeft() + ", bottomright=" + getBottomRight() + ", meta=" + getMetaData();
  }
}
