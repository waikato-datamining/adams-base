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
 * AbstractFFT.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.List;

import JSci.maths.Complex;
import JSci.maths.FourierMath;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.container.DataContainer;
import adams.data.container.DataPoint;
import adams.data.padding.PaddingHelper;
import adams.data.padding.PaddingType;

/**
 * Abstract ancestor for Fast Fourier Transform filters based on the <a href="http://jsci.sourceforge.net/">JSci library</a>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9820 $
 * @param <T> the type of data to process
 */
public abstract class AbstractFFT<T extends DataContainer>
  extends AbstractFilter<T>
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 8562484608944330752L;

  /** the type of padding to use. */
  protected PaddingType m_PaddingType;

  /** whether to perform inverse transformation (wavelet -&gt; normal space). */
  protected boolean m_InverseTransform;

  /** whether to return complex or real part of the transformation. */
  protected boolean m_Real;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A filter that transforms the data with Fast Fourier Transform.\n\n"
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
    result.setValue(Field.AUTHOR, "Mark Hale");
    result.setValue(Field.YEAR, "2009");
    result.setValue(Field.TITLE, "JSci - A science API for Java");
    result.setValue(Field.HTTP, "http://jsci.sourceforge.net/");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "padding", "paddingType",
	    PaddingType.ZERO);

    m_OptionManager.add(
	    "inverse", "inverseTransform",
	    false);

    m_OptionManager.add(
	    "real", "real",
	    true);
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
    return "If true, then the inverse transform is performed.";
  }

  /**
   * Sets whether to return the real or complex part of the result.
   *
   * @param value 	true if real part
   */
  public void setReal(boolean value) {
    m_Real = value;
    reset();
  }

  /**
   * Returns whether to return the real or complex part of the result.
   *
   * @return 		true if real part
   */
  public boolean getReal() {
    return m_Real;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String realTipText() {
    return "If enabled, the real part of the tranformation is returned.";
  }

  /**
   * Returns the Y-value of the DataPoint.
   *
   * @param point	the point to get the Y-Value from
   * @return		the Y-value
   */
  protected abstract double getValue(DataPoint point);

  /**
   * Creates a new DataPoint based on the index and the new Y value. Used for
   * padded points.
   *
   * @param points	the original points
   * @param index	the index of the padded point in the output data
   * @param y		the new Y value
   * @return		the new DataPoint
   */
  protected abstract DataPoint newDataPoint(List<DataPoint> points, int index, double y);

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected T processData(T data) {
    T			result;
    int			i;
    List<DataPoint>	points;
    DataPoint		newPoint;
    double[]		real;
    Complex[]		transformed;
    double		value;

    result = (T) data.getHeader();

    points = data.toList();
    real = new double[data.size()];
    for (i = 0; i < data.size(); i++)
      real[i] = getValue(points.get(i));
    real = PaddingHelper.padPow2(real, m_PaddingType);

    // transform data
    if (m_InverseTransform)
      transformed = FourierMath.inverseTransform(real);
    else
      transformed = FourierMath.transform(real);

    // generate output data
    for (i = 0; i < transformed.length; i++) {
      if (m_Real)
	value = transformed[i].real();
      else
	value = transformed[i].imag();
      newPoint = newDataPoint(points, i, value);
      result.add(newPoint);
    }

    return result;
  }
}
