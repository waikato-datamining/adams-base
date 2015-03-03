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
 * GnumericSpreadSheetWriterTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseDateTime;
import adams.env.Environment;

/**
 * Tests the adams.core.io.GnumericSpreadSheetWriter class. Run from commandline with: <p/>
 * java adams.core.io.GnumericSpreadSheetWriter
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GnumericSpreadSheetWriterTest
  extends AbstractSpreadSheetWriterTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public GnumericSpreadSheetWriterTest(String name) {
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
	"sample.gnumeric",
	"sample2.gnumeric",
	"vote.gnumeric"
    };
  }

  /**
   * Returns the setups to use in the setup tests.
   *
   * @return		the setups
   */
  @Override
  protected SpreadSheetWriter[] getSetups() {
    GnumericSpreadSheetWriter[]	result;

    result = new GnumericSpreadSheetWriter[3];

    result[0] = new GnumericSpreadSheetWriter();
    result[0].setUncompressedOutput(true);
    result[0].setCreationTimestamp(new BaseDateTime(BaseDateTime.INF_PAST));

    result[1] = new GnumericSpreadSheetWriter();
    result[1].setUncompressedOutput(true);
    result[1].setMissingValue("999");
    result[1].setCreationTimestamp(new BaseDateTime(BaseDateTime.INF_PAST));

    result[2] = new GnumericSpreadSheetWriter();
    result[2].setUncompressedOutput(true);
    result[2].setMissingValue("999");
    result[2].setCreationTimestamp(new BaseDateTime(BaseDateTime.INF_PAST));

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
    return new TestSuite(GnumericSpreadSheetWriterTest.class);
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
