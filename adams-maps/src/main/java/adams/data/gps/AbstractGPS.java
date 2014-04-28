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
 * AbstractGPS.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.gps;

import java.io.Serializable;

/**
 * Ancestor for GPS objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  dale (dale at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGPS
  implements Serializable, Comparable<AbstractGPS>, Cloneable {

  /** for serialization. */
  private static final long serialVersionUID = -9037112025830141712L;
  
  /** the number of decimals to user. */
  public final static int NUM_DECIMALS = 6;
  
  /** longitude */
  protected Coordinate m_Longitude;
  
  /** latitude */
  protected Coordinate m_Latitude;

  /**
   * Default constructor.
   */
  public AbstractGPS() {
    this(new Coordinate(), new Coordinate());
  }

  /**
   * Initialize GPS with latitude and longitude in string representation.
   * 
   * @param s		the string representation to parse
   * @see		#fromString(String)
   */
  public AbstractGPS(String s) {
    this();
    fromString(s);
  }

  /**
   * Initialize GPS with latitude and longitude in string representation.
   * 
   * @param s		the string representation to parse
   * @param swapped	whether format is 'long lat' instead of 'lat long'
   * @see		#fromString(String)
   */
  public AbstractGPS(String s, boolean swapped) {
    this();
    fromString(s, swapped);
  }

  /**
   * Initialize GPS with latitude and longitude from the specified object.
   * 
   * @param gps		the GPS object to use the lat/lon from
   */
  public AbstractGPS(AbstractGPS gps) {
    this(gps.getLatitude().clone(), gps.getLongitude().clone());
  }

  /**
   * Initialize GPS with latitude and longitude (in decimal notation).
   * 
   * @param lat		latitude
   * @param lon		longitude
   */
  public AbstractGPS(double lat, double lon ) {
    this(new Coordinate(lat), new Coordinate(lon));
  }

  /**
   * Initialize GPS with latitude and longitude.
   * 
   * @param lat		latitude
   * @param lon		longitude
   */
  public AbstractGPS(Coordinate lat, Coordinate lon ) {
    m_Longitude = lon;
    m_Latitude  = lat;
  }
  
  /**
   * Returns the longitude.
   * 
   * @return		the coordinate
   */
  public Coordinate getLongitude() {
    return m_Longitude;
  }
  
  /**
   * Returns the latitude.
   * 
   * @return		the coordinate
   */
  public Coordinate getLatitude() {
    return m_Latitude;
  }
  
  /**
   * Creates a copy of itself.
   * 
   * @return		the copy
   */
  @Override
  protected abstract AbstractGPS clone();
  
  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(AbstractGPS o) {
    int		result;
    
    if (o == null)
      return 1;

    result = m_Latitude.compareTo(o.m_Latitude);
    if (result == 0)
      result = m_Longitude.compareTo(o.m_Longitude);
    
    return result;
  }

  /**
   * Returns whether the two objects are the same.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof AbstractGPS)
      return (compareTo((AbstractGPS) o) == 0);
    else
      return false;
  }
  
  /**
   * Removes the parentheses and comma in the string: 
   * "(north, west)" becomes "north west"
   */
  protected String preprocess(String s) {
    if (s.startsWith("(") && s.endsWith(")"))
      return s.replace("(", "").replace(",", "").replace(")", "");
    else
      return s;
  }
  
  /**
   * Parses the string.
   * 
   * @param s		the string to parse
   * @param swapped	"long lat" instead of "lat long"
   * @return		the coordinates (lat/long), null if failed to parse
   */
  protected abstract Coordinate[] parse(String s, boolean swapped);
  
  /**
   * Parses the string.
   * 
   * @param s		the string to parse
   * @return		the coordinates (lat/long), null if failed to parse
   */
  protected Coordinate[] parse(String s) {
    return parse(s, false);
  }
  
  /**
   * Checks whether the string can be parsed.
   * 
   * @param s		the string to check
   * @return		true if successfully parsed
   */
  public boolean isValid(String s) {
    return (parse(s) != null);
  }
  
  /**
   * Checks whether the string can be parsed.
   * 
   * @param s		the string to check
   * @param swapped	"long lat" instead of "lat long"
   * @return		true if successfully parsed
   */
  public boolean isValid(String s, boolean swapped) {
    return (parse(s, swapped) != null);
  }
  
  /**
   * Parses the string to get the long/lat values from.
   * If failed to parse, default coordinates are used.
   * 
   * @param s		the string to parse
   */
  public void fromString(String s) {
    Coordinate[]	coords;
    
    if (isValid(s)) {
      coords      = parse(s);
      m_Latitude  = coords[0];
      m_Longitude = coords[1];
    }
    else {
      m_Latitude  = new Coordinate();
      m_Longitude = new Coordinate();
    }
  }
  
  /**
   * Parses the string to get the long/lat values from.
   * If failed to parse, default coordinates are used.
   * 
   * @param s		the string to parse
   * @param swapped	"long lat" instead of "lat long"
   */
  public void fromString(String s, boolean swapped) {
    Coordinate[]	coords;
    
    if (isValid(s, swapped)) {
      coords      = parse(s, swapped);
      m_Latitude  = coords[0];
      m_Longitude = coords[1];
    }
    else {
      m_Latitude  = new Coordinate();
      m_Longitude = new Coordinate();
    }
  }
  
  /**
   * Turns the GPS object back into its string representation.
   * 
   * @return		the string representation
   */
  @Override
  public abstract String toString();
}
