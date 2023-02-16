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
 * AdamsOptionHandlerHelpGenerator.java
 * Copyright (C) 2016-2023 University of Waikato, Hamilton, NZ
 */

package adams.gui.help;

import adams.core.Utils;
import adams.core.option.HtmlHelpProducer;
import adams.core.option.OptionHandler;
import adams.core.option.UserMode;
import adams.core.option.UserModeSupporter;
import adams.gui.core.ConsolePanel;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.logging.Level;

/**
 * For ADAMS {@link OptionHandler}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AdamsOptionHandlerHelpGenerator
  extends AbstractHelpGenerator
  implements UserModeSupporter {

  /** the user mode to use. */
  protected UserMode m_UserMode;

  /**
   * Hook method for initializing the generator.
   */
  @Override
  public void initializeGenerator() {
    super.initializeGenerator();
    m_UserMode = UserMode.HIGHEST;
  }

  /**
   * Sets the user mode to use for displaying the properties.
   *
   * @param value	the mode
   */
  @Override
  public void setUserMode(UserMode value) {
    m_UserMode = value;
  }

  /**
   * Returns the user mode to use for displaying the properties.
   *
   * @return		the mode
   */
  @Override
  public UserMode getUserMode() {
    return m_UserMode;
  }

  /**
   * Returns whether this class is handled by this generator.
   *
   * @param cls		the class to check
   * @return		true if handled
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(OptionHandler.class, cls);
  }

  /**
   * Returns whether the generated help is HTML or plain text.
   *
   * @param cls		the class to generate the help for
   * @return		true if HTML
   */
  @Override
  public boolean isHtml(Class cls) {
    return true;
  }

  /**
   * Generates and returns the help for the specified class.
   *
   * @param cls		the class to generate the help for
   * @return		the help, null if failed to produce
   */
  @Override
  public String generate(Class cls) {
    try {
      return generate(cls.getDeclaredConstructor().newInstance());
    }
    catch (Exception ex) {
      ConsolePanel.getSingleton().append(
	Level.SEVERE, getClass().getName() + ": Failed to instantiate class: " + cls.getName(), ex);
    }

    return null;
  }

  /**
   * Generates and returns the help for the specified object.
   *
   * @param obj		the object to generate the help for
   * @return		the help, null if failed to produce
   */
  @Override
  public String generate(Object obj) {
    HtmlHelpProducer 	producer;

    producer = new HtmlHelpProducer();
    producer.setUserMode(m_UserMode);
    try {
      producer.produce((OptionHandler) obj);
      return producer.toString();
    }
    catch (Exception ex) {
      ConsolePanel.getSingleton().append(
	Level.SEVERE, getClass().getName() + ": Failed to generate help: " + Utils.classToString(obj), ex);
    }

    return null;
  }
}
