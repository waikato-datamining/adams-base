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
 * Coordinate.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.gps;

import java.io.Serializable;

/**
 * GPS coordinate container.
 * 
 * @author  dale (dale at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Coordinate 
  implements Serializable, Comparable<Coordinate>, Cloneable {
  
  /** For serialisation */
  private static final long serialVersionUID = -5163361656893487847L;

  /** the number of decimals to be used by the seconds. */
  public final static int NUM_DECIMALS = 6;
  
  /** whether the coordinate is negative. */
  protected boolean m_Negative;
  
  /** degrees. */
  protected int m_Degree;
  
  /** minutes. */
  protected int m_Minute;
  
  /** seconds. */
  protected double m_Second;
  
  /**
   * Default constructor.
   */
  public Coordinate() {
    this(false, 0, 0, 0.0);
  }
  
  /**
   * Initialise coordinate with degrees, minutes, seconds
   * 
   * @param negative	whether the coordinate is negative
   * @param deg		degrees
   * @param min		minutes
   * @param sec		seconds
   */
  public Coordinate(boolean negative, int deg, int min, double sec) {
    m_Negative = negative;
    m_Degree   = deg;
    m_Minute   = min;
    m_Second   = sec;
  }

  /**
   * Generate Coordinate from decimal notation.
   * 
   * @param deg 	decimal notation
   */
  public Coordinate(double deg) {
    m_Negative = (deg < 0);
    deg        = Math.abs(deg);
    
    // gets the modulus the coordinate divided by one (MOD1).
    // in other words gets all the numbers after the decimal point.
    // e.g. mod = 87.728056 % 1 == 0.728056
    //
    // next get the integer part of the coord. On other words the whole number part.
    // e.g. intPart = 87

    double mod = deg % 1;
    int intPart = (int) deg;
    int degree = intPart;

    //set degrees to the value of intPart
    //e.g. degrees = "87"

    // next times the MOD1 of degrees by 60 so we can find the integer part for minutes.
    // get the MOD1 of the new coord to find the numbers after the decimal point.
    // e.g. coord = 0.728056 * 60 == 43.68336
    //      mod = 43.68336 % 1 == 0.68336
    //
    // next get the value of the integer part of the coord.
    // e.g. intPart = 43

    deg = mod * 60;
    mod = deg % 1;
    intPart = (int)deg;
    int minute = intPart;

    //do the same again for minutes
    //e.g. coord = 0.68336 * 60 == 41.0016
    //e.g. intPart = 41
    deg = mod * 60;
    //intPart = (int)coord;
    //int second = intPart;

    m_Degree   = Math.abs(degree);
    m_Minute   = minute;
    m_Second   = Math.round(deg * Math.pow(10, NUM_DECIMALS)) / Math.pow(10, NUM_DECIMALS);
  }
  
  /**
   * Returns whether the coordinate is negative.
   * 
   * @return		true if negative
   */
  public boolean isNegative() {
    return m_Negative;
  }
  
  /**
   * Returns the degrees.
   * 
   * @return		the degrees
   */
  public int getDegree() {
    return m_Degree;
  }
  
  /**
   * Returns the minutes.
   * 
   * @return		the minutes
   */
  public int getMinute() {
    return m_Minute;
  }
  
  /**
   * Returns the seconds.
   * 
   * @return		the seconds
   */
  public double getSecond() {
    return m_Second;
  }
  
  /**
   * Return the coordinate in decimal notation.
   * 	
   * @return	coordinate as decimal
   */
  public double toDecimal() {
    double seconds = ((double) m_Minute * 60.0) + (double) m_Second;
    double frac = (double) seconds / 3600.0;
    if (m_Negative)
      return -((double) m_Degree + frac);
    else
      return ((double) m_Degree + frac);
  }

  /**
   * Returns a copy of itself.
   * 
   * @return		the copy
   */
  @Override
  protected Coordinate clone() {
    return new Coordinate(m_Negative, m_Degree, m_Minute, m_Second);
  }
  
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
  public int compareTo(Coordinate o) {
    int		result;
    
    if (o == null)
      return 1;

    result = new Double(toDecimal()).compareTo(o.toDecimal());
    
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
    if (o instanceof Coordinate)
      return (compareTo((Coordinate) o) == 0);
    else
      return false;
  }

  /**
   * String representation.
   */
  @Override
  public String toString() {
    return (m_Negative ? "-" : "") + m_Degree + "ᵒ " + m_Minute + "' " + m_Second + "\" (" + toDecimal() + ")";
  }
}