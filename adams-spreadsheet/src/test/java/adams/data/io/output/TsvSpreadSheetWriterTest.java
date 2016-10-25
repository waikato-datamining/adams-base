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
 * TsvSpreadSheetWriterTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.data.io.output.TsvSpreadSheetWriter class. Run from commandline with: <br><br>
 * java adams.data.io.output.TsvSpreadSheetWriter
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TsvSpreadSheetWriterTest
  extends AbstractSpreadSheetWriterTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TsvSpreadSheetWriterTest(String name) {
    super(name);
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the setup tests.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getInputFiles() {
    return new String[]{
	"sample.csv",
	"sample2.csv",
	"vote.csv"
    };
  }

  /**
   * Returns the filenames (without path) of the output data files to use
   * in the setup tests.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getOutputFiles() {
    return new String[]{
	"sample.tsv",
	"sample2.tsv",
	"vote.tsv"
    };
  }

  /**
   * Returns the setups to use in the setup tests.
   *
   * @return		the setups
   */
  @Override
  protected SpreadSheetWriter[] getSetups() {
    TsvSpreadSheetWriter[]	result;

    result = new TsvSpreadSheetWriter[3];

    result[0] = new TsvSpreadSheetWriter();

    result[1] = new TsvSpreadSheetWriter();
    result[1].setMissingValue("999");

    result[2] = new TsvSpreadSheetWriter();
    result[2].setMissingValue("999");

    return result;
 }

  /**
   * Returns whether a regression can be run.
   *
   * @return		always true
   */
  @Override
  protected boolean hasRegressionTest() {
    return true;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(TsvSpreadSheetWriterTest.class);
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
