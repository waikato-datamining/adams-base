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
 * DefaultSpreadSheetTest.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet;

import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.env.Environment;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.File;
import java.util.List;

/**
 * Tests the adams.data.spreadsheet.DefaultSpreadSheet class. Run from commandline with: <br><br>
 * java adams.data.spreadsheet.DefaultSpreadSheetTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultSpreadSheetTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DefaultSpreadSheetTest(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/spreadsheet/data");
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("sample.csv");
    m_TestHelper.copyResourceToTmp("sample2.csv");
    m_TestHelper.copyResourceToTmp("sample3.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("sample.csv");
    m_TestHelper.deleteFileFromTmp("sample2.csv");
    m_TestHelper.deleteFileFromTmp("sample3.csv");

    super.tearDown();
  }

  /**
   * Tests the loading of a sample spreadsheet.
   */
  public void testRead() {
    SpreadSheet sheet = new CsvSpreadSheetReader().read(new TmpFile("sample.csv").getAbsolutePath());
    assertEquals("# of columns differ", 3, sheet.getColumnCount());
    assertEquals("# of rows differ", 16, sheet.getRowCount());
  }

  /**
   * Tests the cell positions.
   */
  public void testCellPositions() {
    int row = 0;
    int col = 0;
    String pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "A2", pos);

    row = 1;
    col = 2;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "C3", pos);

    row = 1;
    col = 25;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "Z3", pos);

    row = 1;
    col = 26;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "AA3", pos);

    row = 2;
    col = 51;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "AZ4", pos);

    row = 2;
    col = 52;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "BA4", pos);

    row = 2;
    col = 701;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "ZZ4", pos);

    row = 2;
    col = 702;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "AAA4", pos);

    row = 20;
    col = 900;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "AHQ22", pos);

    row = 23;
    col = 1000;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "ALM25", pos);

    row = 23;
    col = 26 + 676 + 17576 + 1;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "AAAB25", pos);

    row = 23;
    col = 26 + 676 + 17576 + 456976 + 1;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "AAAAB25", pos);

    row = 23;
    col = 26 + 676 + 17576 + 456976 + 11881376 + 1;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "AAAAAB25", pos);
  }
  
  /**
   * Tests the getCellLocation(String) method.
   */
  public void testCellLocations() {
    try {
      int row = 0;
      int col = 0;
      int[] loc = SpreadSheetUtils.getCellLocation("A2");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 1;
      col = 2;
      loc = SpreadSheetUtils.getCellLocation("C3");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 1;
      col = 25;
      loc = SpreadSheetUtils.getCellLocation("Z3");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 1;
      col = 26;
      loc = SpreadSheetUtils.getCellLocation("AA3");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 51;
      loc = SpreadSheetUtils.getCellLocation("AZ4");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 52;
      loc = SpreadSheetUtils.getCellLocation("BA4");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 701;
      loc = SpreadSheetUtils.getCellLocation("ZZ4");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 702;
      loc = SpreadSheetUtils.getCellLocation("AAA4");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 20;
      col = 900;
      loc = SpreadSheetUtils.getCellLocation("AHQ22");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 1000;
      loc = SpreadSheetUtils.getCellLocation("ALM25");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 26 + 676 + 17576 + 1;
      loc = SpreadSheetUtils.getCellLocation("AAAB25");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 26 + 676 + 17576 + 456976 + 1;
      loc = SpreadSheetUtils.getCellLocation("AAAAB25");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 26 + 676 + 17576 + 456976 + 11881376 + 1;
      loc = SpreadSheetUtils.getCellLocation("AAAAAB25");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);
    }
    catch (Exception e) {
      e.printStackTrace();
      fail(e.toString());
    }
  }

  /**
   * Tests the insertRow method.
   */
  public void testInsertRow() {
    SpreadSheet sheet = new CsvSpreadSheetReader().read(new TmpFile("sample.csv").getAbsolutePath());
    SpreadSheet out;
    
    out = sheet.getClone();
    out.insertRow(0);
    assertEquals("# of columns differ", sheet.getColumnCount(), out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount() + 1, out.getRowCount());
    
    out = sheet.getClone();
    out.insertRow(0);
    assertEquals("# of columns differ", sheet.getColumnCount(), out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount() + 1, out.getRowCount());
  }

  /**
   * Tests the removeRow method.
   */
  public void testRemoveRow() {
    SpreadSheet sheet = new CsvSpreadSheetReader().read(new TmpFile("sample.csv").getAbsolutePath());
    SpreadSheet out;
    
    out = sheet.getClone();
    out.removeRow(0);
    assertEquals("# of columns differ", sheet.getColumnCount(), out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount() - 1, out.getRowCount());
    
    out = sheet.getClone();
    out.removeRow(out.getRowCount() - 1);
    assertEquals("# of columns differ", sheet.getColumnCount(), out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount() - 1, out.getRowCount());
    
    out = sheet.getClone();
    out.removeRow(out.getRowKey(0));
    assertEquals("# of columns differ", sheet.getColumnCount(), out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount() - 1, out.getRowCount());
    
    out = sheet.getClone();
    out.removeRow(out.getRowKey(out.getRowCount() - 1));
    assertEquals("# of columns differ", sheet.getColumnCount(), out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount() - 1, out.getRowCount());
  }

  /**
   * Tests the removeColumn method.
   */
  public void testRemoveColumn() {
    SpreadSheet sheet = new CsvSpreadSheetReader().read(new TmpFile("sample.csv").getAbsolutePath());
    SpreadSheet out;
    
    out = sheet.getClone();
    out.removeColumn(0);
    assertEquals("# of columns differ", sheet.getColumnCount() - 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
    
    out = sheet.getClone();
    out.removeColumn(out.getColumnCount() - 1);
    assertEquals("# of columns differ", sheet.getColumnCount() - 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
    
    out = sheet.getClone();
    out.removeColumn(out.getHeaderRow().getCellKey(0));
    assertEquals("# of columns differ", sheet.getColumnCount() - 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
    
    out = sheet.getClone();
    out.removeColumn(out.getHeaderRow().getCellKey(out.getColumnCount() - 1));
    assertEquals("# of columns differ", sheet.getColumnCount() - 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
  }

  /**
   * Tests the insertColumn method for sparse data rows.
   */
  public void testInsertColumnSparse() {
    CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
    reader.setDataRowType(new SparseDataRow());
    SpreadSheet sheet = reader.read(new TmpFile("sample.csv").getAbsolutePath());
    SpreadSheet out;
    
    // no initial value
    out = sheet.getClone();
    out.insertColumn(0, "new");
    assertEquals("# of columns differ", sheet.getColumnCount() + 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
    
    assertFalse("cell shouldn't be present", out.getRow(0).hasCell(0));
    
    out = sheet.getClone();
    out.insertColumn(1, "new");
    assertEquals("# of columns differ", sheet.getColumnCount() + 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
    
    out = sheet.getClone();
    out.insertColumn(sheet.getColumnCount(), "new");
    assertEquals("# of columns differ", sheet.getColumnCount() + 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
    
    // with initial value
    out = sheet.getClone();
    out.insertColumn(0, "new", "blah");
    assertEquals("# of columns differ", sheet.getColumnCount() + 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
    
    assertTrue("cell should be present", out.getRow(0).hasCell(0));
    
    out = sheet.getClone();
    out.insertColumn(1, "new", "blah");
    assertEquals("# of columns differ", sheet.getColumnCount() + 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
    
    out = sheet.getClone();
    out.insertColumn(sheet.getColumnCount(), "new", "blah");
    assertEquals("# of columns differ", sheet.getColumnCount() + 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
  }

  /**
   * Tests the insertColumn method for dense data rows.
   */
  public void testInsertColumnDense() {
    CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
    reader.setDataRowType(new DenseDataRow());
    SpreadSheet sheet = reader.read(new TmpFile("sample.csv").getAbsolutePath());
    SpreadSheet out;
    
    // no initial value
    out = sheet.getClone();
    out.insertColumn(0, "new");
    assertEquals("# of columns differ", sheet.getColumnCount() + 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
    
    assertTrue("cell should be present", out.getRow(0).hasCell(0));
    
    out = sheet.getClone();
    out.insertColumn(1, "new");
    assertEquals("# of columns differ", sheet.getColumnCount() + 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
    
    out = sheet.getClone();
    out.insertColumn(sheet.getColumnCount(), "new");
    assertEquals("# of columns differ", sheet.getColumnCount() + 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
    
    // with initial value
    out = sheet.getClone();
    out.insertColumn(0, "new", "blah");
    assertEquals("# of columns differ", sheet.getColumnCount() + 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
    
    assertTrue("cell should be present", out.getRow(0).hasCell(0));
    
    out = sheet.getClone();
    out.insertColumn(1, "new", "blah");
    assertEquals("# of columns differ", sheet.getColumnCount() + 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
    
    out = sheet.getClone();
    out.insertColumn(sheet.getColumnCount(), "new", "blah");
    assertEquals("# of columns differ", sheet.getColumnCount() + 1, out.getColumnCount());
    assertEquals("# of rows differ", sheet.getRowCount(), out.getRowCount());
  }
  
  /**
   * Regression test.
   */
  public void testRegression() {
    String	regression;
    SpreadSheet sheet;
    File	output;

    if (m_NoRegressionTest)
      return;

    sheet  = new CsvSpreadSheetReader().read(new TmpFile("sample.csv").getAbsolutePath());
    output = new TmpFile("sample_out.csv");
    new CsvSpreadSheetWriter().write(sheet, output.getAbsolutePath());
    regression = m_Regression.compare(new File[]{output.getAbsoluteFile()});
    assertNull("Output differs:\n" + regression, regression);
  }

  /**
   * Tests the {@link SpreadSheet#sort(RowComparator)}) method.
   */
  public void testSort() {
    CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
    reader.setDataRowType(new DenseDataRow());
    SpreadSheet sheet = reader.read(new TmpFile("sample2.csv").getAbsolutePath());
    SpreadSheet out = sheet.getClone();
    RowComparator comp = new RowComparator(new int[]{0});
    out.sort(comp);
    assertEquals("# rows differ", sheet.getRowCount(), out.getRowCount());
    assertEquals("# cols differ", sheet.getColumnCount(), out.getColumnCount());
  }

  /**
   * Tests the {@link SpreadSheet#sort(RowComparator,boolean)}) method.
   */
  public void testSortUnique() {
    CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
    reader.setDataRowType(new DenseDataRow());
    SpreadSheet sheet = reader.read(new TmpFile("sample2.csv").getAbsolutePath());
    SpreadSheet out = sheet.getClone();
    RowComparator comp = new RowComparator(new int[]{0});
    out.sort(comp, true);
    assertEquals("# rows differ", sheet.getRowCount() - 1, out.getRowCount());
    assertEquals("# cols differ", sheet.getColumnCount(), out.getColumnCount());

    reader = new CsvSpreadSheetReader();
    reader.setDataRowType(new DenseDataRow());
    sheet = reader.read(new TmpFile("sample3.csv").getAbsolutePath());
    out = sheet.getClone();
    comp = new RowComparator(new int[]{0});
    out.sort(comp, true);
    assertEquals("# rows differ", 3, out.getRowCount());
    assertEquals("# cols differ", sheet.getColumnCount(), out.getColumnCount());
  }

  /**
   * Tests the {@link SpreadSheet#getCellValues(int)} and 
   * {@link SpreadSheet#getCellValues(String)} methods.
   */
  public void testCellValues() {
    CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
    SpreadSheet data = reader.read(new TmpFile("sample3.csv").getAbsolutePath());
    
    List<String> values = data.getCellValues(0);
    assertEquals("Number of values differs", 3, values.size());
    assertEquals("Value #1 differs", "A", values.get(0));
    assertEquals("Value #2 differs", "B", values.get(1));
    assertEquals("Value #3 differs", "C", values.get(2));

    values = data.getCellValues(data.getHeaderRow().getCellKey(0));
    assertEquals("Number of values differs", 3, values.size());
    assertEquals("Value #1 differs", "A", values.get(0));
    assertEquals("Value #2 differs", "B", values.get(1));
    assertEquals("Value #3 differs", "C", values.get(2));
  }

  /**
   * Tests the {@link SpreadSheet#clear()} method.
   */
  public void testClear() {
    CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
    SpreadSheet data = reader.read(new TmpFile("sample3.csv").getAbsolutePath());
    data.clear();
    assertEquals("column header differs", "Col", data.getHeaderRow().getCell(0).getContent());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(DefaultSpreadSheetTest.class);
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
