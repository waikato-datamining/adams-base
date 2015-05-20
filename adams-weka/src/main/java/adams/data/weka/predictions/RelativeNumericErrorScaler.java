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
 * RelativeNumericErrorScaler.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.predictions;

import java.util.ArrayList;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;

/**
 <!-- globalinfo-start -->
 * Scales the errors for numeric class attributes.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-max &lt;int&gt; (property: maxSize)
 * &nbsp;&nbsp;&nbsp;The maximum size of the errors after scaling.
 * &nbsp;&nbsp;&nbsp;default: 20
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RelativeNumericErrorScaler
  extends AbstractErrorScaler {

  /** for serialization. */
  private static final long serialVersionUID = -8616657706467047751L;

  /** the maximum size. */
  protected int m_MaxSize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Scales the errors for numeric class attributes.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "max", "maxSize",
	    20, 1, null);
  }

  /**
   * Sets the maximum size for the errors.
   *
   * @param value	the size
   */
  public void setMaxSize(int value) {
    m_MaxSize = value;
    reset();
  }

  /**
   * Returns the maximum size for the errors.
   *
   * @return		the size
   */
  public int getMaxSize() {
    return m_MaxSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxSizeTipText() {
    return "The maximum size of the errors after scaling.";
  }

  /**
   * Returns the capabilities of this object. Returns what types of classes
   * the scaler can handle.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
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
  public ArrayList scale(ArrayList data) {
    ArrayList	result;
    double 	maxErr;
    double 	minErr;
    double 	err;
    int		i;
    Double 	errd;
    double 	temp;

    result = new ArrayList();
    maxErr = Double.NEGATIVE_INFINITY;
    minErr = Double.POSITIVE_INFINITY;

    // find min/max errors
    for (i = 0; i < data.size(); i++) {
      errd = (Double) data.get(i);
      if (errd != null) {
	err = Math.abs(errd.doubleValue());
	if (err < minErr)
	  minErr = err;
	if (err > maxErr)
	  maxErr = err;
      }
    }

    // scale errors
    for (i = 0; i < data.size(); i++) {
      errd = (Double) data.get(i);
      if (errd != null) {
	err = Math.abs(errd.doubleValue());
	if (maxErr - minErr > 0) {
	  temp = (((err - minErr) / (maxErr - minErr)) * m_MaxSize);
	  result.add(new Integer((int) temp));
	}
	else {
	  result.add(new Integer(1));
	}
      }
      else {
	result.add(new Integer(1));
      }
    }

    return result;
  }
}
