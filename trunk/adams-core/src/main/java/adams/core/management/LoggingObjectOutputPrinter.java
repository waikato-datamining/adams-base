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
 * LoggingLevel.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import adams.core.logging.LoggingObject;

/**
 * Default printer for processes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see OutputProcessStream
 */
public class LoggingObjectOutputPrinter 
  extends DefaultOutputPrinter 
  implements LoggingObjectOwner {
  
  /** the owning {@link LoggingObject}. */
  protected LoggingObject m_Owner;
  
  /**
   * Initializes the printer.
   * 
   * @param stdout	whether to use stdout or stderr
   * @param process	the process to monitor
   */
  public LoggingObjectOutputPrinter(boolean stdout, Process process) {
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
    m_Owner = null;
    super.initialize(stdout, process);
  }
  
  /**
   * Sets the owning {@link LoggingObject}, i.e., the object which stdout
   * and stderr objects are used for printing
   */
  public void setOwner(LoggingObject value) {
    m_Owner = value;
  }
  
  /**
   * Returns the owner.
   * 
   * @return		the owner, null if none set
   */
  public LoggingObject getOwner() {
    return m_Owner;
  }
  
  /**
   * Outputs the specified line.
   * 
   * @param line	the text to output
   */
  @Override
  protected void output(String line) {
    if (m_Owner == null)
      super.output(line);
    else
      m_Owner.getLogger().info(line);
  }
  
  /**
   * Returns a short description.
   * 
   * @return		the description
   */
  @Override
  public String toString() {
    return super.toString() + ", owner=" + m_Owner;
  }
}