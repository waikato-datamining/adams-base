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
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.core.Utils;

/**
 <!-- globalinfo-start -->
 * Generates a histogram from the given array.<br/>
 * The formulas for the various width&#47;#bin calculations can be found here:<br/>
 * WikiPedia (2010). Histogram.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;misc{WikiPedia2010,
 *    author = {WikiPedia},
 *    title = {Histogram},
 *    year = {2010},
 *    HTTP = {http:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Histogram}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
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
 * <pre>-normalize &lt;boolean&gt; (property: normalize)
 * &nbsp;&nbsp;&nbsp;If set to true the data gets normalized first before the histogram is calculated.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-use-fixed-min-max &lt;boolean&gt; (property: useFixedMinMax)
 * &nbsp;&nbsp;&nbsp;If enabled, then the user-specified min&#47;max values are used for the bin 
 * &nbsp;&nbsp;&nbsp;calculation rather than the min&#47;max from the data (allows comparison of 
 * &nbsp;&nbsp;&nbsp;histograms when generating histograms over a range of arrays).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-manual-min &lt;double&gt; (property: manualMin)
 * &nbsp;&nbsp;&nbsp;The minimum to use when using manual binning with user-supplied min&#47;max 
 * &nbsp;&nbsp;&nbsp;enabled.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 * <pre>-manual-max &lt;double&gt; (property: manualMax)
 * &nbsp;&nbsp;&nbsp;The maximum to use when using manual binning with user-supplied max&#47;max 
 * &nbsp;&nbsp;&nbsp;enabled.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 * 
 * <pre>-display-ranges &lt;boolean&gt; (property: displayRanges)
 * &nbsp;&nbsp;&nbsp;If enabled, the bins get description according to their range, rather than 
 * &nbsp;&nbsp;&nbsp;a simple index.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-num-decimals &lt;int&gt; (property: numDecimals)
 * &nbsp;&nbsp;&nbsp;The number of decimals to show in the bin descriptions.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 0
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

  /** the key for the minimum used in the meta-data. */
  public final static String METADATA_MINIMUM = "minimum";

  /** the key for the maximum used in the meta-data. */
  public final static String METADATA_MAXIMUM = "maximum";

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

  /** whether to use fixed min/max for manual bin calculation. */
  protected boolean m_UseFixedMinMax;

  /** the manual minimum. */
  protected double m_ManualMin;

  /** the manual maximum. */
  protected double m_ManualMax;

  /** whether to use the ranges as bin description. */
  protected boolean m_DisplayRanges;

  /** the number of decimals to show. */
  protected int m_NumDecimals;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
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
  @Override
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

    m_OptionManager.add(
	    "use-fixed-min-max", "useFixedMinMax",
	    false);

    m_OptionManager.add(
	    "manual-min", "manualMin",
	    0.0);

    m_OptionManager.add(
	    "manual-max", "manualMax",
	    1.0);

    m_OptionManager.add(
	    "display-ranges", "displayRanges",
	    false);

    m_OptionManager.add(
	    "num-decimals", "numDecimals",
	    3, 0, null);
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
   * Sets whether to use user-supplied min/max for bin calculation rather
   * than obtain min/max from data.
   *
   * @param value 	true if to use user-supplied min/max
   */
  public void setUseFixedMinMax(boolean value) {
    m_UseFixedMinMax = value;
    reset();
  }

  /**
   * Returns whether to use user-supplied min/max for bin calculation rather
   * than obtain min/max from data.
   *
   * @return 		true if to use user-supplied min/max
   */
  public boolean getUseFixedMinMax() {
    return m_UseFixedMinMax;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useFixedMinMaxTipText() {
    return 
	"If enabled, then the user-specified min/max values are used for the "
	+ "bin calculation rather than the min/max from the data (allows "
	+ "comparison of histograms when generating histograms over a range "
	+ "of arrays).";
  }

  /**
   * Sets the minimum to use when using manual binning with user-supplied 
   * min/max enabled.
   *
   * @param value 	the minimum
   */
  public void setManualMin(double value) {
    m_ManualMin = value;
    reset();
  }

  /**
   * Returns the minimum to use when using manual binning with user-supplied 
   * min/max enabled.
   *
   * @return 		the minimum
   */
  public double getManualMin() {
    return m_ManualMin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String manualMinTipText() {
    return "The minimum to use when using manual binning with user-supplied min/max enabled.";
  }

  /**
   * Sets the maximum to use when using manual binning with user-supplied 
   * max/max enabled.
   *
   * @param value 	the maximum
   */
  public void setManualMax(double value) {
    m_ManualMax = value;
    reset();
  }

  /**
   * Returns the maximum to use when using manual binning with user-supplied 
   * max/max enabled.
   *
   * @return 		the maximum
   */
  public double getManualMax() {
    return m_ManualMax;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String manualMaxTipText() {
    return "The maximum to use when using manual binning with user-supplied max/max enabled.";
  }

  /**
   * Sets whether to use the bin ranges as their description rather than a 
   * simple index.
   *
   * @param value 	true if to display the ranges
   */
  public void setDisplayRanges(boolean value) {
    m_DisplayRanges = value;
    reset();
  }

  /**
   * Returns whether to use the bin ranges as their description rather than a 
   * simple index.
   *
   * @return 		true if to display the ranges
   */
  public boolean getDisplayRanges() {
    return m_DisplayRanges;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String displayRangesTipText() {
    return "If enabled, the bins get description according to their range, rather than a simple index.";
  }

  /**
   * Sets the number of decimals to show in the bin description.
   *
   * @param value 	the number of decimals
   */
  public void setNumDecimals(int value) {
    m_NumDecimals = value;
    reset();
  }

  /**
   * Returns the number of decimals to show in the bin description.
   *
   * @return 		the number of decimals
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDecimalsTipText() {
    return "The number of decimals to show in the bin descriptions.";
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
  @Override
  public int getMin() {
    return 1;
  }

  /**
   * Returns the maximum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the maximum number, -1 for unbounded
   */
  @Override
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
	if (m_UseFixedMinMax) {
	  min = m_ManualMin;
	  max = m_ManualMax;
	}
	else {
	  min = StatUtils.min(array).doubleValue();
	  max = StatUtils.max(array).doubleValue();
	}
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
  @Override
  protected StatisticContainer doCalculate() {
    StatisticContainer<Double>	result;
    int				n;
    double			binWidth;
    int				numBins;
    Number[]			array;
    int[]			bins;
    int				bin;
    double			min;
    double			max;
    double[]			binX;
    String			prefix;

    array = get(0);
    if (m_Normalize)
      array = StatUtils.normalize(array);

    prefix   = "bin";
    binWidth = calcBinWidth(array);
    numBins  = calcNumBins(array, binWidth);
    if ((m_BinCalculation == BinCalculation.MANUAL) && m_UseFixedMinMax) {
      min = m_ManualMin;
      max = m_ManualMax;
    }
    else {
      min = StatUtils.min(array).doubleValue();
      max = StatUtils.max(array).doubleValue();
    }
    binWidth = (max - min) / numBins;
    bins     = new int[numBins];
    result   = new StatisticContainer<Double>(size(), numBins);
    for (n = 0; n < numBins; n++) {
      if (m_DisplayRanges)
	result.setHeader(
	    n, 
	    "[" 
	    + Utils.doubleToString(min + binWidth * n, m_NumDecimals)
	    + "-"
	    + Utils.doubleToString(min + binWidth * (n + 1), m_NumDecimals)
	    + ((n == numBins - 1) ? "]" : ")"));
      else
	result.setHeader(
	    n, 
	    prefix + " " + (n+1));
    }

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
    result.setMetaData(METADATA_MINIMUM,  min);
    result.setMetaData(METADATA_MAXIMUM,  max);

    return result;
  }
}
