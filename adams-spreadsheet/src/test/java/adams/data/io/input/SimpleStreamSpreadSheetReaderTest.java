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
 * SimpleStreamSpreadSheetReaderTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.data.io.input.SimpleStreamSpreadSheetReader class. Run from commandline with: <br><br>
 * java adams.data.io.input.SimpleStreamSpreadSheetReader
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleStreamSpreadSheetReaderTest
  extends AbstractSpreadSheetReaderTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SimpleStreamSpreadSheetReaderTest(String name) {
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
	"labor.ssf",
	"labor.ssf",
	"labor.ssf",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected SpreadSheetReader[] getRegressionSetups() {
    SimpleStreamSpreadSheetReader[]	result;
    
    result    = new SimpleStreamSpreadSheetReader[3];
    
    result[0] = new SimpleStreamSpreadSheetReader();

    result[1] = new SimpleStreamSpreadSheetReader();
    result[1].setCustomColumnHeaders("DURATION,WAGE-INCREASE-FIRST-YEAR,WAGE-INCREASE-SECOND-YEAR,WAGE-INCREASE-THIRD-YEAR,COST-OF-LIVING-ADJUSTMENT,WORKING-HOURS,PENSION,STANDBY-PAY,SHIFT-DIFFERENTIAL,EDUCATION-ALLOWANCE,STATUTORY-HOLIDAYS,VACATION,LONGTERM-DISABILITY-ASSISTANCE,CONTRIBUTION-TO-DENTAL-PLAN,BEREAVEMENT-ASSISTANCE,CONTRIBUTION-TO-HEALTH-PLAN,CLASS\n");

    result[2] = new SimpleStreamSpreadSheetReader();
    result[2].setNoHeader(true);
    result[2].setCustomColumnHeaders("DURATION,WAGE-INCREASE-FIRST-YEAR,WAGE-INCREASE-SECOND-YEAR,WAGE-INCREASE-THIRD-YEAR,COST-OF-LIVING-ADJUSTMENT,WORKING-HOURS,PENSION,STANDBY-PAY,SHIFT-DIFFERENTIAL,EDUCATION-ALLOWANCE,STATUTORY-HOLIDAYS,VACATION,LONGTERM-DISABILITY-ASSISTANCE,CONTRIBUTION-TO-DENTAL-PLAN,BEREAVEMENT-ASSISTANCE,CONTRIBUTION-TO-HEALTH-PLAN,CLASS\n");

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SimpleStreamSpreadSheetReaderTest.class);
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
