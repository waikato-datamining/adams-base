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
 * TimeseriesSAXTest.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.Constants;
import adams.data.DateFormatString;
import adams.data.timeseries.Timeseries;
import adams.env.Environment;
import adams.test.TimeseriesTestHelper;

/**
 * Test class for the TimeseriesSAX filter. Run from the command line with: <br><br>
 * java adams.data.filter.TimeseriesSAXTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7892 $
 */
public class TimeseriesSAXTest
  extends AbstractTimeseriesFilterTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public TimeseriesSAXTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    ((TimeseriesTestHelper) m_TestHelper).setRegressionTimestampFormatWrite(
	new DateFormatString(Constants.TIMESTAMP_FORMAT));
  }
  
  /**
   * Returns the configured filter.
   *
   * @return		the filter
   */
  public AbstractFilter<Timeseries> getFilter() {
    return new TimeseriesSAX();
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
	"wine_mod.sts",
	"wine_mod.sts",
	"wine_mod.sts",
	"wine_mod.sts",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractFilter[] getRegressionSetups() {
    MultiFilter[]	result;
    TimeseriesRowNorm row;
    TimeseriesSAX sax;

    result = new MultiFilter[4];

    row = new TimeseriesRowNorm();
    
    result[0] = new MultiFilter();
    sax       = new TimeseriesSAX();
    result[0].setSubFilters(new AbstractFilter[]{
	row,
	sax
    });

    result[1] = new MultiFilter();
    sax       = new TimeseriesSAX();
    sax.setNumBins(20);
    sax.setNumWindows(20);
    result[1].setSubFilters(new AbstractFilter[]{
	row,
	sax
    });

    result[2] = new MultiFilter();
    sax       = new TimeseriesSAX();
    sax.setNumBins(5);
    sax.setNumWindows(5);
    result[2].setSubFilters(new AbstractFilter[]{
	row,
	sax
    });
    
    result[3] = new MultiFilter();
    sax       = new TimeseriesSAX();
    sax.setOutputLabels(false);
    result[3].setSubFilters(new AbstractFilter[]{
	row,
	sax
    });

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(TimeseriesSAXTest.class);
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
