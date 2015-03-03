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
 * BinaryFileTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Tests the 'BinaryFile' boolean condition.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BinaryFileTest
  extends AbstractBooleanConditionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public BinaryFileTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("Test.txt");
    m_TestHelper.copyResourceToTmp("adams_logo.png");
  }
  
  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("Test.txt");
    m_TestHelper.deleteFileFromTmp("adams_logo.png");
    
    super.tearDown();
  }
  
  /**
   * Returns the owning actors to use in the regression test (one per regression setup).
   *
   * @return		the owners (not all conditions might need owners)
   */
  @Override
  protected AbstractActor[] getRegressionOwners() {
    return new AbstractActor[]{
	null,
	null,
	null
    };
  }

  /**
   * Returns the input data to use in the regression test (one per regression setup).
   *
   * @return		the input data
   */
  @Override
  protected Object[] getRegressionInputs() {
    return new Object[]{
	"blah",
	"blah",
	"blah"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBooleanCondition[] getRegressionSetups() {
    BinaryFile[]	result;
    
    result    = new BinaryFile[3];
    result[0] = new BinaryFile();
    result[1] = new BinaryFile();
    result[1].setFile(new TmpFile("Test.txt"));
    result[2] = new BinaryFile();
    result[2].setFile(new TmpFile("adams_logo.png"));
    
    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BinaryFileTest.class);
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
