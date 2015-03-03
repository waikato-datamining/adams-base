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
 * GPSDecimalDegrees.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.gps;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import adams.core.Utils;

/**
 * GPS coordinates in decimal notation.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GPSDecimalDegrees
  extends AbstractGPS {

  /** for serialization. */
  private static final long serialVersionUID = 1902709328711736523L;

  /** the regular expression. */
  public static final String FORMAT = "([NnSs\\+-]?)\\s*(\\d+)\\.(\\d+)[\\s,]*([WwEe\\+-]?)\\s*(\\d+)\\.(\\d+)";

  /** the regular expression (swapped). */
  public static final String FORMAT_SWAPPED = "([WwEe\\+-]?)\\s*(\\d+)\\.(\\d+)[\\s,]*([NnSs\\+-]?)\\s*(\\d+)\\.(\\d+)";

  /**
   * Default constructor.
   */
  public GPSDecimalDegrees() {
    super();
  }

  /**
   * Initialize GPS with latitude and longitude in string representation.
   * 
   * @param s		the string representation to parse
   */
  public GPSDecimalDegrees(String s) {
    super(s);
  }

  /**
   * Initialize GPS with latitude and longitude in string representation.
   * 
   * @param s		the string representation to parse
   * @param swapped	whether format is 'long lat' instead of 'lat long'
   */
  public GPSDecimalDegrees(String s, boolean swapped) {
    super(s, swapped);
  }

  /**
   * Initialize GPS with latitude and longitude from the specified object.
   * 
   * @param gps		the GPS object to use the lat/lon from
   */
  public GPSDecimalDegrees(AbstractGPS gps) {
    super(gps);
  }

  /**
   * Initialize GPS with latitude and longitude (in decimal notation).
   * 
   * @param lat		latitude
   * @param lon		longitude
   */
  public GPSDecimalDegrees(double lat, double lon ) {
    super(lat, lon);
  }

  /**
   * Initialize GPS with latitude and longitude.
   * 
   * @param lat		latitude
   * @param lon		longitude
   */
  public GPSDecimalDegrees(Coordinate lat, Coordinate lon) {
    super(lat, lon);
  }

  /**
   * Creates a copy of itself.
   * 
   * @return		the copy
   */
  @Override
  protected GPSDecimalDegrees clone() {
    return new GPSDecimalDegrees(this);
  }
  
  /**
   * Parses the string.
   * 
   * @param s		the string to parse
   * @param swapped	"long lat" instead of "lat long"
   * @return		the coordinates (lat/long), null if failed to parse
   */
  @Override
  protected Coordinate[] parse(String s, boolean swapped) {
    Coordinate[] result = null;
    s = preprocess(s);
    Pattern pattern;
    if (swapped)
      pattern = Pattern.compile(FORMAT_SWAPPED);
    else
      pattern = Pattern.compile(FORMAT);
    Matcher matcher = pattern.matcher(s);
    double latsign = 1;
    double longsign = 1;
    if (matcher.matches()) {
      String slatsign=matcher.group(1);
      String slongsign=matcher.group(4);
      if (swapped) {
	if (slongsign.equalsIgnoreCase("S") || slongsign.equals("-"))
	  longsign = -1;
	if (slatsign.equalsIgnoreCase("E") || slatsign.equals("-"))
	  latsign = -1;
      }
      else {
	if (slatsign.equalsIgnoreCase("S") || slatsign.equals("-"))
	  latsign = -1;
	if (slongsign.equalsIgnoreCase("E") || slongsign.equals("-"))
	  longsign = -1;
      }
      double latdegrees  = Integer.parseInt(matcher.group(2));
      double longdegrees = Integer.parseInt(matcher.group(5));
      
      latdegrees  += Double.parseDouble("." + matcher.group(3));
      longdegrees += Double.parseDouble("." + matcher.group(6));

      latdegrees *= latsign;
      longdegrees *= longsign;
      
      if (swapped)
	result = new Coordinate[]{new Coordinate(longdegrees), new Coordinate(latdegrees)};
      else
	result = new Coordinate[]{new Coordinate(latdegrees), new Coordinate(longdegrees)};
    }
    
    return result;
  }

  /**
   * Turns the GPS object back into its string representation.
   * 
   * @return		the string representation
   */
  @Override
  public String toString() {
    StringBuilder	result;
    double		lat;
    double		lon;
    String		str;
    
    result = new StringBuilder();
    lat    = m_Latitude.toDecimal();
    lon    = m_Longitude.toDecimal();
    
    if (m_Latitude.isNegative())
      result.append("S");
    else
      result.append("N");
    str = Utils.doubleToString(Math.abs(lat), NUM_DECIMALS);
    result.append(str);
    if (str.indexOf('.') == -1)
      result.append(".0");

    result.append(" ");

    if (m_Longitude.isNegative())
      result.append("E");
    else
      result.append("W");
    str = Utils.doubleToString(Math.abs(lon), NUM_DECIMALS);
    result.append(str);
    if (str.indexOf('.') == -1)
      result.append(".0");
    
    return result.toString();
  }
}
