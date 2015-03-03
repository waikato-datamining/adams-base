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
 * LOWESS.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import adams.core.logging.LoggingHelper;
import adams.gui.core.Point2DComparator;

/**
 * A helper class for LOWESS.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LOWESS {

  /** the comparator to use. */
  protected static Point2DComparator m_Comparator = new Point2DComparator();
  
  /** the logger. */
  protected static Logger m_Logger = LoggingHelper.getLogger(LOWESS.class);
  
  /** the minimum window size. */
  public final static int MIN_WINDOW_SIZE = 1;
  
  /**
   * Performs LOWESS on the given data.
   * 
   * @param points	the points to process
   */
  public static List<Point2D> calculate(List<Point2D> points, int windowSize) {
    List<Point2D>	result;
    ArrayList<Point2D> 	closest;
    int 		i;
    int 		j;
    int 		index;
    double 		ref;
    int 		left;
    int 		right;
    double 		max;
    double[] 		relDist;
    double[] 		weighting;
    double 		sumWts;
    double 		sumWtX;
    double 		sumWtX2;
    double 		sumWtY;
    double 		sumWtXY;
    double 		denom;
    double 		slope;
    double 		intercept;
    double 		val;
    
    result = new ArrayList<Point2D>();
    points = new ArrayList<Point2D>(points);

    //sort the points on ascending x value
    Collections.sort(points, m_Comparator);
    
    //If the number of data points is less than the window size specified
    if (windowSize > points.size()) {
      windowSize = points.size();
      m_Logger.warning("Window size changed to number of points");
    }
    
    for (i = 0; i< points.size(); i++) {
      closest = new ArrayList<Point2D>();
      closest.add(points.get(i));
      index = 1;
      ref   = points.get(i).getX();
      left  = i -1;
      right = i +1;
      while (index <windowSize) {
	//if no points to the left
	if (left < 0) {
	  closest.add(points.get(right));
	  right ++;
	}
	//if no points to the right
	else if (right > points.size() -1) {
	  closest.add(points.get(left));
	  left --;
	}
	else {
	  //if point to the right is closer
	  if (Math.abs(points.get(right).getX() - ref) < Math.abs(points.get(left).getX() - ref)) {
	    closest.add(points.get(right));
	    right ++;
	  }
	  //if point to the left is closer
	  else {
	    closest.add(points.get(left));
	    left --;
	  }
	}
	index++;
      }

      //distance from the reference point of the furthest away point
      max = Math.abs(closest.get(windowSize -1).getX() - ref);

      relDist = new double[closest.size()];
      //calculate the relative distances
      for (j = 0; j < closest.size(); j++)
	relDist[j] = Math.abs((closest.get(j).getX() - ref)) / max;

      //apply the tri-cube weight function
      weighting = new double[relDist.length];
      for (j = 0; j < relDist.length; j++)
	weighting[j] = Math.pow((1 - (Math.pow(relDist[j], 3.0))), 3.0);

      //now fit a weighted least squares
      //based on code that excel uses for lowess
      sumWts  = 0;
      sumWtX  = 0;
      sumWtX2 = 0;
      sumWtY  = 0;
      sumWtXY = 0;
      for (j = 0; j< weighting.length; j++) {
	sumWts  += weighting[j];
	sumWtX  += weighting[j] * closest.get(j).getX();
	sumWtX2 += weighting[j] * Math.pow(closest.get(j).getX(), 2.0);
	sumWtY  += weighting[j] * closest.get(j).getY();
	sumWtXY += weighting[j] * closest.get(j).getY() * closest.get(j).getX();
      }
      denom     = sumWts * sumWtX2 - Math.pow(sumWtX, 2.0);
      slope     = (sumWts * sumWtXY - sumWtX * sumWtY)/denom;
      intercept = (sumWtX2 * sumWtY - sumWtX * sumWtXY) /denom;
      val       = slope* closest.get(0).getX() + intercept;

      //add point calculated using weighted least squares
      result.add(new Point2D.Double(closest.get(0).getX(), val));
    }
    
    return result;
  }
}
