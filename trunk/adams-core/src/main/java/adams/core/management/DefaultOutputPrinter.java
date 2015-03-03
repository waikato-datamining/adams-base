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
 * DefaultOutputPrinter.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

/**
 * Default printer for processes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see OutputProcessStream
 */
public class DefaultOutputPrinter 
  extends AbstractOutputPrinter {
  
  /**
   * Initializes the printer.
   * 
   * @param stdout	whether to use stdout or stderr
   * @param process	the process to monitor
   */
  public DefaultOutputPrinter(boolean stdout, Process process) {
    super(stdout, process);
  }

  /**
   * Outputs the specified line.
   * 
   * @param line	the text to output
   */
  @Override
  protected void output(String line) {
    if (m_StdOut)
      System.out.println(line);
    else
      System.err.println(line);
  }
}