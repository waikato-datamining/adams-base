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
 * SpreadSheetToKMLTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.GPSDecimalDegrees;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.test.TmpFile;

/**
 * Tests the SpreadSheetToKML conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7452 $
 */
public class SpreadSheetToKMLTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public SpreadSheetToKMLTest(String name) {
    super(name);
  }
  
  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    m_TestHelper.copyResourceToTmp("gps.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("gps.csv");
    super.tearDown();
  }
  
  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    CsvSpreadSheetReader		reader;
    SpreadSheet				sheet;
    GPSDecimalDegrees			degrees;
    SpreadSheetStringColumnToObject 	conv;
    
    reader  = new CsvSpreadSheetReader();
    sheet   = reader.read(new TmpFile("gps.csv"));
    degrees = new GPSDecimalDegrees();
    conv    = new SpreadSheetStringColumnToObject();
    conv.setColumn(new SpreadSheetColumnIndex("gps"));
    conv.setHandler(degrees);
    conv.setInput(sheet);
    conv.convert();
    sheet   = (SpreadSheet) conv.getOutput();
    conv.cleanUp();
    
    return new SpreadSheet[]{
	sheet,
	sheet,
    };
  }
  
  /* (non-Javadoc)
   * @see adams.data.conversion.AbstractConversionTestCase#toString(java.lang.Object)
   */
  @Override
  protected String toString(Object data) {
    String	result;
    DOMToString	conv;
    
    conv = new DOMToString();
    conv.setInput(data);
    conv.convert();
    result = (String) conv.getOutput();
    conv.cleanUp();
    
    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    SpreadSheetToKML[]	result;
    
    result    = new SpreadSheetToKML[2];
    
    result[0] = new SpreadSheetToKML();
    result[0].setColumnGPS(new SpreadSheetColumnIndex("gps"));

    result[1] = new SpreadSheetToKML();
    result[1].setColumnGPS(new SpreadSheetColumnIndex("gps"));
    result[1].setColumnID(new SpreadSheetColumnIndex("id"));
    result[1].setColumnName(new SpreadSheetColumnIndex("name"));
    result[1].setColumnElevation(new SpreadSheetColumnIndex("elevation"));
    
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
}
