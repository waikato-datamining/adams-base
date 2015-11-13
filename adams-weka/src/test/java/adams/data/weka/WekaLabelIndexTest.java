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
 * WekaLabelIndexTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka;

import adams.core.Index;
import adams.core.IndexTest;
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
 * Tests the WekaLabelIndex class. Run from commandline with: <br><br>
 * java adams.data.weka.WekaLabelIndexTest
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaLabelIndexTest
  extends IndexTest {

  /** the dataset to use for testing. */
  protected Instances m_Data;

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaLabelIndexTest(String name) {
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
    m_Data = loadDataset(filename);

    m_TestHelper.copyResourceToTmp("simple.arff");
  }
  
  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("labor.arff");
    m_TestHelper.deleteFileFromTmp("simple.arff");

    super.tearDown();
  }

  /**
   * Loads the specified dataset from the tmp directory.
   *
   * @param filename	the dataset to load (no path)
   * @return		the dataset
   * @throws Exception	if loading of dataset failed
   */
  protected Instances loadDataset(String filename) throws Exception {
    Instances 	result;

    result = DataSource.read(new TmpFile(filename).getAbsolutePath());
    if (result == null)
      throw new FileNotFoundException("Test file '" + filename + "' not found?");

    return result;
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
      return new WekaLabelIndex();
    else
      return new WekaLabelIndex(index, max);
  }
  
  /**
   * Tests the index using column names.
   */
  public void testColumnNames() {
    WekaLabelIndex index = new WekaLabelIndex();
    
    index.setData(null);
    index.setIndex("blah");
    assertEquals("should be invalid", -1, index.getIntIndex());
    assertEquals("should be same", "blah", index.getIndex());
    
    index.setData(m_Data.attribute("pension"));
    index.setIndex("none");
    assertEquals("should be valid", 0, index.getIntIndex());
    assertEquals("should be same", "none", index.getIndex());

    index.setData(m_Data.attribute("pension"));
    index.setIndex("\"none\"");
    assertEquals("should be valid", 0, index.getIntIndex());
    assertEquals("should be same", "\"none\"", index.getIndex());

    index.setData(m_Data.attribute("pension"));
    index.setIndex("Empl_contr");
    assertEquals("should be invalid", -1, index.getIntIndex());
    assertEquals("should be same", "Empl_contr", index.getIndex());

    index.setData(m_Data.attribute("pension"));
    index.setIndex("empl_contr");
    assertEquals("should be valid", 2, index.getIntIndex());
    assertEquals("should be same", "empl_contr", index.getIndex());

    index.setData(m_Data.attribute("pension"));
    index.setIndex("\"empl_contr\"");
    assertEquals("should be valid", 2, index.getIntIndex());
    assertEquals("should be same", "\"empl_contr\"", index.getIndex());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaLabelIndexTest.class);
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
