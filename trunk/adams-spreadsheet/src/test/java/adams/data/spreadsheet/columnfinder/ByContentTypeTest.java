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
 * ByContentTypeTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.columnfinder;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.spreadsheet.Cell.ContentType;
import adams.env.Environment;

/**
 * Test class for the ByContentType finder.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByContentTypeTest
  extends AbstractColumnFinderTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ByContentTypeTest(String name) {
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
	"labor.csv",
	"labor.csv",
	"labor.csv",
	"labor.csv",
	"labor.csv"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected ColumnFinder[] getRegressionSetups() {
    ByContentType[]	result;

    result = new ByContentType[5];

    result[0] = new ByContentType();
    result[1] = new ByContentType();
    result[1].setContentTypes(new ContentType[]{ContentType.DOUBLE});
    result[2] = new ByContentType();
    result[2].setContentTypes(new ContentType[]{ContentType.LONG});
    result[3] = new ByContentType();
    result[3].setContentTypes(new ContentType[]{ContentType.STRING});
    result[4] = new ByContentType();
    result[4].setContentTypes(new ContentType[]{ContentType.LONG, ContentType.DOUBLE});

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(ByContentTypeTest.class);
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
