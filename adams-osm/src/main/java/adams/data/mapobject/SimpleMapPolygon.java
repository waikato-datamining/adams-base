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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.Arrays;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.core.annotation.ThirdPartyCopyright;

/**
 * Adapted {@link org.openstreetmap.gui.jmapviewer.MapPolygonImpl} class.
 * 
 * @author Vincent (original code: {@link org.openstreetmap.gui.jmapviewer.MapPolygonImpl})
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "OpenStreetMap",
    author = "Vincent",
    license = License.GPL2,
    url = "http://svn.openstreetmap.org/applications/viewer/jmapviewer/releases/1.02/JMapViewer-1.02-Source.zip",
    note = "Original class: org.openstreetmap.gui.jmapviewer.MapPolygonImpl"
)
public class SimpleMapPolygon
  extends AbstractMapObject 
  implements MapPolygon, HitIndicator {

  private List<? extends ICoordinate> points;

  public SimpleMapPolygon(ICoordinate ... points) {
    this(null, null, points);
  }
  
  public SimpleMapPolygon(List<? extends ICoordinate> points) {
    this(null, null, points);
  }
  
  public SimpleMapPolygon(String name, List<? extends ICoordinate> points) {
    this(null, name, points);
  }
  
  public SimpleMapPolygon(String name, ICoordinate ... points) {
    this(null, name, points);
  }
  public SimpleMapPolygon(Layer layer, List<? extends ICoordinate> points) {
    this(layer, null, points);
  }
  
  public SimpleMapPolygon(Layer layer, String name, List<? extends ICoordinate> points) {
    this(layer, name, points, getDefaultStyle());
  }
  
  public SimpleMapPolygon(Layer layer, String name, ICoordinate ... points) {
    this(layer, name, Arrays.asList(points), getDefaultStyle());
  }
  
  public SimpleMapPolygon(Layer layer, String name, List<? extends ICoordinate> points, Style style) {
    super(layer, name, style);
    this.points = points;
  }

  @Override
  public List<? extends ICoordinate> getPoints() {
    return this.points;
  }

  @Override
  public void paint(Graphics g, List<Point> points) {
    Polygon polygon = new Polygon();
    for (Point p : points) {
      polygon.addPoint(p.x, p.y);
    }
    paint(g, polygon);
  }

  @Override
  public void paint(Graphics g, Polygon polygon) {
    // Prepare graphics
    Color oldColor = g.getColor();
    g.setColor(getColor());

    Stroke oldStroke = null;
    if (g instanceof Graphics2D) {
      Graphics2D g2 = (Graphics2D) g;
      oldStroke = g2.getStroke();
      g2.setStroke(getStroke());
    }
    // Draw
    g.drawPolygon(polygon);
    if (g instanceof Graphics2D && getBackColor()!=null) {
      Graphics2D g2 = (Graphics2D) g;
      Composite oldComposite = g2.getComposite();
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
      g2.setPaint(getBackColor());
      g2.fillPolygon(polygon);
      g2.setComposite(oldComposite);
    }
    // Restore graphics
    g.setColor(oldColor);
    if (g instanceof Graphics2D) {
      ((Graphics2D) g).setStroke(oldStroke);
    }
    Rectangle rec = polygon.getBounds();
    Point corner = rec.getLocation();
    Point p= new Point(corner.x+(rec.width/2), corner.y+(rec.height/2));
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
  @ThirdPartyCopyright(
      copyright = "1970-2003 Wm. Randolph Franklin",
      license = License.BSD3,
      url = "http://stackoverflow.com/a/2212851"
  )
  public boolean isHit(JMapViewer viewer, Coordinate coord) {
    boolean	result;
    int		i;
    int		j;
    
    result = false;

    for (i = 0, j = points.size() - 1; i < points.size(); j = i++) {
      if (    ((points.get(i).getLat() > coord.getLat()) != (points.get(j).getLat() > coord.getLat())) 
	   && (coord.getLon() < (points.get(j).getLon() - points.get(i).getLon()) * (coord.getLat() - points.get(i).getLat()) / (points.get(j).getLat() - points.get(i).getLat()) + points.get(i).getLon())) {
	result = !result;
      }
    }
    
    return result;
  }
  
  @Override
  public String toString() {
    return "Polygon: name=" + getName() + ", coords=" + getPoints() + ", meta=" + getMetaData();
  }
}
