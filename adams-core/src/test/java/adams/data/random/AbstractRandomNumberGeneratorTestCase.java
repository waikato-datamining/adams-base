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
 * AbstractRandomNumberGeneratorTestCase.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.io.FileUtils;
import adams.data.conversion.DoubleToString;
import adams.test.AdamsTestCase;
import adams.test.TmpFile;

/**
 * Ancestor for random number generator test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRandomNumberGeneratorTestCase
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractRandomNumberGeneratorTestCase(String name) {
    super(name);
  }

  /**
   * Returns the default number of random numbers to generate.
   *
   * @return		the number of random numbers to generate
   */
  protected int getNumGenerate() {
    return 20;
  }

  /**
   * Returns the generated data.
   *
   * @param scheme	the scheme to generate the data with
   * @return		the generated data
   */
  protected Number[] generate(AbstractRandomNumberGenerator scheme) {
    Number[]	result;
    int		i;

    result = new Number[getNumGenerate()];
    for (i = 0; i < result.length; i++)
      result[i] = scheme.next();

    return result;
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(Number[] data, String filename) {
    String[]		lines;
    int			i;
    DoubleToString	d2s;

    lines = new String[data.length];
    d2s   = new DoubleToString();
    d2s.setNumDecimals(12);
    for (i = 0; i < data.length; i++) {
      d2s.setInput(data[i].doubleValue());
      d2s.convert();
      lines[i] = d2s.getOutput().toString();
    }
    d2s.cleanUp();

    return FileUtils.saveToFile(lines, new TmpFile(filename));
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractRandomNumberGenerator[] getRegressionSetups();

  /**
   * Creates an output filename based on the input filename.
   *
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(int no) {
    return "randomnumbergenerator-out" + no;
  }

  /**
   * Compares the generated data against previously saved output data.
   */
  public void testRegression() {
    Number[]				data;
    boolean				ok;
    String				regression;
    int					i;
    AbstractRandomNumberGenerator[]	setups;
    String[]				output;
    TmpFile[]				outputFiles;

    if (m_NoRegressionTest)
      return;

    setups = getRegressionSetups();
    output = new String[setups.length];

    // generate data
    for (i = 0; i < setups.length; i++) {
      data = generate(setups[i]);
      assertNotNull("Failed to generate data?", data);

      output[i] = createOutputFilename(i);
      ok        = save(data, output[i]);
      assertTrue("Failed to save regression data?", ok);
    }

    // test regression
    outputFiles = new TmpFile[output.length];
    for (i = 0; i < output.length; i++)
      outputFiles[i] = new TmpFile(output[i]);
    regression = m_Regression.compare(outputFiles);
    assertNull("Output differs:\n" + regression, regression);

    // remove output, clean up scheme
    for (i = 0; i < output.length; i++) {
      if (setups[i] instanceof Destroyable)
	((Destroyable) setups[i]).destroy();
      else if (setups[i] instanceof CleanUpHandler)
	((CleanUpHandler) setups[i]).cleanUp();
      m_TestHelper.deleteFileFromTmp(output[i]);
    }
    cleanUpAfterRegression();
  }

  /**
   * For further cleaning up after the regression tests.
   * <p/>
   * Default implementation does nothing.
   */
  protected void cleanUpAfterRegression() {
  }
}
