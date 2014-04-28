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
 * ArrayHistogram.java
 * Copyright (C) 2010-2011 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;

/**
 <!-- globalinfo-start -->
 * Generates a histogram from the given array.<br/>
 * The formulas for the various width/#bin calculations can be found here:<br/>
 * WikiPedia (2010). Histogram.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;misc{WikiPedia2010,
 *    author = {WikiPedia},
 *    title = {Histogram},
 *    year = {2010},
 *    HTTP = {http://en.wikipedia.org/wiki/Histogram}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
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
 * <pre>-bin-calc &lt;MANUAL|DENSITY|STURGES|SCOTT|SQRT&gt; (property: binCalculation)
 * &nbsp;&nbsp;&nbsp;Defines how the number of bins are calculated.
 * &nbsp;&nbsp;&nbsp;default: MANUAL
 * </pre>
 *
 * <pre>-num-bins &lt;int&gt; (property: numBins)
 * &nbsp;&nbsp;&nbsp;The number of bins to use in case of manual bin calculation.
 * &nbsp;&nbsp;&nbsp;default: 50
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-bin-width &lt;double&gt; (property: binWidth)
 * &nbsp;&nbsp;&nbsp;The bin width to use for some of the calculations.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0E-5
 * </pre>
 *
 * <pre>-normalize (property: normalize)
 * &nbsp;&nbsp;&nbsp;If set to true the data gets normalized first before the histogram is calculated.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data to process
 */
