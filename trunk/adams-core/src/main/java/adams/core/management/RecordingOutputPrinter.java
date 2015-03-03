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
 * RecordingOutputPrinter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

/**
 * Output printer that records the output.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RecordingOutputPrinter
  extends LoggingObjectOutputPrinter {

  /** the buffer for recording the output. */
  protected StringBuilder m_Buffer;
  
  /**
   * Initializes the printer.
   * 
   * @param stdout	whether to use stdout or stderr
   * @param process	the process to monitor
   */
  public RecordingOutputPrinter(boolean stdout, Process process) {
    super(stdout, process);
  }

  /**
   * Initializes the printer.
   * 
   * @param stdout	whether to use stdout or stderr
   * @param process	the process to monitor
   */
  @Override
  protected void initialize(boolean stdout, Process process) {
    m_Buffer = new StringBuilder();
    super.initialize(stdout, process);
  }
  
  /**
   * Outputs the specified line.
   * 
   * @param line	the text to output
   */
  @Override
  protected void output(String line) {
    super.output(line);
    m_Buffer.append(line + "\n");
  }
  
  /**
   * Returns the recorded output.
   * 
   * @return		the output
   */
  public String getRecording() {
    return m_Buffer.toString();
  }
}
