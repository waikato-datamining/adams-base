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
 * HasCellTest.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetRowIndex;
import adams.env.Environment;
import adams.flow.core.Actor;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the 'HasCell' boolean condition.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class HasCellTest
  extends AbstractBooleanConditionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public HasCellTest(String name) {
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

    m_TestHelper.copyResourceToTmp("bolts.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.csv");

    super.tearDown();
  }

  /**
   * Returns the owning actors to use in the regression test (one per regression setup).
   *
   * @return		the owners (not all conditions might need owners)
   */
  @Override
  protected Actor[] getRegressionOwners() {
    return new Actor[]{
      null,
      null,
      null,
      null,
      null,
    };
  }

  /**
   * Returns the input data to use in the regression test (one per regression setup).
   *
   * @return		the input data
   */
  @Override
  protected Object[] getRegressionInputs() {
    CsvSpreadSheetReader	reader;
    SpreadSheet			sheet;
    TmpFile			file;

    file = new TmpFile("bolts.csv");
    try {
      reader = new CsvSpreadSheetReader();
      sheet  = reader.read(file);
      return new SpreadSheet[]{
	sheet,
	sheet,
	sheet,
	sheet,
	sheet,
      };
    }
    catch (Exception e) {
      fail("Failed to load file: " + file);
      return null;
    }
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBooleanCondition[] getRegressionSetups() {
    HasCell[]	result;

    result    = new HasCell[5];
    result[0] = new HasCell();
    result[1] = new HasCell();
    result[1].setRow(new SpreadSheetRowIndex("last"));
    result[1].setColumn(new SpreadSheetColumnIndex("last"));
    result[2] = new HasCell();
    result[2].setRow(new SpreadSheetRowIndex("10"));
    result[2].setColumn(new SpreadSheetColumnIndex("100"));
    result[3] = new HasCell();
    result[3].setRow(new SpreadSheetRowIndex("1"));
    result[3].setColumn(new SpreadSheetColumnIndex("1"));
    result[3].setValue("25");
    result[4] = new HasCell();
    result[4].setRow(new SpreadSheetRowIndex("1"));
    result[4].setColumn(new SpreadSheetColumnIndex("1"));
    result[4].setValue("2");

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(HasCellTest.class);
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
