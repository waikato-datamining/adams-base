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
 * CompareDatasetsTest.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.tools;

import adams.core.Range;
import adams.test.TmpFile;

/**
 * Tests the CompareDatasets tool.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CompareDatasetsTest
  extends AbstractToolTestCase {

  /**
   * Constructs the test case.
   *
   * @param name 	the name of the test
   */
  public CompareDatasetsTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("iris_1.arff");
    m_TestHelper.copyResourceToTmp("iris_2.arff");
    m_TestHelper.deleteFileFromTmp("missing.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris_1.arff");
    m_TestHelper.deleteFileFromTmp("iris_2.arff");
    m_TestHelper.deleteFileFromTmp("missing.csv");

    super.tearDown();
  }

  /**
   * The files to use as input in the regression tests, in case of tool
   * implementing the InputFileHandler interface.
   *
   * @return		the files, zero-length if not an InputFileHandler
   */
  protected String[] getRegressionInputFiles() {
    return new String[0];
  }

  /**
   * The files to use as output in the regression tests, in case of tool
   * implementing the OutputFileGenerator interface.
   * <br><br>
   * NB: these names must be all different!
   *
   * @return		the files, zero-length if not an OutputFileGenerator
   */
  protected String[] getRegressionOutputFiles() {
    return new String[]{"out1.csv", "out2.csv", "out3.csv", "out4.csv"};
  }

  /**
   * Returns the setups to test in the regression tests.
   *
   * @return		the setups to test
   */
  protected AbstractTool[] getRegressionSetups() {
    CompareDatasets[]	result;

    result = new CompareDatasets[4];

    result[0] = new CompareDatasets();
    result[0].setDataset1(new TmpFile("iris_1.arff"));
    result[0].setDataset2(new TmpFile("iris_2.arff"));
    result[0].setMissing(new TmpFile("missing.csv"));

    result[1] = new CompareDatasets();
    result[1].setDataset1(new TmpFile("iris_1.arff"));
    result[1].setDataset2(new TmpFile("iris_2.arff"));
    result[1].setRange1(new Range("2-last"));
    result[1].setRange2(new Range("2-last"));
    result[1].setMissing(new TmpFile("missing.csv"));

    result[2] = new CompareDatasets();
    result[2].setDataset1(new TmpFile("iris_1.arff"));
    result[2].setDataset2(new TmpFile("iris_2.arff"));
    result[2].setRange1(new Range("2"));
    result[2].setRange2(new Range("2"));
    result[2].setMissing(new TmpFile("missing.csv"));

    result[3] = new CompareDatasets();
    result[3].setDataset1(new TmpFile("iris_1.arff"));
    result[3].setDataset2(new TmpFile("iris_2.arff"));
    result[3].setMissing(new TmpFile("missing.csv"));
    result[3].setThreshold(1.0);

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
