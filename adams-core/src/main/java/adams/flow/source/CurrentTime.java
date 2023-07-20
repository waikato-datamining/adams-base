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
 * CurrentTime.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class CurrentTime
  extends AbstractSimpleSource {

  private static final long serialVersionUID = 4957327766531564829L;

  /** what time to output. */
  public enum TimeType {
    MILLI_SECONDS,
    NANO_SECONDS,
    MILLI_SECONDS_LONG,
    NANO_SECONDS_LONG,
  }

  /** the type of time to output. */
  protected TimeType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the current time.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      TimeType.MILLI_SECONDS);
  }

  /**
   * Sets the time type.
   *
   * @param value	the type
   */
  public void setType(TimeType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the time type.
   *
   * @return		the type
   */
  public TimeType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The time type to output.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "type", m_Type, "type: ");
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    switch (m_Type) {
      case MILLI_SECONDS:
      case NANO_SECONDS:
	return new Class[]{Double.class};

      case MILLI_SECONDS_LONG:
      case NANO_SECONDS_LONG:
	return new Class[]{Long.class};

      default:
	throw new IllegalStateException("Unhandled time type: " + m_Type);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    switch (m_Type) {
      case MILLI_SECONDS:
        m_OutputToken = new Token((double) System.currentTimeMillis());
        break;

      case NANO_SECONDS:
	m_OutputToken = new Token((double) System.nanoTime());
        break;

      case MILLI_SECONDS_LONG:
	m_OutputToken = new Token(System.currentTimeMillis());
	break;

      case NANO_SECONDS_LONG:
	m_OutputToken = new Token(System.nanoTime());
	break;

      default:
        throw new IllegalStateException("Unhandled time type: " + m_Type);
    }

    return null;
  }
}
