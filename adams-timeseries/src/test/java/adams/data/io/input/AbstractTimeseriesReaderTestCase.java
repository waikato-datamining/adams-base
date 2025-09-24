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
 * AbstractTimeseriesReaderTestCase.java
 * Copyright (C) 2013-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.data.timeseries.Timeseries;
import adams.test.AbstractTestHelper;
import adams.test.TimeseriesTestHelper;

/**
 * Abstract test class for the timeseries readers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <A> the type of reader
 * @param <D> the type of data container
 */
public abstract class AbstractTimeseriesReaderTestCase<A extends DataContainerReader, D extends Timeseries>
  extends AbstractDataContainerReaderTestCase<A, D> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractTimeseriesReaderTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TimeseriesTestHelper(this, "adams/data/io/input/data");
  }
  
  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[]{0};
  }
}
