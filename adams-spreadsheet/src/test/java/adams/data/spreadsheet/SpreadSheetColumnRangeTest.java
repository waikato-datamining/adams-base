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
 * SpreadSheetColumnRangeTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import adams.core.Range;
import adams.core.RangeTest;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.env.Environment;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.FileNotFoundException;

/**
 * Tests the adams.core.SpreadSheetColumnRange class. Run from commandline with: <br><br>
 * java adams.core.SpreadSheetColumnRangeTest
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetColumnRangeTest
  extends RangeTest {

  /** the spreadsheet to use for testing. */
  protected SpreadSheet m_Sheet;
  
  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetColumnRangeTest(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/core/data");
  }
  
  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    String	filename;
    
    super.setUp();
    
    filename = "simple.csv";
    m_TestHelper.copyResourceToTmp(filename);
    m_Sheet = new CsvSpreadSheetReader().read(new TmpFile(filename));
    if (m_Sheet == null)
      throw new FileNotFoundException("Test file '" + filename + "' not found?");
  }
  
  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("simple.csv");
    
    super.tearDown();
  }

  /**
   * Returns a new Range instance.
   * 
   * @param index	the index to use, null for default constructor
   * @param max		the maximum to use
   * @return		the new instance
   */
  @Override
  protected Range newRange(String index, int max) {
    if (index == null)
      return new SpreadSheetColumnRange();
    else
      return new SpreadSheetColumnRange(index, max);
  }
  
  /**
   * Tests the index using column names.
   */
  public void testColumnNames() {
    SpreadSheetColumnRange range;

    range = new SpreadSheetColumnRange(Range.ALL);
    assertEquals("should contain no indices", 0, range.getIntIndices().length);
    assertEquals("should be same", Range.ALL, range.getRange());
    
    range.setSpreadSheet(null);
    range.setRange("blah");
    assertEquals("should contain no indices", 0, range.getIntIndices().length);
    assertEquals("should be same", "blah", range.getRange());
    
    range.setSpreadSheet(m_Sheet);
    range.setRange("Field");
    assertEquals("should be valid", 1, range.getIntIndices().length);
    assertEquals("should be valid", 0, range.getIntIndices()[0]);
    assertEquals("should be same", "Field", range.getRange());

    range.setSpreadSheet(m_Sheet);
    range.setRange("\"Field\"");
    assertEquals("should be valid", 1, range.getIntIndices().length);
    assertEquals("should be valid", 0, range.getIntIndices()[0]);
    assertEquals("should be same", "\"Field\"", range.getRange());

    range.setSpreadSheet(m_Sheet);
    range.setRange("field");
    assertEquals("should not be valid", 0, range.getIntIndices().length);
    assertEquals("should not be same", "field", range.getRange());
    
    range.setSpreadSheet(m_Sheet);
    range.setRange("Feild");
    assertEquals("should be invalid", 0, range.getIntIndices().length);
    assertEquals("should be same", "Feild", range.getRange());
    
    range.setSpreadSheet(null);
    range.setRange("Field-Value");
    assertEquals("should be invalid", 0, range.getIntIndices().length);
    assertEquals("should be same", "Field-Value", range.getRange());

    range.setSpreadSheet(null);
    range.setRange("\"Field\"-\"Value\"");
    assertEquals("should be invalid", 0, range.getIntIndices().length);
    assertEquals("should be same", "\"Field\"-\"Value\"", range.getRange());

    range.setSpreadSheet(m_Sheet);
    range.setRange("Field-Value");
    assertEquals("should be valid", 3, range.getIntIndices().length);
    assertEquals("should be same", "Field-Value", range.getRange());
    assertEquals("should be valid", 0, range.getIntIndices()[0]);
    assertEquals("should be valid", 1, range.getIntIndices()[1]);
    assertEquals("should be valid", 2, range.getIntIndices()[2]);

    range.setSpreadSheet(m_Sheet);
    range.setRange("\"Field\"-\"Value\"");
    assertEquals("should be valid", 3, range.getIntIndices().length);
    assertEquals("should be same", "\"Field\"-\"Value\"", range.getRange());
    assertEquals("should be valid", 0, range.getIntIndices()[0]);
    assertEquals("should be valid", 1, range.getIntIndices()[1]);
    assertEquals("should be valid", 2, range.getIntIndices()[2]);
  }
  
  /**
   * Tests the index using special column names that contain commas or hyphens.
   */
  public void testSpecialColumnNames() {
    SpreadSheetColumnRange range = new SpreadSheetColumnRange();
    
    range.setSpreadSheet(m_Sheet);
    range.setRange("\"Hyphen-ated\"");
    assertEquals("should be valid", 1, range.getIntIndices().length);
    assertEquals("should be valid", 3, range.getIntIndices()[0]);
    assertEquals("should be same", "\"Hyphen-ated\"", range.getRange());
    
    range.setSpreadSheet(m_Sheet);
    range.setRange("\"Under_score\"");
    assertEquals("should be valid", 1, range.getIntIndices().length);
    assertEquals("should be valid", 5, range.getIntIndices()[0]);
    assertEquals("should be same", "\"Under_score\"", range.getRange());
    
    range.setSpreadSheet(m_Sheet);
    range.setRange("\"Com,ma\"");
    assertEquals("should be valid", 1, range.getIntIndices().length);
    assertEquals("should be valid", 4, range.getIntIndices()[0]);
    assertEquals("should be same", "\"Com,ma\"", range.getRange());
    
    range.setSpreadSheet(m_Sheet);
    range.setRange("\"Hyphen-ated\",\"Com,ma\"");
    assertEquals("should be valid", 2, range.getIntIndices().length);
    assertEquals("should be valid", 3, range.getIntIndices()[0]);
    assertEquals("should be valid", 4, range.getIntIndices()[1]);
    assertEquals("should be same", "\"Hyphen-ated\",\"Com,ma\"", range.getRange());
    
    range.setSpreadSheet(m_Sheet);
    range.setRange("\"Hyphen-ated\",\"Under_score\"");
    assertEquals("should be valid", 2, range.getIntIndices().length);
    assertEquals("should be valid", 3, range.getIntIndices()[0]);
    assertEquals("should be valid", 5, range.getIntIndices()[1]);
    assertEquals("should be same", "\"Hyphen-ated\",\"Under_score\"", range.getRange());
  }
  
  /**
   * Tests locating when there are column names that are sub-strings of other
   * column names.
   */
  public void testSubstrings() {
    SpreadSheet sheet = new SpreadSheet();
    sheet.getHeaderRow().addCell("0").setContent("blah");
    sheet.getHeaderRow().addCell("1").setContent("dbtimestamp");
    sheet.getHeaderRow().addCell("2").setContent("time");
    
    SpreadSheetColumnRange range = new SpreadSheetColumnRange();
    range.setRange("bloerk");
    range.setData(sheet);
    assertEquals("shouldn't find any cols", 0, range.getIntIndices().length);
    
    range = new SpreadSheetColumnRange();
    range.setRange("dbtimestamp");
    range.setData(sheet);
    //assertEquals("# indices differs", 1, range.getIntIndices().length);
    assertEquals("index differs", 1, range.getIntIndices()[0]);
    
    range = new SpreadSheetColumnRange();
    range.setRange("time");
    range.setData(sheet);
    assertEquals("# indices differs", 1, range.getIntIndices().length);
    assertEquals("index differs", 2, range.getIntIndices()[0]);
    
    range = new SpreadSheetColumnRange();
    range.setRange("blah");
    range.setData(sheet);
    assertEquals("# indices differs", 1, range.getIntIndices().length);
    assertEquals("index differs", 0, range.getIntIndices()[0]);
  }
  
  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SpreadSheetColumnRangeTest.class);
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
