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
 * RemoveStackTrace.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control.errorpostprocessor;

import adams.core.Utils;
import adams.flow.core.Actor;
import adams.flow.core.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Removes the stacktrace from the message, if possible.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RemoveStackTrace
  extends AbstractErrorPostProcessor {

  private static final long serialVersionUID = -1397592652058333563L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes the stacktrace from the message, if possible.";
  }

  /**
   * Performs the actual post-processing of the error.
   *
   * @param handler the error handler that this call comes from
   * @param source  the source actor where the error originated
   * @param type    the type of error
   * @param msg     the error message
   * @return		the (potentially) updated error message
   */
  @Override
  protected String doPostProcessError(ErrorHandler handler, Actor source, String type, String msg) {
    List<String>	clean;
    String[]		lines;

    clean = new ArrayList<>();
    lines = msg.split("\n");
    for (String line: lines) {
      if (line.contains("\tat adams."))
        break;
      clean.add(line);
    }

    return Utils.flatten(clean, "\n");
  }
}
