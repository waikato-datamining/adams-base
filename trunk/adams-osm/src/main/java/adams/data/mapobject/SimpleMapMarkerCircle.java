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
 * Copyright (C) 2008 Jan Peter Stotz
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */

package adams.data.mapobject;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.MapMarkerCircle;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import adams.core.License;
import adams.core.annotation.MixedCopyright;

/**
 * A simple implementation of the {@link MapMarker} interface. Each map marker
 * is painted as a circle with a black border line and filled with a specified
 * color.
 *
 * @author Jan Peter Stotz (original code: {@link MapMarkerCircle})
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@MixedCopyright(
    copyright = "2008 OpenStreetMap",
    author = "Jan Peter Stotz",
    license = License.GPL2,
    url = "http://svn.openstreetmap.org/applications/viewer/jmapviewer/releases/1.02/JMapViewer-1.02-Source.zip",
    note = "Original class: org.openstreetmap.gui.jmapviewer.MapMarkerCircle"
)
public class SimpleMapMarkerCircle 
  extends AbstractMapObject 
  implements MapMarker, HitIndicator {

  protected Coordinate coord;
  
  protected double radius;
  
  protected STYLE markerStyle;
  
  /** the current radius in pixels. */
  protected int radiusPixels;

  public SimpleMapMarkerCircle(Coordinate coord, double radius) {
    this(null, null, coord, radius);
  }
  
  public SimpleMapMarkerCircle(String name, Coordinate coord, double radius) {
    this(null, name, coord, radius);
  }
  
  public SimpleMapMarkerCircle(Layer layer, Coordinate coord, double radius) {
    this(layer, null, coord, radius);
  }
  
  public SimpleMapMarkerCircle(double lat, double lon, double radius) {
    this(null, null, new Coordinate(lat,lon), radius);
  }
  
  public SimpleMapMarkerCircle(Layer layer, double lat, double lon, double radius) {
    this(layer, null, new Coordinate(lat,lon), radius);
  }
  
  public SimpleMapMarkerCircle(Layer layer, String name, Coordinate coord, double radius) {
    this(layer, name, coord, radius, STYLE.VARIABLE, getDefaultStyle());
  }
  
  public SimpleMapMarkerCircle(Layer layer, String name, Coordinate coord, double radius, STYLE markerStyle, Style style) {
    super(layer, name, style);
    this.markerStyle = markerStyle;
    this.coord = coord;
    this.radius = radius;
    radiusPixels = -1;
  }

  public Coordinate getCoordinate(){
    return coord;
  }
  
  public double getLat() {
    return coord.getLat();
  }

  public double getLon() {
    return coord.getLon();
  }
  
  public void setRadius(double value) {
    radius = value;
  }

  public double getRadius() {
    return radius;
  }

  public STYLE getMarkerStyle() {
    return markerStyle;
  }

  public void paint(Graphics g, Point position, int radio) {
    int size_h = radio;
    int size = size_h * 2;
    radiusPixels = radio;

    if (g instanceof Graphics2D && getBackColor()!=null) {
      Graphics2D g2 = (Graphics2D) g;
      Composite oldComposite = g2.getComposite();
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
      g2.setPaint(getBackColor());
      g.fillOval(position.x - size_h, position.y - size_h, size, size);
      g2.setComposite(oldComposite);
    }
    g.setColor(getColor());
    g.drawOval(position.x - size_h, position.y - size_h, size, size);

    if(getLayer()==null||getLayer().isVisibleTexts()) paintText(g, position);
  }

  public static Style getDefaultStyle(){
    return new Style(Color.ORANGE, new Color(100,100,100,50), null, getDefaultFont());
  }
  
  @Override
  public void setLat(double lat) {
    if(coord==null) coord = new Coordinate(lat,0);
    else coord.setLat(lat);
  }
  
  @Override
  public void setLon(double lon) {
    if(coord==null) coord = new Coordinate(0,lon);
    else coord.setLon(lon);
  }

  /**
   * Checks whether the given coordinate is a hit for a specific mapobject.
   * 
   * @param viewer	the underlying viewer that triggered the call
   * @param coord	the coordinate to check
   * @return		true if the mapobject is "hit" by this coordinate
   */
  @Override
  public boolean isHit(JMapViewer viewer, Coordinate coord) {
    Point 	pos;
    Point	center;
    
    pos    = viewer.getMapPosition(coord);
    center = viewer.getMapPosition(getCoordinate());
    
    if ((pos != null) && (center != null))
      return (Math.pow(pos.x - center.x, 2) + Math.pow(pos.y - center.y, 2) < radiusPixels*radiusPixels);
    else
      return false;
  }
  
  @Override
  public String toString() {
    return "MarkerCircle: name=" + getName() + ", coord=" + getCoordinate() + ", meta=" + getMetaData();
  }
}
