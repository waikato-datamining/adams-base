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
 * HelpSupporter.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

/**
 * Interface for classes that support help information and the display of it.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface HelpSupporter {

  /**
   * Clears any help information.
   */
  public void clearHelp();

  /**
   * Returns whether any help information is available.
   *
   * @return		true if help available
   */
  public boolean hasHelp();

  /**
   * Sets the help information to offer.
   *
   * @param help	the help
   * @param isHtml	whether html or plain text
   */
  public void setHelp(String help, boolean isHtml);

  /**
   * Returns the help information if any.
   *
   * @return		the help information
   */
  public String getHelp();

  /**
   * Returns whether the help is html or plain text.
   *
   * @return		true if html
   */
  public boolean isHelpHtml();

  /**
   * Displays the help.
   */
  public void showHelp();
}
