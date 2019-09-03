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
 * TimeIt.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core;

import java.io.Serializable;

/**
 * Helper class for timing code execution.
 * <br>
 * For example, testing the time it takes reading a CSV file:
 *
 * <pre>
 *   final String filename = "/some/file.csv";
 *   new TimeIt("CSV: ") {
 *     protected void doRun() {
 *	CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
 *	reader.read(filename);
 *     }
 *   }.run();
 * </pre>
 *
 * Or using the static "timeIt" method with a Runnable:
 * <pre>
 *   TimeIt.timeIt("CSV: ", () -> {
 *     CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
 *     reader.read(filename);
 *   });
 * </pre>
 *
 * Will output something like this:
 * <pre>
 * CSV: 00:00:25.929
 * </pre>
 *
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@SuppressWarnings("serial")
public abstract class TimeIt
  implements Serializable {

  private static final long serialVersionUID = -8264923459795817609L;

  /** the prefix for logging. */
  protected String m_Prefix;

  /**
   * Initializes with no output on commandline.
   */
  public TimeIt() {
    this(null);
  }

  /**
   * Initializes with the specified prefix for output.
   *
   * @param prefix	the prefix to use, use null to suppress output on commandline
   */
  public TimeIt(String prefix) {
    m_Prefix = prefix;
  }

  /**
   * Outputs the message.
   *
   * @param msg		the message to output
   */
  protected void output(String msg) {
    if (m_Prefix != null) {
      if (m_Prefix.isEmpty())
	System.out.println(msg);
      else
	System.out.println(m_Prefix + msg);
    }
  }

  /**
   * The actual code to run.
   */
  protected abstract void doRun();

  /**
   * Times the execution and outputs the elapsed time.
   *
   * @return		the duration in msec
   */
  public long run() {
    long 	result;
    long  	start;

    start = System.currentTimeMillis();
    try {
      doRun();
    }
    catch (Throwable t) {
      if (!m_Prefix.isEmpty())
	System.err.println(m_Prefix);
      t.printStackTrace();
    }
    result = System.currentTimeMillis() - start;
    output(DateUtils.msecToString(result));
    return result;
  }

  /**
   * Executes the runnable. Does not output anything on commandline.
   *
   * @param r		the runnable
   * @return		the duration in msec
   */
  public static long timeIt(Runnable r) {
    return timeIt(null, r);
  }

  /**
   * Executes the runnable.
   *
   * @param prefix	the prefix to use for output, use null to suppress output on commandline
   * @param r		the runnable
   * @return		the duration in msec
   */
  public static long timeIt(String prefix, Runnable r) {
    return new TimeIt(prefix) {
      @Override
      protected void doRun() {
	r.run();
      }
    }.run();
  }
}
