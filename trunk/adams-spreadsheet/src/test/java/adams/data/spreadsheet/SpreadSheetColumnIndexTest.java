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
 * SpreadSheetColumnIndexTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import java.io.FileNotFoundException;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.Index;
import adams.core.IndexTest;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.env.Environment;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Tests the adams.core.SpreadSheetColumnIndex class. Run from commandline with: <p/>
 * java adams.core.SpreadSheetColumnIndexTest
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetColumnIndexTest
  extends IndexTest {

  /** the spreadsheet to use for testing. */
  protected SpreadSheet m_Sheet;
  
  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetColumnIndexTest(String name) {
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
   * Returns a new Index instance.
   * 
   * @param index	the index to use, null for default constructor
   * @param max		the maximum to use
   * @return		the new instance
   */
  @Override
  protected Index newIndex(String index, int max) {
    if (index == null)
      return new SpreadSheetColumnIndex();
    else
      return new SpreadSheetColumnIndex(index, max);
  }
  
  /**
   * Tests the index using column names.
   */
  public void testColumnNames() {
    SpreadSheetColumnIndex index = new SpreadSheetColumnIndex();
    
    index.setSpreadSheet(null);
    index.setIndex("blah");
    assertEquals("should be invalid", -1, index.getIntIndex());
    assertEquals("should be same", "blah", index.getIndex());
    
    index.setSpreadSheet(m_Sheet);
    index.setIndex("Field");
    assertEquals("should be valid", 0, index.getIntIndex());
    assertEquals("should be same", "Field", index.getIndex());
    
    index.setSpreadSheet(m_Sheet);
    index.setIndex("field");
    assertEquals("should be invalid", -1, index.getIntIndex());
    assertEquals("should be same", "field", index.getIndex());
    
    index.setSpreadSheet(m_Sheet);
    index.setIndex("Value");
    assertEquals("should be valid", 2, index.getIntIndex());
    assertEquals("should be same", "Value", index.getIndex());
  }
  
  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SpreadSheetColumnIndexTest.class);
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
