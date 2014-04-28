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
 * GPSBoundary.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.gps;

import java.util.Vector;


/**
 * ???
 * 
 * @author  dale (dale at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GPSBoundary {
  protected AbstractGPS m_NE;
  protected AbstractGPS m_SW;
  
  public GPSBoundary(AbstractGPS NE, AbstractGPS SW) {
    m_NE=NE;
    m_SW=SW;
  }
  
  public AbstractGPS getNE() {
    return(m_NE);
  }
  
  public AbstractGPS getSW() {
    return(m_SW);
  }
  
  public AbstractGPS getCentre() {
    double minlat = m_NE.getLatitude().toDecimal();
    double maxlng = m_NE.getLongitude().toDecimal();

    double maxlat = m_SW.getLatitude().toDecimal();
    double minlng = m_SW.getLongitude().toDecimal();

    double ctrlat=(minlat+maxlat)/2.0;
    double ctrlng=(minlng+maxlng)/2.0;

    AbstractGPS c=new GPSDecimalDegrees(new Coordinate(ctrlat), new Coordinate(ctrlng));
    return(c);
  }
  
  public static GPSBoundary createGPSBoundary(Vector<AbstractGPS> v) {
    // at this stage assume within a hemisphere..
    double N=Double.NaN;
    double S=Double.NaN;
    double W=Double.NaN;
    double E=Double.NaN;

    for (AbstractGPS gps:v) {
      double lat=gps.getLatitude().toDecimal();
      double lon=gps.getLongitude().toDecimal();

      if (Double.isNaN(N) || lat > N) {
	N=lat;
      }
      if (Double.isNaN(S) || lat < S) {
	S=lat;
      }

      if (Double.isNaN(W) || lon < W) {
	W=lon;
      }
      if (Double.isNaN(E) || lon > E) {
	E=lon;
      }      
    }
    return(new GPSBoundary(new GPSDecimalDegrees(N, E), new GPSDecimalDegrees(S, W)));
  }
}
