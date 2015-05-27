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
 * SPCUtils.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 * Copyright (C) 2009 godfryd (Python code)
 */

package adams.data.statistics;

import adams.core.EnumWithCustomDisplay;
import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.core.option.AbstractOption;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Helper class for statistical process control (SPC).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
  author = "godfryd - https://launchpad.net/~godfryd",
  license = License.MIT,
  url = "https://launchpad.net/python-spc",
  note = "Original code is in Python"
)
public class SPCUtils {

  //                                   n   0 1  2      3      4      5      6      7      8      9      10
  public static double[] A2 = new double[]{0,0, 1.880, 1.023, 0.729, 0.577, 0.483, 0.419, 0.373, 0.337, 0.308};
  public static double[] D3 = new double[]{0,0, 0,     0,     0,     0,     0,     0.076, 0.136, 0.184, 0.223};
  public static double[] D4 = new double[]{0,0, 3.267, 2.575, 2.282, 2.115, 2.004, 1.924, 1.864, 1.816, 1.777};
  //                                   n   0 1      2      3      4      5      6      7      8      9     10     11     12     13     14     15       20     25
  public static double[] c4 = new double[]{0,0,0.7979,0.8862,0.9213,0.9400,0.9515,0.9594,0.9650,0.9693,0.9727,0.9754,0.9776,0.9794,0.9810,0.9823}; //,0.9869,0.9896};
  public static double[] B3 = new double[]{0,0,     0,     0,     0,     0, 0.030, 0.118, 0.185, 0.239, 0.284, 0.321, 0.354, 0.382, 0.406, 0.428}; //, 0.510, 0.565};
  public static double[] B4 = new double[]{0,0, 3.267, 2.568, 2.266, 2.089, 1.970, 1.882, 1.815, 1.761, 1.716, 1.679, 1.646, 1.618, 1.594, 1.572}; //, 1.490, 1.435};
  public static double[] B5 = new double[]{0,0,     0,     0,     0,     0, 0.029, 0.113, 0.179, 0.232, 0.276, 0.313, 0.346, 0.374, 0.399, 0.421}; //, 0.504, 0.559};
  public static double[] B6 = new double[]{0,0, 2.606, 2.276, 2.088, 1.964, 1.874, 1.806, 1.751, 1.707, 1.669, 1.637, 1.610, 1.585, 1.563, 1.544}; //, 1.470, 1.420};
  public static double[] A3 = new double[]{0,0, 2.659, 1.954, 1.628, 1.427, 1.287, 1.182, 1.099, 1.032, 0.975, 0.927, 0.886, 0.850, 0.817, 0.789}; //, 0.680, 0.606};

  /**
   * Enum for the chart type.
   */
  public enum Chart
    implements EnumWithCustomDisplay<Chart> {

    X_BAR_R_X("Xbar R - X"),
    X_BAR_R_R("Xbar R - R"),
    X_BAR_S_X("Xbar S - X"),
    X_BAR_S_S("Xbar S - S"),
    X_MR_X("X mR - X"),
    X_MR_MR("X mR - mR"),
    P("p"),
    NP("np"),
    C("c"),
    U("u"),
    EWMA("EWMA"),
    CUSUM("CUSUM"),
    THREE_WAY("three way"),
    TIME_SERIES("time series");

    /** the display value. */
    private String m_Display;

    /** the commandline string. */
    private String m_Raw;

    /**
     * Initializes the element.
     *
     * @param display	the display value
     */
    private Chart(String display) {
      m_Display = display;
      m_Raw     = super.toString();
    }

    /**
     * Returns the display string.
     *
     * @return		the display string
     */
    public String toDisplay() {
      return m_Display;
    }

    /**
     * Returns the raw enum string.
     *
     * @return		the raw enum string
     */
    public String toRaw() {
      return m_Raw;
    }

    /**
     * Parses the given string and returns the associated enum.
     *
     * @param s		the string to parse
     * @return		the enum or null if not found
     */
    public Chart parse(String s) {
      return (Chart) valueOf((AbstractOption) null, s);
    }

    /**
     * Returns the displays string.
     *
     * @return		the display string
     */
    @Override
    public String toString() {
      return m_Display;
    }

    /**
     * Returns the enum as string.
     *
     * @param option	the current option
     * @param object	the enum object to convert
     * @return		the generated string
     */
    public static String toString(AbstractOption option, Object object) {
      return ((Chart) object).toRaw();
    }

    /**
     * Returns an enum generated from the string.
     *
     * @param option	the current option
     * @param str		the string to convert to an enum
     * @return		the generated enum or null in case of error
     */
    public static Chart valueOf(AbstractOption option, String str) {
      Chart result;

      result = null;

      // default parsing
      try {
	result = valueOf(str);
      }
      catch (Exception e) {
	// ignored
      }

      // try display
      if (result == null) {
	for (Chart f: values()) {
	  if (f.toDisplay().equals(str)) {
	    result = f;
	    break;
	  }
	}
      }

      return result;
    }
  }

