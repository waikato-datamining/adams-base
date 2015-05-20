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
 * WekaAttributeIndexTest.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka;

import java.io.FileNotFoundException;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import adams.core.Index;
import adams.core.IndexTest;
import adams.env.Environment;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Tests the WekaAttributeIndex class. Run from commandline with: <br><br>
 * java adams.data.weka.WekaAttributeIndexTest
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaAttributeIndexTest
  extends IndexTest {

  /** the dataset to use for testing. */
  protected Instances m_Data;
  
  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaAttributeIndexTest(String name) {
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
   * Returns a new Index instance.
   * 
   * @param index	the index to use, null for default constructor
   * @param max		the maximum to use
   * @return		the new instance
   */
  @Override
  protected Index newIndex(String index, int max) {
    if (index == null)
      return new WekaAttributeIndex();
    else
      return new WekaAttributeIndex(index, max);
  }
  
  /**
   * Tests the index using column names.
   */
  public void testColumnNames() {
    WekaAttributeIndex index = new WekaAttributeIndex();
    
    index.setData(null);
    index.setIndex("blah");
    assertEquals("should be invalid", -1, index.getIntIndex());
    assertEquals("should be same", "blah", index.getIndex());
    
    index.setData(m_Data);
    index.setIndex("duration");
    assertEquals("should be valid", 0, index.getIntIndex());
    assertEquals("should be same", "duration", index.getIndex());
    
    index.setData(m_Data);
    index.setIndex("Duration");
    assertEquals("should be invalid", -1, index.getIntIndex());
    assertEquals("should be same", "Duration", index.getIndex());
    
    index.setData(m_Data);
    index.setIndex("class");
    assertEquals("should be valid", 16, index.getIntIndex());
    assertEquals("should be same", "class", index.getIndex());
  }
  
  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaAttributeIndexTest.class);
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
