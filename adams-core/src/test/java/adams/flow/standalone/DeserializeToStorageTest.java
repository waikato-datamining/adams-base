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
 * DeserializeToStorageTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.data.io.input.SerializedObjectReader;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.core.Actor;
import adams.flow.sink.DumpFile;
import adams.flow.source.ListStorageNames;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the DeserializeToStorage actor.
 * <br>
 * NB: Dummy test.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DeserializeToStorageTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DeserializeToStorageTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method. This implementation creates
   * the default actor.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    m_TestHelper.copyResourceToTmp("simple.ser");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("simple.ser");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  public Actor getActor() {
    Flow result = new Flow();

    DeserializeToStorage des = new DeserializeToStorage();
    des.setModelFile(new TmpFile("simple.ser"));
    des.setReader(new SerializedObjectReader());
    des.setStorageName(new StorageName("model"));
    result.add(des);

    ListStorageNames list = new ListStorageNames();
    result.add(list);

    DumpFile df = new DumpFile();
    df.setOutputFile(new TmpFile("dumpfile.txt"));
    df.setAppend(true);
    result.add(df);

    return result;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.txt"),
        });
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(DeserializeToStorageTest.class);
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
