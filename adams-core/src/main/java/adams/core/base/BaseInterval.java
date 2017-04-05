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
 * BaseInterval.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.base;

import adams.core.Utils;

/**
 * For specifying mathematical intervals like (1;1.2] or [-3.1;7.8].
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseInterval
  extends AbstractBaseString {

  private static final long serialVersionUID = -4760534825195874939L;

  /** the lower bound of the interval. */
  protected double m_Lower;

  /** whether the lower is inclusive. */
  protected boolean m_LowerInclusive;

  /** the upper bound of the interval. */
  protected double m_Upper;

  /** whether the upper is inclusive. */
  protected boolean m_UpperInclusive;

  /**
   * Initializes the string with empty string.
   */
  public BaseInterval() {
    super("");
  }

  /**
   * Initializes the object with the specified interval.
   *
   * @param s		the interval to parse
   */
  public BaseInterval(String s) {
    super(s);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Lower          = Double.NEGATIVE_INFINITY;
    m_LowerInclusive = false;
    m_Upper          = Double.POSITIVE_INFINITY;
    m_UpperInclusive = false;
  }

  /**
   * Parses the string.
   *
   * @param value	the string value to parse
   * @return		null if failed to parse, otherwise lower incl/lower/upper/upper incl
   */
  protected Object[] parse(String value) {
    boolean 	valid;
    String[]	parts;
    double 	lower;
    double 	upper;
    boolean	lowerIncl;
    boolean	upperIncl;

    if (value.isEmpty())
      return null;

    parts = value.split(";");
    if (parts.length != 2)
      return null;

    lower = Double.NEGATIVE_INFINITY;
    upper = Double.POSITIVE_INFINITY;

    // lower
    if (parts[0].startsWith("[") || parts[0].startsWith("(")) {
      valid = Utils.isDouble(parts[0].substring(1));
      if (valid)
	lower = Utils.toDouble(parts[0].substring(1));
    }
    else {
      valid = Utils.isDouble(parts[0]);
      if (valid)
	lower = Utils.toDouble(parts[0]);
    }

    if (valid) {
      // upper
      if (parts[1].endsWith("]") || parts[1].endsWith(")")) {
	valid = Utils.isDouble(parts[1].substring(0, parts[1].length() - 1));
	if (valid)
	  upper = Utils.toDouble(parts[1].substring(0, parts[1].length() - 1));
      }
      else {
	valid = Utils.isDouble(parts[1]);
	if (valid)
	  upper = Utils.toDouble(parts[1]);
      }
    }

    if (valid)
      valid = lower < upper;

    if (!valid)
      return null;

    lowerIncl = !parts[0].startsWith("(");
    upperIncl = !parts[1].endsWith(")");

    if (Double.isInfinite(m_Lower))
      m_LowerInclusive = false;
    if (Double.isInfinite(m_Upper))
      m_UpperInclusive = false;

    return new Object[]{lowerIncl, lower, upper, upperIncl};
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if empty or interval
   */
  @Override
  public boolean isValid(String value) {
    return (value != null) && (value.isEmpty() || (parse(value) != null));
  }

  /**
   * Converts the string according to the specified conversion.
   *
   * @param value	the string to convert
   * @return		the converted string
   */
  @Override
  protected String convert(String value) {
    String	result;
    Object[]	parsed;

    if (value.isEmpty()) {
      m_Lower          = Double.NEGATIVE_INFINITY;
      m_LowerInclusive = false;
      m_Upper          = Double.POSITIVE_INFINITY;
      m_UpperInclusive = false;

      result = "";
    }
    else {
      parsed = parse(value);
      m_LowerInclusive = (Boolean) parsed[0];
      m_Lower = (Double) parsed[1];
      m_Upper = (Double) parsed[2];
      m_UpperInclusive = (Boolean) parsed[3];

      result =
	(m_LowerInclusive ? "[" : "(")
	  + m_Lower
	  + ";"
	  + m_Upper
	  + (m_UpperInclusive ? "]" : ")");
    }

    return result;
  }

  /**
   * Returns whether the lower bound is inclusive.
   * 
   * @return		true if inclusive
   */
  public boolean isLowerInclusive() {
    return m_LowerInclusive;
  }

  /**
   * Returns the lower bound.
   * 
   * @return		the lower bound
   */
  public double getLower() {
    return m_Lower;
  }

  /**
   * Returns whether the upper bound is inclusive.
   * 
   * @return		true if inclusive
   */
  public boolean isUpperInclusive() {
    return m_UpperInclusive;
  }

  /**
   * Returns the upper bound.
   * 
   * @return		the upper bound
   */
  public double getUpper() {
    return m_Upper;
  }

  /**
   * Checks whether the value falls inside the bounds, taking the inclusivity
   * of the bounds into account.
   *
   * @param value	the value to check
   * @return		true if within bounds
   */
  public boolean isInside(double value) {
    boolean	result;

    if (m_LowerInclusive)
      result = (value >= m_Lower);
    else
      result = (value > m_Lower);

    if (result) {
      if (m_UpperInclusive)
	result = (value <= m_Upper);
      else
	result = (value < m_Upper);
    }

    return result;
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Interval definition: (min;max) exclusive borders, [min;max] inclusive borders";
  }
}
