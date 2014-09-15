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
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.features;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import adams.core.EnumWithCustomDisplay;
import adams.core.option.AbstractOption;
import adams.data.base.features.AbstractBufferedImageFeatureGenerator;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.report.DataType;
import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Turns an image into a histogram.<br/>
 * In case of an 8-bit histogram, the image must already be converted to a gray image.
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
 * <pre>-histo-type &lt;8-bit|RGB&gt; (property: histogramType)
 * &nbsp;&nbsp;&nbsp;The type of histogram to generate.
 * &nbsp;&nbsp;&nbsp;default: RGB
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
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

    /** 8-bit. */
    EIGHT_BIT("8-bit"),
    /** RGB. */
    RGB("RGB");

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
      + "to a gray image.";
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
    numAtts = 256;
    for (i = 0; i < numAtts; i++) {
      switch (m_HistogramType) {
	case EIGHT_BIT:
	  result.add("histo_" + (i+1), DataType.NUMERIC);
	  break;
	case RGB:
	  result.add("histo_r_" + (i+1), DataType.NUMERIC);
	  result.add("histo_g_" + (i+1), DataType.NUMERIC);
	  result.add("histo_b_" + (i+1), DataType.NUMERIC);
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
    int[] 			bins;
    double[] 			low;
    double[] 			high;
    javax.media.jai.Histogram 	hist;
    ParameterBlock 		pb;
    PlanarImage			dst;
    int				i;
    BufferedImage		image;

    result = null;
    image  = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR);

    switch (m_HistogramType) {
      case EIGHT_BIT:
	values = new double[256];
	bins   = new int[]{256};             // The number of bins.
	low    = new double[]{0.0D};        // The low value.
	high   = new double[]{256.0D}; // The high value.
	break;

      case RGB:
	values = new double[256 * 3];
	bins   = new int[]{256, 256, 256};             // The number of bins.
	low    = new double[]{0.0D, 0.0D, 0.0D};        // The low value.
	high   = new double[]{256.0D, 256.0D, 256.0D}; // The high value.
	break;

      default:
	throw new IllegalStateException("Unhandled histogram type: " + m_HistogramType);
    }

    // Create the parameter block.
    pb = new ParameterBlock();
    pb.addSource(PlanarImage.wrapRenderedImage(image)); // Specify the source image
    pb.add(null);                      // No ROI
    pb.add(1);                         // xPeriod
    pb.add(1);                         // yPeriod
    pb.add(bins);
    pb.add(low);
    pb.add(high);

    // Perform the histogram operation.
    dst = (PlanarImage) JAI.create("histogram", pb, null);

    // Retrieve the histogram data.
    hist = (javax.media.jai.Histogram) dst.getProperty("histogram");

    switch (m_HistogramType) {
      case EIGHT_BIT:
	for (i = 0; i < 256; i++)
	  values[i] = hist.getBinSize(0, i);
	break;

      case RGB:
	for (i = 0; i < 256; i++) {
	  values[i*3 + 0] = hist.getBinSize(0, i);
	  if (hist.getNumBands() > 1) {
	    values[i*3 + 1] = hist.getBinSize(1, i);
	    values[i*3 + 2] = hist.getBinSize(2, i);
	  }
	}
	break;
    }

    result    = new List[1];
    result[0] = new ArrayList<Object>();
    result[0].addAll(Arrays.asList(StatUtils.toNumberArray(values)));

    return result;
  }
}