  /**
   * The type of rules to apply.
   */
  public enum Rules
    implements EnumWithCustomDisplay<Rules> {

    ONE_BEYOND_THREE_SIGMA("1 beyond 3*sigma"),
    TWO_OF_THREE_BEYOND_TWO_SIGMA("2 of 3 beyond 2*sigma"),
    FOUR_OF_FIVE_BEYOND_ONE_SIGMA("4 of 5 beyond 1*sigma"),
    SEVEN_ON_ONE_SIDE("7 on one side"),
    EIGHT_ON_ONE_SIDE("8 on one side"),
    NINE_ON_ONE_SIDE("9 on one side"),
    SIX_TRENDING("6 trending"),
    FOURTEEN_UP_DOWN("14 up down"),
    FIFTEEN_BELOW_ONE_SIGMA("15 below 1*sigma"),
    EIGHT_BEYOND_ONE_SIGMA_BOTH_SIDES("8 beyond 1*sigma on both sides");

    /** the display value. */
    private String m_Display;

    /** the commandline string. */
    private String m_Raw;

    /**
     * Initializes the element.
     *
     * @param display	the display value
     */
    private Rules(String display) {
      m_Display = display;
      m_Raw     = super.toString();
    }

    /**
     * Returns the display string.
     *
     * @return		the display string
     */
    public String toDisplay() {
      return m_Display;
    }

    /**
     * Returns the raw enum string.
     *
     * @return		the raw enum string
     */
    public String toRaw() {
      return m_Raw;
    }

    /**
     * Parses the given string and returns the associated enum.
     *
     * @param s		the string to parse
     * @return		the enum or null if not found
     */
    public Rules parse(String s) {
      return (Rules) valueOf((AbstractOption) null, s);
    }

    /**
     * Returns the displays string.
     *
     * @return		the display string
     */
    @Override
    public String toString() {
      return m_Display;
    }

    /**
     * Returns the enum as string.
     *
     * @param option	the current option
     * @param object	the enum object to convert
     * @return		the generated string
     */
    public static String toString(AbstractOption option, Object object) {
      return ((Rules) object).toRaw();
    }

    /**
     * Returns an enum generated from the string.
     *
     * @param option	the current option
     * @param str		the string to convert to an enum
     * @return		the generated enum or null in case of error
     */
    public static Rules valueOf(AbstractOption option, String str) {
      Rules result;

      result = null;

      // default parsing
      try {
	result = valueOf(str);
      }
      catch (Exception e) {
	// ignored
      }

      // try display
      if (result == null) {
	for (Rules f: values()) {
	  if (f.toDisplay().equals(str)) {
	    result = f;
	    break;
	  }
	}
      }

      return result;
    }
  }

  // http://itl.nist.gov/div898/handbook/pmc/section3/pmc322.htm
  public static double[] stats_x_mr_x(Number[] data, int size) {
    assert(size == 1);
    double center = StatUtils.mean(data);
    double sd = 0;
    for (int i = 0; i < data.length - 1; i++)
      sd += Math.abs(data[i].doubleValue() - data[i + 1].doubleValue());
    sd /= data.length - 1;
    double d2 = 1.128;
    double lcl = center - 3 * sd / d2;
    double ucl = center + 3 * sd / d2;
    return new double[]{center,lcl, ucl};
  }

