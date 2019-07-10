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
 * AbstractFileBasedDatasetPreparationTestCase.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.preparefilebaseddataset;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.flow.container.FileBasedDatasetContainer;
import adams.test.AdamsTestCase;
import adams.test.TmpFile;

import java.io.File;
import java.util.List;

/**
 * Ancestor for test cases tailored for dataset preparation schemes.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of data to process
 */
public abstract class AbstractFileBasedDatasetPreparationTestCase<T>
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractFileBasedDatasetPreparationTestCase(String name) {
    super(name);
  }

  /**
   * Processes the input data and returns the processed data.
   *
   * @param files	the data to work on
   * @param scheme	the scheme to process the data with
   * @return		the processed data
   */
  protected List<FileBasedDatasetContainer> process(T files, AbstractFileBasedDatasetPreparation<T> scheme) {
    return scheme.prepare(files);
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(List<FileBasedDatasetContainer> data, String filename) {
    int			i;
    StringBuilder	str;
    
    str = new StringBuilder();
    for (i = 0; i < data.size(); i++) {
      if (i > 0)
        str.append("\n");
      str.append(data.get(i).toString());
    }

    return FileUtils.writeToFile(m_TestHelper.getTmpDirectory() + File.separator + filename, str, false);
  }

  /**
   * Returns the data to use in the regression test.
   *
   * @return		the data
   */
  protected abstract T getRegressionInputData();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractFileBasedDatasetPreparation<T>[] getRegressionSetups();

  /**
   * Creates an output filename based on the input filename.
   *
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(int no) {
    return "out" + no + ".txt";
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    T						input;
    List<FileBasedDatasetContainer>		processed;
    boolean					ok;
    String					regression;
    int						i;
    AbstractFileBasedDatasetPreparation<T>[]	setups;
    AbstractFileBasedDatasetPreparation<T>	current;
    String[]					output;
    TmpFile[]					outputFiles;

    if (m_NoRegressionTest)
      return;

    input   = getRegressionInputData();
    setups  = getRegressionSetups();
    output  = new String[setups.length];

    // process data
    for (i = 0; i < setups.length; i++) {
      current = (AbstractFileBasedDatasetPreparation<T>) Utils.deepCopy(setups[i]);
      assertNotNull("Failed to create copy of algorithm: " + OptionUtils.getCommandLine(setups[i]), current);

      processed = process(input, current);
      assertNotNull("Failed to process data?", processed);

      output[i] = createOutputFilename(i);
      ok        = save(processed, output[i]);
      assertTrue("Failed to save regression data?", ok);
      current.destroy();
    }

    // test regression
    outputFiles = new TmpFile[output.length];
    for (i = 0; i < output.length; i++)
      outputFiles[i] = new TmpFile(output[i]);
    regression = m_Regression.compare(outputFiles);
    assertNull("Output differs:\n" + regression, regression);

    // remove output, clean up scheme
    for (i = 0; i < output.length; i++) {
      setups[i].destroy();
      m_TestHelper.deleteFileFromTmp(output[i]);
    }
    cleanUpAfterRegression();
  }

  /**
   * For further cleaning up after the regression tests.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void cleanUpAfterRegression() {
  }
}
