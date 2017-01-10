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
 * AbstractAdamsFilterTest.java
 * Copyright (C) 2011-2017 University of Waikato, Hamilton, New Zealand
 */
package weka.filters;

import weka.test.AdamsTestHelper;
import adams.env.Environment;

/**
 * Abstract test for filters within the ADAMS framework.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAdamsFilterTest
  extends AbstractFilterTest {
  
  static {
    AdamsTestHelper.setRegressionRoot();
  }

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AbstractAdamsFilterTest(String name) {
    super(name);
    setUpEnvironment();
  }

  /**
   * Called by JUnit before each test method. This implementation creates
   * the default filter to test and loads a test set of Instances.
   *
   * @throws Exception if an error occurs reading the example instances.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    if (m_FilteredClassifier != null)
      m_FilteredClassifier.setDoNotCheckForModifiedClassAttribute(true);
  }

  /**
   * Sets up the environment.
   */
  protected void setUpEnvironment() {
    Environment.setEnvironmentClass(adams.env.Environment.class);
    System.setProperty("weka.test.maventest", "true");
  }
}
