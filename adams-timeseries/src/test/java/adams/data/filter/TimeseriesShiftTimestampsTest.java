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
 * TimeseriesShiftTimestampsTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import adams.core.base.BaseDateTime;
import adams.data.filter.TimeseriesShiftTimestamps.TimestampSource;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.timeseries.Timeseries;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test class for the TimeseriesShiftTimestamps filter. Run from the command line with: <p/>
 * java adams.data.filter.TimeseriesShiftTimestampsTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7881 $
 */
public class TimeseriesShiftTimestampsTest
  extends AbstractTimeseriesFilterTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public TimeseriesShiftTimestampsTest(String name) {
    super(name);
  }

  /**
   * Returns the configured filter.
   *
   * @return		the filter
   */
  public AbstractFilter<Timeseries> getFilter() {
    return new TimeseriesShiftTimestamps();
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
	"wine_report.sts",
	"wine_report.sts",
	"wine_report.sts",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractFilter[] getRegressionSetups() {
    TimeseriesShiftTimestamps[]	result;

    result = new TimeseriesShiftTimestamps[3];

    result[0] = new TimeseriesShiftTimestamps();
    result[0].setNewTimestamp(new BaseDateTime("2000-01-01 00:00:00"));
    result[0].setSuppliedTimestamp(new BaseDateTime("2000-01-01 00:00:00"));

    result[1] = new TimeseriesShiftTimestamps();
    result[1].setSource(TimestampSource.SUPPLIED_TIMESTAMP);
    result[1].setSuppliedTimestamp(new BaseDateTime("2013-04-03 00:00:00"));
    result[1].setNewTimestamp(new BaseDateTime("2000-01-01 00:00:00"));

    result[2] = new TimeseriesShiftTimestamps();
    result[2].setSource(TimestampSource.REPORT_FIELD);
    result[2].setCustomFormat("yyyy-MM-dd");
    result[2].setReportField(new Field("loadvesseldate", DataType.STRING));
    result[2].setNewTimestamp(new BaseDateTime("2000-01-01 00:00:00"));

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(TimeseriesShiftTimestampsTest.class);
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
