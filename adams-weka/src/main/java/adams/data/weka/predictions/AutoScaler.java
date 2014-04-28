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
 * AutoScaler.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.predictions;

import java.util.ArrayList;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;

/**
 <!-- globalinfo-start -->
 * Applies the specified numeric scaler to the data in case of a numeric class attribute, otherwise just passes on the data.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-scaler &lt;adams.data.weka.predictions.AbstractErrorScaler [options]&gt; (property: scaler)
 * &nbsp;&nbsp;&nbsp;The scaler to use for numeric data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.weka.predictions.RelativeNumericErrorScaler
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AutoScaler
  extends AbstractErrorScaler {

  /** for serialization. */
  private static final long serialVersionUID = 1719519275224776613L;

  /** the scaler to use for numeric classes. */
  protected AbstractErrorScaler m_Scaler;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Applies the specified numeric scaler to the data in case of a "
      + "numeric class attribute, otherwise just passes on the data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "scaler", "scaler",
	    new RelativeNumericErrorScaler());
  }

  /**
   * Sets the scaler to use for numeric data.
   *
   * @param value	the scaler
   */
  public void setScaler(AbstractErrorScaler value) {
    if (value.getCapabilities().handles(Capability.NUMERIC_CLASS)) {
      m_Scaler = value;
      reset();
    }
    else {
      getLogger().severe("Scaler does not handle numeric attributes, ignored!");
    }
  }

  /**
   * Returns the scaler for numeric data.
   *
   * @return		the scaler
   */
  public AbstractErrorScaler getScaler() {
    return m_Scaler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scalerTipText() {
    return "The scaler to use for numeric data.";
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
    result.enableAllClasses();

    return result;
  }

  /**
   * Scales the errors.
   *
   * @param data	the data containing the errors to scale
   * @return 		the scaled errors
   */
  @Override
  public ArrayList<Integer> scale(ArrayList data) {
    ArrayList<Integer>	result;

    result = new ArrayList<Integer>();

    if (data.size() > 0) {
      if (data.get(0) instanceof Double) {
	result = m_Scaler.scale(data);
      }
      else {
	result.addAll(data);
      }
    }

    return result;
  }
}
