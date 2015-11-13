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
 * WekaLabelRangeTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka;

import adams.core.Range;
import adams.core.RangeTest;
import adams.env.Environment;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.FileNotFoundException;

/**
 * Tests the adams.core.WekaLabelRange class. Run from commandline with: <br><br>
 * java adams.core.WekaLabelRangeTest
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaLabelRangeTest
  extends RangeTest {

  /** the dataset to use for testing. */
  protected Instances m_Data;
  
  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaLabelRangeTest(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/weka/data");
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
    
    filename = "labor.arff";
    m_TestHelper.copyResourceToTmp(filename);
    
    m_Data = DataSource.read(new TmpFile(filename).getAbsolutePath());
    if (m_Data == null)
      throw new FileNotFoundException("Test file '" + filename + "' not found?");
  }
  
  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("labor.arff");
    
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
      return new WekaLabelRange();
    else
      return new WekaLabelRange(index, max);
  }
  
  /**
   * Tests the index using column names.
   */
  public void testColumnNames() {
    WekaLabelRange range = new WekaLabelRange();

    range = new WekaLabelRange(Range.ALL);
    assertEquals("should contain no indices", 0, range.getIntIndices().length);
    assertEquals("should be same", Range.ALL, range.getRange());
    
    range.setData(null);
    range.setRange("blah");
    assertEquals("should contain no indices", 0, range.getIntIndices().length);
    assertEquals("should be same", "blah", range.getRange());
    
    range.setData(m_Data.attribute(m_Data.numAttributes() - 2));
    range.setRange("none");
    assertEquals("should be valid", 1, range.getIntIndices().length);
    assertEquals("should be valid", 0, range.getIntIndices()[0]);
    assertEquals("should be same", "none", range.getRange());

    range.setData(m_Data.attribute(m_Data.numAttributes() - 2));
    range.setRange("non");
    assertEquals("should be invalid", 0, range.getIntIndices().length);
    assertEquals("should be same", "non", range.getRange());
    
    range.setData(null);
    range.setRange("half");
    assertEquals("should be invalid", 0, range.getIntIndices().length);
    assertEquals("should be same", "half", range.getRange());
    
    range.setData(m_Data.attribute(m_Data.numAttributes() - 2));
    range.setRange("half");
    assertEquals("should be valid", 1, range.getIntIndices().length);
    assertEquals("should be same", "half", range.getRange());
  }
  
  /**
   * Tests the index using special column names that contain commas or hyphens.
   */
  public void testSpecialLabelNames() {
    WekaLabelRange range = new WekaLabelRange();
    
    range.setData(m_Data.attribute("pension"));
    range.setRange("\"ret_allw\"");
    assertEquals("should be valid", 1, range.getIntIndices().length);
    assertEquals("should be valid", 1, range.getIntIndices()[0]);
    assertEquals("should be same", "\"ret_allw\"", range.getRange());
  }
  
  /**
   * Tests locating when there are column names that are sub-strings of other
   * column names.
   */
  public void testSubstrings() {
    WekaLabelRange range = new WekaLabelRange();
    range.setRange("averaged");
    range.setData(m_Data.attribute("vacation"));
    assertEquals("shouldn't find any cols", 0, range.getIntIndices().length);
    
    range = new WekaLabelRange();
    range.setRange("average");
    range.setData(m_Data.attribute("vacation"));
    assertEquals("# indices differs", 1, range.getIntIndices().length);
    assertEquals("index differs", 1, range.getIntIndices()[0]);
    
    range = new WekaLabelRange();
    range.setRange("below_average");
    range.setData(m_Data.attribute("vacation"));
    assertEquals("# indices differs", 1, range.getIntIndices().length);
    assertEquals("index differs", 0, range.getIntIndices()[0]);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaLabelRangeTest.class);
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
