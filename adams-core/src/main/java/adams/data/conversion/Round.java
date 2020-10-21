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
 * Round.java
 * Copyright (C) 2013-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.RoundingType;
import adams.data.RoundingUtils;

/**
 <!-- globalinfo-start -->
 * Rounds double values and turns them into integers..
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-action &lt;ROUND|CEILING|FLOOR&gt; (property: action)
 * &nbsp;&nbsp;&nbsp;The action to perform on the doubles passing through.
 * &nbsp;&nbsp;&nbsp;default: ROUND
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Round
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -4941255219517637632L;
  
  /** the action to perform. */
  protected RoundingType m_Action;

  /** the number of decimals. */
  protected int m_NumDecimals;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Rounds double values and turns them into integers.\n"
      + "When specifying a value larger than zero for 'numDecimals', the "
      + "rounding will happen at that decimal. In other words:\n"
      + "  rounding(value * 10^numDecimals) / 10^numDecimals";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "action", "action",
      RoundingType.ROUND);

    m_OptionManager.add(
      "num-decimals", "numDecimals",
      0, 0, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "action", m_Action);
    result += QuickInfoHelper.toString(this, "numDecimals", m_NumDecimals, ", decimals: ");

    return result;
  }

  /**
   * Sets the action to perform on the doubles.
   *
   * @param value	the action
   */
  public void setAction(RoundingType value) {
    m_Action = value;
    reset();
  }

  /**
   * Returns the action to perform on the doubles.
   *
   * @return		the action
   */
  public RoundingType getAction() {
    return m_Action;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actionTipText() {
    return "The action to perform on the doubles passing through.";
  }

  /**
   * Sets the number of decimals after the decimal point to use.
   *
   * @param value	the number of decimals
   */
  public void setNumDecimals(int value) {
    if (getOptionManager().isValid("numDecimals", value)) {
      m_NumDecimals = value;
      reset();
    }
  }

  /**
   * Returns the number of decimals after the decimal point to use.
   *
   * @return		the number of decimals
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDecimalsTipText() {
    return "The number of decimals after the decimal point to use.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Double.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    if (m_NumDecimals == 0)
      return Integer.class;
    else
      return Double.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    double	rounded;

    rounded = RoundingUtils.apply(m_Action, (Double) m_Input, m_NumDecimals);
    if (m_NumDecimals == 0)
      return (int) rounded;
    else
      return rounded;
  }
}
