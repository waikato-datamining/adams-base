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

import adams.env.Environment;
import weka.core.AbstractInstance;
import weka.test.AdamsTestHelper;

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

  /** The default number of decimal places to print during regression tests. */
  public static final int REGRESSION_DECIMAL_PLACES_DEFAULT = 6;

  /** The number of decimal places to print during regression tests. */
  protected int m_RegressionDecimalPlaces = REGRESSION_DECIMAL_PLACES_DEFAULT;

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

  /**
   * Sets the number of decimal places to print for instance values
   * during the regression test.
   */
  public void setRegressionDecimalPlaces(int value) {
    m_RegressionDecimalPlaces = value;
  }

  /**
   * Modifies the standard regression test to use a custom number of
   * decimal places when printing instance values.
   */
  @Override
  public void testRegression() {
    // Remember the current precision setting for printing instance values
    int previousDecimalPlaces = AbstractInstance.s_numericAfterDecimalPoint;

    // Set the precision to our custom value
    AbstractInstance.s_numericAfterDecimalPoint = m_RegressionDecimalPlaces;

    // Run the regression test
    super.testRegression();

    // Restore the saved precision value
    AbstractInstance.s_numericAfterDecimalPoint = previousDecimalPlaces;
  }
}