  public static double[] stats_x_mr_mr(Number[] data, int size) {
    assert(size == 1);
    double sd = 0;
    for (int i = 0; i < data.length - 1; i++)
      sd += Math.abs(data[i].doubleValue() - data[i + 1].doubleValue());
    sd /= data.length - 1;
    double d2 = 1.128;
    double center = sd;
    double lcl = 0;
    double ucl = center + 3 * sd / d2;
    return new double[]{center,lcl, ucl};
  }

  // Xbar and R chart
  // Process mean chart
  // http://www.qimacros.com/control-chart-formulas/x-bar-r-chart-formula/
  public static double[] stats_x_bar_r_x(Number[][] data, int size) {
    int n = size;
    assert(n >= 2);
    assert(n <= 10);

    double Rsum = 0;
    for (Number[] xset: data) {
      assert(xset.length == n);
      Rsum += StatUtils.max(xset).doubleValue() - StatUtils.min(xset).doubleValue();
    }
    double Rbar = Rsum / data.length;

    double Xbar = StatUtils.mean(StatUtils.flatten(data));

    double center = Xbar;
    double lcl = center - A2[n] * Rbar;
    double ucl = center + A2[n] * Rbar;
    return new double[]{center,lcl, ucl};
  }

  // Xbar and R chart
  // Process variation chart
  // http://www.qimacros.com/control-chart-formulas/x-bar-r-chart-formula/
  public static double[] stats_x_bar_r_r(Number[][] data, int size) {
    int n = size;
    assert(n >= 2);
    assert(n <= 10);

    double Rsum = 0;
    for (Number[] xset: data) {
      assert(xset.length == n);
      Rsum += StatUtils.max(xset).doubleValue() - StatUtils.min(xset).doubleValue();
    }
    double Rbar = Rsum / data.length;

    double center = Rbar;
    double lcl = D3[n] * Rbar;
    double ucl = D4[n] * Rbar;
    return new double[]{center,lcl, ucl};
  }

  // Xbar S chart (average)
  // http://www.qimacros.com/control-chart-formulas/x-bar-s-chart-formula/
  public static double[] stats_x_bar_s_x(Number[][] data, int size) {
    int n = size;
    assert(n >= 2);
    assert(n <= 10);

    double[] stddevs = new double[data.length];
    for (int i = 0; i < data.length; i++)
      stddevs[i] = StatUtils.stddev(data[i], true);
    double Sbar = StatUtils.mean(stddevs);
    double Xbar = StatUtils.mean(StatUtils.flatten(data));

    double center = Xbar;
    double lcl = center - A3[n] * Sbar;
    double ucl = center + A3[n] * Sbar;
    return new double[]{center,lcl, ucl};
  }

  // Xbar S chart (stdev)
  // http://www.qimacros.com/control-chart-formulas/x-bar-s-chart-formula/
  public static double[] stats_x_bar_s_s(Number[][] data, int size) {
    int n = size;
    assert(n >= 2);
    assert(n <= 10);

    double[] stddevs = new double[data.length];
    for (int i = 0; i < data.length; i++)
      stddevs[i] = StatUtils.stddev(data[i], true);
    double Sbar = StatUtils.mean(stddevs);

    double center = Sbar;
    double lcl = B3[n] * Sbar;
    double ucl = B4[n] * Sbar;
    return new double[]{center,lcl, ucl};
  }

  // p chart
  // http://www.qimacros.com/control-chart-formulas/p-chart-formula/
  public static double[] stats_p(Number[] data, int size) {
    int n = size;
    assert(n > 1);

    double pbar = (StatUtils.sum(data)) / (double) (n * data.length);
    double sd = Math.sqrt(pbar * (1 - pbar) / (double) n);

    double center = pbar;
    double lcl = center - 3 * sd;
    if (lcl < 0)
      lcl = 0;
    double ucl = center + 3 * sd;
    if (ucl > 1)
      ucl = 1.0;
    return new double[]{center,lcl, ucl};
  }