public class ArrayHistogram<T extends Number>
  extends AbstractArrayStatistic<T>
  implements EqualLengthArrayStatistic, TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 3595293227007460735L;

  /** the key for the number of bins in the meta-data. */
  public final static String METADATA_NUMBINS = "num-bins";

  /** the key for the bin width in the meta-data. */
  public final static String METADATA_BINWIDTH = "bin-width";

  /** the key for the x-values for all the bins in the meta-data. */
  public final static String METADATA_BINX = "bin-x";

  /**
   * Enumeration for the bin calculation.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum BinCalculation {
    /** manual. */
    MANUAL,
    /** density. */
    DENSITY,
    /** Sturges' formula. */
    STURGES,
    /** Scott's choice. */
    SCOTT,
    /** Square root choice. */
    SQRT
  }

  /** how to calculate the number of bins. */
  protected BinCalculation m_BinCalculation;

  /** the number of bins in case of manual bin calculation. */
  protected int m_NumBins;

  /** the bin width - used for some calculations. */
  protected double m_BinWidth;

  /** whether to normalize the data. */
  protected boolean m_Normalize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Generates a histogram from the given array.\n"
      + "The formulas for the various width/#bin calculations can be found here:\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.YEAR, "2010");
    result.setValue(Field.AUTHOR, "WikiPedia");
    result.setValue(Field.TITLE, "Histogram");
    result.setValue(Field.HTTP, "http://en.wikipedia.org/wiki/Histogram");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "bin-calc", "binCalculation",
	    BinCalculation.MANUAL);

    m_OptionManager.add(
	    "num-bins", "numBins",
	    50, 1, null);

    m_OptionManager.add(
	    "bin-width", "binWidth",
	    1.0, 0.00001, null);

    m_OptionManager.add(
	    "normalize", "normalize",
	    false);
  }

  /**
   * Sets how the number of bins is calculated.
   *
   * @param value 	the bin calculation
   */
  public void setBinCalculation(BinCalculation value) {
    m_BinCalculation = value;
    reset();
  }

  /**
   * Returns how the number of bins is calculated.
   *
   * @return 		the bin calculation
   */
  public BinCalculation getBinCalculation() {
    return m_BinCalculation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String binCalculationTipText() {
    return "Defines how the number of bins are calculated.";
  }

  /**
   * Sets the number of bins to use in manual calculation.
   *
   * @param value 	the number of bins
   */
  public void setNumBins(int value) {
    m_NumBins = value;
    reset();
  }

  /**
   * Returns the number of bins to use in manual calculation.
   *
   * @return 		the number of bins
   */
  public int getNumBins() {
    return m_NumBins;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numBinsTipText() {
    return "The number of bins to use in case of manual bin calculation.";
  }

  /**
   * Sets the bin width to use (for some calculations).
   *
   * @param value 	the bin width
   */
  public void setBinWidth(double value) {
    m_BinWidth = value;
    reset();
  }

  /**
   * Returns the bin width in use (for some calculations).
   *
   * @return 		the bin width
   */
  public double getBinWidth() {
    return m_BinWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String binWidthTipText() {
    return "The bin width to use for some of the calculations.";
  }

  /**
   * Sets whether to normalize the data before generating the histogram.
   *
   * @param value 	if true the data gets normalized first
   */
  public void setNormalize(boolean value) {
    m_Normalize = value;
    reset();
  }

  /**
   * Returns whether to normalize the data before generating the histogram.
   *
   * @return 		true if the data gets normalized first
   */
  public boolean getNormalize() {
    return m_Normalize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String normalizeTipText() {
    return "If set to true the data gets normalized first before the histogram is calculated.";
  }

  /**
   * Returns the length of the stored arrays.
   *
   * @return		the length of the arrays, -1 if none stored
   */
  public int getLength() {
    if (size() > 0)
      return get(0).length;
    else
      return -1;
  }

  /**
   * Returns the minimum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the minimum number, -1 for unbounded
   */
  public int getMin() {
    return 1;
  }

  /**
   * Returns the maximum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the maximum number, -1 for unbounded
   */
  public int getMax() {
    return 1;
  }

  /**
   * Calculates the bin width to use.
   * <p/>
   * Formulas taken from here:
   * <ul>
   *   <li><a href="http://en.wikipedia.org/wiki/Histogram" target="_blank">http://en.wikipedia.org/wiki/Histogram</a></li>
   * </ul>
   *
   * @param array	the array to work on
   * @return		the width of the bins, -1 if not necessary
   */
  protected double calcBinWidth(Number[] array) {
    int		numPoints;
    double	stdev;
    double	min;
    double	max;

    numPoints = array.length;

    switch (m_BinCalculation) {
      case MANUAL:
	min = StatUtils.min(array).doubleValue();
	max = StatUtils.max(array).doubleValue();
	return (max - min) / m_NumBins;

      case DENSITY:
	return m_BinWidth;

      case STURGES:
	return -1.0;   // not necessary

      case SCOTT:
	stdev = StatUtils.stddev(array, true);
	return 3.5 * stdev / Math.pow(numPoints, 1/3);

      case SQRT:
	return -1.0;   // not necessary

      default:
	throw new IllegalStateException(
	    "Unhandled bin width calculation: " + m_BinCalculation);
    }
  }

  /**
   * Calculates the number of bins to use.
   * <p/>
   * Formulas taken from here:
   * <ul>
   *   <li><a href="http://en.wikipedia.org/wiki/Histogram" target="_blank">http://en.wikipedia.org/wiki/Histogram</a></li>
   * </ul>
   *
   * @param array	the array to work on
   * @param width	the width of the bins
   * @return		the number of bins to use
   */
  protected int calcNumBins(Number[] array, double width) {
    int		numPoints;
    double	min;
    double	max;

    numPoints = array.length;

    switch (m_BinCalculation) {
      case MANUAL:
	return m_NumBins;

      case DENSITY:
      case SCOTT:
	min = StatUtils.min(array).doubleValue();
	max = StatUtils.max(array).doubleValue();
	return (int) Math.ceil((max - min) / width);

      case STURGES:
	return (int) Math.ceil(Math.log(numPoints) / Math.log(2) + 1);

      case SQRT:
	return (int) Math.round(Math.sqrt(numPoints));

      default:
	throw new IllegalStateException(
	    "Unhandled bin calculation: " + m_BinCalculation);
    }
  }

  /**
   * Generates the actual result.
   *
   * @return		the generated result
   */
  protected StatisticContainer doCalculate() {
    StatisticContainer<Double>	result;
    String			prefix;
    int				n;
    double			binWidth;
    int				numBins;
    Number[]			array;
    int[]			bins;
    int				bin;
    double			min;
    double			max;
    double[]			binX;

    array = get(0);
    if (m_Normalize)
      array = StatUtils.normalize(array);

    prefix   = "bin";
    binWidth = calcBinWidth(array);
    numBins  = calcNumBins(array, binWidth);
    result   = new StatisticContainer<Double>(size(), numBins);
    for (n = 0; n < numBins; n++)
      result.setHeader(n, prefix + " " + (n+1));

    min      = StatUtils.min(array).doubleValue();
    max      = StatUtils.max(array).doubleValue();
    binWidth = (max - min) / numBins;
    bins     = new int[numBins];

    // fill bins
    for (n = 0; n < array.length; n++) {
      bin = (int) Math.floor((array[n].doubleValue() - min) / binWidth);
      // max belongs in the top-most bin
      if (bin == numBins)
	bin--;
      bins[bin]++;
    }

    // fill spreadsheet
    binX = new double[numBins];
    for (n = 0; n < bins.length; n++) {
      binX[n] = min + n * binWidth;
      result.setCell(0, n, new Double(bins[n]));
    }

    // add meta-data
    result.setMetaData(METADATA_NUMBINS,  numBins);
    result.setMetaData(METADATA_BINWIDTH, binWidth);
    result.setMetaData(METADATA_BINX,     binX);

    return result;
  }
}
