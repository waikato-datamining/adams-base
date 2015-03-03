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
 * AbstractOutputPrinter.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Ancestor for printer objects for processes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see OutputProcessStream
 */
public abstract class AbstractOutputPrinter {

  /** whether to output to stdout. */
  protected boolean m_StdOut;

  /** the ID of the process. */
  protected String m_ID;

  /** the reader. */
  protected BufferedReader m_Reader;
  
  /**
   * Initializes the printer.
   * 
   * @param stdout	whether to use stdout or stderr
   * @param process	the process to monitor
   */
  public AbstractOutputPrinter(boolean stdout, Process process) {
    initialize(stdout, process);
  }

  /**
   * Initializes the printer.
   * 
   * @param stdout	whether to use stdout or stderr
   * @param process	the process to monitor
   */
  protected void initialize(boolean stdout, Process process) {
    m_StdOut = stdout;
    m_ID     = "" + process.hashCode();
    if (m_StdOut)
      m_Reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    else
      m_Reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
  }
  
  /**
   * Returns the ID in use.
   * 
   * @return		the ID
   */
  public String getID() {
    return m_ID;
  }
  
  /**
   * Returns whether stdout or stderr is used.
   * 
   * @return		true if stdout is used
   */
  public boolean isStdOut() {
    return m_StdOut;
  }

  /**
   * Reads a line from the reader and returns it.
   * 
   * @return		the line read, or null in case of error
   */
  protected String read() {
    try {
      return m_Reader.readLine();
    }
    catch (Exception e) {
      return null;
    }
  }
  
  /**
   * Outputs the specified line.
   * 
   * @param line	the text to output
   */
  protected abstract void output(String line);
  
  /**
   * Reads and outputs (if possible) a line from the reader.
   */
  public boolean process() {
    String	line;
    
    line = read();
    if (line != null) {
      output(line);
      return true;
    }
    
    return false;
  }
  
  /**
   * Returns a short description.
   * 
   * @return		the description
   */
  @Override
  public String toString() {
    return "id=" + m_ID + ", stdout=" + m_StdOut;
  }
}