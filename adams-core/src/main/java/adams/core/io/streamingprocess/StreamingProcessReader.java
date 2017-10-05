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
 * StreamingProcessReader.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.io.streamingprocess;

import adams.flow.source.Command;
import com.github.fracpete.processoutput4j.reader.AbstractProcessReader;

/**
 * Forwards the output from the process to the owning {@link Command} actor,
 * if appropriate.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class StreamingProcessReader
  extends AbstractProcessReader {

  /** the owner. */
  protected StreamingProcessOwner m_Owner;

  /** whether to forward the output to the owner. */
  protected boolean m_Forward;

  /**
   * Initializes the reader.
   *
   * @param owner the owning actor
   * @param process the process to monitor
   * @param stdout  whether to read stdout or stderr
   */
  public StreamingProcessReader(StreamingProcessOwner owner, Process process, boolean stdout) {
    super(process, stdout);
    m_Owner = owner;
    m_Forward = (stdout && (m_Owner.getOutputType() == OutputType.STDOUT))
	|| (!stdout && (m_Owner.getOutputType() == OutputType.STDERR))
	|| (m_Owner.getOutputType() == OutputType.BOTH);
  }

  /**
   * For processing the line read from stdout/stderr.
   *
   * @param line	the output line
   */
  @Override
  protected void process(String line) {
    if (m_Forward)
      m_Owner.add(line, isStdout());
  }
}
