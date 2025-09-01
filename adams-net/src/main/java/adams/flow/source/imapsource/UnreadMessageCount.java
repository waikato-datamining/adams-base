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
 * ListFolders.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source.imapsource;

import adams.core.MessageCollection;
import adams.flow.standalone.IMAPConnection;

/**
 * Returns the number of unread messages.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class UnreadMessageCount
  extends AbstractIMAPOperation<Integer> {

  private static final long serialVersionUID = -8935273725300311491L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the number of unread messages.";
  }

  /**
   * Returns the type of output the operation generates.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return Integer.class;
  }

  /**
   * Executes the operation and returns the generated output.
   *
   * @param conn   the connection to use
   * @param errors for collecting errors
   * @return the generated output, null in case of error or failed check
   */
  @Override
  protected Integer doExecute(IMAPConnection conn, MessageCollection errors) {
    return conn.getImapSession().getUnreadMessageCount();
  }
}
