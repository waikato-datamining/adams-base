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
 * NoFormat.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.simplelogging.format;

/**
 * Applies no formatting, just forwards the message as is.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class NoFormat
  extends AbstractSimpleFormat {

  private static final long serialVersionUID = -7259936684808701353L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies no formatting, just forwards the message as is.";
  }

  /**
   * Formats the logging message and returns the updated message.
   *
   * @param msg the message to format
   * @return the formatted message
   */
  @Override
  protected String doFormatMessage(String msg) {
    return msg;
  }
}
