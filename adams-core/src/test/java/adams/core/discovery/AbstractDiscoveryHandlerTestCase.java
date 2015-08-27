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
 * AbstractDiscoveryHandlerTestCase.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core.discovery;

import adams.core.discovery.PropertyPath.PropertyContainer;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.test.AbstractDatabaseTestCase;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;
import adams.test.TmpFile;

import java.util.List;

/**
 * Ancestor for discovery handler test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDiscoveryHandlerTestCase
  extends AbstractDatabaseTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractDiscoveryHandlerTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/discovery/data");
  }

  /**
   * Returns the discovery algorithm.
   *
   * @return		the discovery
   */
  protected PropertyDiscovery getDiscovery() {
    return new DefaultPropertyDiscovery();
  }

  /**
   * Processes the object.
   *
   * @param obj		the object to use
   * @return		the processed data (header/row)
   */
  protected AbstractDiscoveryHandler process(Object obj, AbstractDiscoveryHandler scheme) {
    PropertyDiscovery	discovery;

    discovery = getDiscovery();
    discovery.discover(new AbstractDiscoveryHandler[]{scheme}, obj);

    return scheme;
  }

  /**
   * Turns the algorithm (and its containers) into a useful string representation.
   *
   * @param scheme	the algorithm to convert
   * @return		the string representation
   */
  protected String toString(AbstractDiscoveryHandler scheme) {
    List<PropertyContainer> 	conts;
    StringBuilder		result;

    result = new StringBuilder();

    conts = scheme.getContainers();
    for (PropertyContainer cont: conts) {
      result.append(cont.getPath().getFullPath());
      result.append("\n");
    }

    return result.toString();
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(Object data, String filename) {
    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), data.toString(), false);
  }

  /**
   * Returns the objects to use in the regression test.
   *
   * @return		the objects
   */
  protected abstract Object[] getRegressionObjects();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractDiscoveryHandler[] getRegressionSetups();

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Creates an output filename.
   *
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(int no) {
    return "out-" + no + ".txt";
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    AbstractDiscoveryHandler	processed;
    boolean			ok;
    String			regression;
    int				n;
    int				i;
    Object[]			objects;
    AbstractDiscoveryHandler[]	setups;
    AbstractDiscoveryHandler	current;
    String[]			output;
    TmpFile[]			outputFiles;
    int[]			ignored;

    if (m_NoRegressionTest)
      return;

    objects = getRegressionObjects();
    setups  = getRegressionSetups();
    output  = new String[setups.length];
    ignored = getRegressionIgnoredLineIndices();
    assertEquals("Number of objects and setups differ!", objects.length, setups.length);

    // process data
    for (n = 0; n < setups.length; n++) {
      output[n] = createOutputFilename(n);
      current   = (AbstractDiscoveryHandler) OptionUtils.shallowCopy(setups[n], false);
      assertNotNull("Failed to create copy of discovery algorithm: " + OptionUtils.getCommandLine(setups[n]), current);

      processed = process(objects[n], current);
      assertNotNull("Failed to process object: " + objects[n], processed);

      ok        = save(toString(processed), output[n]);
      assertTrue("Failed to save regression data?", ok);

      current.destroy();
    }

    // test regression
    outputFiles = new TmpFile[output.length];
    for (i = 0; i < output.length; i++)
      outputFiles[i] = new TmpFile(output[i]);
    regression = m_Regression.compare(outputFiles, ignored);
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
