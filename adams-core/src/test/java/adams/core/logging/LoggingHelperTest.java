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
 * LoggingHelperTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.logging;

import java.util.logging.Level;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the {@link LoggingHelper} class.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LoggingHelperTest
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public LoggingHelperTest(String name) {
    super(name);
  }

  /**
   * Tests the {@link LoggingHelper#isAtLeast(Level, Level)} method.
   */
  public void testIsAtLeast() {
    assertTrue(LoggingHelper.isAtLeast(Level.INFO, Level.INFO));
    assertTrue(LoggingHelper.isAtLeast(Level.INFO, Level.WARNING));
    assertFalse(LoggingHelper.isAtLeast(Level.INFO, Level.FINE));
  }
  
  /**
   * Tests the {@link LoggingHelper#isAtMost(Level, Level)} method.
   */
  public void testIsAtMost() {
    assertTrue(LoggingHelper.isAtMost(Level.INFO, Level.INFO));
    assertFalse(LoggingHelper.isAtMost(Level.INFO, Level.WARNING));
    assertTrue(LoggingHelper.isAtMost(Level.INFO, Level.FINE));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(LoggingHelperTest.class);
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
