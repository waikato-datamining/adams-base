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
 * SpreadSheetStringColumnToDateTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.Range;
import adams.data.DateFormatString;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.env.Environment;
import adams.test.TmpFile;

/**
 * Tests the SpreadSheetStringColumnToDate conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetStringColumnToDateTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public SpreadSheetStringColumnToDateTest(String name) {
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

    m_TestHelper.copyResourceToTmp("date.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("date.csv");
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
    SpreadSheet[]		result;
    TmpFile			file;
    CsvSpreadSheetReader	reader;

    file = new TmpFile("date.csv");
    try {
      reader    = new CsvSpreadSheetReader();
      reader.setTextColumns(new Range(Range.ALL));
      result    = new SpreadSheet[1];
      result[0] = reader.read(file);
    }
    catch (Exception e) {
      result = new SpreadSheet[0];
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
    SpreadSheetStringColumnToDate[]	result;

    result    = new SpreadSheetStringColumnToDate[2];
    result[0] = new SpreadSheetStringColumnToDate();
    result[0].setColumn(new SpreadSheetColumnIndex("2"));
    result[0].setFormat(new DateFormatString("dd/MM/yyyy"));
    result[1] = new SpreadSheetStringColumnToDate();
    result[1].setColumn(new SpreadSheetColumnIndex("3"));
    result[1].setFormat(new DateFormatString("MM/dd/yyyy"));

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
    return new TestSuite(SpreadSheetStringColumnToDateTest.class);
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
