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
 * Console.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink.sendnotification;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseText;
import adams.core.io.ConsoleHelper;
import adams.core.logging.LoggingHelper;

/**
 * Outputs the message in the console (with optional prefix).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Console
  extends AbstractNotification {

  private static final long serialVersionUID = 4577706017089540470L;

  /** a custom prefix. */
  protected BaseText m_Prefix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the message in the console (with optional prefix).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      new BaseText());
  }

  /**
   * Sets the class label index (1-based index).
   *
   * @param value 	the index
   */
  public void setPrefix(BaseText value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the class label index (1-based index).
   *
   * @return 		the index
   */
  public BaseText getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix to output before the actual data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "prefix", (getPrefix().length() > 0 ? getPrefix() : "-none-"));
  }

  /**
   * Sends the notification.
   *
   * @param msg		the message to send
   * @return		null if successfully sent, otherwise error message
   */
  @Override
  protected String doSendNotification(String msg) {
    String		result;
    StringBuilder	str;

    result = null;

    try {
      str = new StringBuilder();
      if (m_Prefix.length() > 0)
	str.append(m_Prefix);
      str.append(msg);
      ConsoleHelper.printlnOut(str.toString());
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed output message!", e);
    }

    return result;
  }
}
