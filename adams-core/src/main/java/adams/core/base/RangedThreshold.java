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

/*
 * RangedThreshold.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.core.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * For specifying thresholds for ranges.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RangedThreshold
  extends AbstractBaseString {

  private static final long serialVersionUID = 7276914469967286837L;

  public final static String DEFAULT = "0,100:10";

  /**
   * Container for storing min/max and threshold.
   */
  public static class ThresholdSpecification
    implements Serializable {

    private static final long serialVersionUID = 2658806204453289165L;

    /** the minimum. */
    public double min;

    /** the maximum. */
    public double max;

    /** the associated threshold. */
    public double threshold;

    /**
     * Initializes the container.
     *
     * @param min	the minimum
     * @param max	the maximum
     * @param threshold	the threshold
     */
    public ThresholdSpecification(double min, double max, double threshold) {
      if (min >= max)
        throw new IllegalArgumentException("min must be smaller than max: min=" + min + ", max=" + max);

      this.min = min;
      this.max = max;
      this.threshold = threshold;
    }

    /**
     * Outputs the container as simple string.
     *
     * @return		the generated string
     */
    @Override
    public String toString() {
      return min + "," + max + ":" + threshold;
    }
  }

  /**
   * Initializes the string with length 0.
   */
  public RangedThreshold() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public RangedThreshold(String s) {
    super(s);
  }

  /**
   * Initializes the object with the threshold specification.
   *
   * @param t		the threshold spec
   */
  public RangedThreshold(ThresholdSpecification t) {
    super(t.toString());
  }

  /**
   * Initializes the object with the threshold specifications.
   *
   * @param t		the threshold specs
   */
  public RangedThreshold(ThresholdSpecification[] t) {
    super(Utils.flatten(t, ";"));
  }

  /**
   * Parses the string into threshold specifications.
   *
   * @param s		the string to parse
   * @return		the specifications
   * @throws Exception	if parsing fails
   */
  protected ThresholdSpecification[] parse(String s) throws Exception {
    List<ThresholdSpecification> 	result;
    String[]				items;
    String[]				parts;
    String[]				range;
    ThresholdSpecification		spec;

    result = new ArrayList<>();
    items  = s.split(";");
    for (String item: items) {
      if (!item.contains(","))
        throw new IllegalStateException("Invalid format (expected: from,to:threshold): " + item);
      parts = item.split(":");
      if (parts.length != 2)
	throw new IllegalStateException("Invalid format (expected: from,to:threshold): " + item);
      range = parts[0].split(",");
      if (range.length != 2)
	throw new IllegalStateException("Invalid format (expected: from,to:threshold): " + item);
      spec = new ThresholdSpecification(Double.parseDouble(range[0]), Double.parseDouble(range[1]), Double.parseDouble(parts[1]));
      result.add(spec);
    }

    return result.toArray(new ThresholdSpecification[0]);
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if valid specification
   */
  @Override
  public boolean isValid(String value) {
    if (value.isEmpty())
      return true;
    try {
      parse(value);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns the string as threshold specifications.
   *
   * @return		the specs
   */
  public ThresholdSpecification[] thresholdsValue() {
    if (getValue().isEmpty())
      return new ThresholdSpecification[0];
    try {
      return parse(getValue());
    }
    catch (Exception e) {
      return new ThresholdSpecification[0];
    }
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return the tool tip
   */
  @Override
  public String getTipText() {
    return "Format: from,to:threshold[;from,to:threshold[;...]]";
  }
}
