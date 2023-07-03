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
 * SimpleFormat.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.simplelogging.format;

import adams.core.QuickInfoSupporter;
import adams.flow.core.FlowContextHandler;

/**
 * Interface for schemes that format logging messages.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface SimpleFormat
  extends FlowContextHandler, QuickInfoSupporter {

  /**
   * Returns whether flow context is really required.
   *
   * @return		true if required
   */
  public boolean requiresFlowContext();

  /**
   * Formats the logging message and returns the updated message.
   *
   * @param msg		the message to format
   * @return		the formatted message
   */
  public String formatMessage(String msg);
}
