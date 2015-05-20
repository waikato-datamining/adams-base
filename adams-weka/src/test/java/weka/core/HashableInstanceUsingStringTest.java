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
 * Copyright (C) 2012 University of Waikato, Hamilton, NZ
 */

package weka.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.test.AdamsTestHelper;

/**
 * Tests HashableInstanceUsingString. Run from the command line with:<br><br>
 * java weka.core.HashableInstanceUsingStringTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HashableInstanceUsingStringTest
  extends AbstractHashableInstanceTestCase {

  /**
   * Constructs the <code>HashableInstanceUsingStringTest</code>.
   *
   * @param name 	the name of the test
   */
  public HashableInstanceUsingStringTest(String name) {
    super(name);
  }
  
  /**
   * Wraps the instance.
   * 
   * @param data	the instance to wrap
   * @param exclClass	whether to exclude the class
   * @param exclWeight	whether to exclude the weight
   * @return		the wrapped instance
   */
  @Override
  protected HashableInstanceUsingString wrap(Instance data, boolean exclClass, boolean exclWeight) {
    HashableInstanceUsingString	result;
    
    result = new HashableInstanceUsingString(data);
    result.setExcludeClass(exclClass);
    result.setExcludeWeight(exclWeight);
    
    return result;
  }

  /**
   * Returns a suite for this test.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(HashableInstanceUsingStringTest.class);
  }

  /**
   * Runs the test from the command-line.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    AdamsTestHelper.setRegressionRoot();
    TestRunner.run(suite());
  }
}
