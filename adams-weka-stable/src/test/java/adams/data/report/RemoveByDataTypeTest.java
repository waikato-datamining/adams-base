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
 * RemoveByDataTypeTest.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.report;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.io.FileUtils;
import adams.data.instance.Instance;
import adams.data.io.input.InstanceReader;
import adams.env.Environment;
import adams.test.TmpFile;

/**
 * Test class for the RemoveByDataType report filter. Run from the command line with: <br><br>
 * java adams.data.report.RemoveByDataTypeTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoveByDataTypeTest
  extends AbstractReportFilterTestCase<AbstractReportFilter, Instance> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public RemoveByDataTypeTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("bolts.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.arff");
    
    super.tearDown();
  }

  /**
   * Loads the data to process.
   *
   * @param filename	the filename to load (without path)
   * @return		the data, null if it could not be loaded
   */
  @Override
  protected Instance load(String filename) {
    InstanceReader	reader;
    List<Instance>	data;
    
    reader = new InstanceReader();
    reader.setInput(new TmpFile(filename));
    data = reader.read();
    reader.cleanUp();
    
    if (data.size() > 0)
      return data.get(0);
    else
      return null;
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  @Override
  protected boolean save(Instance data, String filename) {
    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), data.getReport(), false);
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
	"bolts.arff",
	"bolts.arff",
	"bolts.arff"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractReportFilter[] getRegressionSetups() {
    RemoveByDataType[]	result;
    
    result    = new RemoveByDataType[3];
    result[0] = new RemoveByDataType();
    result[1] = new RemoveByDataType();
    result[1].setDataTypes(new DataType[]{DataType.NUMERIC});
    result[2] = new RemoveByDataType();
    result[2].setDataTypes(new DataType[]{DataType.NUMERIC});
    result[2].setInvertMatching(true);
    
    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(RemoveByDataTypeTest.class);
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
