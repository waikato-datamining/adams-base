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
 * Round.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.RoundingType;

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
 * @version $Revision$
 */
public class Round
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -4941255219517637632L;
  
  /** the action to perform. */
  protected RoundingType m_Action;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Rounds double values and turns them into integers..";
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
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "action", m_Action);
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
    return Integer.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Double	input;
    
    input = (Double) m_Input;
    
    switch (m_Action) {
      case ROUND:
	return (int) Math.round(input);
      case CEILING:
	return (int) Math.ceil(input);
      case FLOOR:
	return (int) Math.floor(input);
      default:
	throw new IllegalStateException("Unhandled action: " + m_Action);
    }
  }
}
