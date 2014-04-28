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
 * ReportToWekaInstanceTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import adams.core.Range;
import adams.data.instance.Instance;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.test.TmpFile;

/**
 * Tests the InstancesToSpreadSheet conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportToWekaInstanceTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ReportToWekaInstanceTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("bolts.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  protected Object[] getRegressionInput() {
    Report[]	result;
    Instances	data;
    TmpFile	file;
    int		i;
    Instance	inst;

    file = new TmpFile("bolts.arff");
    try {
      data = DataSource.read(file.getAbsolutePath());
      data.setClassIndex(data.numAttributes() - 1);
      result = new Report[data.numInstances()];
      for (i = 0; i < data.numInstances(); i++) {
	inst = new Instance();
	inst.set(data.instance(i), i, new int[0], new Range("first-last"), null);
	result[i] = inst.getReport();
      }
    }
    catch (Exception e) {
      result = new Report[0];
      fail("Failed to load data from '" + file + "': " + e);
    }

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected Conversion[] getRegressionSetups() {
    ReportToWekaInstance[]	result;

    result = new ReportToWekaInstance[1];
    result[0] = new ReportToWekaInstance();
    result[0].setFields(new Field[]{
	new Field("Class", DataType.NUMERIC),
	new Field("Dataset-Row", DataType.NUMERIC),
	new Field("Dataset-Name", DataType.STRING)
    });

    return result;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }
}
