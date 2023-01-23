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
 * AsyncCapableExternalCommandOutputProcessor.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.command;

import com.github.fracpete.processoutput4j.core.StreamingProcessOutputType;
import com.github.fracpete.processoutput4j.core.StreamingProcessOwner;

/**
 * For collecting the output of the asynchronous docker command.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class AsyncCapableExternalCommandOutputProcessor
  implements StreamingProcessOwner {

  /** the owning command. */
  protected AsyncCapableExternalCommand m_Owner;

  /**
   * Initializes the process output manager.
   *
   * @param owner	the owning command
   */
  public AsyncCapableExternalCommandOutputProcessor(AsyncCapableExternalCommand owner) {
    m_Owner = owner;
  }

  /**
   * Returns what output from the process to forward.
   *
   * @return 		the output type
   */
  public StreamingProcessOutputType getOutputType() {
    return StreamingProcessOutputType.BOTH;
  }

  /**
   * Processes the incoming line.
   *
   * @param line	the line to process
   * @param stdout	whether stdout or stderr
   */
  public void processOutput(String line, boolean stdout) {
    if (stdout)
      m_Owner.addStdOut(line);
    else
      m_Owner.addStdErr(line);
  }
}
