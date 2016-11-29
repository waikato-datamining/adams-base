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
 * LogPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.logging.LoggingLevel;

/**
 * Common interface for log panels.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface LogPanel {

  /**
   * Appends the given string.
   *
   * @param level	the logging level
   * @param msg		the message to append
   */
  public void append(LoggingLevel level, String msg);

  /**
   * Clears the text.
   */
  public void clear();

  /**
   * Copies the text to the clipboard.
   */
  public void copy();

  /**
   * Saves the current content to a file.
   */
  public void saveAs();

  /**
   * Sets the line wrap flag.
   *
   * @param value	if true line wrap is enabled
   */
  public void setLineWrap(boolean value);

  /**
   * Returns the current line wrap setting.
   *
   * @return		true if line wrap is enabled
   */
  public boolean getLineWrap();

  /**
   * Sets the current text.
   *
   * @param value	the text
   */
  public void setText(String value);

  /**
   * Returns the current text.
   *
   * @return		the tex
   */
  public String getText();
}
