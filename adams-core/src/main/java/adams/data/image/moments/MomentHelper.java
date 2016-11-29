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
 * Moments.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.moments;
import adams.env.Environment;
import java.util.ArrayList;
import java.util.List;


/**
 * Implements Image Moments for use in feature generation
 * Only implemented for binary images
 *
 * For all boolean matrices the expected format is boolean[height][width]
 *
 * @author sjb90
 * @version $Revision$
 */
public class MomentHelper {

  public static double moment(boolean[][] img, int p, int q) {
    double result = 0.0;
    // For every pixel that is not a background pixel
    for (int v = 0; v < img.length; v++) {
      for (int u = 0; u < img[v].length; u++) {
        if(img[v][u]) {
          // sum u^p * v^q
          result += Math.pow(u,p) * Math.pow(v,q);
        }
      }
    }
    return result;
  }

  public static double centralMoment(boolean[][] img, int p, int q) {
    double result   = 0.0;
    double area     = moment(img, 0, 0); // gives the area of the region
    double xCenter  = moment(img, 1, 0) / area;
    double yCenter  = moment(img, 0 , 1) / area;
    for (int v = 0; v < img.length; v++) {
      for (int u = 0; u < img[v].length; u++) {
        if(img[v][u])
          result += Math.pow(u - xCenter, p) * Math.pow(v - yCenter, q);
      }
    }
    return result;
  }

  public static double normalCentralMoment(boolean[][] img, int p, int q) {
    double result;
    double m00 = moment(img, 0, 0);
    double norm = Math.pow(m00, (double)(p + q + 2) /2);
    result = centralMoment(img, p, q) / norm;
    return result;
  }

  public static double majorAxisDirection(boolean[][] img) {
    return 0.5 * Math.atan2(2 * centralMoment(img,1,1), centralMoment(img, 2 , 0) - centralMoment(img, 0, 2));
  }

  public static List<Double> orientationVector(boolean[][] img) {
    double x;
    double y;
    List<Double> result = new ArrayList<>();
    double a = 2 * centralMoment(img, 1, 1);
    double b = centralMoment(img, 2 , 0) - centralMoment(img, 0, 2);
    if( a == 0 && b == 0) {
      x = 0.0;
      y = 0.0;
    }
    else {
      x = Math.sqrt(0.5 * (1 + (b / Math.sqrt(Math.pow(a,2) + Math.pow(b,2)))));
      if( a >= 0) y = Math.sqrt(0.5 * (1 - (b / Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)))));
      else y = -Math.sqrt(0.5 * (1 - (b / Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)))));
    }
    result.add(x);
    result.add(y);
    return result;
  }

  /**
   * Measures the eccentricity of an object. A value of 1 is for a round object and the value can range from
   * 1 to infinity.
   * @param img
   * @return the eccentricity of the object
   */
  public static double eccentricity(boolean[][] img) {
    double u_20 = centralMoment(img,2,0);
    double u_02 = centralMoment(img,0,2);
    double u_11 = centralMoment(img,1,1);
    double a1 = u_20 + u_02 + Math.sqrt(Math.pow(u_20-u_02,2) + 4 * Math.pow(u_11,2));
    double a2 = u_20 + u_02 - Math.sqrt(Math.pow(u_20-u_02,2) + 4 * Math.pow(u_11,2));

    return a1/a2;
  }

  public static List<Double> husMoments(boolean[][] img) {
    List<Double> result = new ArrayList<>();
    double u_20 = normalCentralMoment(img, 2, 0);
    double u_02 = normalCentralMoment(img, 0, 2);
    double u_11 = normalCentralMoment(img, 1, 1);
    double u_30 = normalCentralMoment(img, 3, 0);
    double u_03 = normalCentralMoment(img, 0, 3);
    double u_12 = normalCentralMoment(img, 1, 2);
    double u_21 = normalCentralMoment(img, 2, 1);
    // H_1
    result.add(u_20 + u_02);
    // H_2
    result.add(Math.pow(u_20 - u_02,2) + 4 * Math.pow(u_11,2));
    // H_3
    result.add(Math.pow(u_30 - 3 * u_12,2) + Math.pow(3*u_21 - u_03, 2));
    // H_4
    result.add(Math.pow(u_30 + u_12,2) + Math.pow(u_21 + u_03,2));
    // H_5
    result.add((u_30 - 3*u_12) * (u_30 + u_12) * (Math.pow(u_30 + u_12,2) - 3*Math.pow(u_21 + u_03,2))
      + (3*u_21 - u_03) * (u_21 + u_03) * (3*Math.pow(u_30 + u_12,2) - Math.pow(u_21 + u_03,2)));
    // H_6
    result.add((u_20 - u_02) * (Math.pow(u_30 + u_12,2) - Math.pow(u_21 +u_03,2)) + 4 * u_11 * (u_30 + u_12) * (u_21 + u_03));
    // H_7
    result.add((3 * u_21 - u_03) * (u_30 + u_12) * (Math.pow(u_30 + u_12,2) - 3*Math.pow(u_21 + u_03, 2)) +
      (3*u_12 - u_30) * (u_21 + u_03) * (3*Math.pow(u_30 + u_12, 2) - Math.pow(u_21 + u_03,2)));

    return result;
  }


  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    boolean[][] img = new boolean[][] {
      {false,false,false,false,true ,false,false},
      {false,false,false,true ,false,false,false},
      {false,false,true ,false,false,false,false},
      {false,true ,true ,true ,true ,true ,false},
      {false,false,false,false,false,false,false},
      {false,false,false,false,false,false,false},
      {false,false,false,false,false,false,false}
    };

    boolean[][] img2 = new boolean[][] {
      {false,false,false,true ,false,false,false},
      {false,false,false,true ,false,false,false},
      {false,false,false,true ,false,false,false},
      {false,false,false,true ,false,false,false},
      {false,false,false,true ,false,false,false},
      {false,false,false,true ,false,false,false},
      {false,false,false,true ,false,false,false}
    };

    boolean[][] img3 = new boolean[][]{
      {false,true,false},
      {true,true,true},
      {false,true,false}
    };

    System.out.println("m00: " + moment(img,0,0));
    System.out.println("m00: " + moment(img2,0,0));
    System.out.println("centralMoment: " + centralMoment(img,0,0));
    System.out.println("centralMoment: " + centralMoment(img2,0,0));
    System.out.println("Normal Central Moment: " + normalCentralMoment(img,0,0));
    System.out.println("Normal Central Moment: " + normalCentralMoment(img2,0,0));

    System.out.println("Direction of major axis: " + majorAxisDirection(img));
    System.out.println("Direction of major axis: " + majorAxisDirection(img2));

    System.out.println("Orientation Vector: " + orientationVector(img));
    System.out.println("Orientation Vector: " + orientationVector(img2));

    System.out.println("Eccentricity: " + eccentricity(img2));
  }



}
