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
 * GnuplotSpreadSheetWriterTest.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.Range;
import adams.env.Environment;

/**
 * Tests the adams.core.io.GnuplotSpreadSheetWriter class. Run from commandline with: <p/>
 * java adams.core.io.GnuplotSpreadSheetWriter
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GnuplotSpreadSheetWriterTest
  extends AbstractSpreadSheetWriterTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public GnuplotSpreadSheetWriterTest(String name) {
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
	"bolts.csv",
	"bolts.csv",
	"bolts.csv",
	"sample.csv"
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
	"bolts-1-out.csv",
	"bolts-2-out.csv",
	"bolts-3-out.csv",
	"sample-out.csv"
    };
  }

  /**
   * Returns the setups to use in the setup tests.
   *
   * @return		the setups
   */
  @Override
  protected SpreadSheetWriter[] getSetups() {
    GnuplotSpreadSheetWriter[]	result;

    result = new GnuplotSpreadSheetWriter[4];
    result[0] = new GnuplotSpreadSheetWriter();
    result[1] = new GnuplotSpreadSheetWriter();
    result[1].setColumns(new Range("1-3"));
    result[2] = new GnuplotSpreadSheetWriter();
    result[2].setColumns(new Range("1-3"));
    result[2].setInvert(true);
    result[3] = new GnuplotSpreadSheetWriter();

    return result;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the line indices
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[]{0,1,2};
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
    return new TestSuite(GnuplotSpreadSheetWriterTest.class);
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
