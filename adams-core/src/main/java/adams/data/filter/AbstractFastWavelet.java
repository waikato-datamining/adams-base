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
 * AbstractWavelet.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.List;

import JSci.maths.wavelet.FWT;
import JSci.maths.wavelet.cdf2_4.FastCDF2_4;
import JSci.maths.wavelet.daubechies2.FastDaubechies2;
import JSci.maths.wavelet.haar.FastHaar;
import JSci.maths.wavelet.symmlet8.FastSymmlet8;
import adams.core.EnumWithCustomDisplay;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.option.AbstractOption;
import adams.data.container.DataContainer;
import adams.data.container.DataPoint;

/**
 * Abstract ancestor for Wavelet filters based on the <a href="http://jsci.sourceforge.net/">JSci library</a>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to process
 */
public abstract class AbstractFastWavelet<T extends DataContainer>
  extends AbstractFilter<T>
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 8562484608944330752L;

  /**
   * The type of available wavelets.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum WaveletType
    implements EnumWithCustomDisplay<WaveletType> {

    /** Haar. */
    HAAR("Haar"),
    /** CDF2 4. */
    CDF2_4("CDF2 4"),
    /** Daubechies2. */
    DAUBECHIES2("Daubechies2"),
    /** Symmlet8. */
    SYMMLET8("Symmlet8");

    /** the display value. */
    private String m_Display;

    /** the commandline string. */
    private String m_Raw;

    /**
     * Initializes the element.
     *
     * @param display	the display value
     */
    private WaveletType(String display) {
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
    public WaveletType parse(String s) {
      return (WaveletType) valueOf((AbstractOption) null, s);
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
      return ((WaveletType) object).toRaw();
    }

    /**
     * Returns an enum generated from the string.
     *
     * @param option	the current option
     * @param str		the string to convert to an enum
     * @return		the generated enum or null in case of error
     */
    public static WaveletType valueOf(AbstractOption option, String str) {
      WaveletType	result;

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
        for (WaveletType f: values()) {
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
   * The type of available paddings.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum PaddingType
    implements EnumWithCustomDisplay<PaddingType> {

    /** pad with zeroes. */
    ZERO("Zero");

    /** the display value. */
    private String m_Display;

    /** the commandline string. */
    private String m_Raw;

    /**
     * Initializes the element.
     *
     * @param display	the display value
     */
    private PaddingType(String display) {
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
    public PaddingType parse(String s) {
      return (PaddingType) valueOf((AbstractOption) null, s);
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
      return ((PaddingType) object).toRaw();
    }

    /**
     * Returns an enum generated from the string.
     *
     * @param option	the current option
     * @param str		the string to convert to an enum
     * @return		the generated enum or null in case of error
     */
    public static PaddingType valueOf(AbstractOption option, String str) {
      PaddingType	result;

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
        for (PaddingType f: values()) {
  	if (f.toDisplay().equals(str)) {
  	  result = f;
  	  break;
  	}
        }
      }

      return result;
    }
  }

  /** the type of wavelet to use. */
  protected WaveletType m_WaveletType;

  /** the type of padding to use. */
  protected PaddingType m_PaddingType;

  /** whether to perform inverse transformation (wavelet -&gt; normal space). */
  protected boolean m_InverseTransform;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "A filter that transforms the data with a wavelet.\n\n"
      + "For more information see:\n\n"
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
    result.setValue(Field.YEAR, "2009");
    result.setValue(Field.TITLE, "JSci - A science API for Java");
    result.setValue(Field.HTTP, "http://jsci.sourceforge.net/");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "wavelet", "waveletType",
	    WaveletType.HAAR);

    m_OptionManager.add(
	    "padding", "paddingType",
	    PaddingType.ZERO);

    m_OptionManager.add(
	    "inverse", "inverseTransform",
	    false);
  }

  /**
   * Sets the wavelet type.
   *
   * @param value 	the wavelet type
   */
  public void setWaveletType(WaveletType value) {
    m_WaveletType = value;
    reset();
  }

  /**
   * Returns the wavelet type.
   *
   * @return 		the wavelet type
   */
  public WaveletType getWaveletType() {
    return m_WaveletType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String waveletTypeTipText() {
    return "The wavelet type to use for transforming the data.";
  }

  /**
   * Sets the padding type.
   *
   * @param value 	the padding type
   */
  public void setPaddingType(PaddingType value) {
    m_PaddingType = value;
    reset();
  }

  /**
   * Returns the padding type.
   *
   * @return 		the padding type
   */
  public PaddingType getPaddingType() {
    return m_PaddingType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paddingTypeTipText() {
    return "The padding type to use.";
  }

  /**
   * Sets whether to perform the inverse transform.
   *
   * @param value 	true if to perform the inverse transform
   */
  public void setInverseTransform(boolean value) {
    m_InverseTransform = value;
    reset();
  }

  /**
   * Returns whether to perform the inverse transform.
   *
   * @return 		true if the inverse transform is performed
   */
  public boolean getInverseTransform() {
    return m_InverseTransform;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inverseTransformTipText() {
    return "If true, then the inverse transform is performed (wavelet -> normal space).";
  }

  /**
   * Returns the X-value of the DataPoint.
   *
   * @param point	the point to get the X-Value from
   * @return		the X-value
   */
  protected abstract double getValue(DataPoint point);

  /**
   * Creates a new DataPoint based on the old one and the new X value.
   *
   * @param oldPoint	the old DataPoint
   * @param x		the new X value
   * @return		the new DataPoint
   */
  protected abstract DataPoint newDataPoint(DataPoint oldPoint, double x);

  /**
   * Creates a new DataPoint based on the index and the new X value. Used for
   * padded points.
   *
   * @param points	the original points
   * @param index	the index of the padded point in the output data
   * @param x		the new X value
   * @return		the new DataPoint
   */
  protected abstract DataPoint newDataPoint(List<DataPoint> points, int index, double x);

  /**
   * returns the next bigger number that's a power of 2. If the number is
   * already a power of 2 then this will be returned. The number will be at
   * least 2^2..
   *
   * @param n		the number to start from
   * @return		the next bigger number
   */
  protected static int nextPowerOf2(int n) {
    int		exp;

    exp = (int) StrictMath.ceil(StrictMath.log(n) / StrictMath.log(2.0));
    exp = StrictMath.max(2, exp);

    return (int) StrictMath.pow(2, exp);
  }

  /**
   * pads the data to conform to the necessary number of data points.
   *
   * @param data	the data to pad
   * @return		the padded data
   */
  protected float[] pad(float[] data) {
    float[] 	result;
    int 	i;
    int		numPoints;

    // determine number of padding attributes
    switch (m_PaddingType) {
      case ZERO:
	numPoints = nextPowerOf2(data.length) - data.length;
	if (m_WaveletType == WaveletType.CDF2_4)
	  numPoints++;
	break;
      default:
	throw new IllegalStateException(
	    "Padding " + m_PaddingType  + " not implemented!");
    }

    result = new float[data.length + numPoints];
    System.arraycopy(data, 0, result, 0, data.length);

    // padding
    switch (m_PaddingType) {
      case ZERO:
	for (i = 0; i < numPoints; i++)
	  result[data.length + i] = 0;
	break;
      default:
	throw new IllegalStateException(
	    "Padding " + m_PaddingType  + " not implemented!");
    }

    return result;
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  protected T processData(T data) {
    T			result;
    int			i;
    List<DataPoint>	points;
    DataPoint		newPoint;
    float[]		values;
    FWT			fwt;

    result = (T) data.getHeader();

    points = data.toList();
    values = new float[points.size()];
    for (i = 0; i < points.size(); i++)
      values[i] = (float) getValue(points.get(i));
    if (!m_InverseTransform)
      values = pad(values);

    // setup wavelet
    switch (m_WaveletType) {
      case HAAR:
	fwt = new FastHaar();
	break;
      case CDF2_4:
	fwt = new FastCDF2_4();
	break;
      case DAUBECHIES2:
	fwt = new FastDaubechies2();
	break;
      case SYMMLET8:
	fwt = new FastSymmlet8();
	break;
      default:
	throw new IllegalStateException("Unhandled wavelet type: " + m_WaveletType);
    }

    // transform data
    if (m_InverseTransform)
      fwt.invTransform(values);
    else
      fwt.transform(values);

    // generate output data
    for (i = 0; i < values.length; i++) {
      if (i < points.size())
	newPoint = newDataPoint(points.get(i), values[i]);
      else
	newPoint = newDataPoint(points, points.size() - i, values[i]);
      result.add(newPoint);
    }

    return result;
  }
}
