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
 * SimpleOutput.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.simplelogging.output;

import adams.core.QuickInfoSupporter;
import adams.flow.core.FlowContextHandler;

/**
 * Interface for schemes that output logging messages.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface SimpleOutput
  extends FlowContextHandler, QuickInfoSupporter {

  /**
   * Returns whether flow context is really required.
   *
   * @return		true if required
   */
  public boolean requiresFlowContext();

  /**
   * Logs the (formatted) logging message.
   *
   * @param msg		the message to log
   * @return		null if successful, otherwise error message
   */
  public String logMessage(String msg);
}
