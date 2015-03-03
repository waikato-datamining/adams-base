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
 * NumericErrorScalerWithReference.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.predictions;

import java.util.ArrayList;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;

/**
 <!-- globalinfo-start -->
 * Scales the errors for numeric class attributes, using an user-specified error as reference point for a specified size.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-reference-error &lt;double&gt; (property: referenceError)
 * &nbsp;&nbsp;&nbsp;The absolute error to use as reference for the error sizes (&gt; 0).
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0E-5
 * </pre>
 * 
 * <pre>-reference-size &lt;int&gt; (property: referenceSize)
 * &nbsp;&nbsp;&nbsp;The size for the reference error (&gt;= 1).
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NumericErrorScalerWithReference
  extends AbstractErrorScaler {

  /** for serialization. */
  private static final long serialVersionUID = -8616657706467047751L;

  /** the reference error. */
  protected double m_ReferenceError;

  /** the reference size. */
  protected int m_ReferenceSize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Scales the errors for numeric class attributes, using an "
	+ "user-specified error as reference point for a specified size.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reference-error", "referenceError",
	    1.0, 0.00001, null);

    m_OptionManager.add(
	    "reference-size", "referenceSize",
	    10, 1, null);
  }

  /**
   * Sets the absolute value of the reference error.
   *
   * @param value	the error
   */
  public void setReferenceError(double value) {
    if (value > 0) {
      m_ReferenceError = value;
      reset();
    }
    else {
      getLogger().severe("Reference error must be greater than 0, provided: " + value);
    }
  }

  /**
   * Returns the absolute value of the reference error.
   *
   * @return		the error
   */
  public double getReferenceError() {
    return m_ReferenceError;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String referenceErrorTipText() {
    return "The absolute error to use as reference for the error sizes (> 0).";
  }

  /**
   * Sets the size for the reference error.
   *
   * @param value	the size
   */
  public void setReferenceSize(int value) {
    if (value >= 1) {
      m_ReferenceSize = value;
      reset();
    }
    else {
      getLogger().severe("Reference size must be at least 1 pixel, provided: " + value);
    }
  }

  /**
   * Returns the size for the reference error.
   *
   * @return		the size
   */
  public int getReferenceSize() {
    return m_ReferenceSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String referenceSizeTipText() {
    return "The size for the reference error (>= 1).";
  }

  /**
   * Returns the capabilities of this object. Returns what types of classes
   * the scaler can handle.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = new Capabilities(this);
    result.enable(Capability.DATE_CLASS);
    result.enable(Capability.NUMERIC_CLASS);

    return result;
  }

  /**
   * Scales the errors.
   *
   * @param data	the data containing the errors to scale
   * @return 		the scaled errors
   */
  @Override
  public ArrayList scale(ArrayList data) {
    ArrayList	result;
    double 	err;
    int		i;
    Double 	errd;
    double 	temp;

    result = new ArrayList();

    // scale errors
    for (i = 0; i < data.size(); i++) {
      errd = (Double) data.get(i);
      if (errd != null) {
	err  = Math.abs(errd.doubleValue());
	temp = err / m_ReferenceError * m_ReferenceSize;
	result.add(new Integer((int) temp));
      }
    }

    return result;
  }
}
