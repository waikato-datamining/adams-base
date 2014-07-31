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
 * Copyright (C) 2010-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagej.features;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;

import java.lang.reflect.Array;
import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import adams.core.EnumWithCustomDisplay;
import adams.core.option.AbstractOption;
import adams.data.imagej.ImagePlusContainer;

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
  extends AbstractImageJFeatureGenerator {

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
  public String globalInfo() {
    return
        "Turns an image into a histogram.\n"
      + "In case of an 8-bit histogram, the image must already be converted "
      + "to a gray image.";
  }

  /**
   * Adds options to the internal list of options.
   */
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
  public Instances createHeader(ImagePlusContainer img) {
    Instances			result;
    ArrayList<Attribute>	atts;
    int				i;
    int				numAtts;

    numAtts = 256;
    atts    = new ArrayList<Attribute>();
    for (i = 0; i < numAtts; i++) {
      switch (m_HistogramType) {
	case EIGHT_BIT:
	  atts.add(new Attribute("histo_" + (i+1)));
	  break;
	case RGB:
	  atts.add(new Attribute("histo_r_" + (i+1)));
	  atts.add(new Attribute("histo_g_" + (i+1)));
	  atts.add(new Attribute("histo_b_" + (i+1)));
	  break;
	default:
	  throw new IllegalStateException("Unhandled histogram type: " + m_HistogramType);
      }
    }
    result = new Instances(getClass().getName(), atts, 0);

    return result;
  }

  /**
   * Performs the actual flattening of the image.
   *
   * @param img		the image to process
   * @return		the generated array
   */
  public Instance[] doGenerate(ImagePlusContainer img) {
    Instance[]		result;
    double[]		values;
    Object		pixels;
    int			i;
    int			n;
    ColorProcessor 	cp;
    ByteProcessor 	bp;
    ImagePlus		ip;
    byte[] 		R;
    byte[] 		G;
    byte[] 		B;

    result = null;
    values = newArray(m_Header.numAttributes());
    ip     = img.getImage();

    switch (m_HistogramType) {
      case EIGHT_BIT:
	pixels = ip.getProcessor().getHistogram();
	for (i = 0; i < Array.getLength(pixels); i++)
	  values[i] = Array.getDouble(pixels, i);
	break;

      case RGB:
	for (n = 0; n < 3; n++) {
	  cp = new ColorProcessor(ip.getImage());
	  R  = new byte[ip.getWidth() * ip.getHeight()];
	  G  = new byte[ip.getWidth() * ip.getHeight()];
	  B  = new byte[ip.getWidth() * ip.getHeight()];
	  cp.getRGB(R, G, B);
	  bp = null;
	  if (n == 0)
	    bp = new ByteProcessor(ip.getWidth(), ip.getHeight(), R, null);
	  else if (n == 1)
	    bp = new ByteProcessor(ip.getWidth(), ip.getHeight(), G, null);
	  else if (n == 2)
	    bp = new ByteProcessor(ip.getWidth(), ip.getHeight(), B, null);
	  pixels = bp.getHistogram();
	  for (i = 0; i < Array.getLength(pixels); i++)
	    values[i*3 + n] = Array.getDouble(pixels, i);
	}
	break;

      default:
	throw new IllegalStateException("Unhandled histogram type: " + m_HistogramType);
    }

    result = new Instance[]{new DenseInstance(1.0, values)};
    result[0].setDataset(m_Header);

    return result;
  }
}
