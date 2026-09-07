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
 * LogEntryToStringTest.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.Properties;
import adams.db.LogEntry;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Date;

/**
 * Tests the LogEntryToString conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class LogEntryToStringTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public LogEntryToStringTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    LogEntry[]	result;
    Properties	props;

    result = new LogEntry[1];
    result[0] = new LogEntry();
    result[0].setHost("localhost");
    result[0].setIP("127.0.0.1");
    result[0].setGeneration(new Date());  // gets removed from regression output
    result[0].setSource("this is the source");
    result[0].setType("rejection");
    result[0].setStatus(LogEntry.STATUS_NEW);
    props = new Properties();
    props.setProperty("Errors", "Something went wrong");
    result[0].setMessage(props);

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    return new Conversion[]{
      new LogEntryToString(),
    };
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[]{3};
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(LogEntryToStringTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
