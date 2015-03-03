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
 * TimestampCheckTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.outlier;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.Index;
import adams.core.base.BaseDateTime;
import adams.data.outlier.TimestampCheck.TimestampCondition;
import adams.env.Environment;

/**
 * Test class for the TimestampCheck outlier detector.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class TimestampCheckTest
  extends AbstractTimeseriesOutlierDetectorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public TimestampCheckTest(String name) {
    super(name);
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
  protected AbstractOutlierDetector[] getRegressionSetups() {
    TimestampCheck[]		result;
    
    result    = new TimestampCheck[3];
    
    result[0] = new TimestampCheck();
    
    result[1] = new TimestampCheck();
    result[1].setIndex(new Index(Index.FIRST));
    result[1].setCondition(TimestampCondition.BEFORE);
    result[1].setTimestamp(new BaseDateTime("1980-01-01 00:00:00"));

    result[2] = new TimestampCheck();
    result[2].setIndex(new Index(Index.LAST));
    result[2].setCondition(TimestampCondition.AFTER);
    result[2].setTimestamp(new BaseDateTime("2000-01-01 00:00:00"));
    
    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(TimestampCheckTest.class);
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
