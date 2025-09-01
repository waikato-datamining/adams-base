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
 * AbstractIMAPOperation.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source.imapsource;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.flow.standalone.IMAPConnection;

/**
 * Ancestor for IMAP operations that generate output.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @param <O> the type of output that is being generated
 */
public abstract class AbstractIMAPOperation<O>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = 3903565012693667706L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the type of output the operation generates.
   *
   * @return		the class
   */
  public abstract Class generates();

  /**
   * Hook method for checks before executing the operation.
   *
   * @param conn	the connection to use
   * @return		the generated output, can be null
   */
  protected String check(IMAPConnection conn) {
    if (conn == null)
      return "No IMAP connection!";
    return null;
  }

  /**
   * Executes the operation and returns the generated output.
   *
   * @param conn	the connection to use
   * @param errors	for collecting errors
   * @return		the generated output, null in case of error or failed check
   */
  protected abstract O doExecute(IMAPConnection conn, MessageCollection errors);

  /**
   * Executes the operation and returns the generated output.
   *
   * @param conn	the connection to use
   * @param errors	for collecting errors
   * @return		the generated output, can be null
   */
  public O execute(IMAPConnection conn, MessageCollection errors) {
    String	msg;

    msg = check(conn);
    if (msg != null) {
      errors.add(msg);
      return null;
    }

    return doExecute(conn, errors);
  }
}
