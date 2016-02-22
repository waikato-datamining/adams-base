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
 * SpreadSheetViewTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet;

import adams.core.Utils;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.env.Environment;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.List;

/**
 * Tests the adams.data.spreadsheet.SpreadSheetView class. Run from commandline with: <br><br>
 * java adams.data.spreadsheet.SpreadSheetViewTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetViewTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetViewTest(String name) {
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

    SpreadSheet view = new SpreadSheetView(sheet, null, null);
    assertEquals("# of columns differ", 3, view.getColumnCount());
    assertEquals("# of rows differ", 16, view.getRowCount());

    view = new SpreadSheetView(sheet, new int[]{0, 2, 3, 4, 5, 8, 9, 10}, new int[]{0, 2});
    assertEquals("# of columns differ", 2, view.getColumnCount());
    assertEquals("# of rows differ", 8, view.getRowCount());
  }

  /**
   * Tests the insertRow method.
   */
  public void testInsertRow() {
    SpreadSheet sheet = new CsvSpreadSheetReader().read(new TmpFile("sample.csv").getAbsolutePath());
    sheet = new SpreadSheetView(sheet, null, null);
    try {
      sheet.insertRow(0);
    }
    catch (NotImplementedException e) {
      // OK!
    }
    catch (Throwable t) {
      fail("Failed to raise " + NotImplementedException.class.getName() + "!");
    }
  }

  /**
   * Tests the removeRow method.
   */
  public void testRemoveRow() {
    SpreadSheet sheet = new CsvSpreadSheetReader().read(new TmpFile("sample.csv").getAbsolutePath());
    sheet = new SpreadSheetView(sheet, null, null);
    try {
      sheet.removeRow(0);
    }
    catch (NotImplementedException e) {
      // OK!
    }
    catch (Throwable t) {
      fail("Failed to raise " + NotImplementedException.class.getName() + "!");
    }
  }

  /**
   * Tests the removeColumn method.
   */
  public void testRemoveColumn() {
    SpreadSheet sheet = new CsvSpreadSheetReader().read(new TmpFile("sample.csv").getAbsolutePath());
    sheet = new SpreadSheetView(sheet, null, null);
    try {
      sheet.removeColumn(0);
    }
    catch (NotImplementedException e) {
      // OK!
    }
    catch (Throwable t) {
      fail("Failed to raise " + NotImplementedException.class.getName() + "!");
    }
  }

  /**
   * Regression test.
   */
  public void testRegression() {
    String	regression;
    SpreadSheet sheet;
    File output;

    if (m_NoRegressionTest)
      return;

    sheet  = new CsvSpreadSheetReader().read(new TmpFile("sample.csv").getAbsolutePath());
    sheet  = new SpreadSheetView(sheet, null, null);
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
    SpreadSheetView out = new SpreadSheetView(sheet, null, null);
    RowComparator comp = new RowComparator(new int[]{0});
    try {
      out.sort(comp);
    }
    catch (NotImplementedException e) {
      // OK!
    }
    catch (Throwable t) {
      fail("Failed to raise " + NotImplementedException.class.getName() + "!");
    }
  }

  /**
   * Tests the {@link SpreadSheet#sort(RowComparator,boolean)}) method.
   */
  public void testSortUnique() {
    CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
    reader.setDataRowType(new DenseDataRow());
    SpreadSheet sheet = reader.read(new TmpFile("sample2.csv").getAbsolutePath());
    SpreadSheet out = new SpreadSheetView(sheet, null, null);
    RowComparator comp = new RowComparator(new int[]{0});
    try {
      out.sort(comp, true);
    }
    catch (NotImplementedException e) {
      // OK!
    }
    catch (Throwable t) {
      fail("Failed to raise " + NotImplementedException.class.getName() + "!");
    }
  }

  /**
   * Tests the {@link SpreadSheet#getCellValues(int)} and
   * {@link SpreadSheet#getCellValues(String)} methods.
   */
  public void testCellValues() {
    CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
    SpreadSheet data = reader.read(new TmpFile("sample3.csv").getAbsolutePath());
    SpreadSheetView view = new SpreadSheetView(data, null, null);

    List<String> values = view.getCellValues(0);
    assertEquals("Number of values differs", 3, values.size());
    assertEquals("Value #1 differs", "A", values.get(0));
    assertEquals("Value #2 differs", "B", values.get(1));
    assertEquals("Value #3 differs", "C", values.get(2));

    values = view.getCellValues(view.getHeaderRow().getCellKey(0));
    assertEquals("Number of values differs", 3, values.size());
    assertEquals("Value #1 differs", "A", values.get(0));
    assertEquals("Value #2 differs", "B", values.get(1));
    assertEquals("Value #3 differs", "C", values.get(2));

    view = new SpreadSheetView(view, new int[]{0, 3, 5, 6}, null);

    values = view.getCellValues(0);
    assertEquals("Number of values differs", 3, values.size());
    assertEquals("Value #1 differs", "A", values.get(0));
    assertEquals("Value #2 differs", "B", values.get(1));
    assertEquals("Value #3 differs", "C", values.get(2));

    values = view.getCellValues(view.getHeaderRow().getCellKey(0));
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
    data = new SpreadSheetView(data, null, null);
    try {
      data.clear();
    }
    catch (NotImplementedException e) {
      // OK!
    }
    catch (Throwable t) {
      fail("Failed to raise " + NotImplementedException.class.getName() + "!");
    }
  }

  /**
   * For classes (with default constructor) that are serializable, are tested
   * whether they are truly serializable.
   */
  public void testSerializable() {
    CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
    SpreadSheet data = reader.read(new TmpFile("sample3.csv").getAbsolutePath());
    SpreadSheetView view = new SpreadSheetView(data, null, null);
    assertNotNull("Failed to serialize!", Utils.deepCopy(view));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SpreadSheetViewTest.class);
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
