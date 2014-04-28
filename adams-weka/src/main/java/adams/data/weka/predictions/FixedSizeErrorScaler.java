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
 * FixedSizeErrorScaler.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.predictions;

import java.util.ArrayList;

import weka.core.Capabilities;

/**
 <!-- globalinfo-start -->
 * Scales the errors to a fixed size.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-size &lt;int&gt; (property: size)
 * &nbsp;&nbsp;&nbsp;The size of the errors after scaling.
 * &nbsp;&nbsp;&nbsp;default: 4
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedSizeErrorScaler
  extends AbstractErrorScaler {

  /** for serialization. */
  private static final long serialVersionUID = 2274050632411008699L;

  /** the size. */
  protected int m_Size;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Scales the errors to a fixed size.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "size", "size",
	    4, 1, null);
  }

  /**
   * Sets the size for the errors.
   *
   * @param value	the size
   */
  public void setSize(int value) {
    m_Size = value;
    reset();
  }

  /**
   * Returns the size for the errors.
   *
   * @return		the size
   */
  public int getSize() {
    return m_Size;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sizeTipText() {
    return "The size of the errors after scaling.";
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
    result.enableAllClasses();

    return result;
  }

  /**
   * Scales the errors.
   *
   * @param data	the data containing the errors to scale
   * @return 		the scaled errors
   */
  public ArrayList<Integer> scale(ArrayList data) {
    ArrayList<Integer>	result;
    int			i;

    result = new ArrayList<Integer>();

    // set fixed size errors
    for (i = 0; i < data.size(); i++)
      result.add(new Integer(m_Size));

    return result;
  }
}
