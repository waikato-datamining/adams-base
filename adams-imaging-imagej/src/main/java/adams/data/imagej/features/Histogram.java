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
 * Copyright (C) 2010-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagej.features;

import adams.core.EnumWithCustomDisplay;
import adams.core.option.AbstractOption;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.imagej.ImagePlusContainer;
import adams.data.report.DataType;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Turns an image into a histogram.<br>
 * In case of an 8-bit histogram, the image must already be converted to a gray image.
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
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The (optional) prefix to use for the feature names.
 * &nbsp;&nbsp;&nbsp;default: 
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
 * <pre>-histo-type &lt;EIGHT_BIT|RGB&gt; (property: histogramType)
 * &nbsp;&nbsp;&nbsp;The type of histogram to generate.
 * &nbsp;&nbsp;&nbsp;default: RGB
 * </pre>
 * 
 * <pre>-group-channels &lt;boolean&gt; (property: groupChannels)
 * &nbsp;&nbsp;&nbsp;If enabled, grouping is by channel rather than by bin.
 * &nbsp;&nbsp;&nbsp;default: false
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

  /** whether to group the channels. */
  protected boolean m_GroupChannels;

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

    m_OptionManager.add(
      "group-channels", "groupChannels",
      false);
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
   * Sets the whether to group by channels.
   *
   * @param value 	true if to group by channels
   */
  public void setGroupChannels(boolean value) {
    m_GroupChannels = value;
    reset();
  }

  /**
   * Returns whether to group by channels.
   *
   * @return 		true if to group by channels
   */
  public boolean getGroupChannels() {
    return m_GroupChannels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String groupChannelsTipText() {
    return "If enabled, grouping is by channel rather than by bin.";
  }

  /**
   * Returns the channel suffixes to use.
   *
   * @return		the suffixes
   */
  protected String[] getChannelSuffixes() {
    switch (m_HistogramType) {
      case EIGHT_BIT:
	return new String[]{"_"};
      case RGB:
	return new String[]{"_r_", "_g_", "_b_"};
      default:
        throw new IllegalStateException("Unhandled histogram type: " + m_HistogramType);
    }
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(ImagePlusContainer img) {
    HeaderDefinition	result;
    int			i;
    int			numAtts;
    String[] 		channels;

    result  = new HeaderDefinition();
    numAtts = 256;
    channels = getChannelSuffixes();
    if (m_GroupChannels) {
      for (String channel : channels) {
	for (i = 0; i < numAtts; i++)
	  result.add("histo" + channel + (i + 1), DataType.NUMERIC);
      }
    }
    else {
      for (i = 0; i < numAtts; i++) {
	for (String channel : channels)
	  result.add("histo" + channel + (i+1), DataType.NUMERIC);
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
  public List<Object>[] generateRows(ImagePlusContainer img) {
    List<Object>[]	result;
    Object		pixels;
    int			i;
    int			n;
    ColorProcessor 	cp;
    ByteProcessor 	bp;
    ImagePlus		ip;
    byte[] 		R;
    byte[] 		G;
    byte[] 		B;

    result = new List[1];
    result[0] = new ArrayList<>();
    ip     = img.getImage();

    switch (m_HistogramType) {
      case EIGHT_BIT:
	pixels = ip.getProcessor().getHistogram();
	for (i = 0; i < Array.getLength(pixels); i++)
	  result[0].add(Array.getDouble(pixels, i));
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
	  // init list
	  if (result[0].size() == 0) {
	    for (i = 0; i < Array.getLength(pixels) * 3; i++)
	      result[0].add(0);
	  }
	  for (i = 0; i < Array.getLength(pixels); i++) {
            if (m_GroupChannels)
              result[0].set(n * 256 + i, Array.getDouble(pixels, i));
            else
              result[0].set(i * 3 + n, Array.getDouble(pixels, i));
          }
	}
	break;

      default:
	throw new IllegalStateException("Unhandled histogram type: " + m_HistogramType);
    }

    return result;
  }
}
