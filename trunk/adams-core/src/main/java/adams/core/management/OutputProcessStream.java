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
 * OutputProcessStream.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import java.lang.reflect.Constructor;

/**
 * For capturing the output of stdout or stderr of a running Process and
 * outputting it on stdout or stderr.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OutputProcessStream
  implements Runnable {

  /** whether the output was stopped. */
  protected boolean m_Stopped;

  /** the output printer in use. */
  protected AbstractOutputPrinter m_Printer;

  /**
   * Initializes the object.
   *
   * @param process	the process to print the output from
   * @param cls		the class of 
   * @param stdout	if true then stdout is used, otherwise stderr
   */
  public OutputProcessStream(Process process, Class cls, boolean stdout) {
    super();

    try {
      Constructor constr = cls.getConstructor(new Class[]{Boolean.TYPE, Process.class});
      m_Printer = (AbstractOutputPrinter) constr.newInstance(new Object[]{stdout, process});
    }
    catch (Exception e) {
      System.err.println("Failed to determine constructor/create instance: " + cls.getName());
      e.printStackTrace();
    }
  }

  /**
   * Returns the printer instance in use.
   * 
   * @return		the output printer
   */
  public AbstractOutputPrinter getPrinter() {
    return m_Printer;
  }
  
  /**
   * Prints the output from the process.
   */
  @Override
  public void run() {
    m_Stopped = false;
    while (!m_Stopped && m_Printer.process());
  }

  /**
   * Stops the output.
   */
  public void stop() {
    m_Stopped = true;
  }
}