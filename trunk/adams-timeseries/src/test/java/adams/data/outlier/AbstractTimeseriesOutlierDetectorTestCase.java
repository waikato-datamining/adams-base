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
 * AbstractTimeseriesOutlierDetectorTestCase.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.outlier;

import adams.data.timeseries.Timeseries;
import adams.test.AbstractTestHelper;
import adams.test.TimeseriesTestHelper;

/**
 * Ancestor for filter test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7881 $
 */
public abstract class AbstractTimeseriesOutlierDetectorTestCase
  extends AbstractOutlierDetectorTestCase<Timeseries> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractTimeseriesOutlierDetectorTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TimeseriesTestHelper(this, "adams/data/outlier/data");
  }

  /**
   * Loads the data to process.
   *
   * @param filename	the filename to load (without path)
   * @return		always null
   */
  @Override
  protected Timeseries load(String filename) {
    return (Timeseries) m_TestHelper.load(filename);
  }
}
