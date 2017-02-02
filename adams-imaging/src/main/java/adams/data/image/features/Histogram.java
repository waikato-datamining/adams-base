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
 * Histogram.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.features;

import adams.core.EnumWithCustomDisplay;
import adams.core.option.AbstractOption;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.report.DataType;
import adams.data.statistics.AbstractArrayStatistic.StatisticContainer;
import adams.data.statistics.ArrayHistogram;
import adams.data.statistics.ArrayHistogram.BinCalculation;
import adams.data.statistics.StatUtils;
import org.j3d.color.ColorUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Turns an image into a histogram.<br>
 * In case of an 8-bit histogram, the image must already be converted to a gray image.<br>
 * The number of bins per channel can be chosen as well (1-256).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-converter &lt;adams.data.featureconverter.AbstractFeatureConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The feature converter to use to produce the output data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featureconverter.SpreadSheet -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 *
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to add to the output.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-notes &lt;adams.core.base.BaseString&gt; [-notes ...] (property: notes)
 * &nbsp;&nbsp;&nbsp;The notes to add as attributes to the generated data, eg 'PROCESS INFORMATION'
 * &nbsp;&nbsp;&nbsp;.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-histo-type &lt;GRAY|RGB|YUV|YIQ|HSV&gt; (property: histogramType)
 * &nbsp;&nbsp;&nbsp;The type of histogram to generate.
 * &nbsp;&nbsp;&nbsp;default: RGB
 * </pre>
 *
 * <pre>-num-bins &lt;int&gt; (property: numBins)
 * &nbsp;&nbsp;&nbsp;The number of bins per channel (1-256).
 * &nbsp;&nbsp;&nbsp;default: 256
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 256
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Histogram
  extends AbstractBufferedImageFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /**
   * The type of histogram to generate.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum HistogramType
    implements EnumWithCustomDisplay<HistogramType> {

    GRAY("Gray"),
    RGB("RGB"),
    YUV("YUV"),
    YIQ("YIQ"),
    HSV("HSV");

    /** the display string. */
    private String m_Display;

    /** the commandline string. */
    private String m_Raw;

    /**
     * The constructor.
     *
     * @param display	the string to use as display
     */
    private HistogramType(String display) {
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
     * Returns the display string.
     *
     * @return		the display string
     */
    @Override
    public String toString() {
      return toDisplay();
    }

    /**
     * Parses the given string and returns the associated enum.
     *
     * @param s		the string to parse
     * @return		the enum or null if not found
     */
    public HistogramType parse(String s) {
      return (HistogramType) valueOf((AbstractOption) null, s);
    }

    /**
     * Returns the enum as string.
     *
     * @param option	the current option
     * @param object	the enum object to convert
     * @return		the generated string
     */
    public static String toString(AbstractOption option, Object object) {
      return ((HistogramType) object).toRaw();
    }

    /**
     * Returns an enum generated from the string.
     *
     * @param option	the current option
     * @param str	the string to convert to an enum
     * @return		the generated enum or null in case of error
     */
    public static HistogramType valueOf(AbstractOption option, String str) {
      HistogramType	result;

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
        for (HistogramType dt: values()) {
  	if (dt.toDisplay().equals(str)) {
  	  result = dt;
  	  break;
  	}
        }
      }

      return result;
    }
  }

  /** the type of histogram to generate. */
  protected HistogramType m_HistogramType;

  /** the number of bins per channel. */
  protected int m_NumBins;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Turns an image into a histogram.\n"
      + "In case of an 8-bit histogram, the image must already be converted "
      + "to a gray image.\n"
      + "The number of bins per channel can be chosen as well (1-256).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "histo-type", "histogramType",
      HistogramType.RGB);

    m_OptionManager.add(
      "num-bins", "numBins",
      256, 1, 256);
  }

  /**
   * Sets the type of histogram to generate.
   *
   * @param value 	the type
   */
  public void setHistogramType(HistogramType value) {
    m_HistogramType = value;
    reset();
  }

  /**
   * Returns the type of histogram to generate.
   *
   * @return 		the type
   */
  public HistogramType getHistogramType() {
    return m_HistogramType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String histogramTypeTipText() {
    return "The type of histogram to generate.";
  }

  /**
   * Sets the number of bins to use per channel.
   *
   * @param value 	the number of bins
   */
  public void setNumBins(int value) {
    if (getOptionManager().isValid("numBins", value)) {
      m_NumBins = value;
      reset();
    }
  }

  /**
   * Returns the number of bins to use per channel.
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
    return "The number of bins per channel (1-256).";
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(BufferedImageContainer img) {
    HeaderDefinition		result;
    int				i;
    int				numAtts;

    result  = new HeaderDefinition();
    numAtts = m_NumBins;
    for (i = 0; i < numAtts; i++) {
      switch (m_HistogramType) {
	case GRAY:
	  result.add("histo_" + (i+1), DataType.NUMERIC);
	  break;
	case RGB:
	  result.add("histo_r_" + (i+1), DataType.NUMERIC);
	  result.add("histo_g_" + (i+1), DataType.NUMERIC);
	  result.add("histo_b_" + (i+1), DataType.NUMERIC);
	  break;
	case YUV:
	  result.add("histo_y_" + (i+1), DataType.NUMERIC);
	  result.add("histo_u_" + (i+1), DataType.NUMERIC);
	  result.add("histo_v_" + (i+1), DataType.NUMERIC);
	  break;
	case YIQ:
	  result.add("histo_y_" + (i+1), DataType.NUMERIC);
	  result.add("histo_i_" + (i+1), DataType.NUMERIC);
	  result.add("histo_q_" + (i+1), DataType.NUMERIC);
	  break;
	case HSV:
	  result.add("histo_h_" + (i+1), DataType.NUMERIC);
	  result.add("histo_s_" + (i+1), DataType.NUMERIC);
	  result.add("histo_v_" + (i + 1), DataType.NUMERIC);
	  break;
	default:
	  throw new IllegalStateException("Unhandled histogram type: " + m_HistogramType);
      }
    }

    return result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(BufferedImageContainer img) {
    List<Object>[]		result;
    double[]			values;
    Integer[][]			p;
    BufferedImage		image;
    int				i;
    int				n;
    int				size;
    int[][]			pixels;
    float[]			rgb;
    float[]			conv;
    ArrayHistogram<Integer>	histogram;
    StatisticContainer		cont;

    result = null;

    // set up data structures
    size = img.getHeight() * img.getWidth();
    switch (m_HistogramType) {
      case GRAY:
        image  = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_BYTE_GRAY);
	values = new double[m_NumBins];
	p      = new Integer[1][size];
	break;

      case RGB:
      case YUV:
      case YIQ:
      case HSV:
        image  = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR);
	values = new double[m_NumBins * 3];
	p      = new Integer[3][size];
	break;

      default:
	throw new IllegalStateException("Unhandled histogram type: " + m_HistogramType);
    }

    // fill data structures
    pixels = BufferedImageHelper.getRGBPixels(image);
    rgb    = new float[3];
    conv   = new float[3];
    switch (m_HistogramType) {
      case GRAY:
	for (i = 0; i < size; i++)
	  p[0][i] = pixels[i][0];
	break;

      case RGB:
	for (i = 0; i < size; i++) {
	  for (n = 0; n < 3; n++)
	    p[n][i] = pixels[i][n];
	}
	break;

      case YUV:
	for (i = 0; i < size; i++) {
	  for (n = 0; n < 3; n++)
	    rgb[n] = (float) (pixels[i][n] / 255.0);
	  ColorUtils.convertRGBtoYUV(rgb, conv);
	  for (n = 0; n < 3; n++)
	    p[n][i] = (int) (conv[n] * 255.0);
	}
	break;

      case YIQ:
	for (i = 0; i < size; i++) {
	  for (n = 0; n < 3; n++)
	    rgb[n] = (float) (pixels[i][n] / 255.0);
	  ColorUtils.convertRGBtoYIQ(rgb, conv);
	  for (n = 0; n < 3; n++)
	    p[n][i] = (int) (conv[n] * 255.0);
	}
	break;

      case HSV:
	for (i = 0; i < size; i++) {
	  for (n = 0; n < 3; n++)
	    rgb[n] = (float) (pixels[i][n] / 255.0);
	  ColorUtils.convertRGBtoHSV(rgb, conv);
	  for (n = 0; n < 3; n++)
	    p[n][i] = (int) (conv[n] * 255.0);
	}
	break;

      default:
	throw new IllegalStateException("Unhandled histogram type: " + m_HistogramType);
    }

    // compute histograms
    histogram = new ArrayHistogram<>();
    histogram.setNumBins(m_NumBins);
    histogram.setNormalize(false);
    histogram.setBinCalculation(BinCalculation.MANUAL);
    switch (m_HistogramType) {
      case GRAY:
	histogram.add(p[0]);
	cont = histogram.calculate();
	for (i = 0; i < m_NumBins; i++)
	  values[i] = (Double) cont.getCell(0, i);
	break;

      case RGB:
      case YUV:
      case YIQ:
      case HSV:
	for (n = 0; n < 3; n++) {
	  histogram.clear();
	  histogram.add(p[n]);
	  cont = histogram.calculate();
	  for (i = 0; i < m_NumBins; i++)
	    values[i * 3 + n] = (Double) cont.getCell(0, i);
	}
	break;
    }

    result    = new List[1];
    result[0] = new ArrayList<>();
    result[0].addAll(Arrays.asList(StatUtils.toNumberArray(values)));

    return result;
  }
}
