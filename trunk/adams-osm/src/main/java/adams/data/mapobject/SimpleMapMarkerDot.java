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

import java.awt.Color;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import adams.core.License;
import adams.core.annotation.MixedCopyright;

/**
 * A simple implementation of the {@link MapMarker} interface. Each map marker
 * is painted as a circle with a black border line and filled with a specified
 * color.
 *
 * @author Jan Peter Stotz (original code: {@link MapMarkerDot})
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "2008 OpenStreetMap",
    author = "Jan Peter Stotz",
    license = License.GPL2,
    url = "http://svn.openstreetmap.org/applications/viewer/jmapviewer/releases/1.02/JMapViewer-1.02-Source.zip",
    note = "Original class: org.openstreetmap.gui.jmapviewer.MapMarkerDot"
)
public class SimpleMapMarkerDot
  extends SimpleMapMarkerCircle
  implements HitIndicator {

  public static final int DOT_RADIUS = 5;

  public SimpleMapMarkerDot(Coordinate coord) {
    this(null, null, coord);
  }
  
  public SimpleMapMarkerDot(String name, Coordinate coord) {
    this(null, name, coord);
  }
  
  public SimpleMapMarkerDot(Layer layer, Coordinate coord) {
    this(layer, null, coord);
  }
  
  public SimpleMapMarkerDot(Layer layer, String name, Coordinate coord) {
    this(layer, name, coord, getDefaultStyle());
  }
  
  public SimpleMapMarkerDot(Color color, double lat, double lon) {
    this(null, null, lat, lon);
    setColor(color);
  }
  
  public SimpleMapMarkerDot(double lat, double lon) {
    this(null, null, lat, lon);
  }
  
  public SimpleMapMarkerDot(Layer layer, double lat, double lon) {
    this(layer, null, lat, lon);
  }
  
  public SimpleMapMarkerDot(Layer layer, String name, double lat, double lon) {
    this(layer, name, new Coordinate(lat, lon), getDefaultStyle());
  }
  
  public SimpleMapMarkerDot(Layer layer, String name, Coordinate coord, Style style) {
    super(layer, name, coord, DOT_RADIUS, STYLE.FIXED, style);
  }

  public static Style getDefaultStyle(){
    return new Style(Color.BLACK, Color.YELLOW, null, getDefaultFont());
  }
  
  @Override
  public String toString() {
    return "MarkerDot: name=" + getName() + ", coord=" + getCoordinate() + ", meta=" + getMetaData();
  }
}
