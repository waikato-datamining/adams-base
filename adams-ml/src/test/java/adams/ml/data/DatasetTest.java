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
 * DatasetTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.ml.data;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.SpreadSheetTest;
import adams.env.Environment;
import adams.test.TmpFile;

/**
 * Tests the {@link Dataset} class.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatasetTest
  extends SpreadSheetTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DatasetTest(String name) {
    super(name);
  }

  /**
   * Tests the {@link Dataset#getClone()} method.
   */
  public void textDatasetClone() {
    CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
    reader.setSpreadSheetType(new Dataset());
    Dataset data = (Dataset) reader.read(new TmpFile("sample.csv").getAbsolutePath());
    Dataset copy = (Dataset) data.getClone();
    assertNull("equalHeaders should return null", data.equalsHeader(copy));

    data.setClassAttribute(1, true);
    assertNotNull("equalHeaders should not return null", data.equalsHeader(copy));

    copy = (Dataset) data.getClone();
    assertNull("equalHeaders should return null (after setting class attribute)", data.equalsHeader(copy));
  }
  
  /**
   * Tests the {@link Dataset#setClassAttribute(int, boolean)},
   * {@link Dataset#setClassAttribute(String, boolean)},
   * {@link Dataset#getClassAttributeIndices()} 
   * and {@link Dataset#getClassAttributeKeys()} methods.
   */
  public void testClassAttribute() {
    CsvSpreadSheetReader reader = new CsvSpreadSheetReader();
    reader.setSpreadSheetType(new Dataset());
    Dataset data = (Dataset) reader.read(new TmpFile("sample.csv").getAbsolutePath());
    Dataset copy = (Dataset) data.getClone();

    data.setClassAttribute(1, true);
    assertNotNull("equalHeaders should return a string", data.equalsHeader(copy));
    assertTrue("col should be a class attribute", data.isClassAttribute(1));

    data = (Dataset) copy.getClone();
    String key = data.getHeaderRow().getCellKey(1);
    data.setClassAttribute(key, true);
    assertNotNull("equalHeaders should return a string", data.equalsHeader(copy));
    assertTrue("col should be a class attribute", data.isClassAttribute(key));

    data = (Dataset) copy.getClone();
    assertTrue("Failed to update class attribute flag at 0", data.setClassAttribute(0, true));
    assertTrue("Failed to update class attribute flag at 1", data.setClassAttribute(1, true));
    assertFalse("Succeeded to update class attribute flag at 3", data.setClassAttribute(3, true));
    assertNotNull("equalHeaders should return a string", data.equalsHeader(copy));
    assertTrue("col should be a class attribute", data.isClassAttribute(0));
    assertTrue("col should be a class attribute", data.isClassAttribute(1));
    assertFalse("col should not be a class attribute", data.isClassAttribute(3));

    String[] keys = data.getClassAttributeKeys();
    assertEquals("# of class attributes differ", 2, keys.length);

    int[] indices = data.getClassAttributeIndices();
    assertEquals("# of class attributes differ", 2, indices.length);
    assertEquals("index of class attribute differs", 0, indices[0]);
    assertEquals("index of class attribute differs", 1, indices[1]);

    data.removeColumn(0);
    indices = data.getClassAttributeIndices();
    assertEquals("# of class attributes differ", 1, indices.length);
    assertEquals("index of class attribute differs", 0, indices[0]);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(DatasetTest.class);
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
