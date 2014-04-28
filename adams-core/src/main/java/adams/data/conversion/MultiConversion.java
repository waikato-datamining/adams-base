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
 * MultiConversion.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.flow.core.Compatibility;

/**
 <!-- globalinfo-start -->
 * Meta-conversion that allows the chaining of multiple conversions.
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
 * <pre>-conversion &lt;adams.data.conversion.AbstractConversion&gt; [-conversion ...] (property: subConversions)
 * &nbsp;&nbsp;&nbsp;The conversions to apply sequentially to the data.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiConversion
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -8173803394483284352L;

  /** the conversions to use. */
  protected Conversion[] m_SubConversions;

  /** whether the compatibility has been checked. */
  protected boolean m_CompatibilityChecked;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Meta-conversion that allows the chaining of multiple conversions.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "conversion", "subConversions",
	    new Conversion[0]);
  }

  /**
   * Resets the state of the conversion.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CompatibilityChecked = false;
  }

  /**
   * Sets the number of decimals for numbers in tables.
   *
   * @param value	the number of decimals
   */
  public void setSubConversions(Conversion[] value) {
    m_SubConversions = value;
    reset();
  }

  /**
   * Returns the number of decimals for numbers in tables.
   *
   * @return 		the number of decimals
   */
  public Conversion[] getSubConversions() {
    return m_SubConversions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String subConversionsTipText() {
    return "The conversions to apply sequentially to the data.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    if (m_SubConversions.length > 0)
      return m_SubConversions[0].accepts();
    else
      return Object.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    if (m_SubConversions.length > 0)
      return m_SubConversions[m_SubConversions.length - 1].generates();
    else
      return Object.class;
  }

  /**
   * Checks whether the data can be processed.
   *
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String checkData() {
    String		result;
    Compatibility	comp;
    int			i;

    result = super.checkData();

    if ((result == null) && !m_CompatibilityChecked) {
      m_CompatibilityChecked = true;
      comp                   = new Compatibility();
      for (i = 1; i < m_SubConversions.length; i++) {
	if (!comp.isCompatible(m_SubConversions[i - 1].generates(), m_SubConversions[i].accepts())) {
	  result =   "Conversion #" + (i) + " is not compatible with #" + (i+1) + ": "
	           + m_SubConversions[i - 1].generates().getClass().getName() + " != "
	           + m_SubConversions[i].accepts().getClass().getName();
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Object	result;
    Object	input;
    int		i;
    String	msg;

    result = m_Input;  // in case there are no conversions specified
    for (i = 0; i < m_SubConversions.length; i++) {
      input = result;
      m_SubConversions[i].setInput(input);
      msg = m_SubConversions[i].convert();
      if (msg != null)
	throw new IllegalStateException(
	    "Conversion #" + (i+1) + " generated the following error: " + msg);
      result = m_SubConversions[i].getOutput();
    }

    return result;
  }
}
