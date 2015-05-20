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
 * ValuesTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.timeseries;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.io.input.AbstractTimeseriesReader;
import adams.data.io.input.SimpleTimeseriesReader;
import adams.env.Environment;

/**
 * Test class for the Values feature generator. Run from the command line with: <br><br>
 * java adams.data.timeseries.flattener.ValuesTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ValuesTest
  extends AbstractTimeseriesFeatureGeneratorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ValuesTest(String name) {
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
	"dummy.sts",
	"dummy.sts",
    };
  }

  /**
   * Returns the timeseries filereaders to use in the regression test for 
   * loading the timeseries files.
   *
   * @return		the readers
   */
  @Override
  protected AbstractTimeseriesReader[] getRegressionInputReaders() {
    return new AbstractTimeseriesReader[]{
	new SimpleTimeseriesReader(),
	new SimpleTimeseriesReader(),
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractTimeseriesFeatureGenerator[] getRegressionSetups() {
    Values[]	result;
    
    result = new Values[2];
    result[0] = new Values();
    result[1] = new Values();
    result[1].setAddTimestamp(true);
    
    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(ValuesTest.class);
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
