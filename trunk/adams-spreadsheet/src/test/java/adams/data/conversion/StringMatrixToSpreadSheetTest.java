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
 * StringMatrixToSpreadSheetTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import adams.test.TmpFile;

/**
 * Tests the StringMatrixToSpreadSheet conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6301 $
 */
public class StringMatrixToSpreadSheetTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public StringMatrixToSpreadSheetTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("labor.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("labor.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    String[][][]	result;
    TmpFile		file;
    SpreadSheet		data;
    int			i;
    int			n;
    Row			row;

    file = new TmpFile("labor.csv");
    try {
      data   = new CsvSpreadSheetReader().read(file);
      result = new String[2][data.getRowCount()][data.getColumnCount()];
      for (i = 0; i < data.getRowCount(); i++) {
	row = data.getRow(i);
	for (n = 0; n < data.getColumnCount(); n++) {
	  result[0][i][n] = row.getCell(n).getContent();
	  result[1][i][n] = row.getCell(n).getContent();
	}
      }
    }
    catch (Exception e) {
      result = new String[0][][];
      fail("Failed to load data from '" + file + "': " + e);
    }

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    StringMatrixToSpreadSheet[]	result;

    result = new StringMatrixToSpreadSheet[2];
    result[0] = new StringMatrixToSpreadSheet();
    result[1] = new StringMatrixToSpreadSheet();
    result[1].setForceString(true);

    return result;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(StringMatrixToSpreadSheetTest.class);
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
