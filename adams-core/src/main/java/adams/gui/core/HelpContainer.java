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
 * HelpContainer.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.Shortening;
import adams.core.ShorteningType;

/**
 * Container for storing the help.
 */
public class HelpContainer {

  /** the help text. */
  protected String m_Help;

  /** whether html or plain text. */
  protected boolean m_Html;

  /**
   * Initializes the container.
   *
   * @param help	the help string
   * @param html	true if html
   */
  public HelpContainer(String help, boolean html) {
    m_Help = help;
    m_Html = html;
  }

  /**
   * Returns the help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return m_Help;
  }

  /**
   * Returns whether the help is plain text or html.
   *
   * @return		true if html
   */
  public boolean isHtml() {
    return m_Html;
  }

  /**
   * Returns a shortened version of the help string.
   *
   * @return		the help string
   */
  public String toString() {
    return Shortening.shorten(m_Help, 80, ShorteningType.END);
  }
}
