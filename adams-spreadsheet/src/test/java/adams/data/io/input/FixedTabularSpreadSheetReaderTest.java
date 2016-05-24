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
 * FixedTabularSpreadSheetReaderTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.core.base.BaseInteger;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.data.io.input.FixedTabularSpreadSheetReader class. Run from commandline with: <br><br>
 * java adams.data.io.input.FixedTabularSpreadSheetReader
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedTabularSpreadSheetReaderTest
  extends AbstractSpreadSheetReaderTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public FixedTabularSpreadSheetReaderTest(String name) {
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
	"precip_header.2014",
	"precip_header.2014",
	"precip.2014",
	"precip.2014",
	"precip_header_prolog.2014",
	"precip.2014",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected SpreadSheetReader[] getRegressionSetups() {
    FixedTabularSpreadSheetReader[]	result;
    BaseInteger[]			colWidths;
    int					i;

    colWidths = new BaseInteger[14];
    for (i = 0; i < colWidths.length; i++)
      colWidths[i] = new BaseInteger(8);
    
    result    = new FixedTabularSpreadSheetReader[6];
    
    result[0] = new FixedTabularSpreadSheetReader();
    result[0].setColumnWidth(colWidths);

    result[1] = new FixedTabularSpreadSheetReader();
    result[1].setColumnWidth(colWidths);
    result[1].setCustomColumnHeaders("Longitude,Latitude,January,February,March,April,May,June,July,August,September,October,November,December");

    result[2] = new FixedTabularSpreadSheetReader();
    result[2].setColumnWidth(colWidths);
    result[2].setNoHeader(true);

    result[3] = new FixedTabularSpreadSheetReader();
    result[3].setColumnWidth(colWidths);
    result[3].setNoHeader(true);
    result[3].setCustomColumnHeaders("Longitude,Latitude,January,February,March,April,May,June,July,August,September,October,November,December");

    result[4] = new FixedTabularSpreadSheetReader();
    result[4].setColumnWidth(colWidths);
    result[4].setFirstRow(3);
    result[4].setNumRows(3);

    result[5] = new FixedTabularSpreadSheetReader();
    result[5].setColumnWidth(colWidths);
    result[5].setNoHeader(true);
    result[5].setFirstRow(3);
    result[5].setNumRows(3);

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(FixedTabularSpreadSheetReaderTest.class);
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