  // np chart
  // http://www.qimacros.com/control-chart-formulas/np-chart-formula/
  public static double[] stats_np(Number[] data, int size) {
    int n = size;
    assert(n > 1);

    double pbar = (StatUtils.sum(data)) / (double) (n * data.length);
    double sd = Math.sqrt((double) n * pbar * (1 - pbar));

    double center = n * pbar;
    double lcl = center - 3 * sd;
    if (lcl < 0)
      lcl = 0;
    double ucl = center + 3 * sd;
    if (ucl > n)
      ucl = n;
    return new double[]{center,lcl, ucl};
  }

  // c chart
  // http://www.qimacros.com/control-chart-formulas/c-chart-formula/
  public static double[] stats_c(Number[] data, double size) {
    double cbar = StatUtils.mean(data);

    double center = cbar;
    double lcl = center - 3 * Math.sqrt(cbar);
    if (lcl < 0)
      lcl = 0;
    double ucl = center + 3 * Math.sqrt(cbar);
    return new double[]{center,lcl, ucl};
  }

  // u chart
  // http://www.qimacros.com/control-chart-formulas/u-chart-formula/
  public static double[][] stats_u(Number[] data, Number[] nonconform) {
    assert(data.length == nonconform.length);
    for (int i = 0; i < nonconform.length; i++)
      assert(nonconform[i].doubleValue() > 1);

    double ubar = StatUtils.sum(nonconform) / StatUtils.sum(data);

    double[][] result = new double[data.length][];
    for (int i = 0; i < data.length; i++) {
      double center = ubar;
      double lcl = center - 3 * Math.sqrt(ubar / data[i].doubleValue());
      if (lcl < 0)
	lcl = 0;
      double ucl = center + 3 * Math.sqrt(ubar / data[i].doubleValue());
      result[i] = new double[]{center,lcl, ucl};
    }
    return result;
  }

  public static double[] prepare_data_x_bar_rs_x(Number[][] data, int size) {
    TDoubleArrayList result = new TDoubleArrayList();
    for (Number[] xset: data)
      result.add(StatUtils.mean(xset));
    return result.toArray();
  }

  public static double[] prepare_data_x_bar_r_r(Number[][] data, int size) {
    TDoubleArrayList result = new TDoubleArrayList();
    for (Number[] xset: data)
      result.add(StatUtils.max(xset).doubleValue() - StatUtils.min(xset).doubleValue());
    return result.toArray();
  }

  public static double[] prepare_data_x_bar_s_s(Number[][] data, int size) {
    TDoubleArrayList result = new TDoubleArrayList();
    for (Number[] xset: data)
      result.add(StatUtils.stddev(xset, true));
    return result.toArray();
  }

  public static double[] prepare_data_x_mr(Number[] data, int size) {
    TDoubleArrayList result = new TDoubleArrayList();
    result.add(0);
    for (int i = 0; i < data.length - 1; i++)
      result.add(Math.abs(data[i].doubleValue() - data[i + 1].doubleValue()));
    return result.toArray();
  }

  public static double[] prepare_data_p(Number[] data, int size) {
    TDoubleArrayList result = new TDoubleArrayList();
    result.add(0);
    for (Number d: data)
      result.add(d.doubleValue() / (double) size);
    return result.toArray();
  }

  public static double[] prepare_data_u(Number[] data, Number nonconform) {
    TDoubleArrayList result = new TDoubleArrayList();
    for (Number d: data)
      result.add(nonconform.doubleValue() / d.doubleValue());
    return result.toArray();
  }

  public static double[] prepare_data_u(Number[] data, Number[] nonconform) {
    assert(data.length == nonconform.length);
    TDoubleArrayList result = new TDoubleArrayList();
    for (int i = 0; i < data.length; i++)
      result.add(nonconform[i].doubleValue() / data[i].doubleValue());
    return result.toArray();
  }
}
