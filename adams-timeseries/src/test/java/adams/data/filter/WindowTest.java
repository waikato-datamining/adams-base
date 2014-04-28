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
 * WindowTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseDateTime;
import adams.data.timeseries.Timeseries;
import adams.env.Environment;

/**
 * Test class for the Window filter. Run from the command line with: <p/>
 * java adams.data.filter.WindowTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WindowTest
  extends AbstractTimeseriesFilterTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public WindowTest(String name) {
    super(name);
  }

  /**
   * Returns the configured filter.
   *
   * @return		the filter
   */
  public AbstractFilter<Timeseries> getFilter() {
    return new Window();
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"wine.sts",
	"wine.sts",
	"wine.sts",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractFilter[] getRegressionSetups() {
    Window[]	result;

    result = new Window[3];

    result[0] = new Window();

    result[1] = new Window();
    result[1].setStart(new BaseDateTime("1985-01-01 00:00:00"));
    result[1].setEnd(new BaseDateTime("1990-12-31 23:59:59"));

    result[2] = new Window();
    result[2].setStart(new BaseDateTime("1985-01-01 00:00:00"));
    result[2].setEnd(new BaseDateTime("1990-12-31 23:59:59"));
    result[2].setInvert(true);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(WindowTest.class);
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
