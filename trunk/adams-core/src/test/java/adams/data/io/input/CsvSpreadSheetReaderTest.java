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
 * CsvSpreadSheetReaderTest.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import adams.test.TmpFile;

/**
 * Tests the adams.data.io.input.CsvSpreadSheetReader class. Run from commandline with: <p/>
 * java adams.data.io.input.CsvSpreadSheetReader
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CsvSpreadSheetReaderTest
  extends AbstractSpreadSheetReaderTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CsvSpreadSheetReaderTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("sample.csv");
  }
  
  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("sample.csv");
    
    super.tearDown();
  }
  
  /**
   * Simply tests reading the default settings.
   */
  public void testDefault() {
    CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
    SpreadSheet sheet = reader.read(new TmpFile("sample.csv"));
    assertEquals("# cols differ", 3, sheet.getColumnCount());
    assertEquals("# rows differ", 16, sheet.getRowCount());
  }
  
  /**
   * Tests reading in no rows.
   */
  public void testNoRows() {
    CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
    reader.setNumRows(0);
    SpreadSheet sheet = reader.read(new TmpFile("sample.csv"));
    assertEquals("# cols differ", 3, sheet.getColumnCount());
    assertEquals("# rows differ", 0, sheet.getRowCount());
  }
  
  /**
   * Tests reading in specific window.
   */
  public void testWindow() {
    CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
    reader.setFirstRow(5);
    reader.setNumRows(5);
    SpreadSheet sheet = reader.read(new TmpFile("sample.csv"));
    assertEquals("# cols differ", 3, sheet.getColumnCount());
    assertEquals("# rows differ", 5, sheet.getRowCount());

    reader = new CsvSpreadSheetReader();
    reader.setFirstRow(10);
    reader.setNumRows(10);
    sheet = reader.read(new TmpFile("sample.csv"));
    assertEquals("# cols differ", 3, sheet.getColumnCount());
    assertEquals("# rows differ", 7, sheet.getRowCount());
  }
  
  /**
   * Tests reading chunks.
   */
  public void testChunking() {
    CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
    reader.setChunkSize(5);
    int numChunks = 1;
    int numRows = 0;
    SpreadSheet sheet = reader.read(new TmpFile("sample.csv"));
    numRows += sheet.getRowCount();
    while (reader.hasMoreChunks()) {
      numChunks++;
      sheet = reader.nextChunk();
      numRows += sheet.getRowCount();
    }
    assertEquals("# chunks differ", 4, numChunks);
    assertEquals("# cols differ", 3, sheet.getColumnCount());
    assertEquals("# rows differ", 16, numRows);

    reader = new CsvSpreadSheetReader();
    reader.setChunkSize(5);
    reader.setFirstRow(10);
    reader.setNumRows(10);
    numChunks = 1;
    numRows = 0;
    sheet = reader.read(new TmpFile("sample.csv"));
    numRows += sheet.getRowCount();
    while (reader.hasMoreChunks()) {
      numChunks++;
      sheet = reader.nextChunk();
      numRows += sheet.getRowCount();
    }
    assertEquals("# chunks differ", 2, numChunks);
    assertEquals("# cols differ", 3, sheet.getColumnCount());
    assertEquals("# rows differ", 7, numRows);
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
	"sample.csv",
	"sample2.csv",
	"sample3.csv",
	"double_quotes.csv",
	"errors1_cr.csv",
	"errors1_crlf.csv",
	"errors1_lf.csv",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected SpreadSheetReader[] getRegressionSetups() {
    return new CsvSpreadSheetReader[]{
	new CsvSpreadSheetReader(),
	new CsvSpreadSheetReader(),
	new CsvSpreadSheetReader(),
	new CsvSpreadSheetReader(),
	new CsvSpreadSheetReader(),
	new CsvSpreadSheetReader(),
	new CsvSpreadSheetReader()
    };
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(CsvSpreadSheetReaderTest.class);
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
