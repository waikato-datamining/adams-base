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
 * SimpleLinePlot.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core.gnuplot;

import adams.core.EnumWithCustomDisplay;
import adams.core.Utils;
import adams.core.option.AbstractOption;

/**
 <!-- globalinfo-start -->
 * For simple plots, like line plots.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-data-file &lt;adams.core.io.PlaceholderFile&gt; (property: dataFile)
 * &nbsp;&nbsp;&nbsp;The data file to use as basis for the plot.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-cols &lt;java.lang.String&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The columns to use in the plot.
 * &nbsp;&nbsp;&nbsp;default: 1:2
 * </pre>
 *
 * <pre>-plot-type &lt;lines|points|linespoints|impulses|dots|steps|fsteps|histeps|errorbars|xerrorbars|yerrorbars|xyerrorbars|errorlines|xerrorlines|yerrorlines|xyerrorlines|boxes|filledcurves|boxerrorbars|boxxyerrorbars|financebars|candlesticks|vectors&gt; (property: plotType)
 * &nbsp;&nbsp;&nbsp;The plot type to use.
 * &nbsp;&nbsp;&nbsp;default: LINES
 * </pre>
 *
 * <pre>-plot-name &lt;java.lang.String&gt; (property: plotName)
 * &nbsp;&nbsp;&nbsp;The name to use for the plot in the key; gnuplot default is used if empty;
 * &nbsp;&nbsp;&nbsp; use 'notitle' to suppress title.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimplePlot
  extends AbstractPlotScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = -3540923217777778401L;

  /**
   * Enumeration of available plot types.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum PlotType
    implements EnumWithCustomDisplay<PlotType> {

    LINES("lines"),
    POINTS("points"),
    LINESPOINTS("linespoints"),
    IMPULSES("impulses"),
    DOTS("dots"),
    STEPS("steps"),
    FSTEPS("fsteps"),
    HISTEPS("histeps"),
    ERRORBARS("errorbars"),
    XERRORBARS("xerrorbars"),
    YERRORBARS("yerrorbars"),
    XYERRORBARS("xyerrorbars"),
    ERRORLINES("errorlines"),
    XERRORLINES("xerrorlines"),
    YERRORLINES("yerrorlines"),
    XYERRORLINES("xyerrorlines"),
    BOXES("boxes"),
    FILLEDCURVES("filledcurves"),
    BOXERRORBARS("boxerrorbars"),
    BOXXYERRORBARS("boxxyerrorbars"),
    FINANCEBARDS("financebars"),
    CANDELSTICKS("candlesticks"),
    VECTORS("vectors");

    /** the display value. */
    private String m_Display;

    /** the commandline string. */
    private String m_Raw;

    /**
     * Initializes the element.
     *
     * @param display		the display value
     */
    private PlotType(String display) {
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
    public PlotType parse(String s) {
      return (PlotType) valueOf((AbstractOption) null, s);
    }

    /**
     * Returns the display string without the "numeric" or "nominal" in
     * parentheses.
     *
     * @return		the field string
     */
    public String getField() {
      return m_Display.replaceAll(" .*", "");
    }

    /**
     * Returns the displays string.
     *
     * @return		the display string
     */
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
      return ((PlotType) object).toRaw();
    }

    /**
     * Returns an enum generated from the string.
     *
     * @param option	the current option
     * @param str		the string to convert to an enum
     * @return		the generated enum or null in case of error
     */
    public static PlotType valueOf(AbstractOption option, String str) {
      PlotType	result;

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
        for (PlotType f: values()) {
          if (f.toDisplay().equals(str)) {
            result = f;
            break;
          }
        }
      }

      return result;
    }
  }

  /** the 'notitle' keyword. */
  public final static String NOTITLE = "notitle";

  /** the plot type to use. */
  protected PlotType m_PlotType;

  /** the name of the plot. */
  protected String m_PlotName;

  /** whether the plot is the first plot. */
  protected boolean m_FirstPlot;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "For simple plots, like line plots.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"plot-type", "plotType",
	PlotType.LINES);

    m_OptionManager.add(
	"plot-name", "plotName",
	"");

    m_OptionManager.add(
	"first-plot", "firstPlot",
	false);
  }

  /**
   * Sets the plot type to use.
   *
   * @param value	the type
   */
  public void setPlotType(PlotType value) {
    m_PlotType = value;
    reset();
  }

  /**
   * Returns the plot type in use.
   *
   * @return		the type
   */
  public PlotType getPlotType() {
    return m_PlotType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String plotTypeTipText() {
    return "The plot type to use.";
  }

  /**
   * Sets the name of the plot.
   *
   * @param value	the name
   */
  public void setPlotName(String value) {
    m_PlotName = value;
    reset();
  }

  /**
   * Returns the name of the plot.
   *
   * @return		the name
   */
  public String getPlotName() {
    return m_PlotName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String plotNameTipText() {
    return
        "The name to use for the plot in the key; gnuplot default is used "
      + "if empty; use '" + NOTITLE + "' to suppress title.";
  }

  /**
   * Sets whether the plot is the first plot or not (because of "plot" or
   * "replot" instruction).
   *
   * @param value	if it is the first plot then use true
   */
  public void setFirstPlot(boolean value) {
    m_FirstPlot = value;
    reset();
  }

  /**
   * Returns whether the plot is the first plot (because of "plot" or "replot"
   * instruction).
   *
   * @return		true if it is the first plot
   */
  public boolean getFirstPlot() {
    return m_FirstPlot;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String firstPlotTipText() {
    return "If enabled, the plot is assumed to be the first plot.";
  }

  /**
   * Generates the actual script code.
   *
   * @return		the script code, null in case of an error
   */
  protected String doGenerate() {
    StringBuilder	result;

    result = new StringBuilder();

    if (m_FirstPlot)
      result.append("plot");
    else
      result.append("replot");
    result.append(" ");
    result.append("\"" + getDataFile().getAbsolutePath() + "\"");
    result.append(" ");
    result.append("using " + m_Columns);
    result.append(" ");
    if (m_PlotName.equals(NOTITLE))
      result.append(NOTITLE);
    else if (m_PlotName.length() > 0)
      result.append("title \"" + Utils.backQuoteChars(m_PlotName) + "\"");
    if (result.charAt(result.length() - 1) != ' ')
      result.append(" ");
    result.append("with " + m_PlotType);
    result.append("\n");

    return result.toString();
  }
}
